package mx.unam.ciencias.edd.laberinto;

/**
 * Interfaz para cuartos del laberinto. Un cuarto de laberinto puede darnos el
 * puntaje de su cuarto, el valor de sus paredes en las cuatro direcciones, su
 * columna y fila en la que se encuentran respecto a la matriz de cuartos, y
 * su índice.
 */
public interface CuartoLaberinto {

    /**
     * Regresa el valor del puntaje del cuarto.
     * 
     * @return el puntaje del cuarto.
     */
    public int getPuntaje();

    /**
     * Regresa el valor de la pueta Este del cuarto.
     * 
     * @return true si la puerta Este existe, false en otro caso.
     */
    public boolean getParedEste();

    /**
     * Regresa el valor de la puerta Norte del cuarto.
     * 
     * @return true si la puerta Norte existe, false en otro caso.
     */
    public boolean getParedNorte();

    /**
     * Regresa el valor de la puerta Oeste del cuarto.
     * 
     * @return true si la puerta Oeste existe, false en otro caso.
     */
    public boolean getParedOeste();

    /**
     * Regresa el valor de la puerta Sur del cuarto.
     * 
     * @return true si la puerta Sur existe, false en otro caso.
     */
    public boolean getParedSur();

    /**
     * Regresa la columna donde se encuentra el cuarto.
     * 
     * @return la columna del cuarto.
     */
    public int getColumna();

    /**
     * Regresa la fila donde se encuentra el cuarto.
     * 
     * @return la columna del cuarto.
     */
    public int getFila();

    /**
     * Regresa el índice del cuarto.
     * 
     * @return el índice del cuarto.
     */
    public int getIndice();

}
