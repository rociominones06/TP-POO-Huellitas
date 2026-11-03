package Excepciones;

public class RegistroDuplicadoException extends Exception {
        private static final long serialVersionUID = 1L;

        public RegistroDuplicadoException(String mensaje) {
            super(mensaje);
        }
    }