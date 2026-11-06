
package VistaMain;

import java.awt.*;
import javax.swing.*;

import GeneralPersistencia.ClinicaVeterinaria;
import PersonasMascota.Dueño;

public class PanelInicio extends JPanel {

    private final Color VERDE_OSCURO = new Color(54, 83, 70);
    private final Color FONDO = new Color(247, 248, 244);
    private final Color BLANCO = Color.WHITE;

    private ClinicaVeterinaria clinica;

    private JLabel valorDueños;
    private JLabel valorMascotas;
    private JLabel valorVeterinarios;
    private JLabel valorProductos;

    public PanelInicio(ClinicaVeterinaria clinica) {

        this.clinica = clinica;

        setLayout(new BorderLayout(20, 20));
        setBackground(FONDO);
        setBorder(BorderFactory.createEmptyBorder(30, 35, 30, 35));

        // Encabezado
        JLabel titulo = new JLabel("¡Bienvenido a Huellitas!");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        titulo.setForeground(VERDE_OSCURO);

        JLabel descripcion = new JLabel(
                "Panel de administración de la clínica veterinaria"
        );
        descripcion.setFont(new Font("SansSerif", Font.PLAIN, 15));
        descripcion.setForeground(Color.GRAY);

        JPanel encabezado = new JPanel();
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setBackground(FONDO);

        encabezado.add(titulo);
        encabezado.add(Box.createVerticalStrut(8));
        encabezado.add(descripcion);

        add(encabezado, BorderLayout.NORTH);

        // Tarjetas de información
        JPanel tarjetas = new JPanel(new GridLayout(2, 2, 20, 20));
        tarjetas.setBackground(FONDO);

        valorDueños = new JLabel("0");
        valorMascotas = new JLabel("0");
        valorVeterinarios = new JLabel("0");
        valorProductos = new JLabel("0");

        tarjetas.add(crearTarjeta("Dueños registrados", valorDueños));
        tarjetas.add(crearTarjeta("Mascotas registradas", valorMascotas));
        tarjetas.add(crearTarjeta("Veterinarios", valorVeterinarios));
        tarjetas.add(crearTarjeta("Productos en inventario", valorProductos));

        add(tarjetas, BorderLayout.CENTER);

        // Mensaje inferior
        JLabel mensaje = new JLabel(
                "Cuidamos a quienes más querés."
        );

        mensaje.setFont(new Font("SansSerif", Font.ITALIC, 16));
        mensaje.setForeground(VERDE_OSCURO);

        add(mensaje, BorderLayout.SOUTH);

        actualizarIndicadores();
    }

    // Construye cada tarjeta
    private JPanel crearTarjeta(String titulo, JLabel etiquetaValor) {

        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));

        tarjeta.setBackground(BLANCO);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 222)),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        JLabel etiquetaTitulo = new JLabel(titulo);
        etiquetaTitulo.setFont(new Font("SansSerif", Font.PLAIN, 16));
        etiquetaTitulo.setForeground(Color.GRAY);

        etiquetaValor.setFont(new Font("SansSerif", Font.BOLD, 35));
        etiquetaValor.setForeground(VERDE_OSCURO);

        tarjeta.add(etiquetaTitulo);
        tarjeta.add(Box.createVerticalStrut(15));
        tarjeta.add(etiquetaValor);

        return tarjeta;
    }

    // Actualiza las cantidades según los datos de la clínica
    public void actualizarIndicadores() {

        int cantidadDueños = clinica.listarDueños().size();
        int cantidadMascotas = 0;

        for (Dueño dueño : clinica.listarDueños()) {
            cantidadMascotas += dueño.getMascotas().size();
        }

        int cantidadVeterinarios = clinica.listarVeterinarios().size();
        int cantidadProductos = clinica.getCatalogoProductos().size();

        valorDueños.setText(String.valueOf(cantidadDueños));
        valorMascotas.setText(String.valueOf(cantidadMascotas));
        valorVeterinarios.setText(String.valueOf(cantidadVeterinarios));
        valorProductos.setText(String.valueOf(cantidadProductos));
    }
}
