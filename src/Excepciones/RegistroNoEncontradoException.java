package Excepciones;

public class RegistroNoEncontradoException extends Exception {
    private static final long serialVersionUID = 1L;

    public RegistroNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
