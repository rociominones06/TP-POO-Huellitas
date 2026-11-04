package GestionTurnos;

import java.time.LocalTime;
import java.io.Serializable;

public class RangoHorario implements Serializable {
    private static final long serialVersionUID = 1L;
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

    public boolean contieneHorario(LocalTime inicio, LocalTime fin) {
        return !inicio.isBefore(horaInicio)
                && !fin.isAfter(horaFin);
    }
}