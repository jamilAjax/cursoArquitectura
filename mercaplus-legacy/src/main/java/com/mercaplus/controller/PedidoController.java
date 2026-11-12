package com.mercaplus.controller;

import com.mercaplus.model.LineaPedido;
import com.mercaplus.model.Pedido;
import com.mercaplus.model.Producto;
import com.mercaplus.repository.PedidoRepository;
import com.mercaplus.repository.ProductoRepository;
import com.mercaplus.service.PedidoService;
import com.mercaplus.service.PromocionService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final PromocionService promocionService;
    private final PedidoService pedidoService;

    public PedidoController(PedidoRepository pedidoRepository, ProductoRepository productoRepository,
                            PromocionService promocionService, PedidoService pedidoService) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.promocionService = promocionService;
        this.pedidoService = pedidoService;
    }

    public record ItemSolicitud(String sku, int cantidad) {}
    public record SolicitudPedido(String clienteId, List<ItemSolicitud> items) {}

    @PostMapping
    @Transactional
    public ResponseEntity<?> crear(@RequestBody SolicitudPedido solicitud) {
        Pedido pedido = new Pedido();
        pedido.setClienteId(solicitud.clienteId());
        BigDecimal subtotal = BigDecimal.ZERO;

        for (ItemSolicitud item : solicitud.items()) {
            Producto producto = productoRepository.findBySku(item.sku()).orElse(null);
            if (producto == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Producto no existe: " + item.sku()));
            }
            if (producto.getStock() < item.cantidad()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Sin stock: " + item.sku()));
            }
            producto.setStock(producto.getStock() - item.cantidad());
            productoRepository.save(producto);

            LineaPedido linea = new LineaPedido();
            linea.setProducto(producto);
            linea.setCantidad(item.cantidad());
            linea.setPrecioUnitario(producto.getPrecio());
            pedido.agregarLinea(linea);
            subtotal = subtotal.add(producto.getPrecio().multiply(BigDecimal.valueOf(item.cantidad())));
        }

        BigDecimal descuento = promocionService.calcularDescuento(pedido.getLineas());
        pedido.setDescuento(descuento);
        pedido.setTotal(subtotal.subtract(descuento));
        if (subtotal.compareTo(new BigDecimal("300000")) > 0) {
            pedido.setTotal(pedido.getTotal().multiply(new BigDecimal("0.95")));
        }
        pedido.pagar();
        return ResponseEntity.ok(pedidoRepository.save(pedido));
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Pedido> porCliente(@PathVariable String clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    @PostMapping("/{id}/cancelar")
    public Pedido cancelar(@PathVariable Long id) {
        return pedidoService.cancelar(id);
    }
}
