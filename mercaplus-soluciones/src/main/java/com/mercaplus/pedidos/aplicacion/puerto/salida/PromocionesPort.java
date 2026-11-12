package com.mercaplus.pedidos.aplicacion.puerto.salida;

import com.mercaplus.pedidos.dominio.LineaPedido;
import java.math.BigDecimal;
import java.util.List;

public interface PromocionesPort {
    /** @return total a pagar despues de promociones y cupon */
    BigDecimal totalConPromociones(List<LineaPedido> lineas, BigDecimal cupon);
}
