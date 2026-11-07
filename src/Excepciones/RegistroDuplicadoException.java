package Excepciones;

// Excepción lanzada al intentar dar de alta un elemento con clave o id ya existente.

public class RegistroDuplicadoException extends Exception {
        private static final long serialVersionUID = 1L;

        public RegistroDuplicadoException(String mensaje) {
            super(mensaje);
        }
    }