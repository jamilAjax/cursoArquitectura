# Hallazgos sembrados en mercaplus-legacy (SOLO DOCENTE)

Lista de problemas puestos a proposito, el laboratorio donde se descubren y donde se corrigen.

| # | Hallazgo | Donde | Lab que lo descubre | Lab que lo corrige |
|---|----------|-------|---------------------|--------------------|
| 1 | Logica de negocio en el controlador (stock, descuentos, 5 % por monto, pago) | `PedidoController.crear` | 1 | 2 |
| 2 | El controlador accede directo a repositorios saltando la capa de aplicacion | `PedidoController`, `ProductoController` | 1 (ArchUnit `capas_respetadas`) | 2 |
| 3 | La API expone entidades JPA | Todos los controladores | 1 (`la_web_no_expone_entidades`) | 2 (DTO en `/api/v2/pedidos`) |
| 4 | Entidad de dominio acoplada a infraestructura (`Pedido.pagar` llama a la pasarela estatica) | `model/Pedido` | 1 (`el_modelo_no_depende_de_infraestructura`) | 2 (`PagosPort`) |
| 5 | Inyeccion SQL por concatenacion | `ProductoController.buscar` | 4 | 4 |
| 6 | Controlador usa `EntityManager` | `ProductoController` | 1 (`los_controladores_no_usan_jpa`) | 4 |
| 7 | Llave de API en el codigo y escrita en logs | `PasarelaPagosClient` | 4 / 5 (escaneo de secretos) | 4 |
| 8 | Usuario de BD "admin" con contrasena en properties | `application.properties` | 4 | 4 (variables de entorno, usuario de minimo privilegio) |
| 9 | Consola H2 expuesta y todos los endpoints de actuator abiertos | `application.properties` | 4 | 4 |
| 10 | Endpoint para cambiar precios sin autenticacion | `ProductoController.cambiarPrecio` | 4 (STRIDE: Tampering / Elevation) | 4 (retirado) |
| 11 | El 2x1 se descuenta dos veces | `PromocionService` (`descuentoBlackFriday`) | 3 (detonante) | 3 (`MotorPromociones`) |
| 12 | Pruebas sin aserciones: cobertura alta, puntaje de mutacion ~0 | `PedidoServiceTest` | 3 (PIT) | 3 |
| 13 | Redondeo distinto segun el monto (HALF_UP vs DOWN) | `PedidoService.calcularTotalConIva` | 3 / 6 (Sonar) | 6 (registro de deuda) |
| 14 | Estado en memoria: el carrito vive en un `HashMap` de una replica | `CarritoService` | 7 (k6 falla con 3 replicas) | 7 (Redis) |
| 15 | Estados como `String` magicos ("NUEVO", "PAGADO") | `model/Pedido` | 6 | 2 (enum `EstadoPedido`) |
| 16 | `System.out.println` en lugar de logging estructurado | `PasarelaPagosClient` | 6 (Sonar) | 6 |

## Resultado esperado de `mvn test -Parquitectura -Dtest=ReglasArquitecturaTest` en el legado

Las 4 reglas fallan. ArchUnit lista cada dependencia ilegal con clase y linea: es la materia prima del informe de diagnostico del Lab 1.

## Demo de la falacia de la cobertura (clase 3)

1. `mvn verify`: `PedidoService` aparece con cobertura de lineas alta.
2. `mvn test-compile org.pitest:pitest-maven:mutationCoverage`: casi todos los mutantes de `PedidoService` sobreviven.
3. Mostrar la version con aserciones (este repo): la cobertura es la misma y los mutantes mueren.

## Nota de verificacion

El codigo de dominio, aplicacion y la kata se compilo y verifico con `javac` (26 casos). Antes de la primera clase, ejecutar
`mvn verify` en ambos repositorios con acceso a Maven Central para confirmar la configuracion de plugins.
