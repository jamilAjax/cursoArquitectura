package com.mercaplus.pedidos.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

interface PedidoSpringDataRepository extends JpaRepository<PedidoJpaEntity, String> {
}
