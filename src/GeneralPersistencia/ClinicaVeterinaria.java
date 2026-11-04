package GeneralPersistencia;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

import AtencionHistoriaClinica.AplicacionVacuna;
import AtencionHistoriaClinica.Consulta;
import AtencionHistoriaClinica.ControladorAtencion;
import Excepciones.RegistroDuplicadoException;
import Excepciones.RegistroNoEncontradoException;
import FacturacionNotificaciones.CalculadorFacturacion;
import FacturacionNotificaciones.ControladorFacturacion;
import FacturacionNotificaciones.ServicioNotificaciones;
import GestionTurnos.PlanificadorTurnos;
import GestionTurnos.Turno;
import GestionTurnos.TipoAtencion;
import GestionTurnos.ResultadoUrgencia;
import InventarioProveedores.ControladorInventario;
import InventarioProveedores.Producto;
import PersonasMascota.Dueño;
import PersonasMascota.Mascota;
import PersonasMascota.Veterinario;

// Controlador raiz (GRASP): reune los datos de la clinica y los controladores de cada modulo.
// Es el unico objeto que GestorArchivos guarda y carga. Al serializar UNA sola raiz, los objetos
// compartidos (por ejemplo el mismo Producto en el inventario y en un Tratamiento) siguen
// siendo el mismo objeto despues de cargar. Por eso no conviene guardar cada controlador por separado.

