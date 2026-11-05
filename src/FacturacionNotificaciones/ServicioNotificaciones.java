package FacturacionNotificaciones;

import Excepciones.ReglaNegocioException;
import java.io.Serializable;

// Abstracción para avisar a los dueños (principio DIP): la lógica de la clínica
// depende de esta interfaz y no de un medio concreto (email, SMS, WhatsApp...).

public interface ServicioNotificaciones extends Serializable {
    void enviarMensaje(String destinatario, String asunto, String mensaje) throws ReglaNegocioException;
    //Lanza ReglaNegocioException si el mensaje no pudo enviarse
}