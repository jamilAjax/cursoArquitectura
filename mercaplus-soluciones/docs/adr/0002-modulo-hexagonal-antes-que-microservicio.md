# ADR 0002 · Módulo hexagonal dentro del monolito antes que microservicio

- **Estado:** aceptada
- **Fecha:** clase 6 (revisión después de Black Friday)

## Contexto
El checkout cambia cada semana y falla en Black Friday. Un microservicio exige pipeline, observabilidad, red y un equipo de guardia que hoy no existen en MercaPlus.

## Opciones consideradas
1. Microservicio de pedidos desde ya.
2. Módulo hexagonal en el mismo despliegue, aislado con reglas de ArchUnit.
3. Refactorizar el monolito sin módulos.

## Decisión
Opción 2: el paquete `com.mercaplus.pedidos` con dominio, aplicación e infraestructura, protegido por `ReglasHexagonalesTest` en el pipeline.

## Consecuencias
- (+) Cambios localizados y pruebas en milisegundos.
- (+) Se puede extraer a un servicio más adelante sin reescribir el núcleo.
- (−) Sigue escalando junto con el monolito.
- (−) Mantener las reglas de ArchUnit es obligatorio para que no se vuelva a acoplar.
