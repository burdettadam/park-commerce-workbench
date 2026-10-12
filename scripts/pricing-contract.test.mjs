/** Offline cross-language contract checks. Requires Node + JDK 21, never Maven or network.
 * Uses test-only in-memory stores. Not Spring/Jackson/GraphQL runtime evidence.
 */
import test, {before, after} from 'node:test';
import assert from 'node:assert/strict';
import {readFileSync, readdirSync, mkdtempSync, rmSync} from 'node:fs';
import {tmpdir} from 'node:os';
import path from 'node:path';
import {fileURLToPath} from 'node:url';
import {spawnSync} from 'node:child_process';

const root=fileURLToPath(new URL('../',import.meta.url));
const pack=JSON.parse(readFileSync(path.join(root,'contracts/pricing/pricing-contract-fixtures.json'),'utf8'));
const contract=JSON.parse(readFileSync(path.join(root,'contracts/pricing/quote-api-v1.json'),'utf8'));

// Independent integer-rational oracle. Java uses BigDecimal and a different control path.
// Literal fixtures below also constrain this oracle, rather than trusting a second implementation alone.
function oracle(input) {
  const {adults,children,offer}=input;
  const bps=BigInt(offer && adults+children>=offer.minimumTickets ? offer.discountBasisPoints : 0);
  const lines=[];
  for(const [variant,quantity,unit] of [['ADULT',adults,input.adultUnitCents],['CHILD',children,input.childUnitCents]]) {
    if(quantity===0)continue;
    const subtotal=BigInt(unit)*BigInt(quantity);
    const discount=(subtotal*bps+5000n)/10000n;
    lines.push({variant,quantity,unitPrice:unit,subtotal:String(subtotal),discount:String(discount),total:String(subtotal-discount)});
  }
  const subtotal=lines.reduce((sum,line)=>sum+BigInt(line.subtotal),0n);
  const discount=lines.reduce((sum,line)=>sum+BigInt(line.discount),0n);
  return {subtotal:String(subtotal),discount:String(discount),total:String(subtotal-discount),
    appliedOffer:bps>0n?'fixture-offer':null,lines};
}
function summary(value) {
  return {subtotal:value.subtotal,discount:value.discount,total:value.total,
    lineDiscounts:value.lines.map(line=>line.discount),appliedOffer:value.appliedOffer};
}
function validateFixture(row) {
  assert.match(row.id,/^[a-z0-9-]{1,64}$/);
  for(const field of ['adultUnitCents','childUnitCents']){
    assert.match(row.input[field],/^(0|[1-9][0-9]*)$/); assert(BigInt(row.input[field])<=1000000n);
  }
  for(const field of ['adults','children'])assert(Number.isInteger(row.input[field]) && row.input[field]>=0 && row.input[field]<=20);
  assert(row.input.adults+row.input.children>=1 && row.input.adults+row.input.children<=20);
  if(row.input.offer){
    assert(Number.isInteger(row.input.offer.minimumTickets) && row.input.offer.minimumTickets>=1 && row.input.offer.minimumTickets<=20);
    assert(Number.isInteger(row.input.offer.discountBasisPoints) && row.input.offer.discountBasisPoints>=0 && row.input.offer.discountBasisPoints<=10000);
  }
}
const generated=[];
// Reproducible, explicitly bounded sample; not a claim of exhaustive/randomized proof.
let seed=0x51a7c0de;
const draw=max=>{seed=(Math.imul(seed,1664525)+1013904223)>>>0;return seed%max;};
for(let i=0;i<384;i++) {
  const size=1+draw(20),adults=draw(size+1);
  generated.push({id:`sample-${String(i).padStart(3,'0')}`,input:{adultUnitCents:String(draw(1000001)),childUnitCents:String(draw(1000001)),
    adults,children:size-adults,offer:i%7===0?null:{minimumTickets:1+draw(20),discountBasisPoints:draw(10001)}}});
}
const all=[...pack.cases,...generated];let build, actual;
function run(command,args,options={}) {
  const result=spawnSync(command,args,{cwd:root,encoding:'utf8',timeout:60000,maxBuffer:8*1024*1024,...options});
  assert.equal(result.error,undefined,`${command}: ${result.error?.message}`);
  assert.equal(result.status,0,`${command} failed; NOT skipped\n${result.stdout}\n${result.stderr}`);
  return result.stdout;
}
function javaFiles(dir) {
  return readdirSync(dir,{withFileTypes:true}).flatMap(entry=>entry.isDirectory()?javaFiles(path.join(dir,entry.name)):
    entry.name.endsWith('.java')?[path.join(dir,entry.name)]:[]);
}
before(()=>{
  assert.equal(pack.revision,'pricing-fixtures-1');assert.equal(pack.currency,'USD');assert.equal(pack.cases.length,22);
  assert.equal(new Set(all.map(row=>row.id)).size,all.length);
  for(const row of all)validateFixture(row);
  build=mkdtempSync(path.join(tmpdir(),'park pricing fixtures '));
  run('javac',['--release','21','-encoding','UTF-8','-Xlint:all','-d',build,...javaFiles(path.join(root,'core/src/main/java')),
    path.join(root,'core/src/test/java/dev/parkcommerce/core/MemoryStores.java'),
    path.join(root,'core/src/test/java/dev/parkcommerce/core/PricingFixtureProbe.java')]);
  const input=all.map(({id,input:i})=>[id,i.adultUnitCents,i.childUnitCents,i.adults,i.children,i.offer?.minimumTickets??0,i.offer?.discountBasisPoints??0].join('\t')).join('\n')+'\n';
  const rows=run('java',['-cp',build,'dev.parkcommerce.core.PricingFixtureProbe'],{input}).trim().split('\n').map(line=>JSON.parse(line));
  assert.equal(rows.length,all.length);actual=new Map(rows.map(row=>[row.id,row]));assert.equal(actual.size,all.length);
});
after(()=>{if(build)rmSync(build,{recursive:true,force:true});});

