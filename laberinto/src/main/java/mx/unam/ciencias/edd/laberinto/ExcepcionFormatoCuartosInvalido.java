package mx.unam.ciencias.edd.laberinto;

/*
 * Clase para excepciones de formato de cuartos 
 * de laberintos invalidos. 
 */
public class ExcepcionFormatoCuartosInvalido extends IllegalArgumentException {

    /*
     * Constructor vacio
     */
    public ExcepcionFormatoCuartosInvalido() {
    }

    /**
     * Excepcion que recibe un mensaje descriptivo
     * explicando el error para el usuario.
     * 
     * @param mensaje
     */
    public ExcepcionFormatoCuartosInvalido(String mensaje) {
        super(mensaje);
    }
}
