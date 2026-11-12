package com.mercaplus.repository;

import com.mercaplus.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Optional<Producto> findBySku(String sku);

    // SOLUCION LAB 4: consulta derivada = consulta parametrizada (sin concatenar SQL)
    List<Producto> findTop50ByNombreContainingIgnoreCase(String nombre);
}
