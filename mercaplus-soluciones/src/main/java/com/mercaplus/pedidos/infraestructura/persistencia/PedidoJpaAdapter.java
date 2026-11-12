package com.mercaplus.pedidos.infraestructura.persistencia;

import com.mercaplus.pedidos.aplicacion.puerto.salida.PedidoRepositoryPort;
import com.mercaplus.pedidos.dominio.EstadoPedido;
import com.mercaplus.pedidos.dominio.LineaPedido;
import com.mercaplus.pedidos.dominio.Pedido;
import org.springframework.stereotype.Component;

import java.util.Optional;

/** Adaptador de persistencia: traduce entre el agregado de dominio y la entidad JPA. */
@Component
public class PedidoJpaAdapter implements PedidoRepositoryPort {

    private final PedidoSpringDataRepository repositorio;

    PedidoJpaAdapter(PedidoSpringDataRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public Pedido guardar(Pedido pedido) {
        repositorio.save(aEntidad(pedido));
        return pedido;
    }

    @Override
    public Optional<Pedido> buscar(String id) {
        return repositorio.findById(id).map(PedidoJpaAdapter::aDominio);
    }

    private static PedidoJpaEntity aEntidad(Pedido p) {
        PedidoJpaEntity e = new PedidoJpaEntity();
        e.setId(p.id());
        e.setClienteId(p.clienteId());
        e.setCreadoEn(p.creadoEn());
        e.setTotalAPagar(p.totalAPagar());
        e.setEstado(p.estado().name());
        e.setReferenciaPago(p.referenciaPago());
        for (LineaPedido l : p.lineas()) {
            PedidoJpaEntity.LineaEmbeddable le = new PedidoJpaEntity.LineaEmbeddable();
            le.sku = l.sku();
            le.categoria = l.categoria();
            le.cantidad = l.cantidad();
            le.precioUnitario = l.precioUnitario();
            le.promo2x1 = l.promo2x1();
            e.getLineas().add(le);
        }
        return e;
    }

    private static Pedido aDominio(PedidoJpaEntity e) {
        return Pedido.reconstruir(e.getId(), e.getClienteId(), e.getCreadoEn(),
                e.getLineas().stream()
                        .map(l -> new LineaPedido(l.sku, l.categoria, l.cantidad, l.precioUnitario, l.promo2x1))
                        .toList(),
                e.getTotalAPagar(), EstadoPedido.valueOf(e.getEstado()), e.getReferenciaPago());
    }
}
