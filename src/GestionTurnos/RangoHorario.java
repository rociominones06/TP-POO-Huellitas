package GestionTurnos;

import java.time.LocalTime;

public class RangoHorario {

    private LocalTime horaInicio;
    private LocalTime horaFin;

    public RangoHorario(LocalTime horaInicio, LocalTime horaFin) {
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }
}