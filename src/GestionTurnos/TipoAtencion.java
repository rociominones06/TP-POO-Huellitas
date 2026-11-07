package GestionTurnos;

// Enumerador que define los tipos de consulta y su duración.

public enum TipoAtencion {

    ConsultaGeneral(30),
    Vacunacion(15),
    Cirugia(120);

    private int duracionMinutos;

    TipoAtencion(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }
}