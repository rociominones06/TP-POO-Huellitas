package GestionTurnos;

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