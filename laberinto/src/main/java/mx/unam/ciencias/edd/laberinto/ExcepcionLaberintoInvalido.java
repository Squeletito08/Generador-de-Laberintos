package mx.unam.ciencias.edd.laberinto;

/*
 * Clase para excepciones cuando un laberinto es invalido
 */
public class ExcepcionLaberintoInvalido extends IllegalArgumentException {

    /*
     * Constructor vacio
     */
    public ExcepcionLaberintoInvalido() {
    }

    /**
     * Excepcion que recibe un mensaje descriptivo
     * explicando el error para el usuario.
     * 
     * @param mensaje
     */
    public ExcepcionLaberintoInvalido(String mensaje) {
        super(mensaje);
    }
}
