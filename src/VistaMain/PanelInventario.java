
package VistaMain;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import GeneralPersistencia.ClinicaVeterinaria;
import InventarioProveedores.*;
import Excepciones.ReglaNegocioException;

public class PanelInventario extends JPanel {

    private ClinicaVeterinaria clinica;
    private JTable tabla;
    private DefaultTableModel modelo;

    private final Color VERDE = new Color(54, 83, 70);
    private final Color FONDO = new Color(247, 248, 244);
    private final Color BLANCO = Color.WHITE;

    public PanelInventario(ClinicaVeterinaria clinica) {

        this.clinica = clinica;

        setLayout(new BorderLayout(20, 20));
        setBackground(FONDO);
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        inicializarComponentes();
        actualizarTabla();
    }

    // Construye la pantalla.
    private void inicializarComponentes() {

        // Encabezado.
        JPanel encabezado = new JPanel();
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setBackground(FONDO);

        JLabel titulo = new JLabel("Inventario y proveedores");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 27));
        titulo.setForeground(VERDE);

        JLabel subtitulo = new JLabel(
                "Administrá los productos, proveedores y existencias."
        );

        subtitulo.setForeground(Color.GRAY);

        encabezado.add(titulo);
        encabezado.add(Box.createVerticalStrut(6));
        encabezado.add(subtitulo);

        add(encabezado, BorderLayout.NORTH);

        // Buscador.
        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        busqueda.setBackground(FONDO);

        JTextField campoBusqueda = new JTextField(20);

        JButton botonBuscar = crearBoton("Buscar", VERDE);
        JButton botonLimpiar = crearBoton(
                "Limpiar", new Color(130, 145, 130)
        );

        busqueda.add(new JLabel("Buscar producto:"));
        busqueda.add(campoBusqueda);
        busqueda.add(botonBuscar);
        busqueda.add(botonLimpiar);

        // Tabla.
        String[] columnas = {
                "ID", "Producto", "Proveedor",
                "Precio", "Stock", "Stock utilizable", "Estado"
        };

        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(35);

        tabla.getTableHeader().setBackground(VERDE);
        tabla.getTableHeader().setForeground(BLANCO);
        tabla.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );

        JScrollPane scroll = new JScrollPane(tabla);

        JPanel centro = new JPanel(new BorderLayout(10, 15));
        centro.setBackground(FONDO);

        centro.add(busqueda, BorderLayout.NORTH);
        centro.add(scroll, BorderLayout.CENTER);

        add(centro, BorderLayout.CENTER);

        // Botones inferiores.
        JPanel acciones = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 12, 5)
        );

        acciones.setBackground(FONDO);

        JButton botonProducto = crearBoton(
                "+ Nuevo producto", VERDE
        );

        JButton botonLote = crearBoton(
                "Recepcionar lote", new Color(100, 130, 105)
        );

        JButton botonDetalle = crearBoton(
                "Ver detalle", new Color(130, 145, 130)
        );

        acciones.add(botonProducto);
        acciones.add(botonLote);
        acciones.add(botonDetalle);

        add(acciones, BorderLayout.SOUTH);

        // Eventos.
        botonBuscar.addActionListener(e -> {
            String texto = campoBusqueda.getText().trim().toLowerCase();
            actualizarTabla(texto);
        });

        botonLimpiar.addActionListener(e -> {
            campoBusqueda.setText("");
            actualizarTabla();
        });

        botonProducto.addActionListener(e -> registrarProducto());
        botonLote.addActionListener(e -> recepcionarLote());
        botonDetalle.addActionListener(e -> verDetalle());
    }

    // Estilo de los botones.
    private JButton crearBoton(String texto, Color color) {

        JButton boton = new JButton(texto);

        boton.setBackground(color);
        boton.setForeground(BLANCO);
        boton.setFont(new Font("SansSerif", Font.BOLD, 12));
        boton.setFocusPainted(false);
        boton.setBorder(
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        );

        return boton;
    }

    // Carga los productos en la tabla.
    private void actualizarTabla() {
        actualizarTabla("");
    }

    private void actualizarTabla(String busqueda) {

        modelo.setRowCount(0);

        for (Producto producto : clinica.getCatalogoProductos()) {

            if (!producto.getNombre().toLowerCase().contains(busqueda)) {
                continue;
            }

            int stock = producto.obtenerStockTotal();
            int utilizable = producto.obtenerStockUtilizable(LocalDate.now());

            String proveedor = producto.getProveedor() == null
                    ? "-"
                    : producto.getProveedor().getRazonSocial();

            String estado = producto.requiereReposicion(LocalDate.now())
                    ? "Stock bajo"
                    : "Disponible";

            modelo.addRow(new Object[]{
                    producto.getIdProducto(),
                    producto.getNombre(),
                    proveedor,
                    "$ " + producto.getPrecioUnitario(),
                    stock,
                    utilizable,
                    estado
            });
        }
    }

    // Registra un producto y su proveedor.
    private void registrarProducto() {

        JTextField nombre = new JTextField();
        JTextField descripcion = new JTextField();
        JTextField precio = new JTextField();
        JTextField stockMinimo = new JTextField();

        JTextField cuit = new JTextField();
        JTextField razonSocial = new JTextField();
        JTextField telefono = new JTextField();
        JTextField direccion = new JTextField();

        JPanel formulario = new JPanel(new GridLayout(0, 2, 10, 10));

        formulario.add(new JLabel("Nombre del producto:"));
        formulario.add(nombre);

        formulario.add(new JLabel("Descripción:"));
        formulario.add(descripcion);

        formulario.add(new JLabel("Precio:"));
        formulario.add(precio);

        formulario.add(new JLabel("Stock mínimo:"));
        formulario.add(stockMinimo);

        formulario.add(new JLabel("CUIT del proveedor:"));
        formulario.add(cuit);

        formulario.add(new JLabel("Razón social:"));
        formulario.add(razonSocial);

        formulario.add(new JLabel("Teléfono:"));
        formulario.add(telefono);

        formulario.add(new JLabel("Dirección:"));
        formulario.add(direccion);

        int opcion = JOptionPane.showConfirmDialog(
                this,
                formulario,
                "Registrar producto y proveedor",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (opcion != JOptionPane.OK_OPTION) return;

        try {

            if (nombre.getText().trim().isEmpty()
                    || cuit.getText().trim().isEmpty()
                    || razonSocial.getText().trim().isEmpty()) {

                mostrarMensaje("Completá los datos obligatorios.");
                return;
            }

            double precioProducto = Double.parseDouble(
                    precio.getText().trim().replace(',', '.')
            );

            int minimo = Integer.parseInt(stockMinimo.getText().trim());

            if (precioProducto < 0 || minimo < 0) {
                mostrarMensaje("El precio y el stock mínimo no pueden ser negativos.");
                return;
            }

            // Generamos un ID que no esté utilizado.
            int id = 1;

            for (Producto producto : clinica.getCatalogoProductos()) {
                if (producto.getIdProducto() >= id) {
                    id = producto.getIdProducto() + 1;
                }
            }

            Proveedor proveedor = new Proveedor(
                    cuit.getText().trim(),
                    razonSocial.getText().trim(),
                    telefono.getText().trim(),
                    direccion.getText().trim()
            );

            Producto producto = new Producto(
                    id,
                    nombre.getText().trim(),
                    descripcion.getText().trim(),
                    precioProducto,
                    proveedor,
                    minimo
            );

            clinica.agregarProducto(producto);

            actualizarTabla();

            JOptionPane.showMessageDialog(
                    this,
                    "Producto registrado correctamente."
            );

        } catch (NumberFormatException e) {
            mostrarMensaje("Ingresá un precio y stock mínimo válidos.");
        }
    }

    // Recepciona un lote para un producto existente.
    private void recepcionarLote() {

        JComboBox<Producto> selector = new JComboBox<>();

        for (Producto producto : clinica.getCatalogoProductos()) {
            selector.addItem(producto);
        }

        selector.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> lista, Object valor, int indice,
                    boolean seleccionado, boolean enfocado) {

                super.getListCellRendererComponent(
                        lista, valor, indice, seleccionado, enfocado
                );

                if (valor instanceof Producto) {
                    setText(((Producto) valor).getNombre());
                }

                return this;
            }
        });

        JTextField numeroLote = new JTextField();
        JTextField vencimiento = new JTextField();
        JTextField cantidad = new JTextField();

        JPanel formulario = new JPanel(new GridLayout(0, 2, 10, 10));

        formulario.add(new JLabel("Producto:"));
        formulario.add(selector);

        formulario.add(new JLabel("Número de lote:"));
        formulario.add(numeroLote);

        formulario.add(new JLabel("Vencimiento (AAAA-MM-DD):"));
        formulario.add(vencimiento);

        formulario.add(new JLabel("Cantidad:"));
        formulario.add(cantidad);

        if (selector.getItemCount() == 0) {
            mostrarMensaje("Primero registrá un producto.");
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(
                this,
                formulario,
                "Recepcionar lote",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (opcion != JOptionPane.OK_OPTION) return;

        try {

            Producto producto = (Producto) selector.getSelectedItem();

            LocalDate fecha = LocalDate.parse(vencimiento.getText().trim());
            int unidades = Integer.parseInt(cantidad.getText().trim());

            if (numeroLote.getText().trim().isEmpty() || unidades <= 0) {
                mostrarMensaje("Verificá los datos del lote.");
                return;
            }

            Lote lote = new Lote(
                    numeroLote.getText().trim(),
                    fecha,
                    unidades
            );

            clinica.getInventario().recepcionarLote(
                    producto.getProveedor(),
                    producto,
                    lote
            );

            actualizarTabla();

            // Se permite registrar para constancia, pero avisa que queda bloqueado
            if (fecha.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(
                        this,
                        "Lote recepcionado correctamente.\n"
                                + "Aviso: Como el lote ya está vencido, quedará registrado pero no sumará al stock utilizable.",
                        "Huellitas",
                        JOptionPane.WARNING_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Lote recepcionado correctamente.",
                        "Huellitas",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (DateTimeParseException e) {
            mostrarMensaje("La fecha debe tener formato AAAA-MM-DD.");
        } catch (NumberFormatException e) {
            mostrarMensaje("Ingresá una cantidad válida.");
        } catch (ReglaNegocioException e) {
            mostrarMensaje(e.getMessage());
        }
    }

    // Muestra información y lotes del producto seleccionado.
    private void verDetalle() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            mostrarMensaje("Seleccioná un producto de la tabla.");
            return;
        }

        int id = Integer.parseInt(modelo.getValueAt(fila, 0).toString());

        for (Producto producto : clinica.getCatalogoProductos()) {

            if (producto.getIdProducto() == id) {

                String detalle = "PRODUCTO: " + producto.getNombre()
                        + "\nDescripción: " + producto.getDescripcion()
                        + "\nPrecio: $ " + producto.getPrecioUnitario()
                        + "\nStock total: " + producto.obtenerStockTotal()
                        + "\nStock utilizable: "
                        + producto.obtenerStockUtilizable(LocalDate.now())
                        + "\n\nLOTES:";

                for (Lote lote : producto.getLotes()) {

                    detalle += "\n\n" + lote.getNumeroLote()
                            + " | Vencimiento: " + lote.getFechaVencimiento()
                            + " | Cantidad: " + lote.getCantidadDisponible();
                }

                JOptionPane.showMessageDialog(
                        this,
                        detalle,
                        "Detalle del producto",
                        JOptionPane.INFORMATION_MESSAGE
                );

                return;
            }
        }
    }

    private void mostrarMensaje(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Huellitas",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
