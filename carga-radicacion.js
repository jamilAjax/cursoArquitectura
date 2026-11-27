// Requisito del reto: p95 < 1 s con 50 usuarios concurrentes radicando solicitudes.
// k6 run k6/carga-radicacion.js
import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
  stages: [
    { duration: '30s', target: 50 },
    { duration: '1m', target: 50 },
    { duration: '15s', target: 0 },
  ],
  thresholds: { http_req_duration: ['p(95)<1000'], checks: ['rate>0.99'] },
};

export default function () {
  const doc = String(10000000 + Math.floor(Math.random() * 89999999));
  const cuerpo = {
    tipoDocumento: 'CC', numeroDocumento: doc,
    monto: 3000000, plazoMeses: 12,
    ingresosMensuales: 4500000, egresosMensuales: 1800000,
  };
  // TODO: ajustar la ruta y el cuerpo al contrato de su API
  const res = http.post(`${BASE}/api/solicitudes`, JSON.stringify(cuerpo), { headers: { 'Content-Type': 'application/json' } });
  check(res, { 'radicada (201)': (r) => r.status === 201 });
  sleep(1);
}
