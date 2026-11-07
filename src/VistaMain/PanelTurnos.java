package VistaMain;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import GeneralPersistencia.ClinicaVeterinaria;
import GestionTurnos.TipoAtencion;
import GestionTurnos.Turno;
import PersonasMascota.Mascota;
import PersonasMascota.Veterinario;
import PersonasMascota.Dueño;

// Pantalla de administración de turnos.

public class PanelTurnos extends JPanel {

    private ClinicaVeterinaria clinica;

    // Elementos de la tabla.
    private JTable tablaTurnos;
    private DefaultTableModel modeloTabla;

    // Colores del sistema.
    private final Color VERDE = new Color(54, 83, 70);
    private final Color FONDO = new Color(247, 248, 244);
    private final Color BLANCO = Color.WHITE;

    public PanelTurnos(ClinicaVeterinaria clinica) {

        this.clinica = clinica;

        setLayout(new BorderLayout(20, 20));
        setBackground(FONDO);
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        inicializarComponentes();
        actualizarTabla();
    }

    // Construye todos los componentes de la pantalla.
    private void inicializarComponentes() {

        // =========================
        // ENCABEZADO
        // =========================

        JPanel encabezado = new JPanel();
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setBackground(FONDO);

        JLabel titulo = new JLabel("Gestión de turnos");

        titulo.setFont(new Font("SansSerif", Font.BOLD, 27));
        titulo.setForeground(VERDE);

        JLabel subtitulo = new JLabel(
                "Administrá los turnos y las consultas veterinarias."
        );

        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitulo.setForeground(Color.GRAY);

        encabezado.add(titulo);
        encabezado.add(Box.createVerticalStrut(6));
        encabezado.add(subtitulo);

        add(encabezado, BorderLayout.NORTH);

        // =========================
        // TABLA DE TURNOS
        // =========================

        String[] columnas = {
                "N° Turno",
                "Mascota",
                "Dueño",
                "Veterinario",
                "Atención",
                "Fecha",
                "Horario",
                "Estado",
                "Urgencia"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaTurnos = new JTable(modeloTabla);
        tablaTurnos.setRowHeight(35);
        tablaTurnos.setFont(new Font("SansSerif", Font.PLAIN, 12));

        tablaTurnos.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );

        tablaTurnos.getTableHeader().setBackground(VERDE);
        tablaTurnos.getTableHeader().setForeground(BLANCO);

        JScrollPane scroll = new JScrollPane(tablaTurnos);

        scroll.setBorder(
                BorderFactory.createLineBorder(
                        new Color(225, 230, 222)
                )
        );

        add(scroll, BorderLayout.CENTER);

        // =========================
        // BOTONES DE ACCIONES
        // =========================

        JPanel acciones = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 12, 5)
        );

        acciones.setBackground(FONDO);

        JButton botonNuevo = crearBoton(
                "+ Nuevo turno", VERDE
        );

        JButton botonUrgencia = crearBoton(
                "Registrar urgencia", new Color(180, 100, 85)
        );

        JButton botonCancelar = crearBoton(
                "Cancelar turno", new Color(130, 145, 130)
        );

        JButton botonAusente = crearBoton(
                "Marcar ausente", new Color(160, 110, 100)
        );

        JButton botonActualizar = crearBoton(
                "Actualizar", new Color(130, 145, 130)
        );

        acciones.add(botonNuevo);
        acciones.add(botonUrgencia);
        acciones.add(botonCancelar);
        acciones.add(botonAusente);
        acciones.add(botonActualizar);

        add(acciones, BorderLayout.SOUTH);

        // EVENTOS

        botonNuevo.addActionListener(e -> registrarTurno());

        botonUrgencia.addActionListener(e -> registrarUrgencia());

        botonCancelar.addActionListener(e -> cancelarTurno());

        botonAusente.addActionListener(e -> marcarAusente());

        botonActualizar.addActionListener(e -> actualizarTabla());
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

    // Carga los turnos existentes en la tabla.
    private void actualizarTabla() {

        modeloTabla.setRowCount(0);

        for (Turno turno : clinica.getPlanificador().getTurnos()) {

            Object[] fila = {
                    turno.getIdTurno(),
                    turno.getMascota().getNombre(),
                    turno.getMascota().getDueño().obtenerNombreCompleto(),
                    turno.getVeterinario().obtenerNombreCompleto(),
                    obtenerNombreAtencion(turno.getTipoAtencion()),
                    turno.getFechaHora().toLocalDate(),
                    turno.getFechaHora().toLocalTime(),
                    turno.getEstado(),
                    turno.isEsUrgencia() ? "Sí" : "No"
            };

            modeloTabla.addRow(fila);
        }
    }

    // Convierte los nombres internos del enum en textos más amigables.
    private String obtenerNombreAtencion(TipoAtencion tipo) {

        switch (tipo) {

            case ConsultaGeneral:
                return "Consulta general";

            case Vacunacion:
                return "Vacunación";

            case Cirugia:
                return "Cirugía";

            default:
                return tipo.toString();
        }
    }

    // Abre el formulario para crear un turno.
    private void registrarTurno() {

        if (clinica.listarDueños().isEmpty()
                || clinica.listarVeterinarios().isEmpty()) {

            mostrarMensaje(
                    "Necesitás tener dueños, mascotas y veterinarios registrados."
            );

            return;
        }

        // Selector de mascotas.
        JComboBox<Mascota> selectorMascota = new JComboBox<>();

        for (PersonasMascota.Dueño dueño : clinica.listarDueños()) {

            for (Mascota mascota : dueño.getMascotas()) {
                selectorMascota.addItem(mascota);
            }
        }

        if (selectorMascota.getItemCount() == 0) {
            mostrarMensaje("No hay mascotas registradas.");
            return;
        }

        // Mostramos nombre y dueño de cada mascota.
        selectorMascota.setRenderer(new DefaultListCellRenderer() {

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

                if (valor instanceof Mascota) {

                    Mascota mascota = (Mascota) valor;

                    setText(
                            mascota.getNombre()
                                    + " - "
                                    + mascota.getDueño().obtenerNombreCompleto()
                    );
                }

                return this;
            }
        });

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
                                    + " - "
                                    + veterinario.getEspecialidad()
                    );
                }

                return this;
            }
        });

        // Tipos de atención disponibles.
        JComboBox<TipoAtencion> selectorTipo =
                new JComboBox<>(TipoAtencion.values());

        selectorTipo.setRenderer(new DefaultListCellRenderer() {

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

                if (valor instanceof TipoAtencion) {
                    setText(obtenerNombreAtencion((TipoAtencion) valor));
                }

                return this;
            }
        });

        // Campos para fecha y hora.
        JTextField campoFecha = new JTextField("2026-11-10");
        JTextField campoHora = new JTextField("10:00");

        JPanel formulario = new JPanel(
                new GridLayout(0, 2, 10, 12)
        );

        formulario.add(new JLabel("Mascota:"));
        formulario.add(selectorMascota);

        formulario.add(new JLabel("Veterinario:"));
        formulario.add(selectorVeterinario);

        formulario.add(new JLabel("Tipo de atención:"));
        formulario.add(selectorTipo);

        formulario.add(new JLabel("Fecha (AAAA-MM-DD):"));
        formulario.add(campoFecha);

        formulario.add(new JLabel("Hora (HH:MM):"));
        formulario.add(campoHora);

        int opcion = JOptionPane.showConfirmDialog(
                this,
                formulario,
                "Registrar nuevo turno",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            // Convertimos los textos a fechas y horarios.
            LocalDate fecha = LocalDate.parse(
                    campoFecha.getText().trim()
            );

            LocalTime hora = LocalTime.parse(
                    campoHora.getText().trim()
            );

            LocalDateTime fechaHora = LocalDateTime.of(fecha, hora);

            // No permitimos asignar turnos en el pasado.
            if (fechaHora.isBefore(LocalDateTime.now())) {

                mostrarMensaje(
                        "No podés registrar un turno en una fecha pasada."
                );

                return;
            }

            Mascota mascota =
                    (Mascota) selectorMascota.getSelectedItem();

            Veterinario veterinario =
                    (Veterinario) selectorVeterinario.getSelectedItem();

            TipoAtencion tipo =
                    (TipoAtencion) selectorTipo.getSelectedItem();

            // La clínica verifica la disponibilidad del veterinario.
            Turno nuevoTurno = clinica.asignarTurno(
                    mascota,
                    veterinario,
                    fechaHora,
                    tipo
            );

            if (nuevoTurno == null) {

                mostrarMensaje(
                        "El veterinario no está disponible en ese horario."
                );

                return;
            }

            actualizarTabla();

            String mensaje = "Turno registrado correctamente.";

            if (nuevoTurno.isRequiereSeña()) {
                mensaje += "\nEste dueño requiere abonar una seña.";
            }

            JOptionPane.showMessageDialog(
                    this,
                    mensaje,
                    "Huellitas",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (DateTimeParseException e) {

            mostrarMensaje(
                    "Verificá el formato de la fecha y la hora."
            );
        }
    }

    // Registra una urgencia utilizando el veterinario de guardia.
    private void registrarUrgencia() {

        JComboBox<Mascota> selectorMascota = new JComboBox<>();

        // Cargamos las mascotas registradas.
        for (PersonasMascota.Dueño dueño : clinica.listarDueños()) {

            for (Mascota mascota : dueño.getMascotas()) {
                selectorMascota.addItem(mascota);
            }
        }

        // Verificamos que existan mascotas.
        if (selectorMascota.getItemCount() == 0) {
            mostrarMensaje("No hay mascotas registradas.");
            return;
        }

        // Personalizamos cómo se muestran las mascotas.
        selectorMascota.setRenderer(new DefaultListCellRenderer() {

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

                if (valor instanceof Mascota) {

                    Mascota mascota = (Mascota) valor;

                    setText(
                            mascota.getNombre()
                                    + " - "
                                    + mascota.getDueño().obtenerNombreCompleto()
                    );
                }

                return this;
            }
        });

        // Solicitamos seleccionar la mascota.
        int opcion = JOptionPane.showConfirmDialog(
                this,
                selectorMascota,
                "Seleccionar mascota para urgencia",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        Mascota mascota =
                (Mascota) selectorMascota.getSelectedItem();

        // Registramos la urgencia directamente desde la clínica.
        Turno urgencia = clinica.registrarUrgencia(mascota);

        // Verificamos si se pudo registrar.
        if (urgencia == null) {

            mostrarMensaje(
                    "No se pudo registrar la urgencia. "
                            + "Verificá que exista un veterinario de guardia."
            );

            return;
        }

        // Actualizamos la tabla.
        actualizarTabla();

        JOptionPane.showMessageDialog(
                this,
                "Urgencia registrada correctamente.\n"
                        + "Veterinario: "
                        + urgencia.getVeterinario().obtenerNombreCompleto()
                        + "\nFecha y hora: "
                        + urgencia.getFechaHora()
        );
    }

    // Cancela el turno seleccionado.
    private void cancelarTurno() {

        int fila = tablaTurnos.getSelectedRow();

        if (fila == -1) {

            mostrarMensaje(
                    "Seleccioná un turno de la tabla."
            );

            return;
        }

        int idTurno = Integer.parseInt(
                modeloTabla.getValueAt(fila, 0).toString()
        );

        // Buscamos el turno por su identificador.
        for (Turno turno : clinica.getPlanificador().getTurnos()) {

            if (turno.getIdTurno() == idTurno) {

                if (turno.getEstado() != Turno.EstadoTurno.PROGRAMADO) {

                    mostrarMensaje(
                            "Solamente se pueden cancelar turnos programados."
                    );

                    return;
                }

                int confirmacion = JOptionPane.showConfirmDialog(
                        this,
                        "¿Querés cancelar el turno seleccionado?",
                        "Confirmar cancelación",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirmacion == JOptionPane.YES_OPTION) {

                    turno.cancelar();

                    actualizarTabla();

                    JOptionPane.showMessageDialog(
                            this,
                            "El turno fue cancelado."
                    );
                }

                return;
            }
        }

        mostrarMensaje("No se encontró el turno seleccionado.");
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

    // Registra la ausencia del dueño y actualiza las penalidades por seña
    private void marcarAusente() {

        int fila = tablaTurnos.getSelectedRow();

        if (fila == -1) {
            mostrarMensaje("Seleccioná un turno de la tabla.");
            return;
        }

        int idTurno = Integer.parseInt(
                modeloTabla.getValueAt(fila, 0).toString()
        );

        for (Turno turno : clinica.getPlanificador().getTurnos()) {

            if (turno.getIdTurno() == idTurno) {

                if (turno.getEstado() != Turno.EstadoTurno.PROGRAMADO) {
                    mostrarMensaje("Solamente se puede registrar ausencia en turnos programados.");
                    return;
                }

                int confirmacion = JOptionPane.showConfirmDialog(
                        this,
                        "¿Deseás marcar como ausente el turno seleccionado?\n"
                                + "Esto incrementará el contador de ausencias consecutivas del dueño.",
                        "Confirmar ausencia",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirmacion == JOptionPane.YES_OPTION) {

                    turno.marcarAusente();

                    Dueño dueño = turno.getMascota().getDueño();
                    if (dueño != null) {
                        dueño.registrarAusencia();
                    }

                    actualizarTabla();

                    String mensaje = "El turno fue marcado como AUSENTE.";
                    if (dueño != null && dueño.debePagarSeña()) {
                        mensaje += "\n\nAVISO: El dueño acumuló ausencias consecutivas.\n"
                                + "A partir de ahora se le exigirá una seña previa para nuevos turnos.";
                    }

                    JOptionPane.showMessageDialog(
                            this,
                            mensaje,
                            "Control de Ausencias",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }

                return;
            }
        }

        mostrarMensaje("No se encontró el turno seleccionado.");
    }

}
