
package VistaMain;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import AtencionHistoriaClinica.HistoriaClinica;
import Excepciones.RegistroDuplicadoException;
import Excepciones.RegistroNoEncontradoException;
import GeneralPersistencia.ClinicaVeterinaria;
import PersonasMascota.*;

// Panel para administrar dueños y mascotas.
public class PanelDuenosMascotas extends JPanel {

    private ClinicaVeterinaria clinica;

    // Componentes visuales
    private JTable tablaDuenos;
    private DefaultTableModel modeloTabla;
    private JTextField campoBusqueda;

    // Paleta de colores
    private final Color VERDE = new Color(54, 83, 70);
    private final Color FONDO = new Color(247, 248, 244);
    private final Color BLANCO = Color.WHITE;

    public PanelDuenosMascotas(ClinicaVeterinaria clinica) {

        this.clinica = clinica;

        setLayout(new BorderLayout(20, 20));
        setBackground(FONDO);
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        inicializarComponentes();
        actualizarTabla();
    }

    // Construye la pantalla principal del módulo.
    private void inicializarComponentes() {

        // Encabezado
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(FONDO);

        JLabel titulo = new JLabel("Dueños y mascotas");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 27));
        titulo.setForeground(VERDE);

        JLabel subtitulo = new JLabel(
                "Administrá los clientes y sus mascotas."
        );
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitulo.setForeground(Color.GRAY);

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setBackground(FONDO);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(6));
        textos.add(subtitulo);

        encabezado.add(textos, BorderLayout.WEST);

        // Buscador
        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        busqueda.setBackground(FONDO);

        campoBusqueda = new JTextField(18);
        campoBusqueda.setPreferredSize(new Dimension(200, 35));

        JButton botonBuscar = crearBoton("Buscar", VERDE);
        JButton botonLimpiar = crearBoton("Limpiar", new Color(130, 145, 130));

        busqueda.add(new JLabel("Buscar:"));
        busqueda.add(campoBusqueda);
        busqueda.add(botonBuscar);
        busqueda.add(botonLimpiar);

        encabezado.add(busqueda, BorderLayout.EAST);

        add(encabezado, BorderLayout.NORTH);

        // Tabla de dueños
        String[] columnas = {
                "Documento",
                "Nombre",
                "Apellido",
                "Teléfono",
                "Correo",
                "Mascotas"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaDuenos = new JTable(modeloTabla);
        tablaDuenos.setRowHeight(35);
        tablaDuenos.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tablaDuenos.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );
        tablaDuenos.getTableHeader().setBackground(VERDE);
        tablaDuenos.getTableHeader().setForeground(BLANCO);

        JScrollPane scroll = new JScrollPane(tablaDuenos);
        scroll.setBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 222))
        );

        add(scroll, BorderLayout.CENTER);

        // Botones inferiores
        JPanel acciones = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 12, 5)
        );
        acciones.setBackground(FONDO);

        JButton botonAgregarDueno =
                crearBoton("+ Registrar dueño", VERDE);

        JButton botonAgregarMascota =
                crearBoton("+ Registrar mascota", new Color(100, 130, 105));

        JButton botonVerMascotas =
                crearBoton("Ver mascotas", new Color(130, 145, 130));

        acciones.add(botonAgregarDueno);
        acciones.add(botonAgregarMascota);
        acciones.add(botonVerMascotas);

        add(acciones, BorderLayout.SOUTH);

        // Eventos de los botones
        botonBuscar.addActionListener(e -> buscarDuenos());

        botonLimpiar.addActionListener(e -> {
            campoBusqueda.setText("");
            actualizarTabla();
        });

        botonAgregarDueno.addActionListener(e -> registrarDueno());

        botonAgregarMascota.addActionListener(e -> registrarMascota());

        botonVerMascotas.addActionListener(e -> verMascotas());
    }

    // Crea botones con un estilo uniforme.
    private JButton crearBoton(String texto, Color color) {

        JButton boton = new JButton(texto);

        boton.setBackground(color);
        boton.setForeground(BLANCO);
        boton.setFont(new Font("SansSerif", Font.BOLD, 12));
        boton.setFocusPainted(false);
        boton.setBorder(
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        );
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return boton;
    }

    // Carga todos los dueños registrados en la tabla.
    private void actualizarTabla() {

        modeloTabla.setRowCount(0);

        for (Dueño dueño : clinica.listarDueños()) {

            Object[] fila = {
                    dueño.getDocumento(),
                    dueño.getNombre(),
                    dueño.getApellido(),
                    dueño.getTelefono(),
                    dueño.getCorreo(),
                    dueño.getMascotas().size()
            };

            modeloTabla.addRow(fila);
        }
    }

    // Busca por nombre, apellido o documento.
    private void buscarDuenos() {

        String texto = campoBusqueda.getText().trim().toLowerCase();

        modeloTabla.setRowCount(0);

        for (Dueño dueño : clinica.listarDueños()) {

            String nombreCompleto =
                    dueño.getNombre() + " " + dueño.getApellido();

            boolean coincide =
                    dueño.getDocumento().toLowerCase().contains(texto)
                            || nombreCompleto.toLowerCase().contains(texto);

            if (coincide) {

                Object[] fila = {
                        dueño.getDocumento(),
                        dueño.getNombre(),
                        dueño.getApellido(),
                        dueño.getTelefono(),
                        dueño.getCorreo(),
                        dueño.getMascotas().size()
                };

                modeloTabla.addRow(fila);
            }
        }
    }

    // Abre el formulario para registrar un dueño.
    private void registrarDueno() {

        JTextField nombre = new JTextField();
        JTextField apellido = new JTextField();
        JTextField documento = new JTextField();
        JTextField telefono = new JTextField();
        JTextField correo = new JTextField();

        JCheckBox planSalud =
                new JCheckBox("Tiene plan de salud");

        JPanel formulario = new JPanel(new GridLayout(0, 2, 10, 10));

        formulario.add(new JLabel("Nombre:"));
        formulario.add(nombre);

        formulario.add(new JLabel("Apellido:"));
        formulario.add(apellido);

        formulario.add(new JLabel("Documento:"));
        formulario.add(documento);

        formulario.add(new JLabel("Teléfono:"));
        formulario.add(telefono);

        formulario.add(new JLabel("Correo electrónico:"));
        formulario.add(correo);

        formulario.add(new JLabel("Plan de salud:"));
        formulario.add(planSalud);

        int opcion = JOptionPane.showConfirmDialog(
                this,
                formulario,
                "Registrar nuevo dueño",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        // Validamos que los datos obligatorios estén completos.
        if (nombre.getText().trim().isEmpty()
                || apellido.getText().trim().isEmpty()
                || documento.getText().trim().isEmpty()
                || telefono.getText().trim().isEmpty()
                || correo.getText().trim().isEmpty()) {

            mostrarError("Completá todos los campos.");
            return;
        }

        try {

            Dueño nuevoDueno = new Dueño(
                    nombre.getText().trim(),
                    apellido.getText().trim(),
                    documento.getText().trim(),
                    telefono.getText().trim(),
                    correo.getText().trim(),
                    planSalud.isSelected()
            );

            // Utilizamos el método de registro existente.
            clinica.registrarDueño(nuevoDueno);

            actualizarTabla();

            JOptionPane.showMessageDialog(
                    this,
                    "El dueño fue registrado correctamente."
            );

        } catch (RegistroDuplicadoException e) {

            mostrarError(e.getMessage());
        }
    }

    // Abre el formulario para registrar una mascota.
    private void registrarMascota() {

        if (clinica.listarDueños().isEmpty()) {
            mostrarError("Primero tenés que registrar un dueño.");
            return;
        }

        JTextField nombre = new JTextField();
        JTextField raza = new JTextField();
        JTextField fechaNacimiento = new JTextField();
        JTextField peso = new JTextField();

        JComboBox<Dueño> selectorDueno = new JComboBox<>();

        // Agregamos los dueños registrados al selector.
        for (Dueño dueño : clinica.listarDueños()) {
            selectorDueno.addItem(dueño);
        }

        // Mostramos nombre y documento en el selector.
        selectorDueno.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(
                    JList<?> lista,
                    Object valor,
                    int indice,
                    boolean seleccionado,
                    boolean enfocado) {

                super.getListCellRendererComponent(
                        lista, valor, indice, seleccionado, enfocado
                );

                if (valor instanceof Dueño) {

                    Dueño dueño = (Dueño) valor;

                    setText(
                            dueño.obtenerNombreCompleto()
                                    + " - DNI: " + dueño.getDocumento()
                    );
                }

                return this;
            }
        });

        JComboBox<String> especie = new JComboBox<>(
                new String[]{"Perro", "Gato", "Ave", "Conejo"}
        );

        JComboBox<String> sexo = new JComboBox<>(
                new String[]{"Macho", "Hembra"}
        );

        JComboBox<String> tamano = new JComboBox<>(
                new String[]{"Pequeño", "Mediano", "Grande"}
        );

        JCheckBox datoEspecial =
                new JCheckBox("Sí");

        JLabel etiquetaEspecial = new JLabel("Dato especial:");

        JPanel formulario = new JPanel(new GridLayout(0, 2, 10, 10));

        formulario.add(new JLabel("Dueño:"));
        formulario.add(selectorDueno);

        formulario.add(new JLabel("Nombre de mascota:"));
        formulario.add(nombre);

        formulario.add(new JLabel("Especie:"));
        formulario.add(especie);

        formulario.add(new JLabel("Raza:"));
        formulario.add(raza);

        formulario.add(new JLabel("Sexo:"));
        formulario.add(sexo);

        formulario.add(new JLabel("Fecha nacimiento (AAAA-MM-DD):"));
        formulario.add(fechaNacimiento);

        formulario.add(new JLabel("Peso en kg:"));
        formulario.add(peso);

        formulario.add(new JLabel("Tamaño (solo perros):"));
        formulario.add(tamano);

        formulario.add(etiquetaEspecial);
        formulario.add(datoEspecial);

        // Adaptamos el dato adicional según la especie.
        Runnable actualizarDatoEspecial = () -> {

            String tipo = (String) especie.getSelectedItem();

            if ("Perro".equals(tipo)) {
                etiquetaEspecial.setText("Número de chip:");
                datoEspecial.setVisible(false);
                tamano.setVisible(true);

            } else if ("Gato".equals(tipo)) {
                etiquetaEspecial.setText("Vive en interior:");
                datoEspecial.setText("Sí");
                datoEspecial.setVisible(true);
                tamano.setVisible(false);

            } else if ("Ave".equals(tipo)) {
                etiquetaEspecial.setText("Puede volar:");
                datoEspecial.setText("Sí");
                datoEspecial.setVisible(true);
                tamano.setVisible(false);

            } else {
                etiquetaEspecial.setText("Dieta especial:");
                datoEspecial.setText("Sí");
                datoEspecial.setVisible(true);
                tamano.setVisible(false);
            }

            formulario.revalidate();
            formulario.repaint();
        };

        // Cuando cambia la especie, actualizamos el formulario.
        especie.addActionListener(e -> actualizarDatoEspecial.run());

        actualizarDatoEspecial.run();

        // En los perros el campo adicional es un número de chip.
        JTextField chip = new JTextField();

        // Para simplificar, el campo de chip se solicita en un segundo paso.
        int opcion = JOptionPane.showConfirmDialog(
                this,
                formulario,
                "Registrar nueva mascota",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        // Validaciones básicas.
        if (nombre.getText().trim().isEmpty()
                || raza.getText().trim().isEmpty()
                || fechaNacimiento.getText().trim().isEmpty()
                || peso.getText().trim().isEmpty()) {

            mostrarError("Completá todos los datos de la mascota.");
            return;
        }

        try {

            LocalDate fecha = LocalDate.parse(
                    fechaNacimiento.getText().trim()
            );

            if (fecha.isAfter(LocalDate.now())) {
                mostrarError("La fecha de nacimiento no puede ser futura.");
                return;
            }

            double pesoActual = Double.parseDouble(
                    peso.getText().trim().replace(',', '.')
            );

            if (pesoActual <= 0) {
                mostrarError("El peso debe ser mayor que cero.");
                return;
            }

            Dueño dueñoSeleccionado =
                    (Dueño) selectorDueno.getSelectedItem();

            String tipo = (String) especie.getSelectedItem();
            String nombreMascota = nombre.getText().trim();
            String razaMascota = raza.getText().trim();
            String sexoMascota = (String) sexo.getSelectedItem();

            // Generamos los identificadores utilizando la clínica.
            int idMascota = clinica.proximoIdMascota();
            int idHistoria = clinica.proximoIdHistoria();

            HistoriaClinica historia =
                    new HistoriaClinica(idHistoria);

            Mascota nuevaMascota;

            // Creamos la subclase correspondiente a la especie.
            if ("Perro".equals(tipo)) {

                String numeroChip = JOptionPane.showInputDialog(
                        this,
                        "Ingresá el número de chip:"
                );

                if (numeroChip == null || numeroChip.trim().isEmpty()) {
                    mostrarError("El número de chip es obligatorio.");
                    return;
                }

                nuevaMascota = new Perro(
                        idMascota,
                        dueñoSeleccionado,
                        historia,
                        nombreMascota,
                        tipo,
                        razaMascota,
                        sexoMascota,
                        fecha,
                        pesoActual,
                        numeroChip.trim(),
                        (String) tamano.getSelectedItem()
                );

            } else if ("Gato".equals(tipo)) {

                nuevaMascota = new Gato(
                        idMascota,
                        dueñoSeleccionado,
                        historia,
                        nombreMascota,
                        tipo,
                        razaMascota,
                        sexoMascota,
                        fecha,
                        pesoActual,
                        datoEspecial.isSelected()
                );

            } else if ("Ave".equals(tipo)) {

                nuevaMascota = new Ave(
                        idMascota,
                        dueñoSeleccionado,
                        historia,
                        nombreMascota,
                        tipo,
                        razaMascota,
                        sexoMascota,
                        fecha,
                        pesoActual,
                        datoEspecial.isSelected()
                );

            } else {

                nuevaMascota = new Conejo(
                        idMascota,
                        dueñoSeleccionado,
                        historia,
                        nombreMascota,
                        tipo,
                        razaMascota,
                        sexoMascota,
                        fecha,
                        pesoActual,
                        datoEspecial.isSelected()
                );
            }

            // Registramos la mascota utilizando la clínica.
            clinica.registrarMascota(
                    dueñoSeleccionado,
                    nuevaMascota
            );

            actualizarTabla();

            JOptionPane.showMessageDialog(
                    this,
                    "La mascota fue registrada correctamente."
            );

        } catch (DateTimeParseException e) {

            mostrarError("La fecha debe tener el formato AAAA-MM-DD.");

        } catch (NumberFormatException e) {

            mostrarError("Ingresá un peso válido.");

        } catch (RegistroNoEncontradoException e) {

            mostrarError(e.getMessage());
        }
    }

    // Muestra las mascotas del dueño seleccionado en la tabla.
    private void verMascotas() {

        int fila = tablaDuenos.getSelectedRow();

        if (fila == -1) {
            mostrarError("Seleccioná un dueño de la tabla.");
            return;
        }

        String documento = modeloTabla.getValueAt(fila, 0).toString();

        try {

            Dueño dueño = clinica.buscarDueño(documento);

            String[] columnas = {
                    "ID",
                    "Nombre",
                    "Especie",
                    "Raza",
                    "Sexo",
                    "Peso"
            };

            DefaultTableModel modeloMascotas =
                    new DefaultTableModel(columnas, 0) {

                        @Override
                        public boolean isCellEditable(int fila, int columna) {
                            return false;
                        }
                    };

            for (Mascota mascota : dueño.getMascotas()) {

                Object[] datos = {
                        mascota.getIdMascota(),
                        mascota.getNombre(),
                        mascota.getEspecie(),
                        mascota.getRaza(),
                        mascota.getSexo(),
                        mascota.getPesoActual()
                };

                modeloMascotas.addRow(datos);
            }

            JTable tablaMascotas = new JTable(modeloMascotas);
            tablaMascotas.setRowHeight(30);

            JScrollPane scroll = new JScrollPane(tablaMascotas);
            scroll.setPreferredSize(new Dimension(650, 250));

            JOptionPane.showMessageDialog(
                    this,
                    scroll,
                    "Mascotas de " + dueño.obtenerNombreCompleto(),
                    JOptionPane.PLAIN_MESSAGE
            );

        } catch (RegistroNoEncontradoException e) {

            mostrarError(e.getMessage());
        }
    }

    // Mensaje reutilizable para mostrar errores.
    private void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Huellitas - Atención",
                JOptionPane.WARNING_MESSAGE
        );
    }
}
