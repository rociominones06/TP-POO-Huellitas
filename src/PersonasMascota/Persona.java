package PersonasMascota;
import java.io.Serializable;

// Representa a una persona con datos de contacto básicos.

public abstract class Persona implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nombre;
    private String apellido;
    private String documento;
    private String telefono;

    public Persona(String nombre, String apellido, String documento, String telefono) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
        this.telefono = telefono;
    }

    public String obtenerNombreCompleto() {
        return nombre + " " + apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getDocumento() {
        return documento;
    }

    public String getTelefono() {
        return telefono;
    }
}