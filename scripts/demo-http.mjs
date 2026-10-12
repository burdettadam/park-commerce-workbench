#!/usr/bin/env node
/** Live HTTP acceptance exercise. Requires the actual local Spring services and databases. */
import {readFile} from 'node:fs/promises';
import {fileURLToPath} from 'node:url';
import assert from 'node:assert/strict';
import {randomUUID} from 'node:crypto';
import {CheckoutRecovery, CheckoutRecoveryClient} from '../storefront/src/app/checkout-recovery.mjs';

async function config(){
 const file=new URL('../.env',import.meta.url);let text;
 try{text=await readFile(file,'utf8');}catch{throw new Error('Run bash scripts/configure-local.sh and start Docker Compose first.');}
 const result={};for(const line of text.split(/\r?\n/)){if(!line.trim()||line.trim().startsWith('#'))continue;const m=line.match(/^([A-Z_]+)=([A-Za-z0-9_-]+)$/);if(m)result[m[1]]=m[2];}
 return {...result,...process.env};
}
const cfg=await config();
for(const key of ['DEMO_EMPLOYEE_TOKEN','DEMO_GUEST_TOKEN','DEMO_OTHER_GUEST_TOKEN'])assert.match(cfg[key]||'',/^[A-Za-z0-9_-]{32,128}$/,'Missing or malformed local token: '+key);
const ports={catalog:cfg.CATALOG_PORT||8081,pricing:cfg.PRICING_PORT||8082,booking:cfg.BOOKING_PORT||8083};
for(const p of Object.values(ports))assert(Number.isInteger(Number(p))&&Number(p)>0&&Number(p)<65536,'Invalid port');
const tokens={employee:cfg.DEMO_EMPLOYEE_TOKEN,guest:cfg.DEMO_GUEST_TOKEN,other:cfg.DEMO_OTHER_GUEST_TOKEN};
let passed=0;
async function api(service,path,{role='guest',body,headers={},expected=200}={}){
 const response=await fetch(`http://127.0.0.1:${ports[service]}${path}`,{
  method:body===undefined?'GET':'POST',headers:{...(body===undefined?{}:{'Content-Type':'application/json'}),...(role?{'Authorization':`Bearer ${tokens[role]}`} : {}),...headers},body:body===undefined?undefined:JSON.stringify(body),signal:AbortSignal.timeout(15000)});
 const data=await response.json().catch(()=>({}));
 if(expected!==null)assert.equal(response.status,expected,`${service} ${path} status; response ${JSON.stringify(data)}`);
 return {status:response.status,data};
}
function check(name,fn){fn();passed++;console.log(`PASS ${name}`);}
const suffix=randomUUID().slice(0,8),productId=`lantern-${suffix}`,eventId=`evening-${suffix}`;
const now=new Date(),day=new Date(now.getTime()+7*86400000).toISOString().slice(0,10);
const product={id:productId,revision:1,name:`Lantern Evening ${suffix}`,kind:'EVENT',variants:['ADULT','CHILD']};
const priceBook=(version,cents)=>({productId,version,catalogRevision:1,rates:[{variant:'ADULT',from:day,through:day,unitPrice:{cents}},{variant:'CHILD',from:day,through:day,unitPrice:{cents:7000}}],offer:{code:'family-four',minimumTickets:4,discountBasisPoints:1000}});
const request={productId,visitDate:day,adults:1,children:0};
try{
 await api('catalog','/api/admin/products',{role:'guest',body:product,expected:403});check('guest cannot publish',()=>{});
 await api('catalog','/api/admin/products',{role:'employee',body:product,expected:201});check('product publication',()=>{});
 const listed=await api('catalog',`/api/products/${productId}`,{role:null});check('public product read',()=>assert.equal(listed.data.id,productId));
 // Independent catalog-only product: does not change the checkout/pricing fixture below.
 const draftId=`editorial-${suffix}`,adminPath=`/api/admin/products/${draftId}`;
 const draftBody=(expectedDraftVersion,basePublishedRevision,name)=>({expectedDraftVersion,basePublishedRevision,content:{name,kind:'EVENT',variants:['ADULT','CHILD']}});
 await api('catalog',`${adminPath}/drafts`,{role:'guest',body:draftBody(0,0,'Hidden'),expected:403});
 await api('catalog',`${adminPath}/history`,{role:null,expected:401});
 check('catalog drafts and audit history are employee-only',()=>{});
 const saved=(await api('catalog',`${adminPath}/drafts`,{role:'employee',body:draftBody(0,0,'Reviewed catalog draft'),expected:201})).data;
 await api('catalog',`/api/products/${draftId}`,{role:null,expected:404});
 check('saved catalog draft is invisible to guests',()=>{assert.equal(saved.version,1);assert.equal(saved.audit.actor,'employee');});
 await api('catalog',`${adminPath}/drafts`,{role:'employee',body:draftBody(0,0,'Stale overwrite'),expected:409});
 check('stale catalog draft write conflicts',()=>{});
 await api('catalog',`${adminPath}/drafts`,{role:'employee',body:draftBody(1,0,'Later unpublished draft'),expected:201});
 const publication=(await api('catalog',`${adminPath}/publications`,{role:'employee',body:{draftVersion:1,expectedPublishedRevision:0},expected:201})).data;
 const publishedProduct=(await api('catalog',`/api/products/${draftId}`,{role:null})).data;
 check('publication uses the reviewed version and server audit actor',()=>{assert.equal(publication.sourceDraftVersion,1);assert.equal(publication.audit.actor,'employee');assert.equal(publishedProduct.name,'Reviewed catalog draft');assert(!Object.hasOwn(publishedProduct,'audit'));});
 const pending=(await api('catalog',`${adminPath}/drafts/latest`,{role:'employee'})).data;
 check('later draft survives another version being published',()=>{assert.equal(pending.version,2);assert.equal(pending.content.name,'Later unpublished draft');});
 await api('catalog',`${adminPath}/publications`,{role:'employee',body:{draftVersion:2,expectedPublishedRevision:0},expected:409});
 check('publication rejects a now-stale draft base',()=>{});
 await api('catalog',`${adminPath}/drafts`,{role:'employee',body:draftBody(2,1,'Second publication'),expected:201});
 await api('catalog',`${adminPath}/publications`,{role:'employee',body:{draftVersion:3,expectedPublishedRevision:1},expected:201});
 const history=(await api('catalog',`${adminPath}/history?limit=1`,{role:'employee'})).data;
 await api('catalog',`${adminPath}/drafts`,{role:'employee',body:draftBody(3,2,'Third publication'),expected:201});
 await api('catalog',`${adminPath}/publications`,{role:'employee',body:{draftVersion:4,expectedPublishedRevision:2},expected:201});
 const historyTail=(await api('catalog',`${adminPath}/history?limit=1&cursor=${history.nextCursor}`,{role:'employee'})).data;
 check('catalog history traversal stays pinned while new publications arrive',()=>{assert.equal(history.snapshotRevision,2);assert.equal(history.items[0].product.revision,2);assert.equal(historyTail.snapshotRevision,2);assert.equal(historyTail.items[0].product.revision,1);assert.equal(historyTail.nextCursor,null);});
 const candidate=priceBook(1,10000),previewRequest={candidate,request:{...request,adults:2,children:2}};
 await api('pricing','/api/admin/price-books/preview',{role:'guest',body:previewRequest,expected:403});check('guest cannot preview unpublished employee rates',()=>{});
 const preview=(await api('pricing','/api/admin/price-books/preview',{role:'employee',body:previewRequest})).data;
 check('unpublished preview is an explainable what-if, not a quote',()=>{assert.equal(preview.mode,'UNPUBLISHED_INPUT');assert.equal(preview.subtotal.cents,34000);assert.equal(preview.discount.cents,3400);assert.equal(preview.total.cents,30600);assert.match(preview.candidateId,/^sha256:[0-9a-f]{64}$/);assert(!Object.hasOwn(preview,'id'));assert(!Object.hasOwn(preview,'expiresAt'));});
 await api('pricing',`/api/admin/price-books/${productId}`,{role:'employee',expected:404});check('preview did not publish its candidate',()=>{});
 await api('pricing','/api/admin/price-books',{role:'employee',body:priceBook(1,10000),expected:201});check('first price book',()=>{});
 const previewedQuote=(await api('pricing','/api/quotes',{body:previewRequest.request,expected:201})).data;
 check('publishing the previewed candidate preserves its calculation',()=>{assert.deepEqual(previewedQuote.lines,preview.lines);assert.equal(previewedQuote.total.cents,preview.total.cents);});
 await api('booking','/api/admin/events',{role:'employee',body:{id:eventId,productId,visitDate:day,capacity:3},expected:201});
 const original=(await api('pricing','/api/quotes',{body:request,expected:201})).data;
 await api('pricing','/api/admin/price-books',{role:'employee',body:priceBook(2,12500),expected:201});
 const newQuote=(await api('pricing','/api/quotes',{body:request,expected:201})).data;
 const reread=(await api('pricing',`/api/quotes/${original.id}`)).data;
 check('published prices do not mutate old quotes',()=>{assert.equal(newQuote.total.cents,12500);assert.equal(reread.total.cents,10000);assert.equal(reread.pricingVersion,1);});
 await api('pricing',`/api/quotes/${original.id}`,{role:'other',expected:404});check('quote ownership',()=>{});
 const quoteStatus=(await api('pricing',`/api/quotes/${original.id}/status`)).data;
 check('quote status observes the immutable snapshot without renewing expiry',()=>{assert.equal(quoteStatus.state,'UNEXPIRED');assert.equal(quoteStatus.parkTimeZone,'America/New_York');assert.deepEqual(quoteStatus.quote,reread);assert(quoteStatus.evaluatedAt);});
 await api('pricing',`/api/quotes/${original.id}/status`,{role:'other',expected:404});check('quote status hides other owners',()=>{});
 const graphStatus=(await api('pricing','/graphql',{body:{query:'query($id:ID!){quoteStatus(id:$id){state evaluatedAt parkTimeZone quote{id catalogRevision pricingVersion createdAt expiresAt kind total{cents amount currency}}}}',variables:{id:original.id}}})).data;
 check('GraphQL status preserves existing string money and version fields',()=>{assert(!graphStatus.errors,JSON.stringify(graphStatus.errors));const s=graphStatus.data.quoteStatus;assert.equal(s.state,'UNEXPIRED');assert.equal(s.quote.id,original.id);assert.equal(s.quote.catalogRevision,'1');assert.equal(s.quote.pricingVersion,'1');assert.equal(s.quote.total.cents,'10000');assert.equal(s.quote.kind,'EVENT');assert.equal(s.quote.expiresAt,original.expiresAt);});
 const invalidId=await api('pricing','/api/quotes/1-1-1-1-1/status',{expected:400});
 const invalidGraph=(await api('pricing','/graphql',{body:{query:'{quoteStatus(id:"1-1-1-1-1"){state}}'}})).data;
 check('REST and GraphQL reject abbreviated quote IDs',()=>{assert.equal(invalidId.data.code,'INVALID_INPUT');assert.equal(invalidGraph.errors?.[0]?.extensions?.code,'INVALID_INPUT');});
 const invalidDate=await api('pricing','/api/quotes',{body:{...request,visitDate:'2030-02-29'},expected:400});
 check('invalid calendar date returns INVALID_INPUT',()=>assert.equal(invalidDate.data.code,'INVALID_INPUT'));

 await api('pricing','/api/quotes',{body:{...request,adults:1.5},expected:400});check('fractional ticket counts rejected',()=>{});
 const gql=(await api('pricing','/graphql',{body:{query:'mutation($input:QuoteInput!){createQuote(input:$input){id total{amount cents currency} appliedOffer lines{quantity variant}}}',variables:{input:{...request,adults:2,children:2}}}})).data;
 check('GraphQL quote and family offer',()=>{assert(!gql.errors,JSON.stringify(gql.errors));assert.equal(gql.data.createQuote.total.amount,'351.00');assert.equal(gql.data.createQuote.appliedOffer,'family-four');});
 const unauth=(await api('pricing','/graphql',{role:null,body:{query:`{quote(id:"${original.id}"){id}}`}})).data;
 check('GraphQL private resolver authorization',()=>assert(unauth.errors?.length));
 const hold=(await api('booking','/api/holds',{body:{eventId,quoteId:original.id},expected:201})).data;
 const identity=(await api('booking','/api/session')).data;
 check('guest identity is derived from authentication',()=>assert.deepEqual(identity,{subject:'guest'}));
 await api('booking','/api/session',{role:null,expected:401});
 await api('booking','/api/session',{role:'employee',expected:403});
 check('guest session endpoint rejects anonymous and employee credentials',()=>{});
 // This script uses a storage test double with REAL HTTP services; it is not an actual browser reload test.
 const sessionRecords=new Map();
 const storage={getItem:k=>sessionRecords.get(k)??null,setItem:(k,v)=>sessionRecords.set(k,v),removeItem:k=>sessionRecords.delete(k)};
 const state=new CheckoutRecovery({getStorage:()=>storage});state.connect(identity.subject);state.begin(hold.id);
 const key=state.request().key;
 const send=async(path,options)=>(await api('booking',path,options)).data;
 const observed=await new CheckoutRecoveryClient(state,send).recover();
 check('recovery observes an active hold without confirming it',()=>{assert.equal(observed.hold.status,'ACTIVE');assert.equal(observed.order,null);});
 await api('booking','/api/orders/recovery',{role:'other',body:{holdId:hold.id},headers:{'Idempotency-Key':key},expected:404});
 check('recovery is owner-scoped',()=>{});
 // Deliberately discard the confirmation receipt, then construct a fresh coordinator.
 await api('booking','/api/orders',{body:{holdId:hold.id},headers:{'Idempotency-Key':key}});
 const reloaded=new CheckoutRecovery({getStorage:()=>storage,keyFactory:()=>{throw new Error('Recovery must not generate a key');}});
 reloaded.connect((await api('booking','/api/session')).data.subject);
 const client=new CheckoutRecoveryClient(reloaded,send);
 const recovered=await client.recover(),order=recovered.order;
 check('fresh coordinator recovers the server order after a discarded response',()=>{assert.equal(recovered.hold.status,'CONFIRMED');assert(order);assert.equal(order.holdId,hold.id);assert.equal(order.idempotencyKey,key);});
 const replay=await client.confirm();
 await api('booking','/api/orders/recovery',{body:{holdId:hold.id},headers:{'Idempotency-Key':randomUUID()},expected:409});
 check('recovery with a replacement key is a conflict',()=>{});
 check('checkout keeps the original amount and is idempotent',()=>{assert.equal(order.total.cents,10000);assert.equal(order.id,replay.id);});
 await api('booking',`/api/orders/${order.id}`,{role:'other',expected:404});check('order ownership',()=>{});
 const candidates=await Promise.all(Array.from({length:10},()=>api('pricing','/api/quotes',{body:request,expected:201})));
 const outcomes=await Promise.all(candidates.map(q=>api('booking','/api/holds',{body:{eventId,quoteId:q.data.id},expected:null})));
 check('concurrent HTTP requests allocate only remaining capacity',()=>{assert.equal(outcomes.filter(r=>r.status===201).length,2);for(const r of outcomes.filter(r=>r.status!==201)){assert.equal(r.status,409);assert.equal(r.data.code,'SOLD_OUT');}});
 const availability=(await api('booking',`/api/events/${eventId}/availability`,{role:null})).data;
 check('no negative or extra availability',()=>assert.equal(availability.available,0));
 // Admission-only flow: same booking service/database, never an event reservation.
 const admissionId=`admission-${suffix}`;
 await api('catalog','/api/admin/products',{role:'employee',body:{id:admissionId,revision:1,name:'Synthetic dated admission',kind:'ADMISSION',variants:['ADULT','CHILD']},expected:201});
 await api('pricing','/api/admin/price-books',{role:'employee',body:{productId:admissionId,version:1,catalogRevision:1,rates:[{variant:'ADULT',from:day,through:day,unitPrice:{cents:8000}},{variant:'CHILD',from:day,through:day,unitPrice:{cents:5000}}],offer:null},expected:201});
 const aq=(await api('pricing','/api/quotes',{body:{productId:admissionId,visitDate:day,adults:2,children:1},expected:201})).data;
 const admissionKey=randomUUID(),admissionBody={quoteId:aq.id},admissionHeaders={'Idempotency-Key':admissionKey};
 const absent=(await api('booking','/api/admission/orders/recovery',{body:admissionBody,headers:admissionHeaders})).data;
 check('admission recovery observes absence without submitting a purchase',()=>{assert.equal(absent.state,'NOT_CONFIRMED');assert.equal(absent.order,null);});
 await api('booking','/api/admission/orders',{role:'employee',body:admissionBody,headers:admissionHeaders,expected:403});
 await api('booking','/api/admission/orders',{role:'other',body:admissionBody,headers:admissionHeaders,expected:404});
 check('admission confirmation requires the quote-owning guest',()=>{});
 const admitted=(await api('booking','/api/admission/orders',{body:admissionBody,headers:admissionHeaders})).data;
 check('admission receipt preserves quote and complete variant entitlement set',()=>{assert.deepEqual(admitted.quote,aq);assert.equal(admitted.quote.total.cents,21000);assert.equal(admitted.entitlements.length,2);assert.equal(admitted.entitlements.reduce((n,e)=>n+e.quantity,0),3);for(const e of admitted.entitlements){assert.equal(e.orderId,admitted.id);assert.equal(e.state,'ACTIVE');}});
 const admissionReplay=(await api('booking','/api/admission/orders',{body:admissionBody,headers:admissionHeaders})).data;
 check('admission retry retains the original order and entitlement identities',()=>assert.deepEqual(admissionReplay,admitted));
 const admissionStatus=(await api('booking','/api/admission/orders/recovery',{body:admissionBody,headers:admissionHeaders})).data;
 const admissionRead=(await api('booking',`/api/admission/orders/${admitted.id}`)).data;
 check('admission recovery and read return the same owner-bound receipt',()=>{assert.equal(admissionStatus.state,'CONFIRMED');assert.deepEqual(admissionStatus.order,admitted);assert.deepEqual(admissionRead,admitted);});
 await api('booking',`/api/admission/orders/${admitted.id}`,{role:'other',expected:404});
 await api('booking','/api/admission/orders',{body:admissionBody,headers:{'Idempotency-Key':randomUUID()},expected:409});
 check('admission receipt cannot be read by another guest or reissued under a new key',()=>{});
 await api('booking','/api/admission/orders',{body:{quoteId:newQuote.id},headers:{'Idempotency-Key':randomUUID()},expected:400});
 check('event quote cannot create admission entitlements',()=>{});
 const eventReceipt=(await api('booking',`/api/orders/${order.id}`)).data;
 const admissionAfter=(await api('booking',`/api/events/${eventId}/availability`,{role:null})).data;
 check('admission leaves old event receipt and confirmed event count unchanged',()=>{assert.deepEqual(eventReceipt,order);assert.equal(admissionAfter.confirmed,availability.confirmed);assert.equal(admissionAfter.capacity,availability.capacity);});
 // Active event holds may naturally expire during HTTP exercise; do not mislabel that as admission mutation.
 console.log(`\n${passed} live HTTP acceptance checks passed. No payment was taken.`);
 console.log(JSON.stringify({productId,eventId,visitDate:day,originalOrderId:order.id,note:'Two extra holds expire after five minutes; confirmed capacity stays sold. This run created synthetic data and did not erase existing data.'},null,2));
}catch(error){console.error('HTTP demonstration FAILED:',error.message);console.error('These checks require live services; an unavailable dependency is not a passed or skipped test.');process.exitCode=1;}
