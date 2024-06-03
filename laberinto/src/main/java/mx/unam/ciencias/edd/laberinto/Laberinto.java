package mx.unam.ciencias.edd.laberinto;

import mx.unam.ciencias.edd.VerticeGrafica;
import mx.unam.ciencias.edd.Lista;
import mx.unam.ciencias.edd.Grafica;

/**
 * Clase para crear un laberinto.
 */
public class Laberinto extends CreadorLaberinto {

    /*
     * Los bytes recibidos por entrada estandar
     * puestos en una matriz
     */
    protected byte[][] datos;

    /*
     * El número de cuartos especiales del laberinto
     * (debe de ser exactamente 2)
     */
    protected int cuartosEspeciales;

    /* La solucion del laberinto */
    protected Lista<VerticeGrafica<Integer>> solucion;

    /* el laberinto visto como una grafica */
    protected Grafica<Integer> laberinto;

    /**
     * Constructor.
     * 
     * @param datos    los bytes recibidos por entrada estandar.
     * @param filas    número de filas del laberinto.
     * @param columnas número de columnas del laberinto.
     */
    public Laberinto(byte[][] datos, int filas, int columnas) {
        super(filas, columnas);
        this.datos = datos;
        cuartosEspeciales = 0;
        laberinto = new Grafica<>();
    }

    /**
     * Crea una grafica sin aristas, donde cada vertice
     * es la posicion de cada cuarto del laberinto de acuerdo
     * al arreglo de bytes de la entrada estandar.
     */
    protected void creaGraficaLaberintoVacia() {
        for (int i = 0; i < (filas * columnas); i++)
            laberinto.agrega(i);
    }

    /**
     * Crea un laberinto con los bytes recibidos por entrada estandar.
     */
    @Override
    public void creaLaberinto() {
        creaGraficaLaberintoVacia();
        creaEsqueletoLaberinto();
        verificaCuartosFrontera();
        procesaLaberinto();
        resuelveLaberinto();
    }

