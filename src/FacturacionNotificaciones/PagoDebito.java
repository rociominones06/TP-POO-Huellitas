package FacturacionNotificaciones;

// Debito: sin recargo. Aplica el descuento del plan de salud si corresponde.

public class PagoDebito implements EstrategiaPago {
    private static final long serialVersionUID = 1L;
    private static final double DESCUENTO_PLAN = 0.15;

    @Override
    public double calcularTotal(double subtotal, boolean tienePlanSalud) {
        EstrategiaPago.validarSubtotal(subtotal);
        double porcentajeDescuento = getPorcentajeDescuento(tienePlanSalud);
        double total = subtotal * (1 - porcentajeDescuento);
        return EstrategiaPago.redondear(total);
    }

    @Override
    public MedioPago getMedioPago() {
        return MedioPago.DEBITO;
    }

    @Override
    public double getPorcentajeDescuento(boolean tienePlanSalud) {
        if (tienePlanSalud) {
            return DESCUENTO_PLAN;
        }
        return 0;
    }

    @Override
    public double getPorcentajeRecargo() {
        return 0;
    }
}
