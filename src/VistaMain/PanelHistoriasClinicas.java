
package VistaMain;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.time.LocalDate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import AtencionHistoriaClinica.Consulta;
import Excepciones.ReglaNegocioException;
import GeneralPersistencia.ClinicaVeterinaria;
import PersonasMascota.Dueño;
import PersonasMascota.Mascota;
import PersonasMascota.Veterinario;

// Pantalla para administrar las historias clínicas de las mascotas.
public class PanelHistoriasClinicas extends JPanel {

    private ClinicaVeterinaria clinica;

    // Componentes de la pantalla.
    private JComboBox<Mascota> selectorMascota;
    private JTable tablaConsultas;
    private DefaultTableModel modeloTabla;

    private JLabel etiquetaDueno;
    private JLabel etiquetaEspecie;

    // Colores generales.
    private final Color VERDE = new Color(54, 83, 70);
    private final Color FONDO = new Color(247, 248, 244);
    private final Color BLANCO = Color.WHITE;

    private final DateTimeFormatter formatoFecha =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public PanelHistoriasClinicas(ClinicaVeterinaria clinica) {

        this.clinica = clinica;

        setLayout(new BorderLayout(20, 20));
        setBackground(FONDO);
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        inicializarComponentes();
        cargarMascotas();
    }

    // Construye la pantalla principal.
    private void inicializarComponentes() {

        // =========================
        // ENCABEZADO
        // =========================

        JPanel encabezado = new JPanel();
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setBackground(FONDO);

        JLabel titulo = new JLabel("Historias clínicas");

        titulo.setFont(new Font("SansSerif", Font.BOLD, 27));
        titulo.setForeground(VERDE);

        JLabel subtitulo = new JLabel(
                "Consultá y registrá la información médica de tus pacientes."
        );

        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitulo.setForeground(Color.GRAY);

        encabezado.add(titulo);
        encabezado.add(Box.createVerticalStrut(6));
        encabezado.add(subtitulo);

        add(encabezado, BorderLayout.NORTH);

        // =========================
        // PANEL DE INFORMACIÓN
        // =========================

        JPanel informacion = new JPanel(new GridLayout(3, 2, 10, 12));
        informacion.setBackground(BLANCO);

        informacion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(225, 230, 222)
                ),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        selectorMascota = new JComboBox<>();

        etiquetaDueno = new JLabel("-");
        etiquetaEspecie = new JLabel("-");

        informacion.add(new JLabel("Seleccionar mascota:"));
        informacion.add(selectorMascota);

        informacion.add(new JLabel("Dueño:"));
        informacion.add(etiquetaDueno);

        informacion.add(new JLabel("Especie y raza:"));
        informacion.add(etiquetaEspecie);

        // Cuando se selecciona una mascota, mostramos su historial.
        selectorMascota.addActionListener(e -> actualizarHistorial());

        // =========================
        // TABLA DE CONSULTAS
        // =========================

        String[] columnas = {
                "N° Consulta",
                "Fecha",
                "Veterinario",
                "Motivo",
                "Peso (kg)",
                "Diagnóstico",
                "Estado"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaConsultas = new JTable(modeloTabla);
        tablaConsultas.setRowHeight(35);

        tablaConsultas.setFont(
                new Font("SansSerif", Font.PLAIN, 12)
        );

        tablaConsultas.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );

        tablaConsultas.getTableHeader().setBackground(VERDE);
        tablaConsultas.getTableHeader().setForeground(BLANCO);

        JScrollPane scroll = new JScrollPane(tablaConsultas);

        scroll.setBorder(
                BorderFactory.createLineBorder(
                        new Color(225, 230, 222)
                )
        );

        // Panel central con información y tabla.
        JPanel centro = new JPanel(new BorderLayout(15, 20));
        centro.setBackground(FONDO);

        centro.add(informacion, BorderLayout.NORTH);
        centro.add(scroll, BorderLayout.CENTER);

        add(centro, BorderLayout.CENTER);

        // =========================
        // BOTONES
        // =========================

