package com.mercaplus.pedidos.aplicacion;

import com.mercaplus.pedidos.aplicacion.puerto.entrada.CrearPedidoUseCase;
import com.mercaplus.pedidos.aplicacion.puerto.salida.CatalogoPort;
import com.mercaplus.pedidos.aplicacion.puerto.salida.CatalogoPort.ProductoCatalogo;
import com.mercaplus.pedidos.aplicacion.puerto.salida.PagosPort;
import com.mercaplus.pedidos.aplicacion.puerto.salida.PedidoRepositoryPort;
import com.mercaplus.pedidos.aplicacion.puerto.salida.PromocionesPort;
import com.mercaplus.pedidos.dominio.LineaPedido;
import com.mercaplus.pedidos.dominio.Pedido;
import com.mercaplus.pedidos.dominio.PedidoInvalidoException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Caso de uso "Crear pedido". Sin anotaciones de Spring: se instancia en
 * infraestructura.config, lo que mantiene el nucleo independiente del framework.
 */
public class CrearPedidoService implements CrearPedidoUseCase {

    private final CatalogoPort catalogo;
    private final PromocionesPort promociones;
    private final PagosPort pagos;
    private final PedidoRepositoryPort pedidos;

    public CrearPedidoService(CatalogoPort catalogo, PromocionesPort promociones,
                              PagosPort pagos, PedidoRepositoryPort pedidos) {
        this.catalogo = catalogo;
        this.promociones = promociones;
        this.pagos = pagos;
        this.pedidos = pedidos;
    }

    @Override
    public ResultadoPedido ejecutar(Comando comando) {
        if (comando.items() == null || comando.items().isEmpty()) {
            throw new PedidoInvalidoException("El pedido debe tener al menos un item");
        }

        List<LineaPedido> lineas = new ArrayList<>();
        for (ItemComando item : comando.items()) {
            ProductoCatalogo producto = catalogo.buscarPorSku(item.sku())
                    .orElseThrow(() -> new PedidoInvalidoException("Producto no existe: " + item.sku()));
            if (producto.stockDisponible() < item.cantidad()) {
                throw new PedidoInvalidoException("Sin stock suficiente: " + item.sku());
            }
            lineas.add(new LineaPedido(producto.sku(), producto.categoria(), item.cantidad(),
                    producto.precio(), producto.promo2x1()));
        }

        BigDecimal cupon = comando.cupon() == null ? BigDecimal.ZERO : comando.cupon();
        BigDecimal total = promociones.totalConPromociones(lineas, cupon);
        Pedido pedido = Pedido.crear(comando.clienteId(), lineas, total);

        PagosPort.ResultadoPago pago = pagos.cobrar(pedido.id(), pedido.clienteId(), pedido.totalAPagar());
        if (pago.aprobado()) {
            pedido.registrarPago(pago.referencia());
            lineas.forEach(l -> catalogo.reservarStock(l.sku(), l.cantidad()));
        } else {
            pedido.rechazarPago();
        }

        Pedido guardado = pedidos.guardar(pedido);
        return new ResultadoPedido(guardado.id(), guardado.estado().name(), guardado.valorBruto(),
                guardado.descuento(), guardado.totalAPagar(), guardado.referenciaPago());
    }
}
