package com.mercaplus.service;

import com.mercaplus.model.Pedido;
import com.mercaplus.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PedidoServiceTest {

    private final PedidoRepository repo = mock(PedidoRepository.class);
    private final PedidoService service = new PedidoService(repo);

    @Test
    void calculaIvaParaPedidosGrandes() {
        Pedido p = new Pedido();
        p.setTotal(new BigDecimal("2000000"));
        service.calcularTotalConIva(p);
    }

    @Test
    void calculaIvaParaPedidosPequenos() {
        Pedido p = new Pedido();
        p.setTotal(new BigDecimal("100000"));
        service.calcularTotalConIva(p);
    }

    @Test
    void cancelaPedido() {
        Pedido p = new Pedido();
        when(repo.findById(1L)).thenReturn(Optional.of(p));
        when(repo.save(any())).thenReturn(p);
        service.cancelar(1L);
    }
}
