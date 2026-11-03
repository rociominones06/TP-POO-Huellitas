package Excepciones;

 // Excepcion propia para reglas de negocio de stock (RN4.3, RN4.6):
 // se lanza cuando se intenta consumir mas unidades de las utilizables.
 
public class StockInsuficienteException extends ReglaNegocioException {

    private static final long serialVersionUID = 1L;

    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }
}