package mx.unam.ciencias.edd.laberinto;

import java.util.NoSuchElementException;
import java.util.Random;

import mx.unam.ciencias.edd.Diccionario;
import mx.unam.ciencias.edd.Pila;

/**
 * Clase para crear un laberinto aleatorio apartir de los datos
 * dados por el usuario.
 */
public class LaberintoRandom extends CreadorLaberinto {

    /* El RNG */
    private Random random;

    /**
     * Constructor.
     * 
     * @param filas     el número de filas del laberinto.
     * @param columnas  el número de columnasd del laberinto.
     * @param bandera_s bandera para saber si ocupar la semilla que pudo haber
     *                  proporcionado el usuario.
     * @param semilla   la semill para el RNG.
     */
    public LaberintoRandom(int filas, int columnas, boolean bandera_s, int semilla) {
        super(filas, columnas);

        if (bandera_s)
            random = new Random(semilla);
        else
            random = new Random();

    }

    /**
     * Crea el laberinto en base a desiciones "aleatorias".
     */
    @Override
    public void creaLaberinto() {
        creaEsqueletoLaberinto();

        while (inicioLab.indice == finalLab.indice) {
            inicioLab = generaInicioFinal();
            finalLab = generaInicioFinal();
        }

        cuartos[inicioLab.fila][inicioLab.columna] = inicioLab;
        cuartos[finalLab.fila][finalLab.columna] = finalLab;

        asignaVecinosCuarto();

        daleFormaAlLaberinto(inicioLab);
    }

    /**
     * Crea un total de (filas*columnas) cuartos para el laberinto,
     * cada uno con un puntaje aleatorio en un rango de 0-15.
     */
    @Override
    protected void creaEsqueletoLaberinto() {
        for (int f = 0; f < filas; f++)
            for (int c = 0; c < columnas; c++)
                cuartos[f][c] = new Cuarto(f, c, random.nextInt(16));
    }

    /*
     * Genera un cuarto especial para el laberinto (ya sea el incio o el final
     * del laberinto).
     */
    private Cuarto generaInicioFinal() {
        int columna = 0;
        int fila = 0;
        Cuarto cuarto;

        switch (random.nextInt(4)) {
            /* norte */
            case 0:
                columna = random.nextInt(columnas);
                cuarto = new Cuarto(0, columna, random.nextInt(16));
                cuarto.paredNorte = false;
                return cuarto;

            /* este */
            case 1:
                fila = random.nextInt(filas);
                cuarto = new Cuarto(fila, columnas - 1, random.nextInt(16));
                cuarto.paredEste = false;
                return cuarto;

            /* sur */
            case 2:
                columna = random.nextInt(columnas);
                cuarto = new Cuarto(filas - 1, columna, random.nextInt(16));
                cuarto.paredSur = false;
                return cuarto;

            /* oeste */
            case 3:
                fila = random.nextInt(filas);
                cuarto = new Cuarto(fila, 0, random.nextInt(16));
                cuarto.paredOeste = false;
                return cuarto;
        }

        return null;
    }

    /**
     * Para cada cuarto trata de asignarle sus cuartos vecinos en las cuatro
     * direcciones posibles.
     */
    protected void asignaVecinosCuarto() {
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                try {
                    CuartoVecino vecino = new CuartoVecino(cuartos[f][c + 1], Direccion.ESTE);
                    cuartos[f][c].vecinos.agrega(0, vecino);
                } catch (ArrayIndexOutOfBoundsException e) {
                }

                try {
                    CuartoVecino vecino = new CuartoVecino(cuartos[f - 1][c], Direccion.NORTE);
                    cuartos[f][c].vecinos.agrega(1, vecino);
                } catch (ArrayIndexOutOfBoundsException e) {
                }

                try {
                    CuartoVecino vecino = new CuartoVecino(cuartos[f][c - 1], Direccion.OESTE);
                    cuartos[f][c].vecinos.agrega(2, vecino);
                } catch (ArrayIndexOutOfBoundsException e) {
                }

                try {
                    CuartoVecino vecino = new CuartoVecino(cuartos[f + 1][c], Direccion.SUR);
                    cuartos[f][c].vecinos.agrega(3, vecino);
                } catch (ArrayIndexOutOfBoundsException e) {
                }
            }
        }
    }

    /*
     * Rompe paredes del laberinto de tal forma que todos los cuartos del laberinto
     * tendrán
     * al menos una pared abierta conectando a otro cuarto, y así mismo haciendo que
     * el laberinto
     * tenga solución.
     */
    private void daleFormaAlLaberinto(Cuarto cuarto) {

        Pila<Cuarto> pila = new Pila<Cuarto>();

        pila.mete(cuarto);
        cuarto.color = ColorCuarto.VISITADO;

        while (!pila.esVacia()) {
            Cuarto actual = pila.mira();

            if (hayVecinosSinVisitar(actual.vecinos)) {
                CuartoVecino vecino = null;
                boolean seObtuvoVecino = false;

                while (!seObtuvoVecino) {
                    try {
                        vecino = actual.vecinos.get(random.nextInt(4));
                        seObtuvoVecino = true;
                    } catch (NoSuchElementException e) {
                    }
                }
                rompeParedDireccionDada(actual, vecino.vecino, vecino.direccion);
                vecino.vecino.color = ColorCuarto.VISITADO;
                vecino.color = ColorCuarto.VISITADO;
                pila.mete(vecino.vecino);
            } else {
                pila.saca();
            }
        }
    }

    /**
     * Nos dice si aún hay vecinos que puedan ser visitador en el diccionario de
     * vecinos recibidos.
     * 
     * @param dic el diccinario de vecinos de un cuarto.
     * @return true si aún hay al menos un vecinos que se pueda visitar, false si
     *         todos los vecinos del diccionario ya fueron visitados.
     */
    private boolean hayVecinosSinVisitar(Diccionario<Integer, CuartoVecino> dic) {
        for (CuartoVecino vecino : dic)
            if (vecino.color == ColorCuarto.NO_VISITADO)
                return true;

        return false;
    }

    /**
     * "Rompe" (pone como false una de las paredes propieadad del cuarto) las
     * paredes
     * correspondientes de los cuartos recibidos en la direccion dada.
     * 
     * @param actual    el cuarto actual.
     * @param vecino    el cuarto vecino al cuarto actual.
     * @param direccion la direccion del vecino respecto al cuarto actual.
     */
    private void rompeParedDireccionDada(Cuarto actual, Cuarto vecino, Direccion direccion) {

        if (vecino.color == ColorCuarto.VISITADO)
            return;

        switch (direccion) {
            case ESTE:
                actual.paredEste = false;
                vecino.paredOeste = false;
                break;

            case NORTE:
                actual.paredNorte = false;
                vecino.paredSur = false;
                break;

            case OESTE:
                actual.paredOeste = false;
                vecino.paredEste = false;
                break;

            case SUR:
                actual.paredSur = false;
                vecino.paredNorte = false;
                break;
        }

    }

}
