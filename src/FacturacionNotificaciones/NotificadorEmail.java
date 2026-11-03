package FacturacionNotificaciones;

import Excepciones.ReglaNegocioException;

// Implementación por correo electrónico de ServicioNotificaciones.
// El envío está SIMULADO por consola; para un envío real, reemplazar el cuerpo
// de enviarMensaje por una librería SMTP (por ejemplo, Jakarta Mail).

public class NotificadorEmail implements ServicioNotificaciones {
    private static final String PATRON_EMAIL = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";

    private final String emailOriginal; // casilla desde la que sale el mensaje

    public NotificadorEmail(String emailOriginal) {
        if (emailOriginal == null || !emailOriginal.matches(PATRON_EMAIL)) {
            throw new IllegalArgumentException("El correo remitente no es válido.");
        }
        this.emailOriginal = emailOriginal;
    }

    @Override
    public void enviarMensaje(String destinatario, String asunto, String mensaje) throws ReglaNegocioException {
        if (destinatario == null || destinatario.isBlank()) {
            throw new ReglaNegocioException("El dueño no tiene un correo registrado.");
        }
        if (!destinatario.matches(PATRON_EMAIL)) {
            throw new ReglaNegocioException("El correo del dueño no es válido: " + destinatario);
        }
        System.out.println("[EMAIL] De: " + emailOriginal + " | Para: " + destinatario
                + " | Asunto: " + asunto + System.lineSeparator() + mensaje);
    }

    public String getEmailOriginal() { return emailOriginal; }
}