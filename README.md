# CrediAndes · servicio de solicitudes de crédito (reto práctico)

Repositorio inicial del **reto práctico de la Certificación 2**. El enunciado completo, los entregables y la rúbrica
están en el documento *Reto práctico – CrediAndes* que entrega el docente.

## Lo que ya viene

| Elemento | Dónde | Regla |
|----------|-------|-------|
| Estructura hexagonal vacía | `com.crediandes.solicitudes.{dominio, aplicacion, infraestructura}` | Respétala |
| Raíz de composición | `infraestructura.config` (créalo) | Único lugar donde se arman casos de uso y adaptadores con `@Configuration`/`@Bean`; la capa de aplicación no puede usar Spring |
| Puertos de buró y listas restrictivas | `aplicacion/puerto/salida` | **No cambies sus firmas** (son el contrato del cliente) |
| Fitness functions ArchUnit | `src/test/.../ReglasArquitecturaTest` | Corren en cada `mvn verify`; puedes agregar reglas, no quitarlas |
| Umbral de cobertura 80 % | `pom.xml` (JaCoCo) | No lo bajes |
| Pruebas de mutación | `mvn test-compile org.pitest:pitest-maven:mutationCoverage` | Meta ≥ 70 % en dominio y aplicación |
| Docker, NGINX, k6, pipeline | `docker/`, `k6/`, `.github/workflows/ci.yml` | Completa los TODO |
| Plantilla de ADR | `docs/adr/0001-plantilla.md` | Mínimo 3 ADR |

## Reglas de negocio (resumen)

1. Monto entre 500.000 y 20.000.000 COP; plazo entre 6 y 36 meses.
2. Cuota fija mensual con tasa de 2,1 % mes vencido: `cuota = M · i / (1 − (1 + i)^−n)`, redondeada al peso.
3. Capacidad de pago: la cuota no puede superar el 30 % de (ingresos − egresos).
4. Documento en listas restrictivas → `RECHAZADA` (sin consultar buró).
5. Puntaje de buró < 500 → `RECHAZADA`; 500–650 → `EN_REVISION`; > 650 con capacidad de pago → `APROBADA`; > 650 sin capacidad de pago → `RECHAZADA`.
6. Si el buró no responde en 2 s o falla → `EN_REVISION` (nunca aprobar sin puntaje).

## Ejecutar

```bash
mvn verify
docker compose -f docker/docker-compose.yml up --build --scale app=2
k6 run k6/carga-radicacion.js
```

Notas:

- El repositorio inicial pasa `mvn verify` en verde. Cada clase que agregues entra al umbral de 80 %, así que escribe la prueba junto con el código.
- PIT necesita al menos una clase en `dominio` o `aplicacion` con lógica; sobre el repositorio vacío responde que no hay mutantes.
- Con varias réplicas, cada una con su H2 en memoria, un `GET /api/solicitudes/{id}` puede caer en otra réplica y responder 404. Configura PostgreSQL en el compose y pasa `DB_URL`, `DB_USER` y `DB_PASSWORD` desde un `.env` que no se sube al repositorio.

## Declaración de uso de IA (obligatoria)

Completa esta sección al entregar: herramienta, para qué la usaron y qué sugerencias descartaron y por qué.
