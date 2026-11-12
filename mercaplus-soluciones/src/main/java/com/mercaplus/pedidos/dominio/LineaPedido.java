package com.mercaplus.pedidos.dominio;

import java.math.BigDecimal;

/** Valor inmutable: una linea del pedido con el precio congelado al momento de la compra. */
public record LineaPedido(String sku, String categoria, int cantidad, BigDecimal precioUnitario, boolean promo2x1) {

    public LineaPedido {
        if (sku == null || sku.isBlank()) {
            throw new PedidoInvalidoException("La linea necesita un SKU");
        }
        if (cantidad <= 0) {
            throw new PedidoInvalidoException("La cantidad debe ser positiva: " + sku);
        }
        if (precioUnitario == null || precioUnitario.signum() < 0) {
            throw new PedidoInvalidoException("Precio invalido: " + sku);
        }
    }

    public BigDecimal subtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}
