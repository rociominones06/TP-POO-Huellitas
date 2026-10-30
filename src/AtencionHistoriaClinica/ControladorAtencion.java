package AtencionHistoriaClinica;
import Excepciones.ReglaNegocioException;
import GeneralPersistencia.ClinicaVeterinaria;
import PersonasMascota.Mascota;
import PersonasMascota.Veterinario;
import InventarioProveedores.Producto;
import InventarioProveedores.Lote;
import java.time.LocalDate;
import java.util.List;

// Controlador del modulo de Atencion Medica e Historia Clinica (consultas, tratamientos, vacunas hc).

public class ControladorAtencion {
    private ClinicaVeterinaria clinica;
    private static int contadorConsultas = 1;
    private static int contadorNotas = 1;
    public ControladorAtencion(ClinicaVeterinaria clinica) {
        this.clinica = clinica;
    }

    // Iniciar el registro de una consulta para una mascota atendida
    public Consulta registrarConsulta(Veterinario vet, Mascota mascota, String motivo, double peso, String diagnostico, String observaciones) throws ReglaNegocioException {
        if (vet == null || mascota == null) {
            throw new ReglaNegocioException("Veterinario y Mascota son datos obligatorios para la consulta!");
        }
        mascota.actualizarPeso(peso);
        Consulta consulta = new Consulta(contadorConsultas++, vet, motivo, peso, diagnostico, observaciones);
        return consulta;
    }

    // Agregar tratamiento a una consulta en proceso
    public void agregarTratamientoAConsulta(Consulta consulta, Producto medicamento, String dosis, String frecuencia, String duracion) throws ReglaNegocioException {
        if (consulta == null) {
            throw new ReglaNegocioException("No hay una consulta activa para agregar el tratamiento.");
        }
        consulta.agregarTratamiento(medicamento, dosis, frecuencia, duracion);
    }

    // Registrar la aplicacion de una vacuna en una consulta
    public void aplicarVacunaAConsulta(Consulta consulta, Vacuna vacuna, Lote lote, Mascota mascota) throws ReglaNegocioException {
        if (consulta == null) {
            throw new ReglaNegocioException("No hay una consulta activa para aplicar la vacuna.");
        }
        if(!vacuna.esAptaParaEspecie(mascota.getEspecie())) {
            throw new ReglaNegocioException("La vacuna "+vacuna.getNombre()+ " no es apta para la especie "+mascota.getEspecie());
        }
        if (!vacuna.puedeAplicarse(mascota.calcularEdadMeses())) {
            throw new ReglaNegocioException("La mascota no cumple con la edad minima de " + vacuna.getEdadMinimaMeses() + " meses.");
        }
        consulta.aplicarVacuna(vacuna, lote, LocalDate.now());
    }

    // Confirmar consulta medica, descontar stock y guardar la HC
    public void finalizarYGuardarConsulta(Consulta consulta, Mascota mascota) throws ReglaNegocioException {
        if(consulta == null || mascota==null) {
            throw new ReglaNegocioException("Datos insuficientes para finalizar la consulta!");
        }
        consulta.confirmarConsulta();
        mascota.getHistoriaClinica().agregarConsulta(consulta);
    }

    // Registrar nota rectificatoria en una consulta registrada
    public void rectificarConsulta(Consulta consulta, String motivo, String textoCorreccion, String firmaProfesional) throws ReglaNegocioException {
        if (consulta==null) {
            throw new ReglaNegocioException("Consulta no encontrada para rectificar.");
        }
        if (firmaProfesional==null || firmaProfesional.trim().isEmpty()) {
            throw new ReglaNegocioException("La firma profesional es obligatoria para registrar una nota rectificatoria.");
        }
        NotaRectificatoria nota = new NotaRectificatoria(contadorNotas++, motivo, textoCorreccion, firmaProfesional);
        consulta.agregarNotaRectificatoria(nota);
    }

    // Obtener historia clinica completa de una mascota
    public List<Consulta> consultarHistoriaClinica(Mascota mascota) throws ReglaNegocioException {
        if (mascota==null) {
            throw new ReglaNegocioException("Mascota no encontrada.");
        }
        return mascota.getHistoriaClinica().obtenerHistorial();
    }
}
