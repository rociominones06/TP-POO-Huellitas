package PersonasMascota;

import java.time.LocalDate;
import java.time.Period;
import AtencionHistoriaClinica.HistoriaClinica;

public abstract class Mascota {

    private int idMascota;
    private Dueño dueño;
    private HistoriaClinica historiaClinica;
    private String Nombre;
    private String Especie;
    private String Raza;
    private String Sexo;
    private LocalDate fechaNacimiento;
    private double pesoActual;

    public Mascota(int idMascota, Dueño dueño, HistoriaClinica historiaClinica, String Nombre, String Especie, String Raza, String Sexo, LocalDate fechaNacimiento, double pesoActual) {

        this.idMascota = idMascota;
        this.dueño = dueño;
        this.historiaClinica = historiaClinica;
        this.Nombre = Nombre;
        this.Especie = Especie;
        this.Raza = Raza;
        this.Sexo = Sexo;
        this.fechaNacimiento = fechaNacimiento;
        this.pesoActual = pesoActual;
    }

    public abstract String obtenerFichaTecnica();

    public void actualizarPeso(double nuevoPeso) {
        pesoActual = nuevoPeso;
    }

    public int calcularEdadMeses() {
        return Period.between(fechaNacimiento, LocalDate.now()).getYears() * 12
                + Period.between(fechaNacimiento, LocalDate.now()).getMonths();
    }

    public HistoriaClinica getHistoriaClinica() {
        return historiaClinica;
    }

    public int getIdMascota() {
        return idMascota;
    }

    public Dueño getDueño() {
        return dueño;
    }

    public String getNombre() {
        return Nombre;
    }

    public String getEspecie() {
        return Especie;
    }

    public String getRaza() {
        return Raza;
    }

    public String getSexo() {
        return Sexo;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public double getPesoActual() {
        return pesoActual;
    }

    public void setDueño(Dueño dueño) {
        this.dueño = dueño;
    }
}