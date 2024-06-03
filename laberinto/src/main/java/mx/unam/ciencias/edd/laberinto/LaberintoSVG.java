package mx.unam.ciencias.edd.laberinto;

import java.util.NoSuchElementException;

import mx.unam.ciencias.edd.Lista;
import mx.unam.ciencias.edd.VerticeGrafica;

/**
 * Clase para crear una imágen SVG de un laberinto.
 */
public class LaberintoSVG {

    /* Aquí va todo el codigo de la imagen */
    private StringBuilder codigo;

    /* Ancho de la imagen */
    private double anchoImagen;

    /* Alto de la imagen */
    private double altoImagen;

    /* Color de las lienas */
    private String colorLinea = "black";

    /* Largo de una flecha */
    private final double largoLinea = 20;

    /* Para las lineas */
    private String stroke_width_lineas = "4";

    /* Los cuartos del laberinto vistos como cuartos de solo lectura */
    private CuartoLaberinto[][] cuartos;

    /* La solucion del laberinto */
    private Lista<VerticeGrafica<Integer>> solucion;

    /* El número de filas del laberinto */
    private int filas;

    /* El número de columnas del laberinto */
    private int columnas;

    /* El cuarto donde comienza la solucion del laberinto */
    private CuartoLaberinto inicioLab;

    /* El cuarto donde termina la solucion del laberinto */
    private CuartoLaberinto finalLab;

    /**
     * Constructor.
     * 
     * @param laberinto el laberinto al que se le hará imagen SVG.
     */
    public LaberintoSVG(Laberinto laberinto) {
        codigo = new StringBuilder();
        cuartos = laberinto.getCuartoLaberintos();
        solucion = laberinto.getSolucionLaberinto();
        filas = laberinto.getFilas();
        columnas = laberinto.getColumnas();

        inicioLab = laberinto.getInicioLab();
        finalLab = laberinto.getFinalLab();
    }

    /**
     * Crea la imagen SVG.
     */
    public void creaLaberintoSVG() {
        agregaInicioImagen(filas, columnas);
        dibujaLaberinto();
        agregaFinalImagen();
    }

    /**
     * Regresa el codigo de la imágen SVG del laberinto.
     * 
     * @return el codigo SVG.
     */
    public String getcodigoLaberinto() {
        return codigo.toString();
    }

    /**
     * Define el ancho y alto de una imagen de acuerdo con el número de
     * elementos enteros que contendrá la estructura.
     */
    private void defineTamanoImagen(int filas, int columnas) {
        /* se le agrega un margen a los lados */
        altoImagen = (filas * largoLinea) + (largoLinea * 2);
        anchoImagen = (columnas * largoLinea) + (largoLinea * 2);
    }

    /**
     * Define las primeras lineas de codigo de la imagen: la version,
     * el encoding, y las dimensiones de la misma.
     */
    private void agregaInicioImagen(int filas, int columnas) {
        defineTamanoImagen(filas, columnas);
        codigo.append("<?xml version='1.0' encoding='UTF-8' ?>\n");
        codigo.append("<svg width='");
        codigo.append(anchoImagen);
        codigo.append("' height='");
        codigo.append(altoImagen);
        codigo.append("'>\n");
    }

    /**
     * Agrega el codigo a la imagen correspondiente a todos los
     * elementos del laberinto recibido.
     */
    private void dibujaLaberinto() {
        dibujaFondo();

        dibujaParedesEste();
        dibujaParedesNorte();
        dibujaParedesOeste();
        dibujaParedesSur();

        dibujaRellenoLaberinto();

        dibujaSolucionLaberinto();
    }

    /**
     * Dibuja las paredes Norte del laberinto.
     */
    private void dibujaParedesNorte() {
        for (int i = 0; i < columnas; i++)
            if (cuartos[0][i].getParedNorte())
                dibujaParedCuarto(cuartos[0][i], Direccion.NORTE);
    }

    /*
     * Dibuja las paredes Este del laberinto.
     */
    private void dibujaParedesEste() {
        for (int i = 0; i < filas; i++)
            if (cuartos[i][columnas - 1].getParedEste())
                dibujaParedCuarto(cuartos[i][columnas - 1], Direccion.ESTE);

    }

    /**
     * Dibuja las paredes Oeste del laberinto.
     */
    private void dibujaParedesOeste() {
        for (int i = 0; i < filas; i++)
            if (cuartos[i][0].getParedOeste())
                dibujaParedCuarto(cuartos[i][0], Direccion.OESTE);

    }

