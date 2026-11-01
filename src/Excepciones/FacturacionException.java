package Excepciones;

/**
 * Excepción de las reglas de negocio de facturación y cobros
 * (factura inmutable, factura sin líneas, anulación duplicada, etc.).
 * Extiende ReglaNegocioException, por lo que la vista puede capturar
 * cualquiera de las dos.
 */

public class FacturacionException extends ReglaNegocioException {
    public FacturacionException(String mensaje) {
        super(mensaje);
    }
}