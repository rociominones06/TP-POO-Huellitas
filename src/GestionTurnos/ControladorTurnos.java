package GestionTurnos;

import java.time.LocalDateTime;
import GeneralPersistencia.ClinicaVeterinaria;
import PersonasMascota.Mascota;
import PersonasMascota.Veterinario;

// Controlador del módulo de turnos: operaciones de asignación, urgencias y cancelaciones.

public class ControladorTurnos {

    private ClinicaVeterinaria clinica;

    public ControladorTurnos(ClinicaVeterinaria clinica) {

        this.clinica = clinica;
    }

    //devuelve el turno creado
    public Turno asignarTurno(Mascota mascota,
                              Veterinario veterinario,
                              LocalDateTime fechaHora,
                              TipoAtencion tipo) {

        return clinica.asignarTurno(
                mascota, veterinario, fechaHora, tipo);
    }
    // Devuelve el turno de urgencia generado
    public Turno registrarUrgencia(Mascota mascota) {

        return clinica.registrarUrgencia(mascota);
    }
}