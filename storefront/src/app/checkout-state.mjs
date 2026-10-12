/** A key belongs to a hold, not an HTTP attempt. No credentials or browser storage. */
export class CheckoutState {
  #hold = null; #key = null; #order = null;
  constructor(keyFactory = () => globalThis.crypto.randomUUID()) { this.keyFactory = keyFactory; }
  begin(holdId) {
    if (typeof holdId !== 'string' || !holdId) throw new Error('A hold ID is required');
    if (holdId !== this.#hold) { this.#hold = holdId; this.#key = this.keyFactory(); this.#order = null; }
    return this.request();
  }
  request() {
    if (!this.#hold) throw new Error('Reserve inventory first');
    return {holdId: this.#hold, key: this.#key};
  }
  complete(order) {
    if (!order || order.holdId !== this.#hold) throw new Error('Order does not match the current hold');
    this.#order = order; return order;
  }
  order() { return this.#order; }
  reset() { this.#hold = null; this.#key = null; this.#order = null; }
}
