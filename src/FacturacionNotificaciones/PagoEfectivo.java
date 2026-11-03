package FacturacionNotificaciones;

// Efectivo: 10 % de descuento. Si el dueño tiene plan de salud (15 %),
// NO se acumulan: se aplica solo el mayor (RN5.6)

public class PagoEfectivo implements EstrategiaPago {
    private static final double DESCUENTO_EFECTIVO = 0.10;
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
        return MedioPago.EFECTIVO;
    }

    @Override
    public double getPorcentajeDescuento(boolean tienePlanSalud) {
        if (tienePlanSalud) {
            return DESCUENTO_PLAN;
        }
        return DESCUENTO_EFECTIVO;
    }

    @Override
    public double getPorcentajeRecargo() {
        return 0;
    }
}
