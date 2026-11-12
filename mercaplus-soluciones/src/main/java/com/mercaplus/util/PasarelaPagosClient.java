package com.mercaplus.util;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Cliente legado de la pasarela "PagaYa" (simulado).
 * SOLUCION LAB 4: la llave se lee del entorno y nunca se imprime en logs.
 * Nota: el modulo nuevo usa PasarelaPagosAdapter; este cliente queda solo para el flujo legado.
 */
public class PasarelaPagosClient {

    public static final BigDecimal LIMITE_APROBACION = new BigDecimal("5000000");

    public static String cobrar(String clienteId, BigDecimal monto) {
        String apiKey = System.getenv().getOrDefault("PAGAYA_API_KEY", "solo-desarrollo-local");
        if (apiKey.isBlank() || monto.compareTo(LIMITE_APROBACION) > 0) {
            return null;
        }
        return "PY-" + UUID.randomUUID();
    }
}
