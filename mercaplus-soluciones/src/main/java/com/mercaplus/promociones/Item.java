package com.mercaplus.promociones;

import java.math.BigDecimal;

/**
 * LAB 3 - Kata TDD. Un item del carrito tal como lo ve el motor de promociones.
 */
public record Item(String sku, String categoria, BigDecimal precioUnitario, int cantidad, boolean promo2x1) {
}
