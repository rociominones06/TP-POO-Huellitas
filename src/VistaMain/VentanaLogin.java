package VistaMain;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

import GeneralPersistencia.ClinicaVeterinaria;
import GeneralPersistencia.GestorArchivos;

// Ventana de inicio de sesión
public class VentanaLogin extends JFrame {

    private ClinicaVeterinaria clinica;
    private GestorArchivos gestor;

    // Colores del sistema
    private final Color VERDE_OSCURO = new Color(54, 83, 70);
    private final Color FONDO = new Color(247, 248, 244);
    private final Color BLANCO = Color.WHITE;

    // Componentes del formulario
    private JTextField campoUsuario;
    private JPasswordField campoPassword;

    // Estructura en memoria: usuario -> [contraseña, rol]
    private static final Map<String, String[]> USUARIOS = new HashMap<>();

    static {
        // Usuarios de prueba según los actores del TP
        USUARIOS.put("recepcion", new String[]{"1234", "Recepcionista"});
        USUARIOS.put("vet", new String[]{"1234", "Veterinario"});
        USUARIOS.put("admin", new String[]{"1234", "Administrador"});
    }

    public VentanaLogin(ClinicaVeterinaria clinica, GestorArchivos gestor) {
        this.clinica = clinica;
        this.gestor = gestor;

        setTitle("Huellitas | Iniciar Sesión");
        setSize(420, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(FONDO);

        // Encabezado
        JPanel panelEncabezado = new JPanel();
        panelEncabezado.setBackground(VERDE_OSCURO);
        panelEncabezado.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));
        panelEncabezado.setLayout(new BoxLayout(panelEncabezado, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("HUELLITAS");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        titulo.setForeground(BLANCO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Acceso al Sistema");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitulo.setForeground(new Color(225, 237, 226));
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelEncabezado.add(titulo);
        panelEncabezado.add(Box.createVerticalStrut(5));
        panelEncabezado.add(subtitulo);

        panelPrincipal.add(panelEncabezado, BorderLayout.NORTH);

        // Formulario central
        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBackground(FONDO);
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));

        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblUsuario.setForeground(VERDE_OSCURO);
        lblUsuario.setAlignmentX(Component.LEFT_ALIGNMENT);

        campoUsuario = new JTextField();
        campoUsuario.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        campoUsuario.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblPassword.setForeground(VERDE_OSCURO);
        lblPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        campoPassword = new JPasswordField();
        campoPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        campoPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Al presionar Enter en la contraseña, intenta iniciar sesión
        campoPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    autenticar();
                }
            }
        });

        JButton botonIngresar = new JButton("Iniciar Sesión");
        botonIngresar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        botonIngresar.setAlignmentX(Component.LEFT_ALIGNMENT);
        botonIngresar.setBackground(VERDE_OSCURO);
        botonIngresar.setForeground(BLANCO);
        botonIngresar.setFont(new Font("SansSerif", Font.BOLD, 14));
        botonIngresar.setFocusPainted(false);
        botonIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonIngresar.addActionListener(e -> autenticar());

        panelFormulario.add(lblUsuario);
        panelFormulario.add(Box.createVerticalStrut(5));
        panelFormulario.add(campoUsuario);
        panelFormulario.add(Box.createVerticalStrut(15));
        panelFormulario.add(lblPassword);
        panelFormulario.add(Box.createVerticalStrut(5));
        panelFormulario.add(campoPassword);
        panelFormulario.add(Box.createVerticalStrut(25));
        panelFormulario.add(botonIngresar);

        panelPrincipal.add(panelFormulario, BorderLayout.CENTER);

        // Ayuda con los usuarios de prueba
        JPanel panelAyuda = new JPanel();
        panelAyuda.setBackground(FONDO);
        panelAyuda.setBorder(BorderFactory.createEmptyBorder(0, 20, 15, 20));
        JLabel lblAyuda = new JLabel("<html><center><font color='#777777' size='2'>"
                + "Usuarios de prueba (clave: <b>1234</b>):<br>"
                + "<b>recepcion</b> (Recepcionista) | <b>vet</b> (Veterinario) | <b>admin</b> (Administrador)"
                + "</font></center></html>");
        panelAyuda.add(lblAyuda);

        panelPrincipal.add(panelAyuda, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private void autenticar() {
        String usuario = campoUsuario.getText().trim().toLowerCase();
        String password = new String(campoPassword.getPassword()).trim();

        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Completá todos los campos para ingresar.",
                    "Huellitas",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (USUARIOS.containsKey(usuario)) {
            String[] datos = USUARIOS.get(usuario);
            String passCorrecta = datos[0];
            String rol = datos[1];

            if (passCorrecta.equals(password)) {
                dispose(); // Cierra el login
                VentanaPrincipal principal = new VentanaPrincipal(clinica, gestor, rol);
                principal.setVisible(true); // Abre la principal mostrando el rol
                return;
            }
        }

        JOptionPane.showMessageDialog(
                this,
                "Usuario o contraseña incorrectos.",
                "Error de Acceso",
                JOptionPane.ERROR_MESSAGE
        );
    }
}