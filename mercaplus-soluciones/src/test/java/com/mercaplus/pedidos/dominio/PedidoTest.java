package com.mercaplus.pedidos.dominio;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Pruebas unitarias del agregado: sin Spring, sin base de datos, milisegundos. */
class PedidoTest {

    private static final LineaPedido CAFE = new LineaPedido("CAF-500", "MERCADO", 2, new BigDecimal("32900"), true);

    @Test
    void unPedidoNuevoQuedaCreadoYCalculaSuDescuento() {
        Pedido p = Pedido.crear("c-1", List.of(CAFE), new BigDecimal("32900"));

        assertThat(p.estado()).isEqualTo(EstadoPedido.CREADO);
        assertThat(p.valorBruto()).isEqualByComparingTo("65800");
        assertThat(p.descuento()).isEqualByComparingTo("32900");
    }

    @Test
    void noSePuedeCrearSinLineas() {
        assertThatThrownBy(() -> Pedido.crear("c-1", List.of(), BigDecimal.ZERO))
                .isInstanceOf(PedidoInvalidoException.class);
    }

    @Test
    void noSePuedeCrearConMasDe50Lineas() {
        List<LineaPedido> lineas = Collections.nCopies(51, CAFE);
        assertThatThrownBy(() -> Pedido.crear("c-1", lineas, BigDecimal.ZERO))
                .isInstanceOf(PedidoInvalidoException.class);
    }

    @Test
    void elTotalNoPuedeSuperarElValorBruto() {
        assertThatThrownBy(() -> Pedido.crear("c-1", List.of(CAFE), new BigDecimal("70000")))
                .isInstanceOf(PedidoInvalidoException.class);
    }

    @Test
    void registrarPagoPasaAPagadoConReferencia() {
        Pedido p = Pedido.crear("c-1", List.of(CAFE), new BigDecimal("32900"));
        p.registrarPago("PY-1");

        assertThat(p.estado()).isEqualTo(EstadoPedido.PAGADO);
        assertThat(p.referenciaPago()).isEqualTo("PY-1");
    }

    @Test
    void noSePuedePagarDosVeces() {
        Pedido p = Pedido.crear("c-1", List.of(CAFE), new BigDecimal("32900"));
        p.registrarPago("PY-1");

        assertThatThrownBy(() -> p.registrarPago("PY-2")).isInstanceOf(PedidoInvalidoException.class);
    }

    @Test
    void unaLineaConCantidadCeroEsInvalida() {
        assertThatThrownBy(() -> new LineaPedido("CAF-500", "MERCADO", 0, BigDecimal.ONE, false))
                .isInstanceOf(PedidoInvalidoException.class);
    }
}
