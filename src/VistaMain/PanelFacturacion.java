
package VistaMain;

import GeneralPersistencia.ClinicaVeterinaria;
import PersonasMascota.*;
import AtencionHistoriaClinica.*;
import FacturacionNotificaciones.*;
import Excepciones.FacturacionException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PanelFacturacion extends JPanel {

    private ClinicaVeterinaria clinica;
    private JTable tabla;
    private DefaultTableModel modelo;
    private List<Factura> facturas;

    private final Color VERDE = new Color(105, 135, 116);
    private final Color FONDO = new Color(248, 247, 242);

    public PanelFacturacion(ClinicaVeterinaria clinica) {
        this.clinica = clinica;
        this.facturas = new ArrayList<>();

        setLayout(new BorderLayout(15, 15));
        setBackground(FONDO);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel titulo = new JLabel("Facturación");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 25));
        titulo.setForeground(VERDE);

        JLabel subtitulo = new JLabel("Administración de facturas y pagos");
        subtitulo.setForeground(Color.GRAY);

        JPanel encabezado = new JPanel(new GridLayout(2, 1, 0, 5));
        encabezado.setOpaque(false);
        encabezado.add(titulo);
        encabezado.add(subtitulo);

        add(encabezado, BorderLayout.NORTH);

        modelo = new DefaultTableModel(
                new String[]{"N° Factura", "Fecha", "Dueño", "Consulta", "Medio de pago", "Total", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(30);
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(tabla);
        add(scroll, BorderLayout.CENTER);

        JButton emitir = crearBoton("Emitir factura");
        JButton consultar = crearBoton("Consultar detalle");
        JButton anular = crearBoton("Anular factura");
        JButton actualizar = crearBoton("Actualizar");

        //parte de las expresiones lambda: Sirve para indicar qué acción se ejecuta cuando ocurre un evento.
        emitir.addActionListener(e -> emitirFactura());
        consultar.addActionListener(e -> consultarFactura());
        anular.addActionListener(e -> anularFactura());
        actualizar.addActionListener(e -> cargarFacturas());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        botones.setOpaque(false);
        botones.add(emitir);
        botones.add(consultar);
        botones.add(anular);
        botones.add(actualizar);

        add(botones, BorderLayout.SOUTH);
        cargarFacturas();
    }

    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setBackground(VERDE);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setFont(new Font("SansSerif", Font.BOLD, 12));
        return boton;
    }

    private void cargarFacturas() {
        modelo.setRowCount(0);
        facturas = clinica.getFacturacion().listarFacturas();

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Factura factura : facturas) {
            modelo.addRow(new Object[]{
                    factura.getNumeroFactura(),
                    factura.getFecha().format(formato),
                    factura.getDueño().obtenerNombreCompleto(),
                    factura.getConsulta().getMotivo(),
                    factura.getMedioPago().getDescripcion(),
                    String.format("$ %.2f", factura.getMontoTotal()),
                    factura.estaAnulada() ? "Anulada" : "Emitida"
            });
        }
    }

    private void emitirFactura() {
        List<Mascota> mascotas = new ArrayList<>();

        for (Dueño dueño : clinica.listarDueños()) {
            mascotas.addAll(dueño.getMascotas());
        }

        if (mascotas.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay mascotas registradas.");
            return;
        }

        JComboBox<Mascota> comboMascotas = new JComboBox<>();
        for (Mascota mascota : mascotas) {
            comboMascotas.addItem(mascota);
        }

        comboMascotas.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                          boolean seleccionado, boolean foco) {
                super.getListCellRendererComponent(lista, valor, indice, seleccionado, foco);
                if (valor instanceof Mascota) {
                    setText(((Mascota) valor).getNombre());
                }
                return this;
            }
        });

        JComboBox<Consulta> comboConsultas = new JComboBox<>();

        comboConsultas.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista,
                                                          Object valor, int indice, boolean seleccionado, boolean foco) {
                super.getListCellRendererComponent(lista, valor, indice, seleccionado, foco);
                if (valor instanceof Consulta) {
                    Consulta consulta = (Consulta) valor;
                    setText("N° " + consulta.getIdConsulta() + " - "
                            + consulta.getMotivo() + " - "
                            + consulta.getFecha().toLocalDate());
                }
                return this;
            }
        });

        Runnable cargarConsultas = () -> {
            comboConsultas.removeAllItems();
            Mascota mascota = (Mascota) comboMascotas.getSelectedItem();

            if (mascota != null && mascota.getHistoriaClinica() != null) {
                for (Consulta consulta : mascota.getHistoriaClinica().obtenerHistorial()) {
                    if (consulta.isEstaConfirmada()) {
                        comboConsultas.addItem(consulta);
                    }
                }
            }
        };

        comboMascotas.addActionListener(e -> cargarConsultas.run());
        cargarConsultas.run();

        JComboBox<MedioPago> comboPago = new JComboBox<>(MedioPago.values());

        JTextField importe = new JTextField(12);

        JPanel formulario = new JPanel(new GridLayout(0, 2, 10, 12));
        formulario.add(new JLabel("Mascota:"));
        formulario.add(comboMascotas);
        formulario.add(new JLabel("Consulta:"));
        formulario.add(comboConsultas);
        formulario.add(new JLabel("Medio de pago:"));
        formulario.add(comboPago);
        formulario.add(new JLabel("Importe de la consulta:"));
        formulario.add(importe);

        int resultado = JOptionPane.showConfirmDialog(this, formulario,
                "Emitir factura", JOptionPane.OK_CANCEL_OPTION);

        if (resultado != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            Mascota mascota = (Mascota) comboMascotas.getSelectedItem();
            Consulta consulta = (Consulta) comboConsultas.getSelectedItem();
            MedioPago medioPago = (MedioPago) comboPago.getSelectedItem();

            if (mascota == null || consulta == null) {
                JOptionPane.showMessageDialog(this,
                        "La mascota debe tener una consulta confirmada para facturar.");
                return;
            }

            double precio = Double.parseDouble(importe.getText().trim());

            if (precio <= 0) {
                JOptionPane.showMessageDialog(this,
                        "El importe debe ser mayor a cero.");
                return;
            }

            List<LineaFactura> lineas = new ArrayList<>();
            lineas.add(new LineaFactura(
                    "Consulta: " + consulta.getMotivo(), 1, precio));

            Factura factura = clinica.getFacturacion().emitirFactura(
                    mascota.getDueño(), consulta, medioPago, lineas);

            cargarFacturas();

            JOptionPane.showMessageDialog(this,
                    "Factura N° " + factura.getNumeroFactura()
                            + " emitida correctamente.\nTotal: $"
                            + String.format("%.2f", factura.getMontoTotal()));

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Ingresá un importe válido.");
        } catch (FacturacionException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private Factura obtenerFacturaSeleccionada() {
        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccioná una factura de la tabla.");
            return null;
        }

        return facturas.get(tabla.convertRowIndexToModel(fila));
    }

    private void consultarFactura() {
        Factura factura = obtenerFacturaSeleccionada();

        if (factura != null) {
            JOptionPane.showMessageDialog(this,
                    factura.obtenerDetalle(),
                    "Detalle de factura",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void anularFactura() {
        Factura factura = obtenerFacturaSeleccionada();

        if (factura == null) {
            return;
        }

        if (factura.estaAnulada()) {
            JOptionPane.showMessageDialog(this,
                    "Esta factura ya se encuentra anulada.");
            return;
        }

        String motivo = JOptionPane.showInputDialog(this,
                "Ingresá el motivo de la anulación:");

        if (motivo == null || motivo.trim().isEmpty()) {
            return;
        }

        try {
            NotaDeCredito nota = clinica.getFacturacion().anularFactura(
                    factura.getNumeroFactura(), motivo.trim());

            cargarFacturas();

            JOptionPane.showMessageDialog(this,
                    "Factura anulada correctamente.\nNota de crédito N° "
                            + nota.getNumeroNota());

        } catch (FacturacionException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}
