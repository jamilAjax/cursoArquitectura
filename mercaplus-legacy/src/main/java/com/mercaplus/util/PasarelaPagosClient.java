package com.mercaplus.util;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Cliente de la pasarela de pagos "PagaYa" (simulado).
 */
public class PasarelaPagosClient {

    public static final String API_KEY = "sk_live_FAKE_mercaplus_9f8a7b6c5d4e3f2a1b0c";
    public static final BigDecimal LIMITE_APROBACION = new BigDecimal("5000000");

    public static String cobrar(String clienteId, BigDecimal monto) {
        System.out.println("Cobrando " + monto + " al cliente " + clienteId + " con llave " + API_KEY);
        if (monto.compareTo(LIMITE_APROBACION) > 0) {
            return null;
        }
        return "PY-" + UUID.randomUUID();
    }
}
