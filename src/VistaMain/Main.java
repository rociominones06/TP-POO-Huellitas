package VistaMain;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.EnumMap;
import java.util.Map;
import AtencionHistoriaClinica.HistoriaClinica;
import Excepciones.RegistroDuplicadoException;
import Excepciones.RegistroNoEncontradoException;
import FacturacionNotificaciones.NotificadorEmail;
import FacturacionNotificaciones.ServicioNotificaciones;
import GeneralPersistencia.ClinicaVeterinaria;
import GeneralPersistencia.GestorArchivos;
import GestionTurnos.DiaSemana;
import GestionTurnos.RangoHorario;
import InventarioProveedores.Lote;
import InventarioProveedores.Producto;
import InventarioProveedores.Proveedor;
import PersonasMascota.Dueño;
import PersonasMascota.Gato;
import PersonasMascota.Perro;
import PersonasMascota.Veterinario;

public class Main {
    private static final String nombreClinica = "Clinica Veterinaria Patitas";
    private static final Path archivoDatos = Paths.get("datos","clinica.dat");
    private static final Path archivoIncidentes = Paths.get("datos","incidentes.log");

    public static void main(String[] args) {
        System.out.println("=====================================================================");
        System.out.println(" SISTEMA DE GESTION INTEGRAL - "+ nombreClinica.toUpperCase());
        System.out.println("=====================================================================");

        GestorArchivos gestor = new GestorArchivos(archivoDatos, archivoIncidentes);
        ServicioNotificaciones notificador = new NotificadorEmail("contacto@clinicapatitas.com");
        ClinicaVeterinaria clinica = null;

        try {
            boolean esPrimeraVez = !gestor.existenDatos();
            clinica = gestor.cargarOCrear(nombreClinica, notificador);
            //Si es la primera vez o esta vacia, cargamos datos de prueba
            if (esPrimeraVez || clinica.listarVeterinarios().isEmpty()) {
                System.out.println("Inicializando sistema por primera vez con datos de prueba.");
                cargarDatosIniciales(clinica);
                gestor.guardar(clinica);
                System.out.println("Datos de prueba guardados con exito!\n");
            } else {
                System.out.println("Base de datos cargada correctamente desde " + archivoDatos + ".\n");
            }

            //Iniciar la ventana principal
            //SwingUtilities.invokeLater(() -> {
                //VentanaPrincipal ventana = new VentanaPrincipal(clinica);
                //ventana.setVisible(true);
            //});

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error en la persistencia del sistema: " + e.getMessage());
            gestor.registrarIncidente("Fallo al iniciar el sistema: " + e.getMessage());
        } finally {
            //Se asegura de guardar los datos al salir
            if (clinica != null) {
                try {
                    gestor.guardar(clinica);
                    System.out.println("\n Todos los datos fueron guardados exitosamente en disco.");
                } catch (IOException e) {
                    System.err.println("No se pudieron guardar los datos finales: " + e.getMessage());
                }
            }
            System.out.println("Gracias por utilizar el sistema de " + nombreClinica + ".");
        }
    }

