package GestionTurnos;

import java.util.List;

public class ResultadoUrgencia {

    private Turno turnoUrgente;
    private List<Turno> turnosReprogramados;
    private boolean resuelta;

    public ResultadoUrgencia(Turno turnoUrgente, List<Turno> turnosReprogramados, boolean resuelta) {

        this.turnoUrgente = turnoUrgente;
        this.turnosReprogramados = turnosReprogramados;
        this.resuelta = resuelta;
    }

    public Turno getTurnoUrgente() {
        return turnoUrgente;
    }

    public List<Turno> getTurnosReprogramados() {
        return turnosReprogramados;
    }

    public boolean isResuelta() {
        return resuelta;
    }
}