package FacturacionNotificaciones;
import Excepciones.ReglaNegocioException;
import java.io.Serializable;

// Implementacion del servicio de notificaciones por correo electronico.

public class NotificadorEmail implements ServicioNotificaciones, Serializable {
    private static final long serialVersionUID = 1L;
    private String emailOriginal;

    public NotificadorEmail(String emailOriginal) {
        this.emailOriginal = emailOriginal;
    }

    public String getEmailOriginal() {
        return emailOriginal;
    }

    @Override
    public void enviarMensaje(String destinatario, String asunto, String mensaje) throws ReglaNegocioException {
        if (destinatario == null || destinatario.trim().isEmpty() || !destinatario.contains("@")) {
            throw new ReglaNegocioException("Direccion de correo electronico no valida para el destinatario: "+destinatario);
        }
        System.out.println("--------------------------------------------------");
        System.out.println("[NOTIFICACION POR EMAIL]");
        System.out.println("De: " + emailOriginal);
        System.out.println("Para: " + destinatario);
        System.out.println("Asunto: " + asunto);
        System.out.println("Mensaje: " + mensaje);
        System.out.println("--------------------------------------------------");
    }
}
