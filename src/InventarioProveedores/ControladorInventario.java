package InventarioProveedores;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Excepciones.RegistroNoEncontradoException;
import Excepciones.RegistroDuplicadoException;

// Controlador (GRASP) del modulo de inventario: recibe las solicitudes de la interfaz
// y delega el trabajo en Producto, Lote y Proveedor.
// Cubre CU13 (consultar stock), CU16 (alertar stock bajo), CU22 (registrar proveedor),
// CU23 (registrar producto) y CU27 (registrar recepcion de productos y lotes).

public class ControladorInventario implements Serializable {

    private static final long serialVersionUID = 1L;
    private final Map<String, Proveedor> proveedores = new LinkedHashMap<>();
    private final Map<String, Producto> productos = new LinkedHashMap<>();
    // Codigos de productos con alerta de reposicion vigente (evita duplicarlas, CU16)
    private final Set<String> alertasActivas = new LinkedHashSet<>();

    // CU22: proveedores

    public Proveedor registrarProveedor(String cuit, String razonSocial, String telefono, String direccion)
            throws RegistroDuplicadoException {
        Proveedor nuevo = new Proveedor(cuit, razonSocial, telefono, direccion); // valida datos
        if (proveedores.containsKey(nuevo.getCuit())) {
            throw new RegistroDuplicadoException("Ya existe un proveedor con CUIT " + nuevo.getCuit() + ".");
        }
        proveedores.put(nuevo.getCuit(), nuevo);
        return nuevo;
    }

    public Proveedor buscarProveedor(String cuit) throws RegistroNoEncontradoException {
        String clave = cuit == null ? "" : cuit.replace("-", "");
        Proveedor p = proveedores.get(clave);
        if (p == null) {
            throw new RegistroNoEncontradoException("No se encontro el proveedor con CUIT " + cuit + ".");
        }
        return p;
    }

    public List<Proveedor> listarProveedores() {
        return Collections.unmodifiableList(new ArrayList<>(proveedores.values()));
    }

    // CU23: productos

    // El producto nace con stock cero; las unidades entran por recepcion de lotes.
    public Producto registrarProducto(String codigo, String nombre, double precioBase, int stockMinimo,
                                      String cuitProveedor)
            throws RegistroDuplicadoException, RegistroNoEncontradoException {
        Proveedor proveedor = buscarProveedor(cuitProveedor);
        Producto nuevo = new Producto(codigo, nombre, precioBase, stockMinimo, proveedor); // valida datos
        if (productos.containsKey(clave(nuevo.getCodigo()))) {
            throw new RegistroDuplicadoException("Ya existe un producto con codigo " + nuevo.getCodigo() + ".");
        }
        productos.put(clave(nuevo.getCodigo()), nuevo);
        return nuevo;
    }

    // Usado tambien por facturacion y consultas para obtener el producto de una linea.
    public Producto buscarProducto(String codigo) throws RegistroNoEncontradoException {
        Producto p = productos.get(clave(codigo));
        if (p == null) {
            throw new RegistroNoEncontradoException("No se encontro el producto con codigo " + codigo + ".");
        }
        return p;
    }

    // CU27: recepcion de lotes 

    // Registra la recepcion de un lote. Si el lote ya esta vencido se registra igual (queda
    // constancia) pero no suma al stock utilizable.
    
    public Lote registrarRecepcion(String codigoProducto, String numeroLote, LocalDate fechaVencimiento,
                                   int cantidad) throws RegistroNoEncontradoException {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad recibida debe ser mayor a cero.");
        }
        Producto producto = buscarProducto(codigoProducto);
        Lote lote = new Lote(numeroLote, fechaVencimiento, cantidad);
        if (producto.getProveedor() != null) {
            producto.getProveedor().suministrarLote(producto, lote);
        } else {
            producto.agregarLote(lote);
        }
        refrescarAlerta(producto, LocalDate.now()); // la recepcion puede resolver una alerta
        return lote;
    }

    // CU13: consultar stock 

    // Listado completo ordenado por nombre (Comparator personalizado). 
    public List<Producto> consultarStock() {
        List<Producto> lista = new ArrayList<>(productos.values());
        lista.sort(Producto.POR_NOMBRE);
        return lista;
    }

    // Detalle de un producto: stock, utilizable, minimo y lotes (senala vencidos y reposicion).
    public String detalleStock(String codigo, LocalDate fechaActual) throws RegistroNoEncontradoException {
        Producto p = buscarProducto(codigo);
        StringBuilder sb = new StringBuilder();
        sb.append(p.getCodigo()).append(" - ").append(p.getNombre()).append('\n');
        sb.append("Stock total: ").append(p.obtenerStockTotal())
          .append(" | Utilizable: ").append(p.obtenerStockUtilizable(fechaActual))
          .append(" | Minimo: ").append(p.getStockMinimo()).append('\n');
        if (p.getLotes().isEmpty()) {
            sb.append("Sin stock disponible (no tiene lotes).\n");
        }
        for (Lote l : p.getLotes()) {
            sb.append("  ").append(l);
            if (l.esVencido(fechaActual)) {
                sb.append("  [VENCIDO - bloqueado]");
            }
            sb.append('\n');
        }
        if (p.alertarReposicion(fechaActual)) {
            sb.append("** Corresponde reponer el producto **\n");
        }
        return sb.toString();
    }

    // CU16 / RF7.2: alertas de stock bajo 

    public List<Producto> obtenerProductosStockBajo(LocalDate fechaActual) {
        List<Producto> bajos = new ArrayList<>();
        for (Producto p : consultarStock()) {
            if (p.alertarReposicion(fechaActual)) {
                bajos.add(p);
            }
        }
        return bajos;
    }

    public List<Producto> obtenerProductosStockBajo() {
        return obtenerProductosStockBajo(LocalDate.now());
    }
    
    // Proceso automatico: genera alerta solo para productos sin alerta activa y devuelve las nuevas.
    // Si un producto repuso stock, su alerta se da de baja.
    
    public List<Producto> generarAlertasReposicion(LocalDate fechaActual) {
        List<Producto> nuevas = new ArrayList<>();
        for (Producto p : consultarStock()) {
            if (p.alertarReposicion(fechaActual)) {
                if (alertasActivas.add(clave(p.getCodigo()))) {
                    nuevas.add(p);
                }
            } else {
                alertasActivas.remove(clave(p.getCodigo()));
            }
        }
        return nuevas;
    }

    public Set<String> getAlertasActivas() {
        return Collections.unmodifiableSet(alertasActivas);
    }

    // ---------- Persistencia en archivo ----------

    public void guardar(Path archivo) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(archivo))) {
            out.writeObject(this);
        }
    }

    // Si el archivo no existe (primera ejecucion) devuelve un controlador vacio
    public static ControladorInventario cargar(Path archivo) throws IOException, ClassNotFoundException {
        if (!Files.exists(archivo)) {
            return new ControladorInventario();
        }
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(archivo))) {
            return (ControladorInventario) in.readObject();
        }
    }

    // ---------- auxiliares ----------

    private void refrescarAlerta(Producto p, LocalDate fecha) {
        if (!p.alertarReposicion(fecha)) {
            alertasActivas.remove(clave(p.getCodigo()));
        }
    }

    private static String clave(String codigo) {
        return codigo == null ? "" : codigo.trim().toLowerCase();
    }
}