    /**
     * Crea un cuarto con y por cada byte recibido por entrada estandar
     */
    @Override
    protected void creaEsqueletoLaberinto() {
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                cuartos[f][c] = new Cuarto(datos[f][c], f, c);
            }
        }
    }

    /**
     * Genera el laberinto correspondiente a los bytes recibidos por
     * entrada estandar.
     */
    protected void procesaLaberinto() {
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                try {
                    procesaCuartos(cuartos[f][c], cuartos[f][c + 1], Direccion.ESTE);
                } catch (ArrayIndexOutOfBoundsException e) {
                }

                try {
                    procesaCuartos(cuartos[f][c], cuartos[f + 1][c], Direccion.SUR);
                } catch (ArrayIndexOutOfBoundsException e) {
                }
            }
        }
    }

    /**
     * Checa que las paredes de los cuartos sean consistentes, por ejemplo:
     * si el cuarto en (0,0) tiene como bits menos significativos a 0110
     * (sólo las puertas Este y Sur existen), entonces el cuarto en (1,0)
     * debe tener su tercer bit menos significativo apagado, porque corresponde
     * a su puerta Oeste: x0xx.
     * 
     * @param cuartoActual    un cuarto del laberinto.
     * @param vecinoActual    un cuarto adyacente al cuarto del laberinto.
     * @param direccionVecino la dirección en la que está el vecino del cuarto.
     */
    protected void procesaCuartos(Cuarto actual, Cuarto vecino, Direccion direccion) {

        boolean paredActual = false;
        boolean paredVecino = false;

        switch (direccion) {

            case ESTE:
                paredActual = actual.paredEste;
                paredVecino = vecino.paredOeste;
                break;

            case SUR:
                paredActual = actual.paredSur;
                paredVecino = vecino.paredNorte;
                break;

            case NORTE:
                break;
            case OESTE:
                break;
        }

        /* Verifica el formato de las paredes */
        if (!paredActual) {
            if (paredVecino) {
                throw new ExcepcionFormatoCuartosInvalido(
                        "Dos cuartos adyacentes deben de ser consistentes con sus puertas");
            } else {
                laberinto.conecta(actual.indice, vecino.indice, actual.puntaje + vecino.puntaje + 1);
            }
        }
    }

    /**
     * Verifica que los cuartos de la frontera del laberinto
     * sean consistentes con las reglas del Proyecto.
     */
    protected void verificaCuartosFrontera() {
        verificaCuartosFronteraEste();
        verificaCuartosFronteraNorte();
        verificaCuartosFronteraOeste();
        verificaCuartosFronteraSur();
    }

    /**
     * Verifica que los cuartos del laberinto con coordenada
     * (j,0) donde la primera coordenada son las columnas,
     * la segunda son los rengloes y con 0≤j<m, tengan la puerta
     * Norte existente.
     */
    protected void verificaCuartosFronteraNorte() {
        for (int i = 0; i < columnas; i++) {
            if (!cuartos[0][i].paredNorte)
                asignaInicioFinalLaberinto(cuartos[0][i]);
        }
    }

    /**
     * Verifica que los cuartos del laberinto con coordenada
     * (m-1,j), donde la primera coordenada son las columnas,
     * la segunda son los rengloes y con 0≤j<n, tengan la puerta
     * Este existente.
     */
    protected void verificaCuartosFronteraEste() {
        for (int i = 0; i < filas; i++) {
            if (!cuartos[i][columnas - 1].paredEste)
                asignaInicioFinalLaberinto(cuartos[i][columnas - 1]);
        }
    }

    /**
     * Verifica que los cuartos del laberinto con coordenada
     * (0,i), donde la primera coordenada son las columnas,
     * la segunda son los rengloes y con 0≤i<n, tengan la puerta
     * Oeste existente.
     */
    protected void verificaCuartosFronteraOeste() {
        for (int i = 0; i < filas; i++) {
            if (!cuartos[i][0].paredOeste)
                asignaInicioFinalLaberinto(cuartos[i][0]);
        }
    }

    /**
     * Verifica que los cuartos del laberinto con coordenada
     * (j,n−1) donde la primera coordenada son las columnas,
     * la segunda son los rengloes y con 0≤j<m, tengan la puerta
     * Sur existente.
     */
    protected void verificaCuartosFronteraSur() {
        for (int i = 0; i < columnas; i++) {
            if (!cuartos[filas - 1][i].paredSur)
                asignaInicioFinalLaberinto(cuartos[filas - 1][i]);
        }
    }

    /**
     * Asigna el número de cuarto correspondiente al inicio
     * o al final del laberinto de acuerdo a su fila y columna
     * en la matriz de cuartos.
     * 
     * @param i la fila del cuarto.
     * @param j la columna del cuarto.
     */
    protected void asignaInicioFinalLaberinto(Cuarto cuartoEspecial) {
        if (cuartosEspeciales == 0)
            inicioLab = cuartoEspecial;

        if (cuartosEspeciales == 1)
            finalLab = cuartoEspecial;

        if (cuartosEspeciales == 2) {
            throw new ExcepcionFormatoCuartosInvalido("El formato del archivo es invalido" +
                    ", pues no hay solo una salida y un final para el laberinto");
        }
        cuartosEspeciales++;
    }

    /**
     * Resuelve el laberinto (visto como una grafica) con el algoritmo de
     * dijkstra, que toma como argumenos al inicio y final del laberinto
     * (los cuartos especiales).
     */
    protected void resuelveLaberinto() {
        solucion = laberinto.dijkstra(inicioLab.indice, finalLab.indice);

        if (solucion.esVacia())
            throw new ExcepcionLaberintoInvalido("El archivo es invalido pues no se puede"
                    + " recorrer del cuarto de entrada al cuarto de salida");
    }

    /**
     * Regresa la solución del laberinto.
     * 
     * @return la trayectoria de peso minimo dada por el algoritmo de dijskitra.
     */
    public Lista<VerticeGrafica<Integer>> getSolucionLaberinto() {
        return solucion;
    }
}
