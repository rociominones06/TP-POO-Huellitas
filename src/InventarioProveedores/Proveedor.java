package InventarioProveedores;

import java.io.Serializable;

// Proveedor de medicamentos e insumos. Se identifica por su CUIT (CU22)
public class Proveedor implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String cuit;
    private String razonSocial;
    private String telefono;
    private String direccion;

    public Proveedor(String cuit, String razonSocial, String telefono, String direccion) {
        if (cuit == null || !cuit.replace("-", "").matches("\\d{11}")) {
            throw new IllegalArgumentException("El CUIT debe tener 11 digitos.");
        }
        if (razonSocial == null || razonSocial.isBlank()) {
            throw new IllegalArgumentException("La razon social es obligatoria.");
        }
        this.cuit = cuit.replace("-", "");
        this.razonSocial = razonSocial.trim();
        this.telefono = telefono;
        this.direccion = direccion;
    }

    // Registra la recepcion de un lote de un producto que este proveedor abastece
    public void suministrarLote(Producto producto, Lote lote) {
        if (producto == null || lote == null) {
            throw new IllegalArgumentException("Producto y lote son obligatorios.");
        }
        if (!this.equals(producto.getProveedor())) {
            throw new IllegalArgumentException("El proveedor " + razonSocial
                    + " no abastece el producto " + producto.getCodigo() + ".");
        }
        producto.agregarLote(lote);
    }

    public String getCuit() { return cuit; }
    public String getRazonSocial() { return razonSocial; }
    public String getTelefono() { return telefono; }
    public String getDireccion() { return direccion; }

    @Override
    public boolean equals(Object o) {
        return o instanceof Proveedor && cuit.equals(((Proveedor) o).cuit);
    }

    @Override
    public int hashCode() {
        return cuit.hashCode();
    }

    @Override
    public String toString() {
        return razonSocial + " (CUIT " + cuit + ")";
    }
}
