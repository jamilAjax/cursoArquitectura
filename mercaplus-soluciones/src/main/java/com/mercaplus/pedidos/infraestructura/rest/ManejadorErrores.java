package com.mercaplus.pedidos.infraestructura.rest;

import com.mercaplus.pedidos.dominio.PedidoInvalidoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = PedidoRestController.class)
public class ManejadorErrores {

    @ExceptionHandler(PedidoInvalidoException.class)
    ProblemDetail pedidoInvalido(PedidoInvalidoException e) {
        ProblemDetail detalle = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        detalle.setTitle("Pedido invalido");
        return detalle;
    }
}
