package Excepciones;

// Excepcion personalizada para representar violaciones a las reglas de negocio

public class ReglaNegocioException extends Exception {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
