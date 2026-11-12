package com.mercaplus.pedidos.aplicacion.puerto.salida;

import com.mercaplus.pedidos.dominio.Pedido;
import java.util.Optional;

public interface PedidoRepositoryPort {
    Pedido guardar(Pedido pedido);
    Optional<Pedido> buscar(String id);
}
