package GestionTurnos;

import java.time.LocalDateTime;
import java.io.Serializable;
import PersonasMascota.Mascota;
import PersonasMascota.Veterinario;

// Representa una cita médica: vincula mascota, veterinario, fecha, horario y estado.

public class Turno implements Serializable {
    private static final long serialVersionUID = 1L;
    private int idTurno;
    private Mascota mascota;
    private Veterinario veterinario;
    private TipoAtencion tipoAtencion;
    private LocalDateTime fechaHora;
    private EstadoTurno estado;
    private boolean esUrgencia;
    private boolean requiereSeña;
    private double señaAbonada;

    public enum EstadoTurno {
        PROGRAMADO,
        ATENDIDO,
        CANCELADO,
        AUSENTE
    }

    public Turno(int idTurno, Mascota mascota,
                 Veterinario veterinario, TipoAtencion tipoAtencion,
                 LocalDateTime fechaHora, boolean esUrgencia,
                 boolean requiereSeña) {

        this.idTurno = idTurno;
        this.mascota = mascota;
        this.veterinario = veterinario;
        this.tipoAtencion = tipoAtencion;
        this.fechaHora = fechaHora;
        this.estado = EstadoTurno.PROGRAMADO;
        this.esUrgencia = esUrgencia;
        this.requiereSeña = requiereSeña;
        this.señaAbonada = 0;
    }

    public int calcularDuracion() {
        return tipoAtencion.getDuracionMinutos();
    }

    public void cancelar() {
        this.estado = EstadoTurno.CANCELADO;
    }

    public void reprogramar(LocalDateTime nuevaFecha) {
        fechaHora = nuevaFecha;
    }

    public void marcarAtendido() {
        estado = EstadoTurno.ATENDIDO;
    }

    public void marcarAusente() {
        estado = EstadoTurno.AUSENTE;
    }

    public void registrarPagoSeña(double monto) {
        señaAbonada += monto;
    }

    public void desplazarPorUrgencia(LocalDateTime nuevaFechaHora) {
        fechaHora = nuevaFechaHora;
    }

    public int getIdTurno() {
        return idTurno;
    }

    public Mascota getMascota() {
        return mascota;
    }

    public Veterinario getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(Veterinario veterinario) {
        this.veterinario = veterinario;
    }

    public TipoAtencion getTipoAtencion() {
        return tipoAtencion;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public EstadoTurno getEstado() {
        return estado;
    }

    public boolean isEsUrgencia() {
        return esUrgencia;
    }

    public void setEsUrgencia(boolean esUrgencia) {
        this.esUrgencia = esUrgencia;
    }

    public boolean isRequiereSeña() {
        return requiereSeña;
    }

    public double getSeñaAbonada() {
        return señaAbonada;
    }
}