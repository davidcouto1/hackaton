import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  vus: 100, // usuários virtuais para stress
  duration: '2m', // duração aumentada
  thresholds: {
    http_req_duration: ['p(95)<1000'], // 95% das requisições em menos de 1s
    http_req_failed: ['rate<0.05'], // menos de 5% de falhas
  },
};

export default function () {
  const url = 'http://localhost:8080/simulacoes';
  const payload = JSON.stringify({
    produto: 'credito-pessoal',
    valor: 10000,
    prazo: 24,
    taxaJuros: 1.5
  });
  const params = { headers: { 'Content-Type': 'application/json' } };
  const res = http.post(url, payload, params);
  check(res, {
    'status é 200': (r) => r.status === 200,
  });
  sleep(1);
}
