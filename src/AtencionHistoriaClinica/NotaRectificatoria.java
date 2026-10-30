package AtencionHistoriaClinica;
import java.time.LocalDateTime;

// Nota medica firmada por un profesional. Permite corregir errores de la HC.

public class NotaRectificatoria {
    private int idNota;
    private LocalDateTime fecha;
    private String motivo;
    private String textoCorreccion;
    private String firmaProfesional;

    public NotaRectificatoria(int idNota, String motivo, String textoCorreccion, String firmaProfesional) {
        this.idNota = idNota;
        this.fecha = LocalDateTime.now();
        this.motivo = motivo;
        this.textoCorreccion = textoCorreccion;
        this.firmaProfesional = firmaProfesional;
    }

    public int getIdNota() {
        return idNota;
    }
    public LocalDateTime getFecha() {
        return fecha;
    }
    public String getMotivo() {
        return motivo;
    }
    public String getTextoCorreccion() {
        return textoCorreccion;
    }
    public String getFirmaProfesional() {
        return firmaProfesional;
    }

    // Retornar detalle
    public String getDetalleRectificacion() {
        return "["+fecha+"] Rectificacion #"+ idNota +
                " - Motivo: " + motivo +
                " - Correccion: " + textoCorreccion +
                " - Firmado por: " +  firmaProfesional;
    }
}
