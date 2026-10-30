package AtencionHistoriaClinica;
import Excepciones.ReglaNegocioException;
import PersonasMascota.Veterinario;
import InventarioProveedores.Producto;
import InventarioProveedores.Lote;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Acto medico individual atendido por un veterinario durante un Turno.

public class Consulta {
    private int idConsulta;
    private Veterinario veterinario;
    private LocalDateTime fecha;
    private String motivo;
    private double pesoDelDia;
    private String diagnostico;
    private String observaciones;
    private boolean estaConfirmada;
    private List<Tratamiento> tratamientos;
    private List<AplicacionVacuna> vacunasAplicadas;
    private List<NotaRectificatoria> notasRectificatorias;

    public Consulta(int idConsulta, Veterinario veterinario, String motivo, double pesoDelDia, String diagnostico, String observaciones) {
        this.idConsulta = idConsulta;
        this.veterinario = veterinario;
        this.fecha = LocalDateTime.now();
        this.motivo = motivo;
        this.pesoDelDia = pesoDelDia;
        this.diagnostico = diagnostico;
        this.observaciones = observaciones;
        this.estaConfirmada = false;
        this.tratamientos = new ArrayList<>();
        this.vacunasAplicadas = new ArrayList<>();
        this.notasRectificatorias = new ArrayList<>();
    }

    public int getIdConsulta() {
        return idConsulta;
    }

    public Veterinario getVeterinario() {
        return veterinario;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getMotivo() {
        return motivo;
    }

    public double getPesoDelDia() {
        return pesoDelDia;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public boolean isEstaConfirmada() {
        return estaConfirmada;
    }

    public List<Tratamiento> getTratamientos() {
        return tratamientos;
    }

    public List<NotaRectificatoria> getNotasRectificatorias() {
        return notasRectificatorias;
    }

    public List<AplicacionVacuna> getVacunasAplicadas() {
        return vacunasAplicadas;
    }

    // Agregar tratamiento a la consulta
    public Tratamiento agregarTratamiento(Producto medicamento, String dosis, String frecuencia, String duracion) throws ReglaNegocioException {
        if (this.pesoDelDia <= 0) {
            throw new ReglaNegocioException("No se puede indicar un medicamento si no se registro el peso de la mascota durante la consulta.");
        }
        Tratamiento nuevoTratamiento = new Tratamiento(medicamento, dosis, frecuencia, duracion);
        this.tratamientos.add(nuevoTratamiento);
        return nuevoTratamiento;
    }

    // Registrar aplicacion de vacuna a la consulta
    public AplicacionVacuna aplicarVacuna(Vacuna vacuna, Lote lote, LocalDate fechaActual) throws ReglaNegocioException {
        if (lote.esVencido(fechaActual)) {
            throw new ReglaNegocioException("No se puede aplicar una vacuna de un lote vencido.");
        }
        AplicacionVacuna nuevaAplicacion = new AplicacionVacuna(fechaActual, vacuna, lote);
        this.vacunasAplicadas.add(nuevaAplicacion);
        return nuevaAplicacion;
    }

    // Adjuntar nota rectificatoria para corregir un error
    public void agregarNotaRectificatoria(NotaRectificatoria nota) {
        if (nota!= null ) {
            this.notasRectificatorias.add(nota);
        }
    }

    // Confirmar consulta (impacta el consumo de stock en productos/lotes)
    public void confirmarConsulta() throws ReglaNegocioException {
        if (this.estaConfirmada) {
            throw new ReglaNegocioException("La consulta ya ha sido confirmada anteriormente.");
        }
        LocalDate hoy = LocalDate.now();
        // Verificar disponibilidad para los tratamientos
        for (Tratamiento t : tratamientos) {
            if (t.getMedicamento().obtenerStockUtilizable(hoy) < 1) {
                throw new ReglaNegocioException("Stock insuficiente para el medicamento: "+ t.getMedicamento().getNombre());
            }
        }
        // Descontar siguiendo FEFO (lote mas proximo a vencer)
        for (Tratamiento t: tratamientos) {
            t.getMedicamento().consumirStock(1, hoy);
        }
        for (AplicacionVacuna av : vacunasAplicadas) {
            av.getLote().reducirCantidad(1);
        }
        this.estaConfirmada = true;
    }
}
