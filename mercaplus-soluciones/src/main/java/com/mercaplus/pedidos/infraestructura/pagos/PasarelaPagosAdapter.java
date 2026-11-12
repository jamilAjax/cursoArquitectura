package com.mercaplus.pedidos.infraestructura.pagos;

import com.mercaplus.pedidos.aplicacion.puerto.salida.PagosPort;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Adaptador de la pasarela "PagaYa". La llave llega por configuracion (variable de entorno
 * o AWS Secrets Manager), nunca desde el codigo. Simulado: aprueba montos hasta el limite.
 */
public class PasarelaPagosAdapter implements PagosPort {

    private final String apiKey;
    private final BigDecimal limiteAprobacion;

    public PasarelaPagosAdapter(String apiKey, BigDecimal limiteAprobacion) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Falta la llave de la pasarela (PAGAYA_API_KEY)");
        }
        this.apiKey = apiKey;
        this.limiteAprobacion = limiteAprobacion;
    }

    @Override
    public ResultadoPago cobrar(String pedidoId, String clienteId, BigDecimal monto) {
        // En un adaptador real: llamada HTTP con timeout, reintentos idempotentes (pedidoId) y circuit breaker.
        if (monto.compareTo(limiteAprobacion) > 0) {
            return new ResultadoPago(false, null);
        }
        return new ResultadoPago(true, "PY-" + UUID.nameUUIDFromBytes(pedidoId.getBytes()));
    }
}
