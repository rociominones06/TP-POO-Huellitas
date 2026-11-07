package Excepciones;

// Excepción lanzada al buscar una entidad o registro inexistente en la clínica.

public class RegistroNoEncontradoException extends Exception {
    private static final long serialVersionUID = 1L;

    public RegistroNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
