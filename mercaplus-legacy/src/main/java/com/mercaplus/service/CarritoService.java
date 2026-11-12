package com.mercaplus.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class CarritoService {

    public record ItemCarrito(String sku, int cantidad) {}

    private final Map<String, List<ItemCarrito>> carritos = new ConcurrentHashMap<>();

    public List<ItemCarrito> agregar(String clienteId, String sku, int cantidad) {
        List<ItemCarrito> items = carritos.computeIfAbsent(clienteId, k -> new CopyOnWriteArrayList<>());
        items.add(new ItemCarrito(sku, cantidad));
        return items;
    }

    public List<ItemCarrito> obtener(String clienteId) {
        return carritos.getOrDefault(clienteId, List.of());
    }

    public void vaciar(String clienteId) {
        carritos.remove(clienteId);
    }
}
