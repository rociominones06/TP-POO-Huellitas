package PersonasMascota;

import AtencionHistoriaClinica.HistoriaClinica;
import java.time.LocalDate;

// Subclase específica para gatos.

public class Gato extends Mascota {
    private static final long serialVersionUID = 1L;
    private boolean viveEnInterior;

    public Gato(int idMascota, Dueño dueño, HistoriaClinica historiaClinica, String nombre, String especie, String raza, String sexo, LocalDate fechaNacimiento, double pesoActual, boolean viveEnInterior) {

        super(idMascota, dueño, historiaClinica, nombre, especie, raza, sexo, fechaNacimiento, pesoActual);

        this.viveEnInterior = viveEnInterior;
    }

    public boolean isViveEnInterior() {
        return viveEnInterior;
    }

    public void setViveEnInterior(boolean viveEnInterior) {
        this.viveEnInterior = viveEnInterior;
    }

    @Override
    public String obtenerFichaTecnica() {
        String ficha = "Nombre: " + getNombre()
                + "\nEspecie: " + getEspecie()
                + "\nRaza: " + getRaza()
                + "\nSexo: " + getSexo()
                + "\nPeso actual: " + getPesoActual()
                + "\nVive en interior: ";

        if (viveEnInterior) {
            ficha = ficha + "Sí";
        } else {
            ficha = ficha + "No";
        }
        return ficha;
    }
}