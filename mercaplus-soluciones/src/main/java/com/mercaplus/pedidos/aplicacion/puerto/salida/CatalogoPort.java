package com.mercaplus.pedidos.aplicacion.puerto.salida;

import java.math.BigDecimal;
import java.util.Optional;

/** Puerto de salida hacia el catalogo e inventario (hoy: tablas del monolito). */
public interface CatalogoPort {

    record ProductoCatalogo(String sku, String categoria, BigDecimal precio, int stockDisponible, boolean promo2x1) {}

    Optional<ProductoCatalogo> buscarPorSku(String sku);

    void reservarStock(String sku, int cantidad);
}
