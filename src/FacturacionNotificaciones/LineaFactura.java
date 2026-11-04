package FacturacionNotificaciones;

import InventarioProveedores.Producto;
import java.io.Serializable;

public class LineaFactura implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String descripcion;
    private final Producto producto; // null cuando la línea es el valor de la consulta
    private final int cantidad;
    private final double precioBase;

    // Línea de un insumo/medicamento: toma nombre y precio base del producto.
    public LineaFactura(Producto producto, int cantidad) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto es obligatorio.");
        }
        double precioBase = producto.getPrecioBase();
        validar(cantidad, precioBase);
        this.descripcion = producto.getNombre();
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioBase = precioBase;
    }

    // Línea de concepto sin producto (por ejemplo, el valor de la consulta).
    public LineaFactura(String descripcion, int cantidad, double precioUnitario) {
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripción es obligatoria.");
        }
        validar(cantidad, precioUnitario);
        this.descripcion = descripcion;
        this.producto = null;
        this.cantidad = cantidad;
        this.precioBase = precioUnitario;
    }

    private static void validar(int cantidad, double precio) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        if (precio < 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo.");
        }
    }

    public double calcularSubtotal() {
        return EstrategiaPago.redondear(cantidad * precioBase);
    }

    public String getDescripcion() { return descripcion; }
    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public double getPrecioBase() { return precioBase; }
}
