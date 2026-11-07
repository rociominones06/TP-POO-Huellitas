package FacturacionNotificaciones;

import java.time.LocalDateTime;
import java.io.Serializable;

// Comprobante que anula una factura emitida sin modificar sus datos originales.

public class NotaDeCredito implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int numeroNota;
    private final String motivo;
    private final double importeTotal;
    private LocalDateTime fecha; 

    public NotaDeCredito(int numeroNota, String motivo, double importeTotal) {
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El motivo de la anulación es obligatorio.");
        }
        if (importeTotal < 0) {
            throw new IllegalArgumentException("El importe no puede ser negativo.");
        }
        this.numeroNota = numeroNota;
        this.motivo = motivo;
        this.importeTotal = importeTotal;
    }

    // Emite la nota registrando su fecha. Solo se puede emitir una vez.
    public void emitirNota() {
        if (fecha != null) {
            throw new IllegalStateException("La nota de crédito ya fue emitida.");
        }
        this.fecha = LocalDateTime.now();
    }

    public boolean estaEmitida() { return fecha != null; }

    public int getNumeroNota() { return numeroNota; }
    public LocalDateTime getFecha() { return fecha; }
    public String getMotivo() { return motivo; }
    public double getImporteTotal() { return importeTotal; }
}
