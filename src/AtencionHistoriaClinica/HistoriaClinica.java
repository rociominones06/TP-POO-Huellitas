package AtencionHistoriaClinica;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Historia clinica de cada mascota. Almacena las consultas.

public class HistoriaClinica {
        private int idHistoria;
        private LocalDate fechaCreacion;
        private List<Consulta> consultas;

    public HistoriaClinica(int idHistoria) {
        this.idHistoria = idHistoria;
        this.fechaCreacion = LocalDate.now();
        this.consultas = new ArrayList<>();
    }

    public int getIdHistoria() {
        return idHistoria;
    }
    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    // Agregar consulta medica confirmada
    public void agregarConsulta(Consulta consulta) {
        if(consulta != null) {
            this.consultas.add(consulta);
        }
    }

    // Obtener lista inalterable de consultas generadas
    public List<Consulta> obtenerHistorial() {
        return Collections.unmodifiableList(this.consultas);
    }
}
