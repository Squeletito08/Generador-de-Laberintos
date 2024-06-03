package mx.unam.ciencias.edd.laberinto;

import java.io.InputStream;
import java.io.IOException;

/*
 * Clase para revisar que se cumplan todas las 
 * reglas especificadas para el formato del archivo.
 */
public class VerificaFormatoArchivo {

    /* la entrada estandar de donde leer */
    InputStream entrada;

    /*
     * los bytes del archivo que representan los
     * cuartos del laberinto
     */
    byte[][] valores;

    /* numero de filas del laberinto */
    int filas;

    /* numero de columnas del laberinto */
    int columnas;

    /**
     * Constructor.
     * 
     * @param in entrada estandar.
     * @throws IOException en caso de algún error leyendo de la entada estandar.
     */
    public VerificaFormatoArchivo(InputStream in) throws IOException {

        entrada = in;

        revisaPrimerosSeisBytes(entrada);

        valores = new byte[filas][columnas];

        /* se guardan en el arreglo los bytes restantes del archivo */
        leeBytesLaberinto();
    }

    /**
     * Lee los bytes de la entrada estandar correspondientes a los cuartos
     * del laberitno, y los almecena en una matriz.
     * 
     * @throws IOException en caso de que haya un error leyendo de la
     *                     entrada estandar.
     */
    private void leeBytesLaberinto() throws IOException {

        /* arreglo para leer solo un byte de la entrada estandar */
        byte[] aux = new byte[1];

        for (int k = 0; k < filas; k++) {
            for (int h = 0; h < columnas; h++) {
                entrada.read(aux);
                valores[k][h] = aux[0];
            }
        }

    }

    /**
     * Verifica que los primeros cuatro bytes del archivo correspondan a los
     * especificados en las reglas del proyecto.
     * 
     * @param entrada la entrada estandar.
     * @throws IOException en caso de que haya un error leyendo de la entrada
     *                     estandar.
     */
    private void revisaPrimerosSeisBytes(InputStream entrada) throws IOException {

        /* crea un arreglo con los primeros 6 bytes del flujo */
        byte[] primerosBytes = new byte[6];

        revisaArchivoMze(primerosBytes);

        revisaFilasColumnas(primerosBytes);
    }

    /**
     * Verifica que los primeros 4 bytes del archivo correspondan a la
     * palabra MAZE.
     * 
     * @param primerosBytes los primeros 6 bytes del archivo.
     * @throws IOException en caso de error al leer de la entrada estandar.
     */
    private void revisaArchivoMze(byte[] primerosBytes) throws IOException {

        entrada.read(primerosBytes);

        if (primerosBytes[0] != 0x4d ||
                primerosBytes[1] != 0x41 ||
                primerosBytes[2] != 0x5a ||
                primerosBytes[3] != 0x45) {
            throw new IndexOutOfBoundsException("Los primeros 4 bytes del archivo" +
                    " no coresponden a 0x4d 0x41 0x5a 0x45");
        }
    }

    /**
     * Revisa que las filas y columnas (correspondientes a los byte 4 y byte 5
     * respectivamente) estén dentro del rango permitido para generar un
     * laberinto.
     * 
     * @param primerosBytes los primeros 6 bytes del archivo.
     */
    private void revisaFilasColumnas(byte[] primerosBytes) {
        /*
         * Se le aplica una mascara al valor para evitar problmas
         * con el complemento a 2 en caso de que el valor sea negativo
         */
        filas = primerosBytes[4] & (0xFF);

        if (filas < 2)
            throw new IndexOutOfBoundsException("El valor minimo para las columnas del" +
                    " labetinto es de 2");

        columnas = primerosBytes[5] & (0xFF);

        if (columnas < 2)
            throw new IndexOutOfBoundsException("El valor minimo para los renglones del" +
                    " labetinto es de 2");
    }

    /**
     * Regresa los cuartos del laberinto inicializados con los
     * bytes del archivo.
     * 
     * @return los cuartos del laberinto.
     */
    public byte[][] getValoresLaberinto() {
        return valores;
    }

    /**
     * Regresa el número de filas del laberinto.
     * 
     * @return los renglones del laberinto.
     */
    public int getFilas() {
        return filas;
    }

    /**
     * Regresa el número de columnas del laberinto.
     * 
     * @return cuántas columnas tiene el laberinto.
     */
    public int getColumnas() {
        return columnas;
    }

}
