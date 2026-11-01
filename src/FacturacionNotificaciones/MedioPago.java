package FacturacionNotificaciones;

public enum MedioPago {
    EFECTIVO("Efectivo"),
    DEBITO("Tarjeta de debito"),
    CREDITO("Tarjeta de credito en cuotas");

    private final String descripcion;

    MedioPago(String descripcion) { this.descripcion = descripcion; }

    public String getDescripcion() { return descripcion; }
}

