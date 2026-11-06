package VistaMain;

//Esta clase nos servirá como pantalla provisional para los módulos
// que todavía no desarrollamos visualmente.

import javax.swing.*;
import java.awt.*;

public class PanelModulo extends JPanel {
    public PanelModulo(String nombreModulo) {

        setLayout(new BorderLayout());
        setBackground(new Color(247, 248, 244));

        // Título del módulo
        JLabel titulo = new JLabel(nombreModulo);

        titulo.setFont(new Font("SansSerif", Font.BOLD, 27));
        titulo.setForeground(new Color(54, 83, 70));

        titulo.setBorder(
                BorderFactory.createEmptyBorder(30, 35, 20, 20)
        );

        add(titulo, BorderLayout.NORTH);

        // Mensaje provisional
        JLabel mensaje = new JLabel(
                "Aquí se desarrollará el módulo de " + nombreModulo,
                SwingConstants.CENTER
        );

        mensaje.setFont(new Font("SansSerif", Font.PLAIN, 17));
        mensaje.setForeground(Color.GRAY);

        add(mensaje, BorderLayout.CENTER);
    }
}
