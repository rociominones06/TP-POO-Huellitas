package VistaMain;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import GeneralPersistencia.ClinicaVeterinaria;
import GeneralPersistencia.GestorArchivos;
import java.io.IOException;

//Esta será la ventana principal de la veterinaria.
// Va a contener el menú lateral y un espacio central donde iremos mostrando cada módulo.
public class VentanaPrincipal extends JFrame {
    // Colores generales de la aplicación
    private final Color VERDE_OSCURO = new Color(54, 83, 70);
    private final Color VERDE_CLARO = new Color(225, 237, 226);
    private final Color FONDO = new Color(247, 248, 244);
    private final Color BLANCO = Color.WHITE;

    // Referencia a la clínica y al gestor de persistencia
    private ClinicaVeterinaria clinica;
    private GestorArchivos gestor;

    // Panel central que permite cambiar de pantalla
    private JPanel panelContenido;
    private CardLayout administradorPantallas;
    private PanelInicio panelInicio;

    public VentanaPrincipal(ClinicaVeterinaria clinica, GestorArchivos gestor) {
        this.clinica = clinica;
        this.gestor = gestor;

        // Configuración general de la ventana
        setTitle("Huellitas | Gestión Veterinaria");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        // Creamos el diseño principal
        inicializarComponentes();

        // Guardamos los datos cuando se cierra la ventana
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarSistema();
            }
        });
    }

    private void inicializarComponentes() {

        setLayout(new BorderLayout());

        // Menú lateral
        add(crearMenuLateral(), BorderLayout.WEST);

        // Panel central con CardLayout
        administradorPantallas = new CardLayout();
        panelContenido = new JPanel(administradorPantallas);
        panelContenido.setBackground(FONDO);

        // Pantalla inicial
        panelInicio = new PanelInicio(clinica);
        panelContenido.add(
                panelInicio,
                "inicio"
        );

        // Abrimos el módulo real de dueños y mascotas
        panelContenido.add(
                new PanelDuenosMascotas(clinica),
                "mascotas"
        );

        // Módulo real de gestión de turnos
        panelContenido.add(
                new PanelTurnos(clinica),
                "turnos"
        );

        // Módulo real de historias clínicas
        panelContenido.add(
                new PanelHistoriasClinicas(clinica),
                "historias"
        );

        panelContenido.add(
                new PanelInventario(clinica),
                "inventario"
        );

        panelContenido.add(
                new PanelFacturacion(clinica),
                "facturacion"
        );

        add(panelContenido, BorderLayout.CENTER);

        // Mostramos el inicio al abrir el sistema
        administradorPantallas.show(panelContenido, "inicio");
    }

    private JPanel crearMenuLateral() {

        JPanel menu = new JPanel();
        menu.setPreferredSize(new Dimension(240, 0));
        menu.setBackground(VERDE_OSCURO);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(BorderFactory.createEmptyBorder(25, 15, 20, 15));

        // Nombre de la veterinaria
        JLabel titulo = new JLabel("HUELLITAS");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 25));
        titulo.setForeground(BLANCO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel("Gestión Veterinaria");
        subtitulo.setForeground(VERDE_CLARO);
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        menu.add(titulo);
        menu.add(Box.createVerticalStrut(5));
        menu.add(subtitulo);
        menu.add(Box.createVerticalStrut(40));

        // Botones de navegación
        agregarBoton(menu, "Inicio", "inicio");
        agregarBoton(menu, "Dueños y mascotas", "mascotas");
        agregarBoton(menu, "Gestión de turnos", "turnos");
        agregarBoton(menu, "Historias clínicas", "historias");
        agregarBoton(menu, "Inventario y proveedores", "inventario");
        agregarBoton(menu, "Facturación", "facturacion");

        menu.add(Box.createVerticalGlue());

        JLabel pie = new JLabel("Sistema Huellitas © 2026");
        pie.setForeground(VERDE_CLARO);
        pie.setFont(new Font("SansSerif", Font.PLAIN, 11));

        menu.add(pie);

        return menu;
    }

    // Crea un botón y lo conecta con la pantalla correspondiente
    private void agregarBoton(JPanel menu, String texto, String pantalla) {

        JButton boton = new JButton(texto);

        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        boton.setHorizontalAlignment(SwingConstants.LEFT);

        boton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        boton.setForeground(BLANCO);
        boton.setBackground(VERDE_OSCURO);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 10));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        boton.addActionListener(e -> {
            if ("inicio".equals(pantalla) && panelInicio != null) {
                panelInicio.actualizarIndicadores(); // Refresca los números de las tarjetas
            }
            administradorPantallas.show(panelContenido, pantalla);
        });

        menu.add(boton);
        menu.add(Box.createVerticalStrut(8));
    }

    // Guarda la clínica antes de cerrar la aplicación
    private void cerrarSistema() {

        try {
            gestor.guardar(clinica);

            JOptionPane.showMessageDialog(
                    this,
                    "Los datos se guardaron correctamente.",
                    "Sistema Huellitas",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudieron guardar los datos: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}


