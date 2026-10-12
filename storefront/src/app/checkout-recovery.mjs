import {CheckoutState} from './checkout-state.mjs';

const PREFIX = 'park-commerce:checkout:v1:';
const UUID = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
const KEY = /^[A-Za-z0-9._-]{1,100}$/;
const PRODUCT_ID = /^[a-z0-9][a-z0-9-]{0,63}$/;
const copy = value => value == null ? value : structuredClone(value);
const ownerValid = owner => typeof owner === 'string' && owner.trim().length > 0 && owner.length <= 100 && !/[\u0000-\u001f\u007f]/.test(owner);
const timestamp = value => typeof value === 'string' && Number.isFinite(Date.parse(value));
const amount = value => value && Number.isSafeInteger(value.cents) && value.cents >= 0;

export class CheckoutRecoveryError extends Error {
  constructor(code, message) { super(message); this.name = 'CheckoutRecoveryError'; this.code = code; }
}
function fail(code, message) { throw new CheckoutRecoveryError(code, message); }
function identity(record, owner) {
  return record && !Array.isArray(record) && Object.keys(record).sort().join(',') === 'holdId,key,owner,version'
    && record.version === 1 && record.owner === owner && typeof record.holdId === 'string'
    && UUID.test(record.holdId) && typeof record.key === 'string' && KEY.test(record.key);
}
function parse(raw, owner) {
  if (typeof raw !== 'string' || raw.length > 4096) fail('INVALID_SNAPSHOT', 'Saved checkout metadata is invalid. Do not start a replacement purchase.');
  let record;
  try { record = JSON.parse(raw); } catch { fail('INVALID_SNAPSHOT', 'Saved checkout metadata is unreadable. Do not start a replacement purchase.'); }
  if (!identity(record, owner)) fail('INVALID_SNAPSHOT', 'Saved checkout identity does not match the signed-in account or supported format.');
  return record;
}
function same(a, b) { return a.owner === b.owner && a.holdId === b.holdId && a.key === b.key && a.version === b.version; }

/** Owns NONSECRET retry metadata only. The caller must obtain owner from an
 * authenticated backend before connect(). This is not an authentication system.
 * Use sessionStorage for same-tab reload recovery; never persist bearer tokens.
 */
