import {Component, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {CheckoutRecovery, CheckoutRecoveryClient} from './checkout-recovery.mjs';
import type {RecoveredHold, RecoveredOrder} from './checkout-recovery.mjs';
interface Product {id:string; name:string; kind:'ADMISSION'|'EVENT'; revision:number;}
interface Amount {amount:string; currency:string; cents:string;}
interface Quote {id:string; productId:string; productName:string; visitDate:string; quantity:number; pricingVersion:string; expiresAt:string; appliedOffer:string|null; subtotal:Amount; discount:Amount; total:Amount; lines:{variant:string; quantity:number; total:Amount}[];}
class RequestFailure extends Error {constructor(readonly status:number,message:string){super(message);}}
@Component({selector:'app-root',standalone:true,imports:[CommonModule,FormsModule],templateUrl:'./app.component.html'})
export class AppComponent {
 readonly products=signal<Product[]>([]); readonly quote=signal<Quote|null>(null);
 readonly hold=signal<RecoveredHold|null>(null); readonly order=signal<RecoveredOrder|null>(null);
 readonly pending=signal(false); readonly resolved=signal(false); readonly expired=signal(false);
 readonly authenticated=signal<string|null>(null); readonly busy=signal(false);
 readonly status=signal('Load the catalog and authenticate. Saved checkout metadata is recovered only after sign-in.');
 token='';productId='';visitDate='';adults=1;children=0;eventId='';
 private readonly checkout=new CheckoutRecovery();
 private readonly recoveryClient=new CheckoutRecoveryClient(this.checkout,
  (path,request)=>this.api<unknown>('/booking'+path,request.body,request.headers));
 constructor(){const d=new Date();d.setDate(d.getDate()+7);this.visitDate=d.toISOString().slice(0,10);}
 private async api<T>(path:string,body?:unknown,headers:Record<string,string>={}):Promise<T>{
  const response=await fetch(path,{method:body===undefined?'GET':'POST',headers:{...(body===undefined?{}:{'Content-Type':'application/json'}),...(this.token?{'Authorization':`Bearer ${this.token}`} : {}),...headers},body:body===undefined?undefined:JSON.stringify(body),signal:AbortSignal.timeout(8000),cache:'no-store'});
  let data:unknown;
  try{data=await response.json();}catch{throw new RequestFailure(response.status,'Service returned unreadable data. A checkout may still have succeeded; keep its saved retry identity.');}
  if(!response.ok){const problem=data as {detail?:string;title?:string};throw new RequestFailure(response.status,`${response.status}: ${problem?.detail || problem?.title || 'Request failed'}`);}
  return data as T;
 }
 private syncCheckout(){
  this.pending.set(this.checkout.pending());this.resolved.set(this.checkout.resolved());this.expired.set(this.checkout.expired());
  this.hold.set(this.checkout.hold());this.order.set(this.checkout.order());
 }
 private async run(operation:()=>Promise<void>){
  if(this.busy())return;this.busy.set(true);
  try{await operation();}catch(e){
   if(e instanceof RequestFailure && e.status===401){this.token='';this.credentialChanged();this.status.set('Authentication expired or failed. Sign in again; unresolved checkout metadata has been retained.');}
   else this.status.set(e instanceof Error?e.message:'Operation failed. Check saved checkout status before starting another purchase.');
  }finally{this.syncCheckout();this.busy.set(false);}
 }
 credentialChanged(){
  this.checkout.disconnect();this.authenticated.set(null);this.quote.set(null);this.syncCheckout();
  this.status.set('Authenticate to continue. Editing credentials does not erase a saved checkout.');
 }
 logout(){if(this.busy())return;this.token='';this.credentialChanged();this.status.set('Signed out locally. Unresolved nonsecret checkout metadata is retained in this tab for the same account.');}
 resetQuote(){if(!this.pending())this.quote.set(null);}
 private requireSession(){if(!this.authenticated() || !this.checkout.connected())throw new Error('Authenticate with the booking service first.');}
 private recoveryMessage(){
  if(this.checkout.order())return 'Recovered the server-confirmed order. No replacement purchase was created.';
  if(this.checkout.expired())return 'The server reports this hold expired without an order. You may now clear it and start another purchase.';
  return 'The server reports an active hold. Nothing was purchased by checking status; confirmation remains an explicit action.';
 }
 connect(){return this.run(async()=>{
  this.credentialChanged();
  if(!/^[A-Za-z0-9_-]{32,128}$/.test(this.token))throw new Error('Enter DEMO_GUEST_TOKEN or DEMO_OTHER_GUEST_TOKEN from your local .env.');
  const session=await this.api<{subject:string}>('/booking/api/session');
  this.checkout.connect(session.subject);this.authenticated.set(session.subject);
  if(this.checkout.pending()){await this.recoveryClient.recover();this.status.set(this.recoveryMessage());}
  else this.status.set('Authenticated. No checkout is saved for this account in this tab. Select a product and request a quote.');
 });}
 load(){return this.run(async()=>{this.products.set(await this.api<Product[]>('/catalog/api/products'));this.status.set('Published products loaded.');});}
 createQuote(){return this.run(async()=>{
  this.requireSession();if(this.checkout.pending())throw new Error('Resolve the saved checkout before requesting another purchase.');
  this.quote.set(null);
  const query=`mutation Quote($input:QuoteInput!){createQuote(input:$input){id productId productName visitDate quantity pricingVersion expiresAt appliedOffer subtotal{amount currency cents} discount{amount currency cents} total{amount currency cents} lines{variant quantity total{amount currency cents}}}}`;
  const result=await this.api<{data?:{createQuote:Quote};errors?:{message:string}[]}>('/pricing/graphql',{query,variables:{input:{productId:this.productId,visitDate:this.visitDate,adults:this.adults,children:this.children}}});
  if(result.errors?.length)throw new Error(result.errors.map(e=>e.message).join('; '));
  if(!result.data?.createQuote)throw new Error('No quote returned');this.quote.set(result.data.createQuote);this.status.set('Quote saved. Its price is protected until expiration; inventory is not yet held.');
 });}
 reserve(){return this.run(async()=>{
  this.requireSession();if(this.checkout.pending())throw new Error('A saved checkout must be resolved first.');
  const quote=this.quote();if(!quote)throw new Error('Create a quote first');
  if(this.products().find(p=>p.id===quote.productId)?.kind!=='EVENT')throw new Error('This booking slice supports evening events only. Admission checkout is future work.');
  const hold=await this.api<RecoveredHold>('/booking/api/holds',{eventId:this.eventId,quoteId:quote.id});
  this.checkout.begin(hold.id); // Write and read back retry identity BEFORE any order request.
  this.checkout.acceptRecovery({hold,order:null});
  this.status.set('Inventory held and retry identity saved in this tab. Confirm before the server deadline.');
 });}
 recover(){return this.run(async()=>{this.requireSession();await this.recoveryClient.recover();this.status.set(this.recoveryMessage());});}
 confirm(){return this.run(async()=>{
  this.requireSession();await this.recoveryClient.confirm();
  this.status.set('Simulated purchase confirmed. Its saved identity remains until you explicitly start another purchase.');
 });}
 startOver(){return this.run(async()=>{
  this.requireSession();this.checkout.clearResolved();this.quote.set(null);this.eventId='';
  this.status.set('Resolved checkout metadata cleared. You can request a new quote. No server order was cancelled.');
 });}
}