public class ClinicaVeterinaria implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String nombre;
    private final Map<String, Dueño> dueños = new LinkedHashMap<>();            // clave: documento
    private final Map<String, Veterinario> veterinarios = new LinkedHashMap<>(); // clave: legajo
    private final ControladorInventario inventario;
    private final ControladorFacturacion facturacion;
    private final PlanificadorTurnos planificador = new PlanificadorTurnos();
    private final ServicioNotificaciones notificaciones;
    private int proximoIdMascota = 1;
    private int proximoIdHistoria = 1;

    // No se guarda: es un objeto liviano que se vuelve a crear cuando hace falta.
    private transient ControladorAtencion atencion;

    public ClinicaVeterinaria(String nombre, ServicioNotificaciones notificaciones) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la clinica es obligatorio.");
        }
        if (notificaciones == null) {
            throw new IllegalArgumentException("El servicio de notificaciones es obligatorio.");
        }
        this.nombre = nombre.trim();
        this.notificaciones = notificaciones;
        this.facturacion = new ControladorFacturacion(new CalculadorFacturacion(), notificaciones);
        this.inventario = new ControladorInventario(this);
    }

    // Dueños y mascotas (CU01, CU02, CU17, CU26)

    public void registrarDueño(Dueño dueño) throws RegistroDuplicadoException {
        if (dueño == null) {
            throw new IllegalArgumentException("El dueño es obligatorio.");
        }
        String clave = clave(dueño.getDocumento());
        if (dueños.containsKey(clave)) {
            throw new RegistroDuplicadoException("Ya existe un dueño con documento " + dueño.getDocumento() + ".");
        }
        dueños.put(clave, dueño);
    }

    public Dueño buscarDueño(String documento) throws RegistroNoEncontradoException {
        Dueño d = dueños.get(clave(documento));
        if (d == null) {
            throw new RegistroNoEncontradoException("No se encontro el dueño con documento " + documento + ".");
        }
        return d;
    }

    public List<Dueño> listarDueños() {
        return Collections.unmodifiableList(new ArrayList<>(dueños.values()));
    }

    // La mascota solo se registra si su dueño ya esta registrado (flujo alternativo de CU02).
    public void registrarMascota(Dueño dueño, Mascota mascota) throws RegistroNoEncontradoException {
        buscarDueño(dueño.getDocumento());
        dueño.agregarMascota(mascota);
    }

    // CU17: la historia clinica viaja con la mascota porque es parte de ella.
    public void transferirMascota(Mascota mascota, Dueño nuevoDueño) throws RegistroNoEncontradoException {
        buscarDueño(nuevoDueño.getDocumento());
        mascota.getDueño().transferirMascota(mascota, nuevoDueño);
        mascota.setDueño(nuevoDueño); // Dueño.transferirMascota no actualiza este lado
    }

    // Los ids se guardan junto con los datos, asi no se repiten entre ejecuciones.
    public int proximoIdMascota() {
        return proximoIdMascota++;
    }

    public int proximoIdHistoria() {
        return proximoIdHistoria++;
    }
    private int proximoIdTurno = 1;

    // Veterinarios

    public void registrarVeterinario(Veterinario vet) throws RegistroDuplicadoException {
        if (vet == null) {
            throw new IllegalArgumentException("El veterinario es obligatorio.");
        }
        String clave = clave(vet.getLegajo());
        if (veterinarios.containsKey(clave)) {
            throw new RegistroDuplicadoException("Ya existe un veterinario con legajo " + vet.getLegajo() + ".");
        }
        veterinarios.put(clave, vet);
    }

    public Veterinario buscarVeterinario(String legajo) throws RegistroNoEncontradoException {
        Veterinario v = veterinarios.get(clave(legajo));
        if (v == null) {
            throw new RegistroNoEncontradoException("No se encontro el veterinario con legajo " + legajo + ".");
        }
        return v;
    }

    public List<Veterinario> listarVeterinarios() {
        return Collections.unmodifiableList(new ArrayList<>(veterinarios.values()));
    }

    // Para urgencias (RN1.7)
    public Veterinario obtenerVeterinarioDeGuardia() throws RegistroNoEncontradoException {
        for (Veterinario v : veterinarios.values()) {
            if (v.esDeGuardia()) {
                return v;
            }
        }
        throw new RegistroNoEncontradoException("No hay un veterinario de guardia.");
    }


    // Gestión de turnos

    // Asigna un turno verificando la disponibilidad del veterinario.
    public Turno asignarTurno(
            Mascota mascota,
            Veterinario veterinario,
            LocalDateTime fechaHora,
            TipoAtencion tipo) {

        if (mascota == null || veterinario == null
                || fechaHora == null || tipo == null) {
            throw new IllegalArgumentException("Los datos del turno son obligatorios.");
        }

        // Verifica la disponibilidad del veterinario.
        int duracion = tipo.getDuracionMinutos();

        if (!planificador.verificarDisponibilidad(
                veterinario, fechaHora, duracion)) {
            return null;
        }

        // Verifica si el dueño tiene dos o mas ausencias seguidas para cobrarle seña
        boolean requiereSeña = mascota.getDueño() != null && mascota.getDueño().debePagarSeña();

        // Crea y registra el turno.
        Turno nuevoTurno = new Turno(
                proximoIdTurno,
                mascota,
                veterinario,
                tipo,
                fechaHora,
                false,
                requiereSeña
        );

        planificador.agregarTurno(nuevoTurno);
        proximoIdTurno++;

        return nuevoTurno;
    }

    // Registra una urgencia buscando un horario para el veterinario de guardia.
    public Turno registrarUrgencia(Mascota mascota) {

        if (mascota == null) {
            throw new IllegalArgumentException("La mascota es obligatoria.");
        }

        try {
            // Obtiene el veterinario de guardia.
            Veterinario vetGuardia = obtenerVeterinarioDeGuardia();

            // Calcula el próximo intervalo de 15 minutos.
            LocalDateTime ahora = LocalDateTime.now();

            int minutosRestantes = 15 - (ahora.getMinute() % 15);

            if (minutosRestantes == 15
                    && ahora.getSecond() == 0
                    && ahora.getNano() == 0) {
                minutosRestantes = 0;
            }

            LocalDateTime horarioCandidato = ahora
                    .withSecond(0)
                    .withNano(0)
                    .plusMinutes(minutosRestantes);

            // La urgencia se considera una consulta general de 30 minutos.
            TipoAtencion tipo = TipoAtencion.ConsultaGeneral;

            // Busca un horario durante los próximos siete días.
            int intentos = 7 * 24 * 4;

            for (int i = 0; i < intentos; i++) {

                Turno turnoUrgente = new Turno(
                        proximoIdTurno,
                        mascota,
                        vetGuardia,
                        tipo,
                        horarioCandidato,
                        true,
                        false
                );

                // Intenta resolver la urgencia y desplazar los turnos necesarios.
                ResultadoUrgencia resultado =
                        planificador.resolverUrgencia(turnoUrgente, vetGuardia);

                if (resultado.isResuelta()) {
                    proximoIdTurno++;
                    return turnoUrgente;
                }

                // Si no se puede, prueba el siguiente intervalo.
                horarioCandidato = horarioCandidato.plusMinutes(15);
            }

        } catch (RegistroNoEncontradoException e) {
            return null;
        }

        return null;
    }


    // Consultas administrativas (CU11, RF7) 

    public List<Producto> obtenerProductosBajoStock() {
        return inventario.obtenerProductosBajoStock(getCatalogoProductos(), LocalDate.now());
    }

    // Refuerzos que vencen dentro de los proximos 7 dias, ordenados por fecha (Comparator).
    public List<AplicacionVacuna> obtenerVacunasProximasVencer(LocalDate fechaActual) {
        List<AplicacionVacuna> proximas = new ArrayList<>();
        for (Dueño d : dueños.values()) {
            for (Mascota m : d.getMascotas()) {
                for (Consulta c : m.getHistoriaClinica().obtenerHistorial()) {
                    for (AplicacionVacuna av : c.getVacunasAplicadas()) {
                        if (av.requiereRecordatorio(fechaActual)) {
                            proximas.add(av);
                        }
                    }
                }
            }
        }
        proximas.sort(Comparator.comparing(AplicacionVacuna::getFechaProximoRefuerzo));
        return proximas;
    }

    public List<AplicacionVacuna> obtenerVacunasProximasVencer() {
        return obtenerVacunasProximasVencer(LocalDate.now());
    }

    // Acceso a los modulos 

    public ControladorInventario getInventario() { return inventario; }
    public ControladorFacturacion getFacturacion() { return facturacion; }
    public PlanificadorTurnos getPlanificador() { return planificador; }
    public ServicioNotificaciones getNotificaciones() { return notificaciones; }
    public String getNombre() { return nombre; }
    public List<Producto> getCatalogoProductos() {
        return inventario.getCatalogoProductos();
    }

    // Guarda el producto en el catálogo
    public void agregarProducto(Producto p) {
      if (p != null && !inventario.getCatalogoProductos().contains(p)) {
          inventario.getCatalogoProductos().add(p);
      }
    }

    public ControladorAtencion getAtencion() {
        if (atencion == null) {
            atencion = new ControladorAtencion(this);
        }
        return atencion;
    }

    private static String clave(String texto) {
        return texto == null ? "" : texto.trim().toLowerCase();
    }
}