export class CheckoutRecovery {
  #owner = null; #state = null; #storage = null; #slot = null; #terminal = null; #hold = null;
  #getStorage; #keyFactory;
  constructor({getStorage = () => globalThis.sessionStorage, keyFactory = () => globalThis.crypto.randomUUID()} = {}) {
    this.#getStorage = getStorage; this.#keyFactory = keyFactory;
  }
  connect(owner) {
    this.disconnect(); // A failed login/switch must not leave the previous receipt visible.
    if (!ownerValid(owner)) fail('SIGN_IN_REQUIRED', 'A valid authenticated subject is required.');
    let storage, raw;
    const slot = PREFIX + encodeURIComponent(owner);
    try {
      storage = this.#getStorage();
      if (!storage || !['getItem','setItem','removeItem'].every(name => typeof storage[name] === 'function')) throw new Error('Unavailable');
      raw = storage.getItem(slot);
    } catch { fail('STORAGE_UNAVAILABLE', 'Browser session storage is unavailable; checkout is blocked to preserve retry safety.'); }
    const record = raw === null ? null : parse(raw, owner);
    this.#owner = owner; this.#slot = slot; this.#storage = storage;
    if (record) { this.#state = new CheckoutState(() => record.key); this.#state.begin(record.holdId); }
    return this.pending();
  }
  disconnect() {
    this.#owner = null; this.#slot = null; this.#storage = null;
    this.#state = null; this.#terminal = null; this.#hold = null;
    // Keep saved metadata: logout or credential editing is not an order cancellation.
  }
  connected() { return this.#owner !== null; }
  pending() { return this.#state !== null; }
  resolved() { return this.#terminal === 'CONFIRMED' || this.#terminal === 'EXPIRED'; }
  expired() { return this.#terminal === 'EXPIRED'; }
  hold() { return copy(this.#hold); }
  order() { return copy(this.#state?.order() ?? null); }
  #requireSession() { if (!this.connected()) fail('SIGN_IN_REQUIRED', 'Authenticate with the booking service before continuing.'); }
  #read() {
    try { return this.#storage.getItem(this.#slot); }
    catch { fail('STORAGE_UNAVAILABLE', 'Cannot verify saved checkout identity. No checkout request was sent.'); }
  }
  #record(request) { return {version: 1, owner: this.#owner, holdId: request.holdId, key: request.key}; }
  begin(holdId) {
    this.#requireSession();
    if (typeof holdId !== 'string' || !UUID.test(holdId)) fail('INVALID_RESPONSE', 'The server did not return a valid hold identity.');
    if (this.#state) {
      if (this.#state.request().holdId !== holdId) fail('UNRESOLVED_CHECKOUT', 'Resolve the saved checkout before starting another purchase.');
      return this.request();
    }
    if (this.#read() !== null) fail('IDENTITY_CHANGED', 'A saved checkout already exists. Reauthenticate to recover it.');
    const key = this.#keyFactory();
    if (typeof key !== 'string' || !KEY.test(key)) fail('INVALID_SNAPSHOT', 'Cannot generate a valid checkout retry key.');
    const record = this.#record({holdId, key});
    try { this.#storage.setItem(this.#slot, JSON.stringify(record)); }
    catch { fail('STORAGE_UNAVAILABLE', 'Could not save retry identity. No order was sent; the unconfirmed hold will expire.'); }
    const stored = parse(this.#read(), this.#owner);
    if (!same(record, stored)) fail('IDENTITY_CHANGED', 'Saved checkout changed during creation. Do not confirm with a replacement key.');
    this.#state = new CheckoutState(() => key); this.#state.begin(holdId);
    this.#terminal = null; this.#hold = null;
    return this.request();
  }
  request() {
    this.#requireSession();
    if (!this.#state) fail('UNRESOLVED_CHECKOUT', 'Reserve inventory or recover a saved checkout first.');
    const request = this.#state.request();
    const stored = parse(this.#read(), this.#owner);
    if (!same(stored, this.#record(request))) fail('IDENTITY_CHANGED', 'Saved checkout identity changed. No request was sent.');
    return request;
  }
  #validateOrder(order, request) {
    if (!order || typeof order.id !== 'string' || !UUID.test(order.id)
        || order.holdId !== request.holdId || order.owner !== this.#owner || order.idempotencyKey !== request.key
        || typeof order.quoteId !== 'string' || !UUID.test(order.quoteId)
        || typeof order.eventId !== 'string' || !PRODUCT_ID.test(order.eventId)
        || !Number.isInteger(order.quantity) || order.quantity < 1 || order.quantity > 20
        || !amount(order.total) || !timestamp(order.confirmedAt))
      fail('INVALID_RESPONSE', 'The receipt does not match this account and saved checkout. Keep the original retry key.');
  }
  complete(order) {
    const request = this.request();
    this.#validateOrder(order, request);
    if (this.#hold && (order.quoteId !== this.#hold.quoteId || order.eventId !== this.#hold.eventId
        || order.quantity !== this.#hold.quantity || order.total.cents !== this.#hold.total.cents))
      fail('INVALID_RESPONSE', 'The receipt does not match the previously observed hold. Keep the retry identity.');
    const previous = this.#state.order();
    if (previous && (order.id !== previous.id || order.quoteId !== previous.quoteId || order.eventId !== previous.eventId
        || order.quantity !== previous.quantity || order.total.cents !== previous.total.cents
        || Date.parse(order.confirmedAt) !== Date.parse(previous.confirmedAt)))
      fail('INVALID_RESPONSE', 'A checkout replay returned a different order. Keep the original receipt and retry identity.');
    this.#state.complete(copy(order)); this.#terminal = 'CONFIRMED';
    if (this.#hold) this.#hold = {...this.#hold, status:'CONFIRMED'};
    return this.order();
  }
  acceptRecovery(response) {
    const request = this.request(), hold = response?.hold, order = response?.order;
    if (!hold || hold.id !== request.holdId || hold.owner !== this.#owner
        || typeof hold.quoteId !== 'string' || !UUID.test(hold.quoteId)
        || typeof hold.eventId !== 'string' || !PRODUCT_ID.test(hold.eventId)
        || !Number.isInteger(hold.quantity) || hold.quantity < 1 || hold.quantity > 20 || !amount(hold.total)
        || !timestamp(hold.createdAt) || !timestamp(hold.expiresAt) || Date.parse(hold.createdAt) >= Date.parse(hold.expiresAt)
        || !['ACTIVE','CONFIRMED','EXPIRED'].includes(hold.status)
        || (hold.status === 'CONFIRMED' ? !order : order !== null))
      fail('INVALID_RESPONSE', 'Booking returned an invalid recovery result. The original checkout remains saved.');
    if (this.#terminal === 'CONFIRMED' && hold.status !== 'CONFIRMED'
        || this.#terminal === 'EXPIRED' && hold.status === 'ACTIVE')
      fail('INVALID_RESPONSE', 'Booking returned an older checkout state. Do not discard the retry identity.');
    if (order) {
      this.#validateOrder(order, request);
      if (order.quoteId !== hold.quoteId || order.eventId !== hold.eventId || order.quantity !== hold.quantity || order.total.cents !== hold.total.cents)
        fail('INVALID_RESPONSE', 'Recovered hold and receipt do not agree. Keep the retry identity.');
      this.complete(order);
    } else this.#terminal = hold.status === 'EXPIRED' ? 'EXPIRED' : null;
    this.#hold = copy(hold);
    return {hold: this.hold(), order: this.order()};
  }
  clearResolved() {
    this.#requireSession();
    if (!this.resolved()) fail('UNRESOLVED_CHECKOUT', 'An uncertain checkout cannot be discarded. Check its status with the server first.');
    this.request(); // Do not erase a replacement record written by another actor.
    try { this.#storage.removeItem(this.#slot); }
    catch { fail('STORAGE_UNAVAILABLE', 'Could not clear the resolved checkout. Retry cleanup before starting another purchase.'); }
    if (this.#read() !== null) fail('STORAGE_UNAVAILABLE', 'Resolved checkout metadata was not removed.');
    this.#state = null; this.#terminal = null; this.#hold = null;
  }
}

/** The Angular UI and live HTTP exercise share this dispatch path. `send` accepts
 * relative API paths; credentials remain in its caller and never enter storage.
 * Any exception leaves the saved request intact. Recovery is not confirmation.
 */
export class CheckoutRecoveryClient {
  constructor(state, send) { this.state = state; this.send = send; }
  #request() { const {holdId,key} = this.state.request(); return {body:{holdId},headers:{'Idempotency-Key':key}}; }
  async recover() {
    const request = this.#request();
    return this.state.acceptRecovery(await this.send('/api/orders/recovery', request));
  }
  async confirm() {
    if (this.state.expired()) fail('EXPIRED', 'This hold expired without an order. Resolve it before making a new purchase.');
    const request = this.#request();
    return this.state.complete(await this.send('/api/orders', request));
  }
}
