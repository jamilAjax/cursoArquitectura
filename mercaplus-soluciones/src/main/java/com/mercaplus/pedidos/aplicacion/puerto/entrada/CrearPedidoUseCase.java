package com.mercaplus.pedidos.aplicacion.puerto.entrada;

import java.math.BigDecimal;
import java.util.List;

/** Puerto de entrada: lo que el mundo exterior puede pedirle al nucleo. */
public interface CrearPedidoUseCase {

    ResultadoPedido ejecutar(Comando comando);

    record ItemComando(String sku, int cantidad) {}

    record Comando(String clienteId, List<ItemComando> items, BigDecimal cupon) {}

    record ResultadoPedido(String pedidoId, String estado, BigDecimal valorBruto,
                           BigDecimal descuento, BigDecimal totalAPagar, String referenciaPago) {}
}
