package com.mercaplus.pedidos.aplicacion.puerto.salida;

import java.math.BigDecimal;

/** Puerto de salida hacia cualquier pasarela de pagos: cambiarla = escribir otro adaptador. */
public interface PagosPort {

    record ResultadoPago(boolean aprobado, String referencia) {}

    ResultadoPago cobrar(String pedidoId, String clienteId, BigDecimal monto);
}
