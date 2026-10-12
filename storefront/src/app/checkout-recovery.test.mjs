import test from 'node:test';
import assert from 'node:assert/strict';
import {CheckoutRecovery, CheckoutRecoveryClient} from './checkout-recovery.mjs';

const HOLD='00000000-0000-4000-8000-000000000001';
const OTHER='00000000-0000-4000-8000-000000000002';
const ORDER='00000000-0000-4000-8000-000000000003';
const QUOTE='00000000-0000-4000-8000-000000000004';
const key='retry-key';
class MemoryStorage {
 data=new Map(); readError=false; writeError=false; deleteError=false; discardWrites=false;
 getItem(k){if(this.readError)throw new Error('read denied');return this.data.get(k)??null;}
 setItem(k,v){if(this.writeError)throw new Error('quota');if(!this.discardWrites)this.data.set(k,v);}
 removeItem(k){if(this.deleteError)throw new Error('delete denied');this.data.delete(k);}
}
function fixture(storage=new MemoryStorage(),keyFactory=()=>key){
 return {storage,state:new CheckoutRecovery({getStorage:()=>storage,keyFactory})};
}
function held(status='ACTIVE',owner='guest') {return {id:HOLD,eventId:'night',owner,quoteId:QUOTE,quantity:1,total:{cents:10000},createdAt:'2030-10-01T12:00:00Z',expiresAt:'2030-10-01T12:05:00Z',status};}
function ordered(overrides={}) {return {id:ORDER,holdId:HOLD,eventId:'night',owner:'guest',idempotencyKey:key,quoteId:QUOTE,quantity:1,total:{cents:10000},confirmedAt:'2030-10-01T12:01:00Z',...overrides};}
function pending(){const f=fixture();f.state.connect('guest');f.state.begin(HOLD);return f;}
function saved(f){return [...f.storage.data.values()][0];}
function slot(f){return [...f.storage.data.keys()][0];}

