package InventarioProveedores;
import Excepciones.ReglaNegocioException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.io.Serializable;

// Representa un producto, insumo o medicamento en stock, y administra sus lotes.

public class Producto implements Serializable {
    private static final long serialVersionUID = 1L;
    private int idProducto;
    private String nombre;
    private String descripcion;
    private double precioUnitario;
    private Proveedor proveedor;
    private int stockMinimo;
    private List<Lote> lotes;

    public Producto(int idProducto, String nombre, String descripcion, double precioUnitario, Proveedor proveedor, int stockMinimo) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioUnitario = precioUnitario;
        this.proveedor = proveedor;
        this.stockMinimo = stockMinimo;
        this.lotes = new ArrayList<>();
    }

    public int getIdProducto() {
        return idProducto;
    }
    public String getNombre() {
        return nombre;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public double getPrecioUnitario() {
        return precioUnitario;
    }
    public double getPrecioBase() {
        return precioUnitario;
    }
    public Proveedor getProveedor() {
        return proveedor;
    }
    public int getStockMinimo() {
        return stockMinimo;
    }
    public List<Lote> getLotes() {
        return lotes;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    // Agregar nuevo lote de este producto
    public void agregarLote(Lote lote) {
        if (lote != null) {
            this.lotes.add(lote);
        }
    }

    // Calcular la suma total de stock en deposito (vencidos y no)
    public int obtenerStockTotal() {
        int total=0;
        for (Lote lote : lotes) {
            total += lote.getCantidadDisponible();
        }
        return total;
    }

    // Calcular stock utilizable (unicamente no vencidos)
    public int obtenerStockUtilizable(LocalDate hoy) {
        int utilizable = 0;
        for (Lote lote : lotes) {
            if(!lote.esVencido(hoy)) {
                utilizable += lote.getCantidadDisponible();
            }
        }
        return utilizable;
    }

    // Evaluar si requiere reposicion
    public boolean requiereReposicion(LocalDate hoy) {
        return obtenerStockUtilizable(hoy) <= this.stockMinimo;
    }

    // Descontar stock (mas proximo a vencer)
    public void consumirStock(int cantidadAConsumir, LocalDate hoy) throws ReglaNegocioException {
        if (cantidadAConsumir <= 0) return;
        if (obtenerStockUtilizable(hoy) < cantidadAConsumir) {
            throw new ReglaNegocioException("Stock insuficiente utilizable para el producto: "+nombre);
        }
        // Filtrar lotes validos
        List<Lote> lotesValidos = new ArrayList<>();
        for (Lote lote : lotes) {
            if(!lote.esVencido(hoy) && lote.tieneStock()) {
                lotesValidos.add(lote);
            }
        }
        // Ordenar lotes por fecha de vencimiento ascendente
        lotesValidos.sort(Comparator.comparing(Lote::getFechaVencimiento));
        // Descontar unidades de los lotes ordenados
        int pendiente = cantidadAConsumir;
        for (Lote lote : lotesValidos) {
            if (pendiente==0) break;
            int disponibleEnLote = lote.getCantidadDisponible();
            if (disponibleEnLote <= pendiente) {
                lote.reducirCantidad(disponibleEnLote);
                pendiente -= disponibleEnLote;
            } else {
                lote.reducirCantidad(pendiente);
                pendiente=0;
            }
        }
    }
}
