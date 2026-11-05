package PersonasMascota;
import java.io.Serializable;

public abstract class Persona implements Serializable {
    private static final long serialVersionUID = 1L;
    private String Nombre;
    private String Apellido;
    private String Documento;
    private String Telefono;

    public Persona(String Nombre, String Apellido, String Documento, String Telefono) {
        this.Nombre = Nombre;
        this.Apellido = Apellido;
        this.Documento = Documento;
        this.Telefono = Telefono;
    }

    public String obtenerNombreCompleto() {
        return Nombre + " " + Apellido;
    }

    public String getDocumento() {
        return Documento;
    }
}