test('sign-in must precede persistence or a request',()=>{const {state,storage}=fixture();assert.throws(()=>state.begin(HOLD));assert.throws(()=>state.request());assert.equal(storage.data.size,0);});
test('no state is restored merely by constructing the coordinator',()=>{const f=pending(),copy=fixture(f.storage);assert.equal(copy.state.pending(),false);assert.throws(()=>copy.state.request());});
test('begin persists only owner, hold and key with a schema version',()=>{const f=pending();assert.deepEqual(JSON.parse(saved(f)),{version:1,owner:'guest',holdId:HOLD,key});assert.deepEqual(f.state.request(),{holdId:HOLD,key});});
test('reloading after authentication reuses the exact key without its generator',()=>{const f=pending(),copy=fixture(f.storage,()=>{throw new Error('must not regenerate');});copy.state.connect('guest');assert.deepEqual(copy.state.request(),f.state.request());assert.equal(copy.state.resolved(),false);});
test('a pending hold cannot be silently replaced',()=>{const f=pending();assert.throws(()=>f.state.begin(OTHER));assert.deepEqual(f.state.begin(HOLD),{holdId:HOLD,key});});
test('logout disconnects memory without deleting uncertain state',()=>{const f=pending(),before=saved(f);f.state.disconnect();assert.throws(()=>f.state.request());assert.equal(saved(f),before);f.state.connect('guest');assert.deepEqual(f.state.request(),{holdId:HOLD,key});});
test('another authenticated owner does not restore the previous checkout',()=>{const f=pending();f.state.connect('other-guest');assert.equal(f.state.pending(),false);assert.throws(()=>f.state.request());f.state.begin(OTHER);assert.equal(f.storage.data.size,2);f.state.connect('guest');assert.equal(f.state.request().holdId,HOLD);});
test('copying a foreign owner record into this owner slot is rejected',()=>{const f=pending();f.storage.setItem(slot(f),JSON.stringify({...JSON.parse(saved(f)),owner:'other-guest'}));assert.throws(()=>fixture(f.storage).state.connect('guest'));});
for(const malformed of ['not json','null','[]','{}',JSON.stringify({version:2,owner:'guest',holdId:HOLD,key}),JSON.stringify({version:1,owner:'guest',holdId:HOLD,key,token:'must-not-be-retained'})]) {
 test(`malformed/unknown snapshot fails closed: ${malformed.slice(0,32)}`,()=>{const f=pending();f.storage.setItem(slot(f),malformed);const copy=fixture(f.storage);assert.throws(()=>copy.state.connect('guest'));assert.throws(()=>copy.state.begin(HOLD));assert.equal(saved(f),malformed);});
}
test('oversized saved payload is rejected without dispatch',()=>{const f=pending();f.storage.setItem(slot(f),' '.repeat(5000));assert.throws(()=>fixture(f.storage).state.connect('guest'));});
test('credentials are neither accepted as state nor stored',()=>{const f=pending();assert.deepEqual(Object.keys(JSON.parse(saved(f))).sort(),['holdId','key','owner','version']);assert(!saved(f).includes('Authorization'));});
test('storage getter failure does not fall back to unsafe volatile checkout',()=>{const s=new CheckoutRecovery({getStorage:()=>{throw new Error('blocked');}});assert.throws(()=>s.connect('guest'));assert.throws(()=>s.begin(HOLD));});
test('a failed snapshot write blocks checkout identity creation',()=>{const f=fixture();f.state.connect('guest');f.storage.writeError=true;assert.throws(()=>f.state.begin(HOLD));assert.throws(()=>f.state.request());});
test('a write silently discarded by storage is detected by readback',()=>{const f=fixture();f.state.connect('guest');f.storage.discardWrites=true;assert.throws(()=>f.state.begin(HOLD));assert.equal(f.state.pending(),false);});
test('request fails if saved metadata is removed or replaced',()=>{const f=pending(),k=slot(f);f.storage.removeItem(k);assert.throws(()=>f.state.request());f.storage.setItem(k,JSON.stringify({version:1,owner:'guest',holdId:OTHER,key}));assert.throws(()=>f.state.request());});
test('read failure preserves pending identity and allows recovery after repair',()=>{const f=pending();f.storage.readError=true;assert.throws(()=>f.state.request());assert.equal(f.state.pending(),true);f.storage.readError=false;assert.equal(f.state.request().key,key);});
test('expired local clocks never erase unresolved metadata',()=>{const f=pending(),copy=fixture(f.storage);copy.state.connect('guest');assert.equal(copy.state.pending(),true);assert.throws(()=>copy.state.clearResolved());});
test('unconfirmed state cannot be cleared by a form reset',()=>{const f=pending();assert.throws(()=>f.state.clearResolved());assert.equal(f.state.pending(),true);});
test('active backend recovery retains the key without inventing a receipt',()=>{const f=pending();f.state.acceptRecovery({hold:held(),order:null});assert.equal(f.state.resolved(),false);assert.equal(f.state.order(),null);assert.equal(f.state.hold().id,HOLD);});
test('authoritative expired result permits explicit cleanup, not automatic loss',()=>{const f=pending();f.state.acceptRecovery({hold:held('EXPIRED'),order:null});assert.equal(f.state.pending(),true);assert.equal(f.state.resolved(),true);f.state.clearResolved();assert.equal(f.storage.data.size,0);assert.equal(f.state.pending(),false);});
test('confirmed recovery validates receipt and allows explicit next purchase',()=>{const f=pending();f.state.acceptRecovery({hold:held('CONFIRMED'),order:ordered()});assert.equal(f.state.order().id,ORDER);assert.equal(f.state.resolved(),true);f.state.clearResolved();f.state.begin(OTHER);assert.equal(f.state.request().holdId,OTHER);});
for(const overrides of [{owner:'other-guest'},{holdId:OTHER},{idempotencyKey:'wrong-key'},{id:'bad-id'},{quantity:-1},{total:{cents:-1}}]) {
 test(`bad order response retains unresolved state: ${JSON.stringify(overrides)}`,()=>{const f=pending();assert.throws(()=>f.state.complete(ordered(overrides)));assert.equal(f.state.resolved(),false);assert.equal(f.state.order(),null);assert.equal(f.state.request().key,key);});
}
for(const response of [{hold:held('ACTIVE'),order:ordered()},{hold:held('CONFIRMED'),order:null},{hold:held('EXPIRED'),order:ordered()},{hold:held('ACTIVE','other-guest'),order:null},{hold:{...held(),status:'UNKNOWN'},order:null}]) {
 test(`invalid recovery response is not trusted: ${response.hold.status}/${response.hold.owner}/${!!response.order}`,()=>{const f=pending();assert.throws(()=>f.state.acceptRecovery(response));assert.equal(f.state.resolved(),false);assert.equal(f.state.request().key,key);});
}
test('a confirmed receipt cannot regress to active on a stale response',()=>{const f=pending();f.state.complete(ordered());assert.throws(()=>f.state.acceptRecovery({hold:held(),order:null}));assert.equal(f.state.order().id,ORDER);});
test('returned objects cannot mutate internal retry or resolution state',()=>{const f=pending();f.state.acceptRecovery({hold:held('CONFIRMED'),order:ordered()});f.state.request().key='wrong';f.state.order().total.cents=999;f.state.hold().status='ACTIVE';assert.equal(f.state.request().key,key);assert.equal(f.state.order().total.cents,10000);assert.equal(f.state.hold().status,'CONFIRMED');});
test('a cleanup storage failure does not allow a new checkout',()=>{const f=pending();f.state.complete(ordered());f.storage.deleteError=true;assert.throws(()=>f.state.clearResolved());assert.equal(f.state.pending(),true);assert.throws(()=>f.state.begin(OTHER));});
test('failed authentication/reconnect removes prior in-memory receipt',()=>{const f=pending();f.state.complete(ordered());f.storage.readError=true;assert.throws(()=>f.state.connect('guest'));assert.equal(f.state.order(),null);assert.equal(f.state.pending(),false);});
test('invalid subjects/keys/hold IDs fail before persisting',()=>{const f=fixture();assert.throws(()=>f.state.connect(''));f.state.connect('guest');assert.throws(()=>f.state.begin('not-a-uuid'));const bad=fixture(new MemoryStorage(),()=> 'bad key');bad.state.connect('guest');assert.throws(()=>bad.state.begin(HOLD));assert.equal(bad.storage.data.size,0);});

