package PersonasMascota;

import java.util.ArrayList;
import java.util.List;

// Representa al dueño de una o más mascotas.

public class Dueño extends Persona {
    private static final long serialVersionUID = 1L;
    private String correo;
    private List<Mascota> mascotas;
    private boolean esPlanSalud;
    private int cantidadAusenciasSeguidas;

    public Dueño(String nombre, String apellido,
                 String documento, String telefono,
                 String correo, boolean esPlanSalud) {

        super(nombre, apellido, documento, telefono);

        this.correo = correo;
        this.esPlanSalud = esPlanSalud;
        this.mascotas = new ArrayList<Mascota>();
        this.cantidadAusenciasSeguidas = 0;
    }
    
    public String getCorreo() {
        return correo;
    }

    public List<Mascota> getMascotas() {
        return mascotas;
    }
    
    public void agregarMascota(Mascota mascota) {
        mascotas.add(mascota);
    }

    public void transferirMascota(Mascota mascota, Dueño nuevoDueño) {
        if (mascotas.contains(mascota)) {
            mascotas.remove(mascota);
            nuevoDueño.agregarMascota(mascota);
        }
    }

    public void registrarAusencia() {
        cantidadAusenciasSeguidas++;
    }

    public void resetearAusencias() {
        cantidadAusenciasSeguidas = 0;
    }

    public boolean debePagarSeña() {
        return cantidadAusenciasSeguidas >= 3;
    }

    public boolean tieneDescuentoPlan() {
        return esPlanSalud;
    }
}