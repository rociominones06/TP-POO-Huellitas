package FacturacionNotificaciones;

public interface EstrategiaPago {
    /** Importe final = subtotal con descuento/recargo del medio de pago (RN5.2 a RN5.6). */
    double calcularTotal(double subtotal, boolean tienePlanSalud);

    /** Medio de pago que representa esta estrategia. */
    MedioPago getMedioPago();

    /** Porcentaje de descuento aplicado (0.15 = 15 %). Se usa para mostrar el detalle (CU24). */
    double getPorcentajeDescuento(boolean tienePlanSalud);

    /** Porcentaje de recargo aplicado (0.08 = 8 %). */
    double getPorcentajeRecargo();

    /** Redondeo a 2 decimales para importes monetarios. */
    static double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    /** Valida que el subtotal sea utilizable. */
    static void validarSubtotal(double subtotal) {
        if (subtotal < 0) {
            throw new IllegalArgumentException("El subtotal no puede ser negativo.");
        }
    }
}
