// LAB 7 - Prueba de carga de Black Friday
// Ejecutar:  k6 run k6/carga-black-friday.js
// Cada usuario virtual agrega 3 productos a su carrito y luego lo consulta.
// Si el carrito vive en memoria de una sola replica, la verificacion "carrito completo" falla.
import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
  stages: [
    { duration: '30s', target: 20 },
    { duration: '1m', target: 80 },
    { duration: '30s', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<800'],
    'checks{verificacion:carrito}': ['rate>0.99'],
  },
};

const SKUS = ['AUD-BT-01', 'CAF-500', 'BAL-FUT-5'];

export default function () {
  const cliente = `cliente-${__VU}-${__ITER}`;
  const headers = { 'Content-Type': 'application/json' };

  for (const sku of SKUS) {
    http.post(`${BASE}/api/carrito/${cliente}/items`, JSON.stringify({ sku, cantidad: 1 }), { headers });
  }

  const res = http.get(`${BASE}/api/carrito/${cliente}`);
  check(res, {
    'respuesta 200': (r) => r.status === 200,
  });
  check(res, {
    'carrito completo': (r) => r.status === 200 && r.json().length === SKUS.length,
  }, { verificacion: 'carrito' });

  sleep(1);
}
