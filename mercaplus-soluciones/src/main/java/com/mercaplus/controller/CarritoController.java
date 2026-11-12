package com.mercaplus.controller;

import com.mercaplus.service.CarritoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.InetAddress;
import java.util.List;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    public record SolicitudItem(String sku, int cantidad) {}

    @PostMapping("/{clienteId}/items")
    public ResponseEntity<List<CarritoService.ItemCarrito>> agregar(@PathVariable String clienteId,
                                                                    @RequestBody SolicitudItem item) {
        return ResponseEntity.ok()
                .header("X-Instancia", instancia())
                .body(carritoService.agregar(clienteId, item.sku(), item.cantidad()));
    }

    @GetMapping("/{clienteId}")
    public ResponseEntity<List<CarritoService.ItemCarrito>> obtener(@PathVariable String clienteId) {
        return ResponseEntity.ok()
                .header("X-Instancia", instancia())
                .body(carritoService.obtener(clienteId));
    }

    private static String instancia() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "desconocida";
        }
    }
}
