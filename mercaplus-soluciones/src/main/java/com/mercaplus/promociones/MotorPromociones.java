package com.mercaplus.promociones;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * SOLUCION LAB 3 - Motor de promociones de Black Friday, construido con TDD (reglas 1 a 8).
 */
public class MotorPromociones {

    private static final BigDecimal TOPE_AHORRO = new BigDecimal("0.50");

    private final Map<String, BigDecimal> descuentoPorCategoria;

    public MotorPromociones(Map<String, BigDecimal> descuentoPorCategoria) {
        this.descuentoPorCategoria = Map.copyOf(descuentoPorCategoria);
    }

    public BigDecimal totalAPagar(List<Item> items, BigDecimal cupon) {
        BigDecimal bruto = BigDecimal.ZERO;
        BigDecimal ahorroPromos = BigDecimal.ZERO;

        for (Item item : items) {
            validar(item);
            BigDecimal valorItem = item.precioUnitario().multiply(BigDecimal.valueOf(item.cantidad()));
            bruto = bruto.add(valorItem);
            ahorroPromos = ahorroPromos.add(mejorAhorro(item, valorItem));
        }

        BigDecimal valorCupon = cupon == null ? BigDecimal.ZERO : cupon.max(BigDecimal.ZERO);
        BigDecimal ahorroTotal = ahorroPromos.add(valorCupon)
                .min(bruto.multiply(TOPE_AHORRO));

        return bruto.subtract(ahorroTotal)
                .max(BigDecimal.ZERO)
                .setScale(0, RoundingMode.HALF_UP);
    }

    private BigDecimal mejorAhorro(Item item, BigDecimal valorItem) {
        BigDecimal ahorro2x1 = item.promo2x1()
                ? item.precioUnitario().multiply(BigDecimal.valueOf(item.cantidad() / 2))
                : BigDecimal.ZERO;
        BigDecimal ahorroCategoria = valorItem.multiply(
                descuentoPorCategoria.getOrDefault(item.categoria(), BigDecimal.ZERO));
        return ahorro2x1.max(ahorroCategoria);
    }

    private static void validar(Item item) {
        if (item.cantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva: " + item.sku());
        }
        if (item.precioUnitario() == null || item.precioUnitario().signum() < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo: " + item.sku());
        }
    }
}
