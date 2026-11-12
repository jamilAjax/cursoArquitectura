package com.mercaplus.pedidos.dominio;

public class PedidoInvalidoException extends RuntimeException {
    public PedidoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
