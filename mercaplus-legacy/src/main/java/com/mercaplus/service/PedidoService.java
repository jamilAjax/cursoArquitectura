package com.mercaplus.service;

import com.mercaplus.model.Pedido;
import com.mercaplus.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public BigDecimal calcularTotalConIva(Pedido pedido) {
        BigDecimal base = pedido.getTotal();
        if (base.compareTo(new BigDecimal("1000000")) > 0) {
            return base.multiply(new BigDecimal("1.19")).setScale(0, RoundingMode.HALF_UP);
        }
        return base.multiply(new BigDecimal("1.19")).setScale(0, RoundingMode.DOWN);
    }

    public Pedido cancelar(Long id) {
        Pedido p = pedidoRepository.findById(id).orElseThrow();
        if (p.getEstado().equals("ENVIADO")) {
            throw new IllegalStateException("No se puede cancelar un pedido enviado");
        }
        p.setEstado("CANCELADO");
        return pedidoRepository.save(p);
    }
}
