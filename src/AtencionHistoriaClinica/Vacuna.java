package AtencionHistoriaClinica;
import java.util.List;

// Ficha que define propiedades de cada vacuna

public class Vacuna {
    private int idVacuna;
    private String nombre;
    private List<String> especiesAplicables;
    private int edadMinimaMeses;
    private int intervaloRefuerzoDias;

    public Vacuna(int idVacuna, String nombre, List<String> especiesAplicables, int edadMinimaMeses, int intervaloRefuerzoDias) {
        this.idVacuna = idVacuna;
        this.nombre = nombre;
        this.especiesAplicables = especiesAplicables;
        this.edadMinimaMeses = edadMinimaMeses;
        this.intervaloRefuerzoDias = intervaloRefuerzoDias;
    }

    public int getIdVacuna() {
        return idVacuna;
    }
    public String getNombre() {
        return nombre;
    }
    public int getEdadMinimaMeses() {
        return edadMinimaMeses;
    }
    public List<String> getEspeciesAplicables() {
        return especiesAplicables;
    }
    public int getIntervaloRefuerzoDias() {
        return intervaloRefuerzoDias;
    }

    // Verificar si la vacuna es adecuada para la especie
    public boolean esAptaParaEspecie(String especie) {
        if (especie == null || especiesAplicables == null) return false;
        for (String e : especiesAplicables) {
            if (e.equalsIgnoreCase(especie)) {
                return true;
            }
        }
        return false;
    }

    // Validar que la mascota cumpla con la edad minima
    public boolean puedeAplicarse(int edadMeses) {
        return edadMeses >= this.edadMinimaMeses;
    }
}
