package InventarioProveedores;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import Excepciones.ReglaNegocioException;
import GeneralPersistencia.ClinicaVeterinaria;

// Controlador del módulo de inventario: gestiona productos, recepción de lotes y alertas de stock bajo.

public class ControladorInventario implements Serializable {
    private static final long serialVersionUID = 1L;
    private ClinicaVeterinaria clinica;
    private static int contadorProductos = 1;
    private List<Producto> productos = new ArrayList<>();

    public ControladorInventario(ClinicaVeterinaria clinica){
        this.clinica = clinica;
    }

    // Devuelve el catalogo
    public List<Producto> getCatalogoProductos() {
        return productos;
    }

    // Registrar nuevo producto en el catalogo
    public Producto registrarProducto(String nombre, String descripcion, double precio, Proveedor proveedor, int stockMinimo) throws ReglaNegocioException {
        if (nombre==null || nombre.trim().isEmpty()) {
            throw new ReglaNegocioException("El nombre del producto es obligatorio.");
        }
        if (proveedor == null) {
            throw new ReglaNegocioException("El producto debe tener un proveedor asignado.");
        }
        Producto nuevoProducto = new Producto(contadorProductos++, nombre, descripcion, precio, proveedor, stockMinimo);
        this.productos.add(nuevoProducto);
        return nuevoProducto;
    }

    // Registrar ingreso de un nuevo lote
    public void recepcionarLote(Proveedor proveedor, Producto producto, Lote lote) throws ReglaNegocioException {
        if (proveedor==null || producto==null || lote==null) {
            throw new ReglaNegocioException("Proveedor, producto y lote son requeridos para la recepcion.");
        }
        if (lote.getCantidadDisponible() <= 0) {
            throw new ReglaNegocioException("La cantidad ingresada en el lote debe ser mayor a 0.");
        }
        // Si el producto no tenia proveedor asignado, le asignamos el que entrega el lote
        if (producto.getProveedor() == null) {
            producto.setProveedor(proveedor);
        }
        proveedor.suministrarLote(producto, lote);
    }

    // Obtener productos que esten con stock minimo, (reposicion)
    public List<Producto> obtenerProductosBajoStock(List<Producto> catalogoProductos, LocalDate hoy) {
        List<Producto> bajoStock = new ArrayList<>();
        if (catalogoProductos == null) return bajoStock;
        for (Producto p : catalogoProductos) {
            if (p.requiereReposicion(hoy)) {
                bajoStock.add(p);
            }
        }
        return bajoStock;
    }

    // Consultar stock utilizable disponible de un producto
    public int consultarStockUtilizable(Producto producto, LocalDate hoy) {
        if (producto == null) return 0;
        return producto.obtenerStockUtilizable(hoy);
    }
}
