# Kata TDD: motor de promociones de Black Friday (Lab 3)

Implementa `MotorPromociones.totalAPagar` **una regla a la vez**, con el ciclo rojo-verde-refactor.
Haz un commit por cada paso en verde (`regla N - verde`, `regla N - refactor`).

| # | Regla | Ejemplo |
|---|-------|---------|
| 1 | Sin promociones, el total es la suma de precio x cantidad. | 2 x 21.500 + 1 x 249.000 = 292.000 |
| 2 | 2x1: en un producto marcado `promo2x1`, de cada 2 unidades se cobra 1. Una unidad impar sobrante se cobra. | 3 cafes de 32.900 -> se cobran 2 = 65.800 |
| 3 | Descuento porcentual por categoria (configurado en el constructor). | 20 % en TECNOLOGIA: TV de 2.199.000 -> 1.759.200 |
| 4 | Las promociones NO se acumulan: cada item recibe solo la que mas ahorra. | Audifonos 2x1 y 20 % TECNOLOGIA, 2 unidades de 189.900 -> 2x1 gana: 189.900 |
| 5 | Cupon de monto fijo sobre el total ya promocionado; el total nunca es negativo. | Total 100.000, cupon 20.000 -> 80.000 (con la regla 6 el cupon nunca deja el total en 0) |
| 6 | Tope: el ahorro total (promos + cupon) no supera el 50 % del valor bruto. | Bruto 100.000, ahorro calculado 70.000 -> se paga 50.000 |
| 7 | El resultado se redondea a pesos (sin decimales) con HALF_UP. | 10.000,5 -> 10.001 |
| 8 | Cantidad menor o igual a cero o precio negativo -> `IllegalArgumentException`. | |

## Reglas del juego

- No escribas codigo de produccion sin una prueba en rojo que lo pida.
- Una sola razon para fallar por prueba; nombres que se lean como especificacion.
- Al final ejecuta:
  - Cobertura: `mvn verify` y abre `target/site/jacoco/index.html`
  - Mutacion: `mvn test-compile org.pitest:pitest-maven:mutationCoverage` y abre `target/pit-reports/index.html`
- Meta de la tarea: puntaje de mutacion del paquete `promociones` >= 80 %.

## Pregunta para el comite

Las pruebas de `PedidoServiceTest` dan cobertura alta al servicio. Ejecuta PIT sobre ellas: que puntaje de mutacion obtienen y por que?
