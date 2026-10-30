package PersonasMascota;

import AtencionHistoriaClinica.HistoriaClinica;
import java.time.LocalDate;

public class Perro extends Mascota {

    private String numeroChip;
    private String tamaño;

    public Perro(int idMascota, Dueño dueño, HistoriaClinica historiaClinica, String nombre, String especie, String raza, String sexo, LocalDate fechaNacimiento, double pesoActual, String numeroChip, String tamaño) {

        super(idMascota, dueño, historiaClinica, nombre, especie, raza, sexo, fechaNacimiento, pesoActual);

        this.numeroChip = numeroChip;
        this.tamaño = tamaño;
    }

    public String getNumeroChip() {
        return numeroChip;
    }

    public void setNumeroChip(String numeroChip) {
        this.numeroChip = numeroChip;
    }

    public String getTamaño() {
        return tamaño;
    }

    public void setTamaño(String tamaño) {
        this.tamaño = tamaño;
    }

    @Override
    public String obtenerFichaTecnica() {
        String ficha = "Nombre: " + getNombre()
                + "\nEspecie: " + getEspecie()
                + "\nRaza: " + getRaza()
                + "\nSexo: " + getSexo()
                + "\nPeso actual: " + getPesoActual()
                + "\nNúmero de chip: " + numeroChip
                + "\nTamaño: " + tamaño;
        return ficha;
    }
}