package InventarioProveedores;
import java.time.LocalDate;

//  Representa un lote fisico de insumos

import java.io.Serializable;
import java.time.LocalDate;

// Lote de un producto recibido con su propia fecha de vencimiento.
// Experto en Informacion (GRASP): decide si esta vencido y modifica su propia cantidad.
// Orden natural (Comparable): por fecha de vencimiento (criterio FEFO), luego por numero.

public class Lote implements Comparable<Lote>, Serializable {

    private static final long serialVersionUID = 1L;

    public static class StockInsuficienteException extends Exception {
        private static final long serialVersionUID = 1L;

        public StockInsuficienteException(String message) {
            super(message);
        }
    }

    private final String numeroLote;
    private final LocalDate fechaVencimiento;
    private int cantidadDisponible;

    public Lote(String numeroLote, LocalDate fechaVencimiento, int cantidadDisponible) {
        if (numeroLote == null || numeroLote.isBlank()) {
            throw new IllegalArgumentException("El numero de lote es obligatorio.");
        }
        if (fechaVencimiento == null) {
            throw new IllegalArgumentException("La fecha de vencimiento es obligatoria.");
        }
        if (cantidadDisponible < 0) {
            throw new IllegalArgumentException("La cantidad recibida no puede ser negativa.");
        }
        this.numeroLote = numeroLote.trim();

public class Lote {
    private String numeroLote;
    private LocalDate fechaVencimiento;
    private int cantidadDisponible;

    public Lote(String numeroLote, LocalDate fechaVencimiento, int cantidadDisponible) {
        this.numeroLote = numeroLote;

        this.fechaVencimiento = fechaVencimiento;
        this.cantidadDisponible = cantidadDisponible;
    }


    /** Supuesto: un lote vence el dia posterior a su fecha de vencimiento (ese dia aun se puede usar). */
    public boolean esVencido(LocalDate fechaActual) {
        return fechaVencimiento.isBefore(fechaActual);
    }

    public boolean tieneStock() {
        return cantidadDisponible > 0;
    }

    /** Un lote es utilizable si no esta vencido y tiene unidades (RN4.3 y RN4.6). */
    public boolean esUtilizable(LocalDate fechaActual) {
        return !esVencido(fechaActual) && tieneStock();
    }

    public void reducirCantidad(int cantidad) throws StockInsuficienteException {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a reducir debe ser mayor a cero.");
        }
        if (cantidad > cantidadDisponible) {
            throw new StockInsuficienteException("El lote " + numeroLote + " solo tiene "
                    + cantidadDisponible + " unidades y se pidieron " + cantidad + ".");
        }
        cantidadDisponible -= cantidad;
    }

    public String getNumeroLote() { return numeroLote; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public int getCantidadDisponible() { return cantidadDisponible; }

    @Override
    public int compareTo(Lote otro) {
        int cmp = fechaVencimiento.compareTo(otro.fechaVencimiento);
        return cmp != 0 ? cmp : numeroLote.compareTo(otro.numeroLote);
    }

    @Override
    public String toString() {
        return "Lote " + numeroLote + " | vence " + fechaVencimiento + " | disponible: " + cantidadDisponible;
=======
    public String getNumeroLote() {
        return numeroLote;
    }
    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }
    public int getCantidadDisponible() {
        return cantidadDisponible;
    }

    // Evaluar si el lote esta vencido
    public boolean esVencido(LocalDate fechaActual) {
        if (this.fechaVencimiento == null) return false;
        return fechaActual.isAfter(this.fechaVencimiento);
    }

    // Indicar si el lote posee existencias disponibles
    public boolean tieneStock() {
        return this.cantidadDisponible > 0;
    }

    // Reducir cantidad fisica del lote
    public void reducirCantidad(int cantidad) {
        if (cantidad > 0 && this.cantidadDisponible >= cantidad) {
            this.cantidadDisponible -= cantidad;
        }

    }
}
