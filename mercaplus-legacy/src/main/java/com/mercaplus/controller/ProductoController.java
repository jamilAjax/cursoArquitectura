package com.mercaplus.controller;

import com.mercaplus.model.Producto;
import com.mercaplus.repository.ProductoRepository;
import jakarta.persistence.EntityManager;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoRepository productoRepository;
    private final EntityManager entityManager;

    public ProductoController(ProductoRepository productoRepository, EntityManager entityManager) {
        this.productoRepository = productoRepository;
        this.entityManager = entityManager;
    }

    @GetMapping
    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    @GetMapping("/buscar")
    @SuppressWarnings("unchecked")
    public List<Producto> buscar(@RequestParam String nombre) {
        String sql = "SELECT * FROM producto WHERE nombre LIKE '%" + nombre + "%'";
        return entityManager.createNativeQuery(sql, Producto.class).getResultList();
    }

    @PutMapping("/{id}/precio")
    public Producto cambiarPrecio(@PathVariable Long id, @RequestBody Producto datos) {
        Producto p = productoRepository.findById(id).orElseThrow();
        p.setPrecio(datos.getPrecio());
        return productoRepository.save(p);
    }
}
