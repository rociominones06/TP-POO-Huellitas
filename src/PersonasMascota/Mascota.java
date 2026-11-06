package PersonasMascota;

import java.time.LocalDate;
import java.time.Period;
import java.io.Serializable;
import AtencionHistoriaClinica.HistoriaClinica;

public abstract class Mascota implements Serializable {
    private static final long serialVersionUID = 1L;
    private int idMascota;
    private Dueño dueño;
    private HistoriaClinica historiaClinica;
    private String nombre;
    private String especie;
    private String raza;
    private String sexo;
    private LocalDate fechaNacimiento;
    private double pesoActual;

    public Mascota(int idMascota, Dueño dueño, HistoriaClinica historiaClinica, String nombre, String especie, String raza, String sexo, LocalDate fechaNacimiento, double pesoActual) {

        this.idMascota = idMascota;
        this.dueño = dueño;
        this.historiaClinica = historiaClinica;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.sexo = sexo;
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
        return nombre;
    }

    public String getEspecie() {
        return especie;
    }

    public String getRaza() {
        return raza;
    }

    public String getSexo() {
        return sexo;
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

    @Override
    public String toString() {
        return nombre + " (" + especie + ")";
    }
}