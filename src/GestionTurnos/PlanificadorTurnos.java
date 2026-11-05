package GestionTurnos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.time.LocalDateTime;
import java.io.Serializable;

import PersonasMascota.Veterinario;

//Clase encargada de administrar los turnos,
// verificar la disponibilidad de los veterinarios y resolver urgencias.
public class PlanificadorTurnos implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Turno> turnos;

    public PlanificadorTurnos() {
        turnos = new ArrayList<>();
    }

    public boolean verificarDisponibilidad(
            Veterinario veterinario,
            LocalDateTime fechaHora,
            int duracion) {

        if (!veterinario.esHorarioDisponible(fechaHora, duracion)) {
            return false;
        }

        LocalDateTime fin = fechaHora.plusMinutes(duracion);

        for (Turno turno : turnos) {

            if (turno.getVeterinario().equals(veterinario)
                    && turno.getEstado() == Turno.EstadoTurno.PROGRAMADO) {

                LocalDateTime inicioExistente = turno.getFechaHora();

                LocalDateTime finExistente = inicioExistente.plusMinutes(
                        turno.calcularDuracion());

                if (inicioExistente.isBefore(fin)
                        && finExistente.isAfter(fechaHora)) {
                    return false;
                }
            }
        }

        return true;
    }

    public ResultadoUrgencia resolverUrgencia(
            Turno turnoUrgente,
            Veterinario vetGuardia) {

        List<Turno> desplazados = new ArrayList<>();
        List<LocalDateTime> nuevosHorarios = new ArrayList<>();

        if (turnoUrgente == null || vetGuardia == null
                || !vetGuardia.esDeGuardia()) {

            return new ResultadoUrgencia(
                    turnoUrgente, desplazados, false);
        }

        LocalDateTime inicioUrgencia = turnoUrgente.getFechaHora();

        int duracionUrgencia = turnoUrgente.calcularDuracion();

        LocalDateTime finUrgencia =
                inicioUrgencia.plusMinutes(duracionUrgencia);

        // Obtener y ordenar los turnos del veterinario de guardia.
        List<Turno> agenda = new ArrayList<>();

        for (Turno turno : turnos) {

            if (turno != turnoUrgente
                    && turno.getVeterinario().equals(vetGuardia)
                    && turno.getEstado() == Turno.EstadoTurno.PROGRAMADO) {

                agenda.add(turno);
            }
        }

        agenda.sort(Comparator.comparing(Turno::getFechaHora));

        // Planificar los desplazamientos.
        LocalDateTime finAnterior = finUrgencia;

        for (Turno turno : agenda) {

            LocalDateTime inicioActual = turno.getFechaHora();

            LocalDateTime finActual = inicioActual.plusMinutes(
                    turno.calcularDuracion());

            if (inicioActual.isBefore(finAnterior)
                    && finActual.isAfter(inicioUrgencia)) {

                LocalDateTime nuevoInicio = finAnterior;

                int duracion = turno.calcularDuracion();

                if (!vetGuardia.esHorarioDisponible(
                        nuevoInicio, duracion)) {

                    return new ResultadoUrgencia(
                            turnoUrgente, new ArrayList<>(), false);
                }

                desplazados.add(turno);
                nuevosHorarios.add(nuevoInicio);

                finAnterior = nuevoInicio.plusMinutes(duracion);

            } else {

                if (finActual.isAfter(finAnterior)) {
                    finAnterior = finActual;
                }
            }
        }

        // Confirmar la urgencia y los desplazamientos.
        turnoUrgente.setVeterinario(vetGuardia);
        turnoUrgente.setEsUrgencia(true);

        for (int i = 0; i < desplazados.size(); i++) {

            desplazados.get(i).desplazarPorUrgencia(
                    nuevosHorarios.get(i));
        }

        if (!turnos.contains(turnoUrgente)) {
            turnos.add(turnoUrgente);
        }

        return new ResultadoUrgencia(
                turnoUrgente, desplazados, true);
    }

    public double calcularCargoCancelacion(
            Turno turno,
            LocalDateTime fechaCancelacion) {

        return turno.cancelar(fechaCancelacion);
    }

    public void agregarTurno(Turno turno) {
        turnos.add(turno);
    }

    public List<Turno> getTurnos() {
        return new ArrayList<>(turnos);
    }
}