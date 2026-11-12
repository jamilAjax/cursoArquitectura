# MercaPlus · monolito legado

Repositorio base del **caso hilo conductor** de la Certificación 2 (Arquitectura Moderna y Diseño Evolutivo de Software).
MercaPlus es un retailer omnicanal ficticio. Su plataforma de comercio electrónico es un monolito Spring Boot "en capas"
que funcionó durante años, pero hoy cada cambio es lento y riesgoso, y no resiste el tráfico de Black Friday.

Tu equipo es la firma consultora contratada para modernizarlo. **Este código tiene problemas a propósito**: encontrarlos,
explicarlos al cliente y corregirlos es el trabajo de los laboratorios.

## Requisitos

- JDK 21 y Maven 3.9+
- Docker (labs 6 y 7), k6 (lab 7)

## Ejecutar

```bash
mvn spring-boot:run
# Productos:        GET  http://localhost:8080/api/productos
# Buscar:           GET  http://localhost:8080/api/productos/buscar?nombre=cafe
# Crear pedido:     POST http://localhost:8080/api/pedidos
#                   {"clienteId":"c-001","items":[{"sku":"CAF-500","cantidad":2}]}
# Carrito:          POST http://localhost:8080/api/carrito/c-001/items  {"sku":"AUD-BT-01","cantidad":1}
#                   GET  http://localhost:8080/api/carrito/c-001
```

## Comandos de los laboratorios

| Lab | Comando | Qué esperar |
|-----|---------|-------------|
| 1 | `mvn test -Parquitectura -Dtest=ReglasArquitecturaTest` | Falla: cada violación es un hallazgo |
| 2 | `mvn test -Parquitectura -Dtest=ReglasHexagonalesTest` | Debe quedar en verde con el nuevo paquete `com.mercaplus.pedidos` |
| 3 | `mvn verify` y `mvn test-compile org.pitest:pitest-maven:mutationCoverage` | Reportes en `target/site/jacoco` y `target/pit-reports` |
| 5 | Copiar `docs/ci-plantilla.yml` a `.github/workflows/ci.yml` | Pipeline en GitHub Actions |
| 6 | `docker run -d -p 9000:9000 sonarqube:community` y `mvn verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.token=<token>` | Informe de deuda técnica |
| 7 | `docker compose -f docker/docker-compose.yml up --build --scale app=3` y `k6 run k6/carga-black-friday.js` | El carrito "se pierde" entre réplicas |

## Estructura

```
src/main/java/com/mercaplus
├── controller     capa web
├── service        capa de aplicación
├── repository     capa de datos (Spring Data JPA)
├── model          entidades JPA
├── util           integración con la pasarela de pagos
└── promociones    kata TDD del Lab 3 (dominio puro)
docs/              kata, plantilla del pipeline
docker/            Dockerfile, Compose con balanceador NGINX
k6/                prueba de carga de Black Friday
```

## Uso de IA

Puedes usar asistentes de IA. Declara en el README de tu entrega qué herramienta usaste, para qué, y qué sugerencias descartaste y por qué.
