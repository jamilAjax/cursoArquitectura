package com.mercaplus.pedidos.infraestructura.persistencia;

import com.mercaplus.model.Producto;
import com.mercaplus.pedidos.aplicacion.puerto.salida.CatalogoPort;
import com.mercaplus.repository.ProductoRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Capa anticorrupcion: el nuevo modulo lee el catalogo del monolito sin que el
 * modelo legado (entidad Producto) se filtre hacia el dominio.
 */
@Component
public class CatalogoLegadoAdapter implements CatalogoPort {

    private final ProductoRepository productos;

    public CatalogoLegadoAdapter(ProductoRepository productos) {
        this.productos = productos;
    }

    @Override
    public Optional<ProductoCatalogo> buscarPorSku(String sku) {
        return productos.findBySku(sku)
                .map(p -> new ProductoCatalogo(p.getSku(), p.getCategoria(), p.getPrecio(), p.getStock(), p.isPromo2x1()));
    }

    @Override
    public void reservarStock(String sku, int cantidad) {
        Producto p = productos.findBySku(sku).orElseThrow();
        p.setStock(p.getStock() - cantidad);
        productos.save(p);
    }
}
