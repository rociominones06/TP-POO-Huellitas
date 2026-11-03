package FacturacionNotificaciones;

public interface EstrategiaPago {
    // Importe final = subtotal con descuento/recargo del medio de pago (RN5.2 a RN5.6)
    double calcularTotal(double subtotal, boolean tienePlanSalud);

    MedioPago getMedioPago();

    // Porcentaje de descuento aplicado (0.15 = 15 %). Se usa para mostrar el detalle (CU24).
    double getPorcentajeDescuento(boolean tienePlanSalud);

    // Porcentaje de recargo aplicado (0.08 = 8 %).
    double getPorcentajeRecargo();

    static double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    static void validarSubtotal(double subtotal) {
        if (subtotal < 0) {
            throw new IllegalArgumentException("El subtotal no puede ser negativo.");
        }
    }
}
