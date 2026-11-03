package InventarioProveedores;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// Medicamento o insumo. Gestiona el inventario de sus lotes (Experto en Informacion).
// Consume siempre primero el lote NO vencido con vencimiento mas proximo (FEFO, RN4.5).

public class Producto implements Serializable {

    private static final long serialVersionUID = 1L;

    // Comparator personalizado: orden alfabetico por nombre.
    public static final Comparator<Producto> POR_NOMBRE =
            Comparator.comparing(Producto::getNombre, String.CASE_INSENSITIVE_ORDER);

    private final String codigo;
    private String nombre;
    private double precioBase;
    private int stockMinimo;
    private Proveedor proveedor;
    private final List<Lote> lotes = new ArrayList<>();

    // El stock inicial es cero: las unidades entran por recepcion de lotes (CU23).
    public Producto(String codigo, String nombre, double precioBase, int stockMinimo, Proveedor proveedor) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El codigo es obligatorio.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (precioBase < 0) {
            throw new IllegalArgumentException("El precio base no puede ser negativo.");
        }
        if (stockMinimo < 0) {
            throw new IllegalArgumentException("El stock minimo no puede ser negativo.");
        }
        this.codigo = codigo.trim();
        this.nombre = nombre.trim();
        this.precioBase = precioBase;
        this.stockMinimo = stockMinimo;
        this.proveedor = proveedor;
    }

    // Registra un lote recibido. Los lotes vencidos se aceptan pero quedan bloqueados (supuesto del TP).
    public void agregarLote(Lote lote) {
        if (lote == null) {
            throw new IllegalArgumentException("El lote no puede ser nulo.");
        }
        for (Lote l : lotes) {
            if (l.getNumeroLote().equalsIgnoreCase(lote.getNumeroLote())) {
                throw new IllegalArgumentException("Ya existe el lote " + lote.getNumeroLote()
                        + " para el producto " + codigo + ".");
            }
        }
        lotes.add(lote);
    }

    // Stock actual: suma de todos los lotes, incluidos los vencidos.
    public int obtenerStockTotal() {
        int total = 0;
        for (Lote l : lotes) {
            total += l.getCantidadDisponible();
        }
        return total;
    }

    // Stock utilizable: solo lotes no vencidos.
    public int obtenerStockUtilizable(LocalDate fechaActual) {
        int total = 0;
        for (Lote l : lotes) {
            if (l.esUtilizable(fechaActual)) {
                total += l.getCantidadDisponible();
            }
        }
        return total;
    }

    // Sirve para validar ANTES de descontar (Consulta.confirmarConsulta es todo o nada).
    public boolean haySuficiente(int cantidad, LocalDate fechaActual) {
        return cantidad > 0 && obtenerStockUtilizable(fechaActual) >= cantidad;
    }

    public void consumirStock(int cantidad, LocalDate fechaActual) throws StockInsuficienteException {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a consumir debe ser mayor a cero.");
        }
        int utilizable = obtenerStockUtilizable(fechaActual);
        if (utilizable < cantidad) {
            throw new StockInsuficienteException("Stock insuficiente de " + nombre + " (" + codigo
                    + "): se necesitan " + cantidad + " y hay " + utilizable + " utilizables.");
        }
        List<Lote> ordenados = new ArrayList<>(lotes);
        Collections.sort(ordenados); // orden natural: vencimiento mas proximo primero
        int restante = cantidad;
        for (Lote lote : ordenados) {
            if (restante == 0) {
                break;
            }
            if (!lote.esUtilizable(fechaActual)) {
                continue;
            }
            int aTomar = Math.min(restante, lote.getCantidadDisponible());
            try {
                lote.reducirCantidad(aTomar);
            } catch (Lote.StockInsuficienteException e) {
                throw new IllegalStateException(
                        "Inconsistencia de stock en el lote " + lote.getNumeroLote() + ".",
                        e);
            }
            restante -= aTomar;
        }
    }

    // RN4.2: alerta cuando el stock utilizable es menor o igual al minimo
    public boolean alertarReposicion(LocalDate fechaActual) {
        return obtenerStockUtilizable(fechaActual) <= stockMinimo;
    }

    public boolean alertarReposicion() {
        return alertarReposicion(LocalDate.now());
    }

    public List<Lote> obtenerLotesVencidos(LocalDate fechaActual) {
        List<Lote> vencidos = new ArrayList<>();
        for (Lote l : lotes) {
            if (l.esVencido(fechaActual)) {
                vencidos.add(l);
            }
        }
        return vencidos;
    }

    // Copia de solo lectura ordenada por vencimiento: nadie externo toca la lista interna
    public List<Lote> getLotes() {
        List<Lote> copia = new ArrayList<>(lotes);
        Collections.sort(copia);
        return Collections.unmodifiableList(copia);
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public double getPrecioBase() { return precioBase; }
    public int getStockMinimo() { return stockMinimo; }
    public Proveedor getProveedor() { return proveedor; }

    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }

    @Override
    public boolean equals(Object o) {
        return o instanceof Producto && codigo.equalsIgnoreCase(((Producto) o).codigo);
    }

    @Override
    public int hashCode() {
        return codigo.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre + " (stock total: " + obtenerStockTotal()
                + ", minimo: " + stockMinimo + ")";
    }

    public static class StockInsuficienteException extends Exception {
        private static final long serialVersionUID = 1L;

        public StockInsuficienteException(String message) {
            super(message);
        }
    }
}