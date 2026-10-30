package PersonasMascota;

import java.time.LocalDate;
import AtencionHistoriaClinica.HistoriaClinica;

public class Ave extends Mascota {

    private boolean puedeVolar;

    public Ave(int idMascota, Dueño dueño,HistoriaClinica historiaClinica, String nombre, String especie, String raza, String sexo, LocalDate fechaNacimiento, double pesoActual, boolean puedeVolar) {

        super(idMascota, dueño, historiaClinica, nombre, especie, raza, sexo, fechaNacimiento, pesoActual);

        this.puedeVolar = puedeVolar;
    }

    public boolean puedeVolar() {
        return puedeVolar;
    }

    public void setPuedeVolar(boolean puedeVolar) {
        this.puedeVolar = puedeVolar;
    }

    @Override
    public String obtenerFichaTecnica() {
        String ficha = "Nombre: " + getNombre()
                + "\nEspecie: " + getEspecie()
                + "\nRaza: " + getRaza()
                + "\nSexo: " + getSexo()
                + "\nPeso actual: " + getPesoActual()
                + "\nPuede volar: ";

        if (puedeVolar) {
            ficha = ficha + "Sí";
        } else {
            ficha = ficha + "No";
        }
        return ficha;
    }
}
