package FacturacionNotificaciones;

import AtencionHistoriaClinica.Consulta;
import Excepciones.FacturacionException;
import Excepciones.ReglaNegocioException;
import PersonasMascota.Dueño;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.io.Serializable;

// Controlador MVC de cobros y facturación (GRASP Controlador).
// Recibe las acciones de la vista (CU14, CU24, CU25), coordina el modelo
// y dispara las notificaciones. No imprime ni lee por consola: devuelve datos
// o lanza FacturacionException para que la vista los muestre.

public class ControladorFacturacion implements Serializable {
    private static final long serialVersionUID = 1L;
    private final CalculadorFacturacion calculador;
    private final ServicioNotificaciones notificaciones;
    private final Map<MedioPago, EstrategiaPago> estrategias = new EnumMap<>(MedioPago.class);
    private final Map<Integer, Factura> facturas = new TreeMap<>();
    private final List<String> incidentes = new ArrayList<>();
    private int proximaFactura = 1;
    private int proximaNota = 1;
    private int notificacionesProcesadas = 0;

    public ControladorFacturacion(CalculadorFacturacion calculador, ServicioNotificaciones notificaciones) {
        if (calculador == null || notificaciones == null) {
            throw new IllegalArgumentException("El calculador y el servicio de notificaciones son obligatorios.");
        }
        this.calculador = calculador;
        this.notificaciones = notificaciones;
        registrarEstrategia(new PagoEfectivo());
        registrarEstrategia(new PagoDebito());
        registrarEstrategia(new PagoCredito());
    }

    // Habilita un medio de pago sin modificar el resto del controlador (OCP)
    public final void registrarEstrategia(EstrategiaPago estrategia) {
        estrategias.put(estrategia.getMedioPago(), estrategia);
    }

    // CU14: emite la factura de una consulta, registra el medio de pago y avisa al dueño.
    public Factura emitirFactura(Dueño dueño, Consulta consulta, MedioPago medioPago,
                                 List<LineaFactura> lineas) throws FacturacionException {
        if (dueño == null || consulta == null || lineas == null) {
            throw new FacturacionException("Faltan datos necesarios para facturar.");
        }
        if (medioPago == null) {
            throw new FacturacionException("Debe seleccionarse un medio de pago.");
        }
        EstrategiaPago estrategia = estrategias.get(medioPago);
        if (estrategia == null) {
            throw new FacturacionException("El medio de pago no está habilitado: " + medioPago);
        }
        if (tieneFacturaVigente(consulta)) {
            throw new FacturacionException("La consulta ya tiene una factura vigente.");
        }

        Factura factura = new Factura(proximaFactura, dueño, consulta, estrategia);
        for (LineaFactura linea : lineas) {
            factura.agregarLinea(linea);
        }
        factura.emitirFactura(calculador);

        facturas.put(proximaFactura, factura);
        proximaFactura++;

        notificar(dueño, "Factura N° " + factura.getNumeroFactura() + " emitida",
                String.format("Se emitió la factura N° %d por $%.2f (%s).",
                        factura.getNumeroFactura(), factura.getMontoTotal(),
                        factura.getMedioPago().getDescripcion()));
        return factura;
    }

    // CU25: anula una factura mediante una nota de crédito y avisa al dueño.
    public NotaDeCredito anularFactura(int numeroFactura, String motivo) throws FacturacionException {
        Factura factura = consultarFactura(numeroFactura);

        NotaDeCredito nota;
        try {
            nota = new NotaDeCredito(proximaNota, motivo, factura.getMontoTotal());
        } catch (IllegalArgumentException e) {
            throw new FacturacionException(e.getMessage());
        }
        factura.anular(nota);
        proximaNota++;

        notificar(factura.getDueño(), "Factura N° " + numeroFactura + " anulada",
                String.format("Se emitió la nota de crédito N° %d que anula la factura N° %d. Motivo: %s",
                        nota.getNumeroNota(), numeroFactura, motivo));
        return nota;
    }

    // CU24: busca una factura por número.
    public Factura consultarFactura(int numeroFactura) throws FacturacionException {
        Factura factura = facturas.get(numeroFactura);
        if (factura == null) {
            throw new FacturacionException("No se encontró la factura N° " + numeroFactura + ".");
        }
        return factura;
    }

    // CU24: facturas emitidas en un período (inclusive), en orden natural por número.
    public List<Factura> consultarPorPeriodo(LocalDate desde, LocalDate hasta) throws FacturacionException {
        if (desde == null || hasta == null || desde.isAfter(hasta)) {
            throw new FacturacionException("El período ingresado no es válido.");
        }
        List<Factura> resultado = new ArrayList<>();
        for (Factura f : facturas.values()) {
            LocalDate dia = f.getFecha().toLocalDate();
            if (!dia.isBefore(desde) && !dia.isAfter(hasta)) {
                resultado.add(f);
            }
        }
        Collections.sort(resultado); // Comparable: por número de factura
        return resultado;
    }

    // Listado con orden personalizado (Comparator): mayor importe primero.
    public List<Factura> listarPorImporteDescendente() {
        List<Factura> lista = new ArrayList<>(facturas.values());
        lista.sort(Comparator.comparingDouble(Factura::getMontoTotal).reversed());
        return lista;
    }

    // Constancia de los avisos que no pudieron enviarse.
    public List<String> getIncidentes() { return Collections.unmodifiableList(incidentes); }

    public int getNotificacionesProcesadas() { return notificacionesProcesadas; }

    private boolean tieneFacturaVigente(Consulta consulta) {
        for (Factura f : facturas.values()) {
            if (f.getConsulta().equals(consulta) && !f.estaAnulada()) {
                return true;
            }
        }
        return false;
    }

    // Un fallo al avisar no revierte la operación: queda registrado como incidente.
    private void notificar(Dueño dueño, String asunto, String cuerpo) {
        try {
            notificaciones.enviarMensaje(dueño.getCorreo(), asunto, cuerpo);
        } catch (ReglaNegocioException e) {
            incidentes.add("No se pudo notificar a " + dueño.obtenerNombreCompleto() + ": " + e.getMessage());
        } finally {
            notificacionesProcesadas++;
        }
    }
    public List<Factura> listarFacturas() {
        return new ArrayList<>(facturas.values());
    }
}
