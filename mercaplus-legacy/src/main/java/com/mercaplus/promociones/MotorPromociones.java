package com.mercaplus.promociones;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * LAB 3 - Kata TDD: motor de promociones de Black Friday.
 *
 * Implementa las reglas de docs/kata-promociones.md UNA A UNA con el ciclo rojo-verde-refactor.
 * Esta clase NO debe depender de Spring ni de JPA: es dominio puro.
 */
public class MotorPromociones {

    private final Map<String, BigDecimal> descuentoPorCategoria;

    /**
     * @param descuentoPorCategoria porcentaje de descuento por categoria, ej. {"TECNOLOGIA" -> 0.20}
     */
    public MotorPromociones(Map<String, BigDecimal> descuentoPorCategoria) {
        this.descuentoPorCategoria = Map.copyOf(descuentoPorCategoria);
    }

    /**
     * @param items carrito
     * @param cupon valor fijo en pesos a descontar del total (puede ser cero)
     * @return total a pagar en pesos colombianos, sin decimales
     */
    public BigDecimal totalAPagar(List<Item> items, BigDecimal cupon) {
        throw new UnsupportedOperationException("Implementar con TDD - empieza por la regla 1");
    }
}
