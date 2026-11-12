package com.mercaplus.pedidos.dominio;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Invariantes del valor LineaPedido: sin Spring, sin base de datos, milisegundos. */
class LineaPedidoTest {

    private static final BigDecimal PRECIO = new BigDecimal("32900");

    @Test
    void elSubtotalEsPrecioPorCantidad() {
        LineaPedido linea = new LineaPedido("CAF-500", "MERCADO", 3, PRECIO, true);

        assertThat(linea.subtotal()).isEqualByComparingTo("98700");
    }

    @Test
    void unProductoGratisTieneSubtotalCero() {
        LineaPedido linea = new LineaPedido("CAF-500", "MERCADO", 2, BigDecimal.ZERO, false);

        assertThat(linea.subtotal()).isEqualByComparingTo("0");
    }

    @Test
    void noSePuedeCrearSinSku() {
        assertThatThrownBy(() -> new LineaPedido(null, "MERCADO", 1, PRECIO, false))
                .isInstanceOf(PedidoInvalidoException.class)
                .hasMessageContaining("SKU");
        assertThatThrownBy(() -> new LineaPedido("  ", "MERCADO", 1, PRECIO, false))
                .isInstanceOf(PedidoInvalidoException.class);
    }

    @Test
    void laCantidadDebeSerPositiva() {
        assertThatThrownBy(() -> new LineaPedido("CAF-500", "MERCADO", 0, PRECIO, false))
                .isInstanceOf(PedidoInvalidoException.class)
                .hasMessageContaining("cantidad");
        assertThatThrownBy(() -> new LineaPedido("CAF-500", "MERCADO", -1, PRECIO, false))
                .isInstanceOf(PedidoInvalidoException.class);
    }

    @Test
    void elPrecioNoPuedeSerNuloNiNegativo() {
        assertThatThrownBy(() -> new LineaPedido("CAF-500", "MERCADO", 1, null, false))
                .isInstanceOf(PedidoInvalidoException.class)
                .hasMessageContaining("Precio");
        assertThatThrownBy(() -> new LineaPedido("CAF-500", "MERCADO", 1, new BigDecimal("-1"), false))
                .isInstanceOf(PedidoInvalidoException.class);
    }
}
