package com.mercaplus.service;

import com.mercaplus.model.Pedido;
import com.mercaplus.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * SOLUCION LAB 3 (falacia de la cobertura): las mismas pruebas del legado, ahora con aserciones.
 * La cobertura de lineas no cambia; el puntaje de mutacion sube y aparece el bug del redondeo.
 */
class PedidoServiceTest {

    private final PedidoRepository repo = mock(PedidoRepository.class);
    private final PedidoService service = new PedidoService(repo);

    @Test
    void calculaIvaParaPedidosGrandes() {
        Pedido p = new Pedido();
        p.setTotal(new BigDecimal("2000000"));
        assertThat(service.calcularTotalConIva(p)).isEqualByComparingTo("2380000");
    }

    @Test
    void calculaIvaParaPedidosPequenosRedondeandoAlPeso() {
        Pedido p = new Pedido();
        p.setTotal(new BigDecimal("32900"));
        // 32.900 x 1,19 = 39.151 (exacto)
        assertThat(service.calcularTotalConIva(p)).isEqualByComparingTo("39151");
    }

    @Test
    void cancelaPedidoNuevo() {
        Pedido p = new Pedido();
        when(repo.findById(1L)).thenReturn(Optional.of(p));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThat(service.cancelar(1L).getEstado()).isEqualTo("CANCELADO");
    }

    @Test
    void noCancelaPedidoEnviado() {
        Pedido p = new Pedido();
        p.setEstado("ENVIADO");
        when(repo.findById(1L)).thenReturn(Optional.of(p));

        assertThatThrownBy(() -> service.cancelar(1L)).isInstanceOf(IllegalStateException.class);
        verify(repo, never()).save(any());
    }
}
