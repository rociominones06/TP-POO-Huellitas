package Excepciones;

 // Excepcion propia para reglas de negocio de stock:
 // se lanza cuando se intenta consumir mas unidades de las utilizables.
 
public class StockInsuficienteException extends ReglaNegocioException {

    private static final long serialVersionUID = 1L;

    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }
}