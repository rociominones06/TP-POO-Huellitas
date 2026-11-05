package PersonasMascota;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import GestionTurnos.Turno;
import GestionTurnos.DiaSemana;
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
        if (fechaHora == null || horariosAtencion == null || duracionMinutos <=0) {
            return false;
        }
        //Convertir dia de la semana a enum DiaSemana
        DiaSemana dia;
        switch (fechaHora.getDayOfWeek()) {
            case MONDAY: dia = DiaSemana.LUNES; break;
            case TUESDAY: dia = DiaSemana.MARTES; break;
            case WEDNESDAY: dia = DiaSemana.MIERCOLES; break;
            case THURSDAY: dia = DiaSemana.JUEVES; break;
            case FRIDAY: dia = DiaSemana.VIERNES; break;
            case SATURDAY: dia = DiaSemana.SABADO; break;
            case SUNDAY: dia = DiaSemana.DOMINGO; break;
            default: return false;
        }
        RangoHorario rango = horariosAtencion.get(dia);
        if (rango == null) {
            return false;
        }
        LocalTime inicio = fechaHora.toLocalTime();
        LocalTime fin = inicio.plusMinutes(duracionMinutos);
        return rango.contieneHorario(inicio, fin);
    }

    public Consulta registrarConsulta(Turno turno, String motivo, double pesoDelDia, String diagnostico, String observaciones) {
        // Implementación pendiente
        return null;
    }

    public boolean esDeGuardia() {
        return esDeGuardia;
    }

    public String getLegajo() {
        return Legajo;
    }
}
