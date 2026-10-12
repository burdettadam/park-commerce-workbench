export class CheckoutState {
 constructor(keyFactory?: () => string);
 begin(holdId: string): {holdId: string; key: string};
 request(): {holdId: string; key: string};
 complete<T extends {holdId: string}>(order:T): T;
 order(): unknown;
 reset(): void;
}
