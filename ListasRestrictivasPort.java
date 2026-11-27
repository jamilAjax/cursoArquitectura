package com.crediandes.solicitudes.aplicacion.puerto.salida;

/**
 * Contrato del area de cumplimiento (SARLAFT). NO modificar la firma.
 */
public interface ListasRestrictivasPort {

    /** @return true si el documento aparece en listas restrictivas */
    boolean estaReportado(String tipoDocumento, String numeroDocumento);
}
