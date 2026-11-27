package com.crediandes.solicitudes.aplicacion.puerto.salida;

/**
 * Contrato entregado por el area de riesgo de CrediAndes. NO modificar la firma.
 * El proveedor real (central de riesgo) responde en 300-2.000 ms y a veces no responde:
 * el adaptador debe manejar timeout y la caida del servicio.
 */
public interface BuroCreditoPort {

    /** @return puntaje entre 150 y 950 */
    int consultarPuntaje(String tipoDocumento, String numeroDocumento);
}
