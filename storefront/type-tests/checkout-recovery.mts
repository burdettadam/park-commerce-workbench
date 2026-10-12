// Compile-only public contract checks. This is NOT an Angular framework build.
import {CheckoutRecovery, CheckoutRecoveryClient} from '../src/app/checkout-recovery.mjs';
import type {RecoveredOrder, RecoveryPayload} from '../src/app/checkout-recovery.mjs';
const state = new CheckoutRecovery({getStorage: () => sessionStorage});
const restored: boolean = state.connect('server-authenticated-subject');
const client = new CheckoutRecoveryClient(state, async (path, options): Promise<unknown> => {
  const headers: {'Idempotency-Key':string} = options.headers;
  const hold: string = options.body.holdId;
  return {path, headers, hold};
});
const recovered: Promise<RecoveryPayload> = client.recover();
const receipt: Promise<RecoveredOrder> = client.confirm();
const optionalReceipt: RecoveredOrder | null = state.order();
// @ts-expect-error Authenticated subject must be a string, not a credential object.
state.connect({Authorization:'not-an-identity'});
// @ts-expect-error Invalid storage API must not typecheck.
new CheckoutRecovery({getStorage: () => ({token:'not-storage'})});
void [restored, recovered, receipt, optionalReceipt];
