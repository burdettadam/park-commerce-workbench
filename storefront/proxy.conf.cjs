// Development proxies only. Upstream ports can be overridden in this Node process.
const rule = (prefix, port) => ({context: [prefix, `${prefix}/**`], target: `http://127.0.0.1:${port}`, secure: false, changeOrigin: false, pathRewrite: {[`^${prefix}`]: ''}});
module.exports = [rule('/catalog',process.env.CATALOG_PORT || 8081), rule('/pricing',process.env.PRICING_PORT || 8082), rule('/booking',process.env.BOOKING_PORT || 8083)];
