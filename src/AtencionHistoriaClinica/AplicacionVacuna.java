package AtencionHistoriaClinica;
import InventarioProveedores.Lote;
import java.time.LocalDate;

// Registro de la aplicacion de una vacuna a un paciente.

public class AplicacionVacuna {
    private LocalDate fechaAplicacion;
    private Vacuna vacuna;
    private Lote lote;
    private LocalDate fechaProximoRefuerzo;

    public AplicacionVacuna(LocalDate fechaAplicacion, Vacuna vacuna, Lote lote) {
        this.fechaAplicacion = fechaAplicacion;
        this.vacuna = vacuna;
        this.lote = lote;
        this.fechaProximoRefuerzo = calcularProximoRefuerzo(vacuna.getIntervaloRefuerzoDias());
    }

    public LocalDate getFechaAplicacion() {
        return fechaAplicacion;
    }
    public Vacuna getVacuna() {
        return vacuna;
    }
    public Lote getLote() {
        return lote;
    }
    public LocalDate getFechaProximoRefuerzo() {
        return fechaProximoRefuerzo;
    }

    // Calcular fecha del proximo refuerzo
    public LocalDate calcularProximoRefuerzo(int intervaloDias) {
        return this.fechaAplicacion.plusDays(intervaloDias);
    }

    // Retornar si la fecha actual esta a 7 dias o menos del prox refuerzo
    public boolean requiereRecordatorio(LocalDate fechaActual) {
        if (this.fechaProximoRefuerzo == null) return false;
        LocalDate fechaAlerta = this.fechaProximoRefuerzo.minusDays(7);
        return !fechaActual.isBefore(fechaAlerta) && !fechaActual.isAfter(this.fechaProximoRefuerzo);
    }

    // Evaluar si transcurrieron mas de 30 dias posteriores a refuerzo sin aplicarse
    public boolean estaVencida(LocalDate fechaActual) {
        if (this.fechaProximoRefuerzo == null) return false;
        return fechaActual.isAfter(this.fechaProximoRefuerzo.plusDays(30));
    }
}
