package FacturacionNotificaciones;

import Excepciones.ReglaNegocioException;
import java.io.Serializable;

// Abstracción para avisar a los dueños (principio DIP): la lógica de la clínica
// depende de esta interfaz y no de un medio concreto (email, SMS, WhatsApp...).

public interface ServicioNotificaciones implements Serializable {
    private static final long serialVersionUID = 1L;
    /** @throws ReglaNegocioException si el mensaje no pudo enviarse (sin contacto o contacto inválido) */
    void enviarMensaje(String destinatario, String asunto, String mensaje) throws ReglaNegocioException;
}