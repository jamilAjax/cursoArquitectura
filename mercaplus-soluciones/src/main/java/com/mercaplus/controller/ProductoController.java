package com.mercaplus.controller;

import com.mercaplus.model.Producto;
import com.mercaplus.repository.ProductoRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * SOLUCION LAB 4:
 *  - busqueda con consulta parametrizada (adios inyeccion SQL);
 *  - validacion de entrada en el adaptador (lista blanca de caracteres y longitud);
 *  - se elimina el endpoint que permitia cambiar precios sin autenticacion (mover a un modulo de backoffice protegido).
 * Pendiente (deuda registrada): la API sigue exponiendo la entidad JPA; se resuelve en el modulo hexagonal.
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoRepository productoRepository;

    public ProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @GetMapping
    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    @GetMapping("/buscar")
    public List<Producto> buscar(@RequestParam @NotBlank @Size(max = 50)
                                 @Pattern(regexp = "^[\\p{L}0-9 .,-]+$") String nombre) {
        return productoRepository.findTop50ByNombreContainingIgnoreCase(nombre);
    }
}
