package com.mercaplus.pedidos.infraestructura;

import com.mercaplus.pedidos.dominio.EstadoPedido;
import com.mercaplus.pedidos.dominio.LineaPedido;
import com.mercaplus.pedidos.dominio.Pedido;
import com.mercaplus.pedidos.infraestructura.persistencia.PedidoJpaAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Prueba de integracion del adaptador de persistencia contra una base real (H2 en memoria). */
@DataJpaTest
@Import(PedidoJpaAdapter.class)
class PedidoJpaAdapterTest {

    @Autowired PedidoJpaAdapter adaptador;

    @Test
    void guardaYReconstruyeElAgregadoCompleto() {
        Pedido original = Pedido.crear("c-1",
                List.of(new LineaPedido("CAF-500", "MERCADO", 2, new BigDecimal("32900"), true)),
                new BigDecimal("32900"));
        original.registrarPago("PY-9");

        adaptador.guardar(original);
        Pedido leido = adaptador.buscar(original.id()).orElseThrow();

        assertThat(leido.estado()).isEqualTo(EstadoPedido.PAGADO);
        assertThat(leido.lineas()).hasSize(1);
        assertThat(leido.totalAPagar()).isEqualByComparingTo("32900");
        assertThat(leido.referenciaPago()).isEqualTo("PY-9");
    }
}
