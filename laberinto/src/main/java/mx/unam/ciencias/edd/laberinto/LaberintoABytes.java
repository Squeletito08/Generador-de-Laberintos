package mx.unam.ciencias.edd.laberinto;

import java.io.IOException;

/**
 * Clase para transformar un laberinto a bytes que puedan ser
 * escritos en la salida estandar.
 */
public class LaberintoABytes {

    /** Los cuartos del laberitno vistos como cuartos de solo lectura. */
    private CuartoLaberinto[][] cuartos;

    /* El número de filas del laberinto */
    private int filas;

    /* El número de columnas del laberinto */
    private int columnas;

    /* Arreglo auxiliar para escribir en la salida estandar */
    private byte[] aux;

    /**
     * Constructor.
     * 
     * @param laberinto un laberinto creado por el programa.
     */
    public LaberintoABytes(LaberintoRandom laberinto) {
        this.cuartos = laberinto.getCuartoLaberintos();
        this.filas = laberinto.getFilas();
        this.columnas = laberinto.getColumnas();
        aux = new byte[filas * columnas + 6];
    }

    /**
     * Escribe el laberinto recibido como bytes en la salida estandar.
     * 
     * @throws IOException en caso de que haya un error escribiendo
     *                     en la salida estandar.
     */
    public void escribeSalidaEstandarLaberinto() throws IOException {

        escribeFormatoArchivo();
        escribeDimensiones();

        int cont = 6;
        for (int f = 0; f < filas; f++)
            for (int c = 0; c < columnas; c++)
                aux[cont++] = procesaCuarto(cuartos[f][c]);

        System.out.write(aux);
    }

    /**
     * Escribe los bytes correpondientes a la palabra MAZE en
     * la salida estandar.
     * 
     * @throws IOException en caso de que haya un error escribiendo
     *                     en la salida estandar
     */
    private void escribeFormatoArchivo() throws IOException {
        aux[0] = 0x4d;
        aux[1] = 0x41;
        aux[2] = 0x5a;
        aux[3] = 0x45;
    }

    /**
     * Escribe las filas y las columnas del laberinto en la salida
     * estandar.
     * 
     * @throws IOException en caso de que haya un error escribiendo
     *                     en la salida estandar.
     */
    private void escribeDimensiones() throws IOException {
        aux[4] = (byte) filas;
        aux[5] = (byte) columnas;
    }

    /**
     * Dado un cuarto, lo transforma a un byte.
     * 
     * @param cuarto en cuarto del laberinto.
     * @return el byte correspondiente al cuarto recibido.
     */
    private byte procesaCuarto(CuartoLaberinto cuarto) {
        return (byte) ((puntajeEnByte(cuarto.getPuntaje())) |
                (paredEsteEnByte(cuarto.getParedEste())) |
                (paredNorteEnByte(cuarto.getParedNorte())) |
                (paredOesteEnByte(cuarto.getParedOeste())) |
                (paredSurEnByte(cuarto.getParedSur())) & (0xFF));
    }

    /**
     * Regresa el puntaje del cuarto recorrido 4 posiciones a la izquierda
     * para poder hacer un OR de bits y combinarlo en un solo byte.
     * 
     * @param puntaje el puntaje del cuarto.
     * @return el puntajde del cuarto recorrido 4 posiciones a la izquierda.
     */
    private int puntajeEnByte(int puntaje) {
        return (puntaje << 4);
    }

    /**
     * Regresa la mascará correspondiente al número 1 en caso de que la paredEste
     * del cuarto existe.
     * 
     * @param paredEste el valor de la paredEste del cuarto.
     * @return el número 1 si la paredEste existe, 0 en otro caso.
     */
    private int paredEsteEnByte(boolean paredEste) {
        return (paredEste) ? 1 : 0;
    }

    /**
     * Regresa la mascará correspondiente al número 1 en caso de que la paredNorte
     * del cuarto existe.
     * 
     * @param paredEste el valor de la paredNorte del cuarto.
     * @return el número 2 si la paredNorte existe, 0 en otro caso.
     */
    private int paredNorteEnByte(boolean paredNorte) {
        return (paredNorte) ? 2 : 0;
    }

    /**
     * Regresa la mascará correspondiente al número 1 en caso de que la paredOeste
     * del cuarto existe.
     * 
     * @param paredEste el valor de la paredOeste del cuarto.
     * @return el número 4 si la paredOeste existe, 0 en otro caso.
     */
    private int paredOesteEnByte(boolean paredOeste) {
        return (paredOeste) ? 4 : 0;
    }

    /**
     * Regresa la mascará correspondiente al número 1 en caso de que la paredSur
     * del cuarto existe.
     * 
     * @param paredEste el valor de la paredSur del cuarto.
     * @return el número 1 si la paredSur existe, 0 en otro caso.
     */
    private int paredSurEnByte(boolean paredSur) {
        return (paredSur) ? 8 : 0;
    }

}