for(const fixture of pack.cases)test(`pricing fixture: ${fixture.id}`,()=>{
  const independent=oracle(fixture.input);
  assert.deepEqual(summary(independent),fixture.expected,'literal fixture disagrees with independent rational arithmetic');
  assert.deepEqual(actual.get(fixture.id)?.preview,independent,'actual Java preview disagrees with expected values');
  assert.deepEqual(actual.get(fixture.id)?.quote,independent,'actual Java quote disagrees with expected values');
});
test('384 deterministic parties agree on every line and conserved total',()=>{
  for(const row of generated){
    const expected=oracle(row.input);const result=actual.get(row.id);
    assert.deepEqual(result.preview,expected,row.id+' preview');assert.deepEqual(result.quote,expected,row.id+' quote');
    assert.equal(BigInt(result.quote.subtotal)-BigInt(result.quote.discount),BigInt(result.quote.total),row.id+' conservation');
    assert(result.quote.lines.every(line=>BigInt(line.discount)<=BigInt(line.subtotal)),row.id+' no negative line totals');
  }
});
test('an altered fixture expectation is detected, not regenerated from Java',()=>{
  const fixture=structuredClone(pack.cases[0]);fixture.expected.total=String(BigInt(fixture.expected.total)+1n);
  assert.throws(()=>assert.deepEqual(summary(oracle(fixture.input)),fixture.expected));
});
test('per-ticket and whole-cart discount policies would fail the selected fixtures',()=>{
  const split=pack.cases.find(row=>row.id==='split-variant-rounding');
  assert.notEqual(BigInt(split.expected.discount),(BigInt(split.expected.subtotal)*1000n+5000n)/10000n);
  const grouped=pack.cases.find(row=>row.id==='grouped-variant-rounding');
  assert.notEqual(BigInt(grouped.expected.discount),2n*((5n*1000n+5000n)/10000n));
});

// Source-only SDL compatibility checks: these do NOT start the GraphQL engine or validate resolvers.
const schema=readFileSync(path.join(root,'pricing-service/src/main/resources/graphql/pricing.graphqls'),'utf8').replace(/#[^\n]*/g,'');
function fields(name) {
  const body=schema.match(new RegExp(`type\\s+${name}\\s*\\{([^}]+)\\}`))?.[1];assert(body,`missing type ${name}`);
  return Object.fromEntries([...body.matchAll(/(\w+)\s*:\s*([\w!\[\]]+)/g)].map(match=>[match[1],match[2]]));
}
for(const [type,expected] of [['Quote',contract.graphqlFields],['Money',contract.moneyFields],['QuoteStatus',contract.statusFields]])
  test(`SDL source: ${type} retains the pinned field names and representations`,()=>{
    const found=fields(type);for(const [name,shape] of Object.entries(expected))assert.equal(found[name],shape,`${type}.${name}`);
  });
test('SDL source: historical quote query remains and status query is additive',()=>{
  assert.match(schema,/quote\(id:\s*ID!\):\s*Quote\b/);assert.match(schema,/quoteStatus\(id:\s*ID!\):\s*QuoteStatus\b/);
  const states=schema.match(/enum\s+QuoteState\s*\{([^}]+)\}/)?.[1].trim().split(/\s+/);
  assert.deepEqual(states,contract.statusStates);
});