test('lost confirmation response -> new coordinator -> recovery returns one test-server order',async()=>{
 const f=pending();let orders=0,drop=true;const requests=[];
 // Contract test double, NOT the real Java/MySQL service or an actual browser reload.
 const send=async(path,request)=>{
  requests.push({path,...request});assert.equal(request.headers['Idempotency-Key'],key);assert.deepEqual(request.body,{holdId:HOLD});
  if(path==='/api/orders'){if(!orders)orders++;if(drop){drop=false;throw new Error('response lost after server commit');}return ordered();}
  assert.equal(path,'/api/orders/recovery');return {hold:held(orders?'CONFIRMED':'ACTIVE'),order:orders?ordered():null};
 };
 await assert.rejects(new CheckoutRecoveryClient(f.state,send).confirm(),/lost/);
 assert.equal(f.state.resolved(),false);assert.equal(orders,1);
 const reloaded=fixture(f.storage,()=>{throw new Error('new key forbidden');});reloaded.state.connect('guest');
 const client=new CheckoutRecoveryClient(reloaded.state,send);await client.recover();assert.equal(reloaded.state.order().id,ORDER);
 await client.confirm();assert.equal(orders,1);assert.equal(new Set(requests.map(r=>r.headers['Idempotency-Key'])).size,1);
});
test('recovery never dispatches a confirmation POST',async()=>{const f=pending();const paths=[];await new CheckoutRecoveryClient(f.state,async(path)=>{paths.push(path);return {hold:held(),order:null};}).recover();assert.deepEqual(paths,['/api/orders/recovery']);});
for(const problem of ['503 unavailable','401 unauthorized','409 conflict','unreadable JSON','timeout']) {
 test(`transport failure retains the same key: ${problem}`,async()=>{const f=pending(),before=saved(f);await assert.rejects(new CheckoutRecoveryClient(f.state,async()=>{throw new Error(problem);}).recover());assert.equal(saved(f),before);assert.equal(f.state.resolved(),false);});
}
test('changed storage blocks the network request itself',async()=>{const f=pending();f.storage.removeItem(slot(f));let calls=0;await assert.rejects(new CheckoutRecoveryClient(f.state,async()=>{calls++;}).confirm());assert.equal(calls,0);});
test('a recovered expired checkout is never automatically confirmed',async()=>{const f=pending();f.state.acceptRecovery({hold:held('EXPIRED'),order:null});let calls=0;await assert.rejects(new CheckoutRecoveryClient(f.state,async()=>{calls++;}).confirm());assert.equal(calls,0);});

test('direct confirmation must agree with a previously observed hold amount',()=>{
 const f=pending();f.state.acceptRecovery({hold:held(),order:null});
 assert.throws(()=>f.state.complete(ordered({total:{cents:99999}})));
 assert.equal(f.state.resolved(),false);assert.equal(f.state.request().key,key);
});
test('a replay cannot silently replace a previously confirmed order identity',()=>{
 const f=pending();f.state.complete(ordered());
 assert.throws(()=>f.state.complete(ordered({id:OTHER})));
 assert.equal(f.state.order().id,ORDER);
});
test('a response arriving after logout cannot restore a receipt without authentication',async()=>{
 const f=pending();let release;const response=new Promise(resolve=>{release=resolve;});
 const pendingRequest=new CheckoutRecoveryClient(f.state,async()=>response).confirm();
 f.state.disconnect();release(ordered());await assert.rejects(pendingRequest);
 assert.equal(f.state.order(),null);assert.equal(JSON.parse(saved(f)).key,key);
});
test('late recovery for an old purchase cannot overwrite a new purchase',async()=>{
 const f=pending();f.state.complete(ordered());let release;const response=new Promise(resolve=>{release=resolve;});
 const pendingRequest=new CheckoutRecoveryClient(f.state,async()=>response).recover();
 f.state.clearResolved();f.state.begin(OTHER);release({hold:held('CONFIRMED'),order:ordered()});
 await assert.rejects(pendingRequest);assert.equal(f.state.request().holdId,OTHER);assert.equal(f.state.order(),null);
});

test('direct confirmation updates the observed hold status as well as the receipt',()=>{
 const f=pending();f.state.acceptRecovery({hold:held(),order:null});f.state.complete(ordered());
 assert.equal(f.state.hold().status,'CONFIRMED');assert.equal(f.state.order().id,ORDER);
});
