package FacturacionNotificaciones;

import java.util.List;

import Excepciones.FacturacionException;

public class CalculadorFacturacion {
    public double calcularSubtotal(List<LineaFactura> lineas) throws FacturacionException {
        if (lineas == null || lineas.isEmpty()) {
            throw new FacturacionException("No hay conceptos para facturar.");
        }
        double suma = 0;
        for (LineaFactura linea : lineas) {
            suma += linea.calcularSubtotal();
        }
        return EstrategiaPago.redondear(suma);
    }

    public double calcularImporteFinal(List<LineaFactura> lineas,
                                       EstrategiaPago estrategia,
                                       boolean tienePlanSalud) throws FacturacionException {
        if (estrategia == null) {
            throw new FacturacionException("Debe seleccionarse un medio de pago.");
        }
        double subtotal = calcularSubtotal(lineas);
        return estrategia.calcularTotal(subtotal, tienePlanSalud);
    }
}
