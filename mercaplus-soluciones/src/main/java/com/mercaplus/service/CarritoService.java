package com.mercaplus.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.List;

/**
 * SOLUCION LAB 7: el carrito vive en Redis, no en la memoria de una replica.
 * Cualquier instancia detras del balanceador ve el mismo carrito -> el servicio queda sin estado.
 */
@Service
public class CarritoService {

    private static final Duration EXPIRACION = Duration.ofDays(2);

    public record ItemCarrito(String sku, int cantidad) {}

    private final StringRedisTemplate redis;

    public CarritoService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public List<ItemCarrito> agregar(String clienteId, String sku, int cantidad) {
        String clave = clave(clienteId);
        redis.opsForList().rightPush(clave, sku + ":" + cantidad);
        redis.expire(clave, EXPIRACION);
        return obtener(clienteId);
    }

    public List<ItemCarrito> obtener(String clienteId) {
        List<String> crudos = redis.opsForList().range(clave(clienteId), 0, -1);
        if (crudos == null) {
            return List.of();
        }
        return crudos.stream().map(CarritoService::parsear).toList();
    }

    public void vaciar(String clienteId) {
        redis.delete(clave(clienteId));
    }

    private static String clave(String clienteId) {
        return "carrito:" + clienteId;
    }

    private static ItemCarrito parsear(String valor) {
        int i = valor.lastIndexOf(':');
        return new ItemCarrito(valor.substring(0, i), Integer.parseInt(valor.substring(i + 1)));
    }
}