    private static void cargarDatosIniciales(ClinicaVeterinaria clinica) {
        try {
            // Vet 1, de guardia para urgencias
            Map<DiaSemana, RangoHorario> horariosVet1 = new EnumMap<>(DiaSemana.class);
            RangoHorario jornadaCompleta = new RangoHorario(LocalTime.of(9, 0), LocalTime.of(19, 0));
            horariosVet1.put(DiaSemana.LUNES, jornadaCompleta);
            horariosVet1.put(DiaSemana.MARTES, jornadaCompleta);
            horariosVet1.put(DiaSemana.MIERCOLES, jornadaCompleta);
            horariosVet1.put(DiaSemana.JUEVES, jornadaCompleta);
            horariosVet1.put(DiaSemana.VIERNES, jornadaCompleta);
            horariosVet1.put(DiaSemana.SABADO, new RangoHorario(LocalTime.of(9, 0), LocalTime.of(14, 0)));
            Veterinario vet1 = new Veterinario(
                    "Juan", "Pérez", "32111222", "1144332211",
                    "VET001", "MP-10520", "Clínica General",
                    horariosVet1, true
            );
            clinica.registrarVeterinario(vet1);

            // Vet 2, cirujana
            Map<DiaSemana, RangoHorario> horariosVet2 = new EnumMap<>(DiaSemana.class);
            RangoHorario turnoManana = new RangoHorario(LocalTime.of(8, 0), LocalTime.of(16, 0));
            horariosVet2.put(DiaSemana.LUNES, turnoManana);
            horariosVet2.put(DiaSemana.MIERCOLES, turnoManana);
            horariosVet2.put(DiaSemana.VIERNES, turnoManana);
            Veterinario vet2 = new Veterinario(
                    "Laura", "Gómez", "29888777", "1166778899",
                    "VET002", "MP-20415", "Cirugía y Traumatología",
                    horariosVet2, false
            );
            clinica.registrarVeterinario(vet2);

            // Proveedores y Productos con Lotes
            Proveedor provFarmavet = new Proveedor("30-71122334-9", "Farmavet S.A.", "1145678901", "Av. San Martín 1500");
            Proveedor provInsumos = new Proveedor("30-65432198-4", "Distribuidora Mascotas SRL", "1149876543", "Calle 25 de Mayo 430");
            Producto amoxicilina = new Producto(1, "Amoxicilina 500mg", "Antibiótico de amplio espectro", 3500.0, provFarmavet, 10);
            amoxicilina.agregarLote(new Lote("LOTE-AMOX-01", LocalDate.now().plusMonths(6), 25));
            amoxicilina.agregarLote(new Lote("LOTE-AMOX-02", LocalDate.now().plusMonths(12), 40));
            clinica.agregarProducto(amoxicilina);
            Producto pipeta = new Producto(2, "Pipeta Antipulgas Perro", "Antiparasitario externo 10-20kg", 4200.0, provInsumos, 5);
            pipeta.agregarLote(new Lote("LOTE-PIP-01", LocalDate.now().plusMonths(8), 15));
            clinica.agregarProducto(pipeta);
            Producto vacAntirrabica = new Producto(3, "Vacuna Antirrábica", "Vacuna obligatoria anual", 5500.0, provFarmavet, 5);
            vacAntirrabica.agregarLote(new Lote("LOTE-VAC-01", LocalDate.now().plusMonths(9), 20));
            // Lote vencido para verificar que el sistema no lo tome
            vacAntirrabica.agregarLote(new Lote("LOTE-VAC-VENCIDO", LocalDate.now().minusMonths(1), 10));
            clinica.agregarProducto(vacAntirrabica);

            // Dueños y mascotas
            Dueño d1 = new Dueño("Carlos", "López", "30123456", "1155443322", "carlos.lopez@email.com", true); // Con plan de salud
            clinica.registrarDueño(d1);
            Perro perro1 = new Perro(
                    clinica.proximoIdMascota(), d1, new HistoriaClinica(clinica.proximoIdHistoria()),
                    "Bobby", "Perro", "Labrador", "Macho",
                    LocalDate.now().minusYears(3), 28.5,
                    "CHIP-98451", "Grande"
            );
            clinica.registrarMascota(d1, perro1);
            Dueño d2 = new Dueño("María", "Rodríguez", "28987654", "1122334455", "maria.rod@email.com", false); // Sin plan
            clinica.registrarDueño(d2);
            Gato gato1 = new Gato(
                    clinica.proximoIdMascota(), d2, new HistoriaClinica(clinica.proximoIdHistoria()),
                    "Michi", "Gato", "Siamés", "Hembra",
                    LocalDate.now().minusYears(2), 4.2,
                    true
            );
            clinica.registrarMascota(d2, gato1);

        } catch (RegistroDuplicadoException | RegistroNoEncontradoException e) {
            System.err.println("Aviso al cargar datos iniciales: " + e.getMessage());
        }
    }
}
