package com.mercaplus.pedidos.dominio;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Agregado Pedido. No conoce Spring, JPA ni la pasarela de pagos:
 * las transiciones de estado son reglas de negocio y se prueban sin infraestructura.
 */
public class Pedido {

    public static final int MAX_LINEAS = 50;

    private final String id;
    private final String clienteId;
    private final Instant creadoEn;
    private final List<LineaPedido> lineas;
    private final BigDecimal totalAPagar;
    private EstadoPedido estado;
    private String referenciaPago;

    private Pedido(String id, String clienteId, Instant creadoEn, List<LineaPedido> lineas,
                   BigDecimal totalAPagar, EstadoPedido estado, String referenciaPago) {
        this.id = id;
        this.clienteId = clienteId;
        this.creadoEn = creadoEn;
        this.lineas = List.copyOf(lineas);
        this.totalAPagar = totalAPagar;
        this.estado = estado;
        this.referenciaPago = referenciaPago;
    }

    public static Pedido crear(String clienteId, List<LineaPedido> lineas, BigDecimal totalAPagar) {
        if (clienteId == null || clienteId.isBlank()) {
            throw new PedidoInvalidoException("El pedido necesita un cliente");
        }
        if (lineas == null || lineas.isEmpty()) {
            throw new PedidoInvalidoException("El pedido debe tener al menos una linea");
        }
        if (lineas.size() > MAX_LINEAS) {
            throw new PedidoInvalidoException("El pedido supera " + MAX_LINEAS + " lineas");
        }
        BigDecimal bruto = lineas.stream().map(LineaPedido::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalAPagar == null || totalAPagar.signum() < 0 || totalAPagar.compareTo(bruto) > 0) {
            throw new PedidoInvalidoException("El total a pagar debe estar entre 0 y el valor bruto");
        }
        return new Pedido(UUID.randomUUID().toString(), clienteId, Instant.now(), lineas,
                totalAPagar, EstadoPedido.CREADO, null);
    }

    /** Reconstruye un pedido ya existente (usado por los adaptadores de persistencia). */
    public static Pedido reconstruir(String id, String clienteId, Instant creadoEn, List<LineaPedido> lineas,
                                     BigDecimal totalAPagar, EstadoPedido estado, String referenciaPago) {
        return new Pedido(id, clienteId, creadoEn, lineas, totalAPagar, estado, referenciaPago);
    }

    public void registrarPago(String referencia) {
        exigirEstado(EstadoPedido.CREADO);
        if (referencia == null || referencia.isBlank()) {
            throw new PedidoInvalidoException("Un pago aprobado requiere referencia");
        }
        this.referenciaPago = referencia;
        this.estado = EstadoPedido.PAGADO;
    }

    public void rechazarPago() {
        exigirEstado(EstadoPedido.CREADO);
        this.estado = EstadoPedido.PAGO_RECHAZADO;
    }

    public void cancelar() {
        if (estado == EstadoPedido.CANCELADO) {
            throw new PedidoInvalidoException("El pedido ya estaba cancelado");
        }
        this.estado = EstadoPedido.CANCELADO;
    }

    private void exigirEstado(EstadoPedido esperado) {
        if (estado != esperado) {
            throw new PedidoInvalidoException("Transicion invalida desde " + estado);
        }
    }

    public BigDecimal valorBruto() {
        return lineas.stream().map(LineaPedido::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal descuento() {
        return valorBruto().subtract(totalAPagar);
    }

    public String id() { return id; }
    public String clienteId() { return clienteId; }
    public Instant creadoEn() { return creadoEn; }
    public List<LineaPedido> lineas() { return lineas; }
    public BigDecimal totalAPagar() { return totalAPagar; }
    public EstadoPedido estado() { return estado; }
    public String referenciaPago() { return referenciaPago; }
}
