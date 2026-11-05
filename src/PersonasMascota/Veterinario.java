package PersonasMascota;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import GestionTurnos.Turno;
import GestionTurnos.DiaSemana;
import AtencionHistoriaClinica.Consulta;
import GestionTurnos.RangoHorario;

public class Veterinario extends Persona {

    private String legajo;
    private String matricula;
    private String especialidad;
    private Map<DiaSemana, RangoHorario> horariosAtencion;
    private boolean esDeGuardia;

    public Veterinario(String nombre, String apellido,
                       String documento, String telefono,
                       String legajo, String matricula,
                       String especialidad,
                       Map<DiaSemana, RangoHorario> horariosAtencion,
                       boolean esDeGuardia) {
        super(nombre, apellido, documento, telefono);
        this.legajo = legajo;
        this.matricula = matricula;
        this.especialidad = especialidad;
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

    public boolean esDeGuardia() {
        return esDeGuardia;
    }

    public String getLegajo() {
        return legajo;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public Map<DiaSemana, RangoHorario> getHorariosAtencion() {
        return horariosAtencion;
    }
}
