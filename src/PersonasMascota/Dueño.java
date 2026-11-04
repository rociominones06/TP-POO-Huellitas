package PersonasMascota;

import java.util.ArrayList;
import java.util.List;

public class Dueño extends Persona {

    private String correo;
    private List<Mascota> mascotas;
    private boolean esPlanSalud;
    private int cantidadAusenciasSeguidas;

    public Dueño(String Nombre, String Apellido,
                 String Documento, String Telefono,
                 String correo, boolean esPlanSalud) {

        super(Nombre, Apellido, Documento, Telefono);

        this.correo = correo;
        this.esPlanSalud = esPlanSalud;
        this.mascotas = new ArrayList<Mascota>();
        this.cantidadAusenciasSeguidas = 0;
    }
    
    public String getCorreo() {
        return correo;
    }

    public String getDocumento() {
        return super.getDocumento();
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
        return cantidadAusenciasSeguidas >= 2;
    }

    public boolean tieneDescuentoPlan() {
        return esPlanSalud;
    }
}