package FacturacionNotificaciones;

//Credito: 8 % de recargo.
 
public class PagoCredito implements EstrategiaPago {
    private static final long serialVersionUID = 1L;
    private static final double RECARGO_CREDITO = 0.08;
    private static final double DESCUENTO_PLAN = 0.15;

    @Override
    public double calcularTotal(double subtotal, boolean tienePlanSalud) {
        EstrategiaPago.validarSubtotal(subtotal);
        double porcentajeDescuento = getPorcentajeDescuento(tienePlanSalud);
        double totalConDescuento = subtotal * (1 - porcentajeDescuento);
        double totalConRecargo = totalConDescuento * (1 + getPorcentajeRecargo());
        return EstrategiaPago.redondear(totalConRecargo);
    }

    @Override
    public MedioPago getMedioPago() {
        return MedioPago.CREDITO;
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
        return RECARGO_CREDITO;
    }
}
