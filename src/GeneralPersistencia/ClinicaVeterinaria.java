package GeneralPersistencia;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import AtencionHistoriaClinica.AplicacionVacuna;
import AtencionHistoriaClinica.Consulta;
import AtencionHistoriaClinica.ControladorAtencion;
import Excepciones.RegistroDuplicadoException;
import Excepciones.RegistroNoEncontradoException;
import FacturacionNotificaciones.CalculadorFacturacion;
import FacturacionNotificaciones.ControladorFacturacion;
import FacturacionNotificaciones.ServicioNotificaciones;
import GestionTurnos.PlanificadorTurnos;
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
    public void agregarProducto(Producto p) {
        // Guarda el producto en el catálogo
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