package Excepciones;

// Excepción de las reglas de negocio de facturación y cobros
// (factura inmutable, factura sin líneas, anulación duplicada, etc.).

public class FacturacionException extends ReglaNegocioException {
    public FacturacionException(String mensaje) {
        super(mensaje);
    }
}