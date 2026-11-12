package com.mercaplus.pedidos.infraestructura.config;

import com.mercaplus.pedidos.aplicacion.CrearPedidoService;
import com.mercaplus.pedidos.aplicacion.puerto.entrada.CrearPedidoUseCase;
import com.mercaplus.pedidos.aplicacion.puerto.salida.CatalogoPort;
import com.mercaplus.pedidos.aplicacion.puerto.salida.PagosPort;
import com.mercaplus.pedidos.aplicacion.puerto.salida.PedidoRepositoryPort;
import com.mercaplus.pedidos.aplicacion.puerto.salida.PromocionesPort;
import com.mercaplus.pedidos.infraestructura.pagos.PasarelaPagosAdapter;
import com.mercaplus.pedidos.infraestructura.promociones.MotorPromocionesAdapter;
import com.mercaplus.promociones.MotorPromociones;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.Map;

/** Composicion: el unico lugar donde el nucleo se conecta con Spring. */
@Configuration
public class PedidosConfig {

    @Bean
    PromocionesPort promocionesPort() {
        return new MotorPromocionesAdapter(new MotorPromociones(Map.of("TECNOLOGIA", new BigDecimal("0.20"))));
    }

    @Bean
    PagosPort pagosPort(@Value("${pagaya.api-key}") String apiKey,
                        @Value("${pagaya.limite-aprobacion:5000000}") BigDecimal limite) {
        return new PasarelaPagosAdapter(apiKey, limite);
    }

    @Bean
    CrearPedidoUseCase crearPedidoUseCase(CatalogoPort catalogo, PromocionesPort promociones, PagosPort pagos,
                                          PedidoRepositoryPort pedidos, TransactionTemplate transacciones) {
        CrearPedidoUseCase nucleo = new CrearPedidoService(catalogo, promociones, pagos, pedidos);
        // Decorador transaccional: la frontera de transaccion es responsabilidad de la infraestructura.
        return comando -> transacciones.execute(estado -> nucleo.ejecutar(comando));
    }
}
