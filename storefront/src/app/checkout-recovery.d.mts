export interface RecoveryRequest {holdId:string; key:string;}
export interface SessionStorageLike {
 getItem(key:string):string|null;
 setItem(key:string,value:string):void;
 removeItem(key:string):void;
}
export interface RecoveredHold {
 id:string; eventId:string; owner:string; quoteId:string; quantity:number;
 total:{cents:number}; createdAt:string; expiresAt:string;
 status:'ACTIVE'|'CONFIRMED'|'EXPIRED';
}
export interface RecoveredOrder {
 id:string; holdId:string; eventId:string; owner:string; idempotencyKey:string;
 quoteId:string; quantity:number; total:{cents:number}; confirmedAt:string;
}
export interface RecoveryPayload {hold:RecoveredHold; order:RecoveredOrder|null;}
export class CheckoutRecoveryError extends Error {code:string; constructor(code:string,message:string);}
export class CheckoutRecovery {
 constructor(options?:{getStorage?:()=>SessionStorageLike;keyFactory?:()=>string});
 connect(authenticatedSubject:string):boolean;
 disconnect():void;
 connected():boolean;
 pending():boolean;
 resolved():boolean;
 expired():boolean;
 hold():RecoveredHold|null;
 order():RecoveredOrder|null;
 begin(holdId:string):RecoveryRequest;
 request():RecoveryRequest;
 complete(order:unknown):RecoveredOrder;
 acceptRecovery(response:unknown):RecoveryPayload;
 clearResolved():void;
}
export class CheckoutRecoveryClient {
 constructor(state:CheckoutRecovery,send:(path:string,request:{body:{holdId:string};headers:{'Idempotency-Key':string}})=>Promise<unknown>);
 recover():Promise<RecoveryPayload>;
 confirm():Promise<RecoveredOrder>;
}