        JPanel acciones = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 12, 5)
        );

        acciones.setBackground(FONDO);

        JButton botonNuevaConsulta = crearBoton(
                "+ Nueva consulta", VERDE
        );

        JButton botonVerDetalle = crearBoton(
                "Ver detalle", new Color(100, 130, 105)
        );

        JButton botonActualizar = crearBoton(
                "Actualizar", new Color(130, 145, 130)
        );

        acciones.add(botonNuevaConsulta);
        acciones.add(botonVerDetalle);
        acciones.add(botonActualizar);

        add(acciones, BorderLayout.SOUTH);

        // Eventos.
        botonNuevaConsulta.addActionListener(e -> registrarConsulta());

        botonVerDetalle.addActionListener(e -> verDetalleConsulta());

        botonActualizar.addActionListener(e -> {
            cargarMascotas();
            actualizarHistorial();
        });
    }

    // Crea botones con el diseño de Huellitas.
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

    // Carga todas las mascotas registradas.
    private void cargarMascotas() {

        selectorMascota.removeAllItems();

        for (Dueño dueño : clinica.listarDueños()) {

            for (Mascota mascota : dueño.getMascotas()) {
                selectorMascota.addItem(mascota);
            }
        }

        if (selectorMascota.getItemCount() == 0) {
            etiquetaDueno.setText("No hay mascotas registradas");
            etiquetaEspecie.setText("-");
        }
    }

    // Actualiza la información de la mascota seleccionada.
    private void actualizarHistorial() {

        modeloTabla.setRowCount(0);

        Mascota mascota =
                (Mascota) selectorMascota.getSelectedItem();

        if (mascota == null) {
            etiquetaDueno.setText("-");
            etiquetaEspecie.setText("-");
            return;
        }

        etiquetaDueno.setText(
                mascota.getDueño().obtenerNombreCompleto()
        );

        etiquetaEspecie.setText(
                mascota.getEspecie() + " - " + mascota.getRaza()
        );

        // Obtenemos el historial de la mascota.
        List<Consulta> historial =
                mascota.getHistoriaClinica().obtenerHistorial();

        for (Consulta consulta : historial) {

            Object[] fila = {

                    consulta.getIdConsulta(),

                    consulta.getFecha().format(formatoFecha),

                    consulta.getVeterinario().obtenerNombreCompleto(),

                    consulta.getMotivo(),

                    consulta.getPesoDelDia(),

                    consulta.getDiagnostico(),

                    consulta.isEstaConfirmada()
                            ? "Confirmada"
                            : "Pendiente"
            };

            modeloTabla.addRow(fila);
        }
    }

    // Abre el formulario para registrar una nueva consulta.
    private void registrarConsulta() {

        Mascota mascota =
                (Mascota) selectorMascota.getSelectedItem();

        if (mascota == null) {
            mostrarMensaje("Primero seleccioná una mascota.");
            return;
        }

        // Selector de veterinarios.
        JComboBox<Veterinario> selectorVeterinario =
                new JComboBox<>();

        for (Veterinario veterinario : clinica.listarVeterinarios()) {
            selectorVeterinario.addItem(veterinario);
        }

        selectorVeterinario.setRenderer(new DefaultListCellRenderer() {

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

                if (valor instanceof Veterinario) {

                    Veterinario veterinario = (Veterinario) valor;

                    setText(
                            veterinario.obtenerNombreCompleto()
                                    + " - " + veterinario.getEspecialidad()
                    );
                }

                return this;
            }
        });

        JTextField campoMotivo = new JTextField();
        JTextField campoPeso = new JTextField(
                String.valueOf(mascota.getPesoActual())
        );

        JTextArea campoDiagnostico = new JTextArea(3, 20);
        JTextArea campoObservaciones = new JTextArea(3, 20);

        campoDiagnostico.setLineWrap(true);
        campoDiagnostico.setWrapStyleWord(true);

        campoObservaciones.setLineWrap(true);
        campoObservaciones.setWrapStyleWord(true);

        JPanel formulario = new JPanel(
                new GridLayout(0, 2, 10, 12)
        );

        formulario.add(new JLabel("Mascota:"));
        formulario.add(new JLabel(mascota.getNombre()));

        formulario.add(new JLabel("Veterinario:"));
        formulario.add(selectorVeterinario);

        formulario.add(new JLabel("Motivo de consulta:"));
        formulario.add(campoMotivo);

        formulario.add(new JLabel("Peso actual (kg):"));
        formulario.add(campoPeso);

        formulario.add(new JLabel("Diagnóstico:"));
        formulario.add(new JScrollPane(campoDiagnostico));

        formulario.add(new JLabel("Observaciones:"));
        formulario.add(new JScrollPane(campoObservaciones));

        int opcion = JOptionPane.showConfirmDialog(
                this,
                formulario,
                "Registrar consulta médica",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        // Validamos los campos obligatorios.
        if (campoMotivo.getText().trim().isEmpty()
                || campoPeso.getText().trim().isEmpty()
                || campoDiagnostico.getText().trim().isEmpty()) {

            mostrarMensaje("Completá los campos obligatorios.");
            return;
        }

        try {

            double peso = Double.parseDouble(
                    campoPeso.getText().trim().replace(',', '.')
            );

            if (peso <= 0) {
                mostrarMensaje("El peso debe ser mayor que cero.");
                return;
            }

            Veterinario veterinario =
                    (Veterinario) selectorVeterinario.getSelectedItem();

            if (veterinario == null) {
                mostrarMensaje("No hay veterinarios disponibles.");
                return;
            }

            // Obtenemos el controlador de atención de la clínica.
            // Registramos la consulta utilizando la lógica existente.
            Consulta consulta = clinica.getAtencion().registrarConsulta(
                    veterinario,
                    mascota,
                    campoMotivo.getText().trim(),
                    peso,
                    campoDiagnostico.getText().trim(),
                    campoObservaciones.getText().trim()
            );

            // Opción para agregar tratamiento con medicamento del inventario
            int agregarTratamiento = JOptionPane.showConfirmDialog(
                    this,
                    "¿Deseás indicar un tratamiento/medicamento para esta consulta?",
                    "Indicar Tratamiento",
                    JOptionPane.YES_NO_OPTION
            );
            if (agregarTratamiento == JOptionPane.YES_OPTION) {
                if (clinica.getCatalogoProductos().isEmpty()) {
                    mostrarMensaje("No hay medicamentos ni productos en el inventario para recetar.");
                } else {
                    JComboBox<InventarioProveedores.Producto> comboMedicamentos = new JComboBox<>();
                    for (InventarioProveedores.Producto prod : clinica.getCatalogoProductos()) {
                        comboMedicamentos.addItem(prod);
                    }
                    comboMedicamentos.setRenderer(new DefaultListCellRenderer() {
                        @Override
                        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                            if (value instanceof InventarioProveedores.Producto) {
                                InventarioProveedores.Producto p = (InventarioProveedores.Producto) value;
                                setText(p.getNombre() + " (Stock utilizable: " + p.obtenerStockUtilizable(LocalDate.now()) + ")");
                            }
                            return this;
                        }
                    });
                    JTextField campoDosis = new JTextField("1 comprimido");
                    JTextField campoFrecuencia = new JTextField("Cada 12 horas");
                    JTextField campoDuracion = new JTextField("7 días");
                    JPanel panelTratamiento = new JPanel(new GridLayout(0, 2, 8, 8));
                    panelTratamiento.add(new JLabel("Medicamento:"));
                    panelTratamiento.add(comboMedicamentos);
                    panelTratamiento.add(new JLabel("Dosis:"));
                    panelTratamiento.add(campoDosis);
                    panelTratamiento.add(new JLabel("Frecuencia:"));
                    panelTratamiento.add(campoFrecuencia);
                    panelTratamiento.add(new JLabel("Duración:"));
                    panelTratamiento.add(campoDuracion);
                    int optTrat = JOptionPane.showConfirmDialog(
                            this,
                            panelTratamiento,
                            "Detalle del Tratamiento",
                            JOptionPane.OK_CANCEL_OPTION
                    );
                    if (optTrat == JOptionPane.OK_OPTION) {
                        InventarioProveedores.Producto medSeleccionado = (InventarioProveedores.Producto) comboMedicamentos.getSelectedItem();
                        clinica.getAtencion().agregarTratamientoAConsulta(
                                consulta,
                                medSeleccionado,
                                campoDosis.getText().trim(),
                                campoFrecuencia.getText().trim(),
                                campoDuracion.getText().trim()
                        );
                    }
                }
            }

            // Confirmamos la consulta y la guardamos en la historia clínica.
            int confirmar = JOptionPane.showConfirmDialog(
                    this,
                    "¿Deseás confirmar y guardar esta consulta?\n"
                            + "Al confirmar se aplicarán los descuentos de stock "
                            + "correspondientes a tratamientos.",
                    "Confirmar consulta",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmar != JOptionPane.YES_OPTION) {

                mostrarMensaje(
                        "La consulta no fue confirmada ni guardada."
                );

                return;
            }

            clinica.getAtencion().finalizarYGuardarConsulta(
                    consulta,
                    mascota
            );

            actualizarHistorial();

            JOptionPane.showMessageDialog(
                    this,
                    "La consulta se registró correctamente.",
                    "Huellitas",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (NumberFormatException e) {

            mostrarMensaje("Ingresá un peso válido.");

        } catch (ReglaNegocioException e) {

            mostrarMensaje(e.getMessage());
        }
    }

    // Muestra los detalles de la consulta seleccionada.
    private void verDetalleConsulta() {

        int fila = tablaConsultas.getSelectedRow();

        if (fila == -1) {
            mostrarMensaje("Seleccioná una consulta de la tabla.");
            return;
        }

        Mascota mascota =
                (Mascota) selectorMascota.getSelectedItem();

        if (mascota == null) {
            return;
        }

        int idConsulta = Integer.parseInt(
                modeloTabla.getValueAt(fila, 0).toString()
        );

        for (Consulta consulta :
                mascota.getHistoriaClinica().obtenerHistorial()) {

            if (consulta.getIdConsulta() == idConsulta) {

                String detalle =
                        "CONSULTA N° " + consulta.getIdConsulta()
                                + "\n\nFecha: "
                                + consulta.getFecha().format(formatoFecha)
                                + "\nVeterinario: "
                                + consulta.getVeterinario().obtenerNombreCompleto()
                                + "\nMotivo: "
                                + consulta.getMotivo()
                                + "\nPeso: "
                                + consulta.getPesoDelDia() + " kg"
                                + "\nDiagnóstico: "
                                + consulta.getDiagnostico()
                                + "\nObservaciones: "
                                + consulta.getObservaciones()
                                + "\n\nEstado: "
                                + (consulta.isEstaConfirmada()
                                ? "Confirmada"
                                : "Pendiente");

                // Mostramos los tratamientos registrados.
                detalle += "\n\nTRATAMIENTOS:";

                if (consulta.getTratamientos().isEmpty()) {
                    detalle += "\nSin tratamientos registrados.";
                } else {
                    for (AtencionHistoriaClinica.Tratamiento tratamiento
                            : consulta.getTratamientos()) {

                        detalle += "\n- "
                                + tratamiento.getResumenTratamiento();
                    }
                }

                // Mostramos las vacunas aplicadas.
                detalle += "\n\nVACUNAS:";

                if (consulta.getVacunasAplicadas().isEmpty()) {
                    detalle += "\nSin vacunas registradas.";
                } else {
                    for (AtencionHistoriaClinica.AplicacionVacuna vacuna
                            : consulta.getVacunasAplicadas()) {

                        detalle += "\n- "
                                + vacuna.getVacuna().getNombre()
                                + " | Aplicación: "
                                + vacuna.getFechaAplicacion()
                                + " | Próximo refuerzo: "
                                + vacuna.getFechaProximoRefuerzo();
                    }
                }

                JOptionPane.showMessageDialog(
                        this,
                        detalle,
                        "Detalle de historia clínica",
                        JOptionPane.INFORMATION_MESSAGE
                );

                return;
            }
        }
    }

    // Mensaje reutilizable.
    private void mostrarMensaje(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Huellitas",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
