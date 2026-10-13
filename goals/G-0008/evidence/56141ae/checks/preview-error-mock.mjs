import http from 'node:http';

http.createServer((request, response) => {
  if (request.method !== 'POST' || request.url !== '/api/admin/price-books/preview') {
    response.writeHead(404).end();
    return;
  }
  let length = 0;
  request.on('data', chunk => { length += chunk.length; });
  request.on('end', () => {
    process.stdout.write(`Preview POST received; request bytes=${length}\n`);
    response.writeHead(400, { 'content-type': 'application/problem+json' });
    response.end(JSON.stringify({code: 'INVALID_INPUT', detail: 'Visit date is in the past in the park time zone'}));
  });
}).listen(8782, '127.0.0.1', () => process.stdout.write('Loopback preview error fixture ready\n'));
