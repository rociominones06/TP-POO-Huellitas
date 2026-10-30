package PersonasMascota;

import java.time.LocalDate;
import AtencionHistoriaClinica.HistoriaClinica;

public class Conejo extends Mascota {

    private boolean requiereDietaEspecial;

    public Conejo(int idMascota, Dueño dueño, HistoriaClinica historiaClinica, String nombre, String especie, String raza, String sexo, LocalDate fechaNacimiento, double pesoActual, boolean requiereDietaEspecial) {

        super(idMascota, dueño,  historiaClinica , nombre, especie, raza, sexo, fechaNacimiento, pesoActual);

        this.requiereDietaEspecial = requiereDietaEspecial;
    }

    public boolean isRequiereDietaEspecial() {
        return requiereDietaEspecial;
    }

    public void setRequiereDietaEspecial(boolean requiereDietaEspecial) {
        this.requiereDietaEspecial = requiereDietaEspecial;
    }

    @Override
    public String obtenerFichaTecnica() {
        String ficha = "Nombre: " + getNombre()
                + "\nEspecie: " + getEspecie()
                + "\nRaza: " + getRaza()
                + "\nSexo: " + getSexo()
                + "\nPeso actual: " + getPesoActual()
                + "\nRequiere dieta especial: ";

        if (requiereDietaEspecial) {
            ficha = ficha + "Sí";
        } else {
            ficha = ficha + "No";
        }
        return ficha;
    }
}
