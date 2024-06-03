package mx.unam.ciencias.edd.laberinto;

/*
 * Clase para excepciones de parametros invalidos de la linea de comandos
 */
public class ExcepcionParametroInvalido extends IllegalArgumentException {

    /*
     * Constructor vacio
     */
    public ExcepcionParametroInvalido() {
    }

    /**
     * Excepcion que recibe un mensaje descriptivo
     * explicando el error para el usuario.
     * 
     * @param mensaje
     */
    public ExcepcionParametroInvalido(String mensaje) {
        super(mensaje);
    }
}
