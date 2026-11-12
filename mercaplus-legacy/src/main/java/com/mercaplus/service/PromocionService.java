package com.mercaplus.service;

import com.mercaplus.model.LineaPedido;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class PromocionService {

    public BigDecimal calcularDescuento(List<LineaPedido> lineas) {
        BigDecimal descuento = BigDecimal.ZERO;
        for (LineaPedido l : lineas) {
            if (l.getProducto().isPromo2x1()) {
                int gratis = l.getCantidad() / 2;
                descuento = descuento.add(l.getPrecioUnitario().multiply(BigDecimal.valueOf(gratis)));
            }
        }
        // Black Friday: promociones especiales
        descuento = descuento.add(descuentoBlackFriday(lineas));
        return descuento;
    }

    private BigDecimal descuentoBlackFriday(List<LineaPedido> lineas) {
        BigDecimal extra = BigDecimal.ZERO;
        for (LineaPedido l : lineas) {
            if (l.getProducto().isPromo2x1() && l.getCantidad() >= 2) {
                extra = extra.add(l.getPrecioUnitario().multiply(BigDecimal.valueOf(l.getCantidad() / 2)));
            }
        }
        return extra;
    }
}
