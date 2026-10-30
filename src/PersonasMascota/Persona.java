package PersonasMascota;

public abstract class Persona {

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
}