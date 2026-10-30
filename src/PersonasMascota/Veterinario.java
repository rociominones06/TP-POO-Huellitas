package PersonasMascota;

import java.time.LocalDateTime;
import java.util.Map;
import GestionTurnos.Turno;
import AtencionHistoriaClinica.Consulta;
import GestionTurnos.DiaSemana;
import GestionTurnos.RangoHorario;

public class Veterinario extends Persona {

    private String Legajo;
    private String Matricula;
    private String Especialidad;
    private Map<DiaSemana, RangoHorario> horariosAtencion;
    private boolean esDeGuardia;

    public Veterinario(String Nombre, String Apellido,
                       String Documento, String Telefono,
                       String Legajo, String Matricula,
                       String Especialidad,
                       Map<DiaSemana, RangoHorario> horariosAtencion,
                       boolean esDeGuardia) {

        super(Nombre, Apellido, Documento, Telefono);

        this.Legajo = Legajo;
        this.Matricula = Matricula;
        this.Especialidad = Especialidad;
        this.horariosAtencion = horariosAtencion;
        this.esDeGuardia = esDeGuardia;
    }

    public boolean esHorarioDisponible(LocalDateTime fechaHora, int duracionMinutos) {
        // Implementación pendiente
        return false;
    }

    public Consulta registrarConsulta(Turno turno, String motivo, double pesoDelDia, String diagnostico, String observaciones) {
        // Implementación pendiente
        return null;
    }

    public boolean esDeGuardia() {
        return esDeGuardia;
    }
}