# ADR 0003 · Motor de promociones como librería de dominio

- **Estado:** aceptada
- **Fecha:** clase 6

## Contexto
Las reglas de promoción cambian en cada campaña y el error del 2x1 cobrado dos veces costó dinero y reclamos.

## Opciones consideradas
1. Motor de reglas externo.
2. Hoja de cálculo cargada en tiempo de ejecución.
3. Clase de dominio pura, construida con TDD y expuesta por el puerto `PromocionesPort`.

## Decisión
Opción 3: `com.mercaplus.promociones.MotorPromociones`, conectado al caso de uso con `MotorPromocionesAdapter`.

## Consecuencias
- (+) Pruebas en milisegundos y puntaje de mutación de 80 % o más.
- (+) El caso de uso no conoce la implementación.
- (−) Cada regla nueva requiere un despliegue; se acepta porque el pipeline (Lab 5) reduce ese costo.
