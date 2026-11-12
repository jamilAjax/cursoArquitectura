package com.mercaplus.pedidos.infraestructura.rest;

import com.mercaplus.pedidos.aplicacion.puerto.entrada.CrearPedidoUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/** Adaptador primario REST. Valida la forma de la peticion; las reglas de negocio viven en el dominio. */
@RestController
@RequestMapping("/api/v2/pedidos")
public class PedidoRestController {

    private final CrearPedidoUseCase crearPedido;

    public PedidoRestController(CrearPedidoUseCase crearPedido) {
        this.crearPedido = crearPedido;
    }

    public record ItemDto(
            @NotBlank @Pattern(regexp = "^[A-Z0-9-]{3,20}$") String sku,
            @Min(1) @Max(100) int cantidad) {}

    public record SolicitudPedidoDto(
            @NotBlank @Size(max = 40) String clienteId,
            @NotEmpty @Size(max = 50) List<@Valid ItemDto> items,
            @PositiveOrZero BigDecimal cupon) {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CrearPedidoUseCase.ResultadoPedido crear(@Valid @RequestBody SolicitudPedidoDto solicitud) {
        var comando = new CrearPedidoUseCase.Comando(
                solicitud.clienteId(),
                solicitud.items().stream().map(i -> new CrearPedidoUseCase.ItemComando(i.sku(), i.cantidad())).toList(),
                solicitud.cupon());
        return crearPedido.ejecutar(comando);
    }
}