    /**
     * Dibuja las paredesSur del laberinto.
     */
    private void dibujaParedesSur() {
        for (int i = 0; i < columnas; i++)
            if (cuartos[filas - 1][i].getParedSur())
                dibujaParedCuarto(cuartos[filas - 1][i], Direccion.SUR);

    }

    /**
     * Dibuja las puertas este y sur de los cuartos del laberinto, pues una vez
     * dibujadas la frontera del laberinto, a cada cuarto solo hace falta dibujarle
     * sus puertas en dichas direcciones.
     */
    private void dibujaRellenoLaberinto() {
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                if (cuartos[f][c].getParedEste())
                    dibujaParedCuarto(cuartos[f][c], Direccion.ESTE);
                if (cuartos[f][c].getParedSur())
                    dibujaParedCuarto(cuartos[f][c], Direccion.SUR);
            }
        }
    }

    /**
     * Agrega la linea que cierra la etiqueta <svg> principal.
     */
    public void agregaFinalImagen() {
        codigo.append("</svg>\n");
    }

    /**
     * Dibuja un fondo blanco para la imágen.
     */
    private void dibujaFondo() {
        codigo.append("\t\t<rect fill='");
        codigo.append("white");
        codigo.append("' stroke='");
        codigo.append("white");
        codigo.append("' stroke-width='");
        codigo.append("1");
        codigo.append("' width='");
        codigo.append(anchoImagen);
        codigo.append("' height='");
        codigo.append(altoImagen);
        codigo.append("' /> \n");
    }

    /**
     * Dibuja una pared del cuarto recibida.
     * 
     * @param cuarto         el cuarto a dibujarle pared.
     * @param direccionPared la direccion en la que se dibujará la pared.
     */
    public void dibujaParedCuarto(CuartoLaberinto cuarto, Direccion direccionPared) {

        double posX = calculaPosX(cuarto.getColumna());
        double posY = calculaPosY(cuarto.getFila());

        switch (direccionPared) {
            case ESTE:
                dibujaLinea((posX + largoLinea), posY, (posX + largoLinea), (posY + largoLinea));
                break;
            case NORTE:
                dibujaLinea(posX, posY, (posX + largoLinea), posY);
                break;
            case OESTE:
                dibujaLinea(posX, posY, posX, (posY + largoLinea));
                break;
            case SUR:
                dibujaLinea(posX, (posY + largoLinea), (posX + largoLinea), (posY + largoLinea));
                break;
        }
    }

    /**
     * Dibuja la solucion del laberinto.
     */
    public void dibujaSolucionLaberinto() {

        colorLinea = "#960C0C";
        stroke_width_lineas = "7";

        int longitud = solucion.getLongitud();

        int indiceActual = solucion.eliminaPrimero().get();
        int indiceVecinoActual;
        Direccion direccionVecino;

        double p1_x = calculaPosXEnMedio(obtenColumnaCuarto(indiceActual));
        double p1_y = calculaPosYEnMedio(obtenFilaCuarto(indiceActual));

        double p2_x;
        double p2_y;

        for (int i = 0; i < longitud; i++) {
            try {
                indiceVecinoActual = solucion.eliminaPrimero().get();

                direccionVecino = obtenDireccionVecino(indiceActual, indiceVecinoActual);

                p2_x = calculaPosXEnMedio(obtenColumnaCuarto(indiceVecinoActual));
                p2_y = calculaPosYEnMedio(obtenFilaCuarto(indiceVecinoActual));

                switch (direccionVecino) {
                    case ESTE:
                        dibujaLinea(p1_x, p1_y, p2_x, p2_y);
                        break;
                    case NORTE:
                        dibujaLinea(p1_x, p1_y, p2_x, p2_y);
                        break;
                    case OESTE:
                        dibujaLinea(p1_x, p1_y, p2_x, p2_y);
                        break;
                    case SUR:
                        dibujaLinea(p1_x, p1_y, p2_x, p2_y);
                        break;
                }

                p1_x = p2_x;
                p1_y = p2_y;
            } catch (NoSuchElementException e) {
            }
        }

        dibujaInicio();
        dibujaFinal();
    }

    /**
     * Dibuja un circulo en el cuarto correspondiente al inicio
     * de la solución del laberinto.
     */
    public void dibujaInicio() {

        double cx = calculaPosXEnMedio(inicioLab.getColumna());
        double cy = calculaPosYEnMedio(inicioLab.getFila());

        dibujaCirculo(cx, cy, "#2FC489");
    }

    /**
     * Dibuja un circulo en el cuarto correspondienre al final
     * de la solución del laberinto.
     */
    public void dibujaFinal() {

        double cx = calculaPosXEnMedio(finalLab.getColumna());
        double cy = calculaPosYEnMedio(finalLab.getFila());

        dibujaCirculo(cx, cy, "#5B60D2");
    }

    /**
     * Dibuja una línea.
     * 
     * @param p1_x la posicion x inicial
     * @param p1_y la posicion y inicial.
     * @param p2_x la posicion x final.
     * @param p2_y la posicion y final.
     */
    private void dibujaLinea(double p1_x, double p1_y,
            double p2_x, double p2_y) {
        codigo.append("\t\t<line x1='");
        codigo.append(p1_x);
        codigo.append("' y1='");
        codigo.append(p1_y);
        codigo.append("' x2='");
        codigo.append(p2_x);
        codigo.append("' y2='");
        codigo.append(p2_y);
        codigo.append("' stroke='");
        codigo.append(colorLinea);
        codigo.append("' stroke-width='");
        codigo.append(stroke_width_lineas);
        codigo.append("'/> \n");
    }

    /**
     * Dibuja un circulo.
     * 
     * @param cx    la posicion x del circulo.
     * @param cy    la posicion y del circulo.
     * @param color el color del circulo.
     */
    private void dibujaCirculo(double cx, double cy, String color) {

        codigo.append("\t\t<circle cx='");
        codigo.append(cx);
        codigo.append("' cy='");
        codigo.append(cy);
        codigo.append("' r='");
        codigo.append("6");
        codigo.append("' stroke='");
        codigo.append("black");
        codigo.append("' stroke-width='");
        codigo.append("2");
        codigo.append("' fill='");
        codigo.append(color);
        codigo.append("'/> \n");
    }

    /**
     * Obtiene un punto en la posicion X para dibujar una linea de un cuarto
     * que está en la columna dada.
     * 
     * @param columnaCuarto la columna donde está el cuarto donde se dibujará.
     * @return el valor en X donde debe de comenzarse a dibujar la línea.
     */
    private double calculaPosX(int columnaCuarto) {
        return (largoLinea * columnaCuarto) + largoLinea;
    }

    /**
     * Obtiene un punto en la posición Y para dibujar una línea de un cuarto
     * que está en la fila dada.
     * 
     * @param filaCuarto la fila donde está el cuarto donde se dibujará.
     * @return el valor en Y donde se debe a comenzar a dibujar la línea.
     */
    private double calculaPosY(int filaCuarto) {
        return (largoLinea * filaCuarto) + largoLinea;
    }

    /**
     * Obtiene un punto en la posicion X para dibujar una linea justo en
     * medio de un cuarto que está en la columna dada.
     * 
     * @param columnaCuarto la columna donde está el cuarto donde se dibujará.
     * @return el valor en X donde debe de comenzarse a dibujar la línea.
     */
    private double calculaPosXEnMedio(int columnaCuarto) {
        return (largoLinea * columnaCuarto) + (largoLinea * 1.5);
    }

    /**
     * Obtiene un punto en la posición Y para dibujar una línea justo en
     * medio de un cuarto que está en la fila dada.
     * 
     * @param filaCuarto la fila donde está el cuarto donde se dibujará.
     * @return el valor en Y donde se debe a comenzar a dibujar la línea.
     */
    private double calculaPosYEnMedio(int filaCuarto) {
        return (largoLinea * filaCuarto) + (largoLinea * 1.5);
    }

    /**
     * Obtiene la columna donde se encunetra el cuarto correspondiente
     * al indice recibido.
     * 
     * @param numeroCuarto el indice del cuarto.
     * @return la columna donde se encuentra el cuarto respecto a la matriz
     *         de cuartos.
     */
    private int obtenColumnaCuarto(int numeroCuarto) {
        return (numeroCuarto % columnas);
    }

    /**
     * Obtiene la fila donde se encuentra el cuarto correspondiente
     * al indice recibido.
     * 
     * @param numeroCuarto el indice del cuarto.
     * @return la columna donde se encuentra el cuarto respecto a la matriz
     *         de cuartos.
     */
    private int obtenFilaCuarto(int numeroCuarto) {
        return (numeroCuarto / columnas);
    }

    /**
     * Regresa la direccion en la que se encuentra el vecino del
     * elemento actual.
     * 
     * @param actual el cuarto actual.
     * @param vecino el cuarto vecino del actual.
     * @return la dirección del vecino respecto al cuarto actual.
     */
    private Direccion obtenDireccionVecino(int indiceActual, int indiceVecino) {

        if ((indiceActual + 1) == indiceVecino)
            return Direccion.ESTE;

        if ((indiceActual - 1) == indiceVecino)
            return Direccion.OESTE;

        if ((indiceActual + columnas) == indiceVecino)
            return Direccion.SUR;

        return Direccion.NORTE;
    }

}
