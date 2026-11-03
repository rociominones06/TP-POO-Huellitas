package GeneralPersistencia;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import FacturacionNotificaciones.ServicioNotificaciones;

// Fabricacion pura (GRASP): se ocupa solo de la entrada/salida de archivos, para que
// las clases del negocio no sepan nada de como se guardan los datos.
// - Datos de la clinica: un unico archivo binario (serializacion de ClinicaVeterinaria).
// - Incidentes (mails que no se pudieron enviar, archivo danado, etc.): log de texto.

public class GestorArchivos {

    private final Path archivoDatos;
    private final Path archivoIncidentes;

    public GestorArchivos(Path archivoDatos, Path archivoIncidentes) {
        if (archivoDatos == null || archivoIncidentes == null) {
            throw new IllegalArgumentException("Las rutas de los archivos son obligatorias.");
        }
        this.archivoDatos = archivoDatos;
        this.archivoIncidentes = archivoIncidentes;
    }

    // Datos de la clinica

    // Escribe primero en un archivo temporal y despues lo mueve al definitivo:
    // si el programa se corta a mitad de la escritura, el archivo anterior queda intacto.
    public void guardar(ClinicaVeterinaria clinica) throws IOException {
        if (clinica == null) {
            throw new IllegalArgumentException("No hay clinica para guardar.");
        }
        crearCarpetaDe(archivoDatos);
        Path temporal = archivoDatos.resolveSibling(archivoDatos.getFileName() + ".tmp");
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(temporal))) {
            out.writeObject(clinica);
        }
        try {
            Files.move(temporal, archivoDatos,
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporal, archivoDatos, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    // Primera ejecucion (no hay archivo): devuelve una clinica nueva y vacia.
    // Si el archivo existe pero no se puede leer (por ejemplo, cambiaron las clases
    // despues de guardarlo), lo aparta como .corrupto, deja constancia y avisa.
    public ClinicaVeterinaria cargarOCrear(String nombreClinica, ServicioNotificaciones notificaciones)
            throws IOException, ClassNotFoundException {
        if (!Files.exists(archivoDatos)) {
            return new ClinicaVeterinaria(nombreClinica, notificaciones);
        }
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(archivoDatos))) {
            return (ClinicaVeterinaria) in.readObject();
        } catch (ObjectStreamException e) { // archivo daniado o version de clases distinta
            Path respaldo = archivoDatos.resolveSibling(archivoDatos.getFileName() + ".corrupto");
            Files.move(archivoDatos, respaldo, StandardCopyOption.REPLACE_EXISTING);
            registrarIncidente("No se pudo leer " + archivoDatos + " (" + e + "). Se guardo como " + respaldo);
            throw new IOException("El archivo de datos estaba danado y se aparto como " + respaldo.getFileName(), e);
        }
    }

    public boolean existenDatos() {
        return Files.exists(archivoDatos);
    }

    // Log de incidentes (texto)

    // Nunca lanza excepcion: un fallo al escribir el log no debe cortar la operacion principal.
    public void registrarIncidente(String detalle) {
        String linea = LocalDateTime.now() + " | " + detalle + System.lineSeparator();
        try {
            crearCarpetaDe(archivoIncidentes);
            Files.writeString(archivoIncidentes, linea, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("No se pudo escribir el log de incidentes: " + e.getMessage());
        }
    }

    public List<String> leerIncidentes() throws IOException {
        if (!Files.exists(archivoIncidentes)) {
            return Collections.emptyList();
        }
        return Files.readAllLines(archivoIncidentes, StandardCharsets.UTF_8);
    }

    private static void crearCarpetaDe(Path archivo) throws IOException {
        Path carpeta = archivo.toAbsolutePath().getParent();
        if (carpeta != null) {
            Files.createDirectories(carpeta);
        }
    }
}