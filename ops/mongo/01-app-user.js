// Runs only when initializing an empty Mongo volume. This account is limited to catalog.
const catalog = db.getSiblingDB('catalog');
catalog.createUser({user: 'catalog', pwd: process.env.MONGO_APP_PASSWORD, roles: [{role: 'readWrite', db: 'catalog'}]});
