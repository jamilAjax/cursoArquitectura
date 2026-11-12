package com.mercaplus.pedidos.aplicacion;

import com.mercaplus.pedidos.aplicacion.puerto.entrada.CrearPedidoUseCase.Comando;
import com.mercaplus.pedidos.aplicacion.puerto.entrada.CrearPedidoUseCase.ItemComando;
import com.mercaplus.pedidos.aplicacion.puerto.salida.CatalogoPort;
import com.mercaplus.pedidos.aplicacion.puerto.salida.PagosPort;
import com.mercaplus.pedidos.aplicacion.puerto.salida.PedidoRepositoryPort;
import com.mercaplus.pedidos.aplicacion.puerto.salida.PromocionesPort;
import com.mercaplus.pedidos.dominio.LineaPedido;
import com.mercaplus.pedidos.dominio.Pedido;
import com.mercaplus.pedidos.dominio.PedidoInvalidoException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * El caso de uso se prueba con adaptadores falsos en memoria: este es el beneficio
 * directo de los puertos. Ni Spring, ni base de datos, ni pasarela real.
 */
class CrearPedidoServiceTest {

    static class CatalogoEnMemoria implements CatalogoPort {
        final Map<String, ProductoCatalogo> productos = new HashMap<>();
        final Map<String, Integer> reservas = new HashMap<>();

        @Override public Optional<ProductoCatalogo> buscarPorSku(String sku) { return Optional.ofNullable(productos.get(sku)); }
        @Override public void reservarStock(String sku, int cantidad) { reservas.merge(sku, cantidad, Integer::sum); }
    }

    static class PedidosEnMemoria implements PedidoRepositoryPort {
        final Map<String, Pedido> datos = new HashMap<>();
        @Override public Pedido guardar(Pedido p) { datos.put(p.id(), p); return p; }
        @Override public Optional<Pedido> buscar(String id) { return Optional.ofNullable(datos.get(id)); }
    }

    static class PagosFalsos implements PagosPort {
        boolean aprobar = true;
        @Override public ResultadoPago cobrar(String pedidoId, String clienteId, BigDecimal monto) {
            return aprobar ? new ResultadoPago(true, "PY-TEST") : new ResultadoPago(false, null);
        }
    }

    /** Promocion falsa: descuenta un 10 % fijo para no acoplar esta prueba al motor real. */
    static final PromocionesPort DIEZ_POR_CIENTO = (List<LineaPedido> lineas, BigDecimal cupon) ->
            lineas.stream().map(LineaPedido::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add)
                    .multiply(new BigDecimal("0.9"));

    private final CatalogoEnMemoria catalogo = new CatalogoEnMemoria();
    private final PedidosEnMemoria pedidos = new PedidosEnMemoria();
    private final PagosFalsos pagos = new PagosFalsos();
    private final CrearPedidoService servicio = new CrearPedidoService(catalogo, DIEZ_POR_CIENTO, pagos, pedidos);

    CrearPedidoServiceTest() {
        catalogo.productos.put("LIC-1L", new CatalogoPort.ProductoCatalogo("LIC-1L", "HOGAR", new BigDecimal("100000"), 5, false));
    }

    private static Comando comando(String sku, int cantidad) {
        return new Comando("c-1", List.of(new ItemComando(sku, cantidad)), null);
    }

    @Test
    void pedidoPagadoAplicaPromocionReservaStockYSeGuarda() {
        var resultado = servicio.ejecutar(comando("LIC-1L", 2));

        assertThat(resultado.estado()).isEqualTo("PAGADO");
        assertThat(resultado.totalAPagar()).isEqualByComparingTo("180000");
        assertThat(resultado.descuento()).isEqualByComparingTo("20000");
        assertThat(catalogo.reservas).containsEntry("LIC-1L", 2);
        assertThat(pedidos.datos).containsKey(resultado.pedidoId());
    }

    @Test
    void pagoRechazadoNoReservaStockPeroQuedaRegistrado() {
        pagos.aprobar = false;

        var resultado = servicio.ejecutar(comando("LIC-1L", 1));

        assertThat(resultado.estado()).isEqualTo("PAGO_RECHAZADO");
        assertThat(catalogo.reservas).isEmpty();
        assertThat(pedidos.datos).hasSize(1);
    }

    @Test
    void productoInexistenteEsRechazado() {
        assertThatThrownBy(() -> servicio.ejecutar(comando("NO-EXISTE", 1)))
                .isInstanceOf(PedidoInvalidoException.class)
                .hasMessageContaining("NO-EXISTE");
        assertThat(pedidos.datos).isEmpty();
    }

    @Test
    void sinStockSuficienteEsRechazado() {
        assertThatThrownBy(() -> servicio.ejecutar(comando("LIC-1L", 6)))
                .isInstanceOf(PedidoInvalidoException.class);
    }
}
