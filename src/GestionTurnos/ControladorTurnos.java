package GestionTurnos;

import java.time.LocalDateTime;
import GeneralPersistencia.ClinicaVeterinaria;
import PersonasMascota.Mascota;
import PersonasMascota.Veterinario;

// Controlador encargado de coordinar las operaciones relacionadas
// con la gestión de turnos entre la vista y la clínica.
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