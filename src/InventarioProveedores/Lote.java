package InventarioProveedores;
import java.time.LocalDate;
import java.io.Serializable;

//  Representa un lote fisico de insumos. Controla vencimiento y disponibilidad.

public class Lote implements Serializable {
    private static final long serialVersionUID = 1L;
    private String numeroLote;
    private LocalDate fechaVencimiento;
    private int cantidadDisponible;

    public Lote(String numeroLote, LocalDate fechaVencimiento, int cantidadDisponible) {
        this.numeroLote = numeroLote;
        this.fechaVencimiento = fechaVencimiento;
        this.cantidadDisponible = cantidadDisponible;
    }

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
