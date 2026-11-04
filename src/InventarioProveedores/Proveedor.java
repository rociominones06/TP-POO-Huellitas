package InventarioProveedores;
import java.io.Serializable;

// Representa a un proveedor de medicamentos e insumos.

public class Proveedor implements Serializable {
    private static final long serialVersionUID = 1L;
    private String cuit;
    private String razonSocial;
    private String telefono;
    private String direccion;

    public Proveedor(String cuit, String razonSocial, String telefono, String direccion) {
        this.cuit = cuit;
        this.razonSocial = razonSocial;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    public String getCuit() {
        return cuit;
    }
    public String getRazonSocial() {
        return razonSocial;
    }
    public String getTelefono() {
        return telefono;
    }
    public String getDireccion() {
        return direccion;
    }

    // Registrar el suministro de un nuevo lote
    public void suministrarLote(Producto producto, Lote lote) {
        if (producto != null && lote != null) {
            producto.agregarLote(lote);
        }
    }

    @Override
    public String toString() {
        return razonSocial + " (CUIT: "+ cuit + ")";
    }
}
