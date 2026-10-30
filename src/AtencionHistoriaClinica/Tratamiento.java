package AtencionHistoriaClinica;
import InventarioProveedores.Producto;

// Indicacion medica. Registra medicamento, dosis, frecuencia y duracion.

public class Tratamiento {
    private Producto medicamento;
    private String dosis;
    private String frecuencia;
    private String duracion;

    public Tratamiento(Producto medicamento, String dosis, String frecuencia, String duracion) {
        this.medicamento = medicamento;
        this.dosis = dosis;
        this.frecuencia = frecuencia;
        this.duracion = duracion;
    }

    public Producto getMedicamento() {
        return medicamento;
    }
    public String getDosis() {
        return dosis;
    }
    public String getFrecuencia() {
        return frecuencia;
    }
    public String getDuracion() {
        return duracion;
    }

    // Descripcion textual del tratamiento recetado
    public String getResumenTratamiento() {
        return "Medicamento: " + (medicamento != null ? medicamento.getNombre() : "N/A") +
                " - Dosis: " + dosis +
                " - Frecuencia: " + frecuencia +
                " - Duracion: " + duracion;
    }
}
