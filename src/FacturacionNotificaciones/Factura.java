package FacturacionNotificaciones;

import AtencionHistoriaClinica.Consulta;
import Excepciones.FacturacionException;
import PersonasMascota.Dueño;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


// Comprobante de una consulta. Una vez emitida no puede modificarse (RN5.7);
// si tiene un error se anula con una NotaDeCredito (RN5.8).
// El cálculo del importe lo hace CalculadorFacturacion (SRP).

public class Factura implements Comparable<Factura> {
    private final int numeroFactura;
    private final Dueño dueño;
    private final Consulta consulta;
    private final EstrategiaPago estrategiaPago;
    private final List<LineaFactura> lineas = new ArrayList<>();

    private NotaDeCredito notaDeCredito;
    private LocalDateTime fecha;
    private double subtotal;
    private double montoTotal;
    private boolean aplicoPlanSalud; // foto del plan al momento de emitir
    private boolean emitida;
    private boolean estaAnulada;

    public Factura(int numeroFactura, Dueño dueño, Consulta consulta, EstrategiaPago estrategiaPago) {
        if (dueño == null || consulta == null || estrategiaPago == null) {
            throw new IllegalArgumentException("Dueño, consulta y medio de pago son obligatorios.");
        }
        this.numeroFactura = numeroFactura;
        this.dueño = dueño;
        this.consulta = consulta;
        this.estrategiaPago = estrategiaPago;
    }

    public void agregarLinea(LineaFactura linea) throws FacturacionException {
        if (emitida) {
            throw new FacturacionException("La factura ya fue emitida y no puede modificarse.");
        }
        if (linea == null) {
            throw new IllegalArgumentException("La línea no puede ser nula.");
        }
        lineas.add(linea);
    }

    // Calcula el importe final, fija la fecha y deja la factura emitida (CU14).
    public void emitirFactura(CalculadorFacturacion calculador) throws FacturacionException {
        if (emitida) {
            throw new FacturacionException("La factura ya fue emitida.");
        }
        this.aplicoPlanSalud = dueño.tieneDescuentoPlan();
        this.subtotal = calculador.calcularSubtotal(lineas);
        this.montoTotal = calculador.calcularImporteFinal(lineas, estrategiaPago, aplicoPlanSalud);
        this.fecha = LocalDateTime.now();
        this.emitida = true;
    }

    // Anula la factura con una nota de crédito sin tocar sus datos originales (CU25).
    public void anular(NotaDeCredito nota) throws FacturacionException {
        if (!emitida) {
            throw new FacturacionException("No se puede anular una factura que no fue emitida.");
        }
        if (estaAnulada) {
            throw new FacturacionException("La factura ya fue anulada previamente.");
        }
        if (nota == null) {
            throw new FacturacionException("Se requiere una nota de crédito para anular.");
        }
        if (nota.estaEmitida()) {
            throw new FacturacionException("La nota de crédito ya está asociada a otra anulación.");
        }
        if (Math.abs(nota.getImporteTotal() - montoTotal) > 0.001) {
            throw new FacturacionException("El importe de la nota debe coincidir con el total de la factura.");
        }
        nota.emitirNota();
        this.notaDeCredito = nota;
        this.estaAnulada = true;
    }

    public double getPorcentajeDescuento() {
        return estrategiaPago.getPorcentajeDescuento(aplicoPlanSalud);
    }

    public double getPorcentajeRecargo() { return estrategiaPago.getPorcentajeRecargo(); }

    // Texto de detalle para consultas (CU24)
    public String obtenerDetalle() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Factura N° %d - %s%n", numeroFactura, estaAnulada ? "ANULADA" : "EMITIDA"));
        sb.append("Dueño: ").append(dueño.obtenerNombreCompleto()).append(System.lineSeparator());
        for (LineaFactura l : lineas) {
            sb.append(String.format("  %-25s x%d  $%.2f  = $%.2f%n",
                    l.getDescripcion(), l.getCantidad(), l.getPrecioUnitario(), l.calcularSubtotal()));
        }
        sb.append(String.format("Subtotal: $%.2f%n", subtotal));
        sb.append(String.format("Medio de pago: %s | Descuento: %.0f%% | Recargo: %.0f%%%n",
                estrategiaPago.getMedioPago().getDescripcion(),
                getPorcentajeDescuento() * 100, getPorcentajeRecargo() * 100));
        sb.append(String.format("TOTAL: $%.2f%n", montoTotal));
        if (estaAnulada) {
            sb.append(String.format("Anulada por nota de crédito N° %d (%s)%n",
                    notaDeCredito.getNumeroNota(), notaDeCredito.getMotivo()));
        }
        return sb.toString();
    }

    // Orden natural: por número de factura
    @Override
    public int compareTo(Factura otra) {
        return Integer.compare(numeroFactura, otra.numeroFactura);
    }

    public int getNumeroFactura() { return numeroFactura; }
    public Dueño getDueño() { return dueño; }
    public Consulta getConsulta() { return consulta; }
    public List<LineaFactura> getLineas() { return Collections.unmodifiableList(lineas); }
    public NotaDeCredito getNotaDeCredito() { return notaDeCredito; }
    public LocalDateTime getFecha() { return fecha; }
    public MedioPago getMedioPago() { return estrategiaPago.getMedioPago(); }
    public double getSubtotal() { return subtotal; }
    public double getMontoTotal() { return montoTotal; }
    public boolean estaEmitida() { return emitida; }
    public boolean estaAnulada() { return estaAnulada; }
}