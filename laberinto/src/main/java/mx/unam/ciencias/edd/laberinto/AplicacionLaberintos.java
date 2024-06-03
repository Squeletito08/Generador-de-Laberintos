package mx.unam.ciencias.edd.laberinto;

import java.io.InputStream;
import java.io.IOException;

/*
 * Clase para administrar el modo en el cual se ejecutará
 * el programa.
 */
public class AplicacionLaberintos {

    /* numero de columnas del laberinto */
    private int columnas;

    /* numero de filas del laberinto */
    private int filas;

    /* semilla del generador */
    private int semilla;

    /* para saber si se colocó una semilla */
    private boolean bandera_s;

    /* para saber si el usuario colocó la bandera -g */
    private boolean bandera_g;

    /* para saber si el usuario colocó la bandera -h */
    private boolean bandera_h;

    /* para saber si el usuario colocó la bandera -w */
    private boolean bandera_w;

    /* para saber en que modo ejectuar el programa */
    private boolean entradaEstandar;

    /**
     * Constructor.
     * 
     * @param args argumentos de la linea de comandos.
     */
    public AplicacionLaberintos(String[] args) {

        if (args.length == 0) {
            entradaEstandar = true;
        } else {
            inicializaPropieades(args);

            if (!bandera_g)
                throw new IllegalArgumentException("No se especificó la bandera -g");

            if (!bandera_w)
                throw new IllegalArgumentException("No se especificó la bandera -w");

            if (!bandera_h)
                throw new IllegalArgumentException("No se especificó la bandera -h");

            if (filas > 255 || filas < 2) {
                throw new IndexOutOfBoundsException("El rango valido para las " +
                        "filas del laberinto es 2 <= filas <= 255 ");
            }

            if (columnas > 255 || columnas < 2) {
                throw new IndexOutOfBoundsException("El rango valido para las " +
                        "columnas del laberinto es 2 <= columnas <= 255 ");
            }

            if ((!bandera_s && args.length != 5)
                    || (bandera_s && args.length != 7))
                ProyectoLaberinto.uso();
        }

    }

    /**
     * Busca las banderas "-s", "-w", "-h" y "-g" en los argumentos
     * propocioandos por el usuario, para con estas poder
     * determinar como crear el laberinto.
     * 
     * @param args arguementos de la linea de comandos.
     */
    private void inicializaPropieades(String[] args) {

        for (int i = 0; i < args.length; i++) {

            if (args[i].equals("-g")) {
                if (!bandera_g)
                    bandera_g = true;
                else
                    throw new ExcepcionParametroInvalido("No puede haber 2 banderas '-g'");
            }

            else if (args[i].equals("-s")) {
                try {
                    if (!bandera_s)
                        bandera_s = true;
                    else
                        throw new ExcepcionParametroInvalido("No puede haber 2 banderas 's");

                    semilla = Integer.parseInt(args[i + 1]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new ArrayIndexOutOfBoundsException("Despues de la bandera " +
                            "'-s' debe de ir una semilla para el generador");
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("La semilla " + args[i + 1] + " no es valida");
                }
            }

            else if (args[i].equals("-w")) {
                try {
                    if (!bandera_w)
                        bandera_w = true;
                    else
                        throw new ExcepcionParametroInvalido("No puede haber 2 banderas '-w'");

                    columnas = Integer.parseInt(args[i + 1]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new ArrayIndexOutOfBoundsException("Despues de la bandera " +
                            "'-w' debe ir el numero de columnas del laberinto");
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("El numero de columnas: " + args[i + 1] + " no es valido");
                }
            }

            else if (args[i].equals("-h")) {
                try {
                    if (!bandera_h)
                        bandera_h = true;
                    else
                        throw new ExcepcionParametroInvalido("No puede haber 2 banderas '-h");

                    filas = Integer.parseInt(args[i + 1]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new ArrayIndexOutOfBoundsException("Despues de la bandera " +
                            "'-h' debe ir el numero de filas del laberinto");
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("El numero de filas: " + args[i + 1] + " no es valido");
                }
            }

            else {
                try {
                    if (filas != Integer.parseInt(args[i])
                            && columnas != Integer.parseInt(args[i])
                            && semilla != Integer.parseInt(args[i]))
                        throw new ExcepcionParametroInvalido("El parametro " + args[i] + " no es valido");
                } catch (NumberFormatException e) {
                    throw new ExcepcionParametroInvalido("El parametro " + args[i] + " no es valido");
                }
            }
        }

    }

    /**
     * Decide si el programa leerá de la entrada estandar, o
     * si generará su propio labenrito con los datos proporcionados.
     */
    public void ejecuta() {
        if (entradaEstandar)
            entradaEstandar();
        else
            generedarAleatorio();
    }

    /**
     * El programa lee bytes de la entrada estandar, y dichos bytes serán
     * verificados de acuerdo a las reglas a seguir para el formato de archivos.
     */
    private void entradaEstandar() {
        try {
            /* la entrada estandar del programa */
            InputStream entrada = System.in;

            VerificaFormatoArchivo archivo = new VerificaFormatoArchivo(entrada);
            entrada.close();

            Laberinto lab = new Laberinto(archivo.getValoresLaberinto(), archivo.getFilas(), archivo.getColumnas());
            lab.creaLaberinto();

            LaberintoSVG labSVG = new LaberintoSVG(lab);

            labSVG.creaLaberintoSVG();

            System.out.println(labSVG.getcodigoLaberinto());

        } catch (IOException e) {
            System.out.println("Ocurrio un error al leer de la entrada estandar");
            System.exit(1);
        }
    }

    /**
     * Se crea un objeto instancia de la clase LaberintoRandom para generar un
     * laberinto
     * con el número de filas y columnas especificado. Así mismo, se utiliza una
     * semilla
     * para el generador de número aleatorio en caso de que el usuario la haya
     * especificado.
     */
    private void generedarAleatorio() {
        LaberintoRandom lab = new LaberintoRandom(filas, columnas, bandera_s, semilla);
        lab.creaLaberinto();

        try {
            LaberintoABytes escritor = new LaberintoABytes(lab);
            escritor.escribeSalidaEstandarLaberinto();
        } catch (IOException e) {
            System.out.println("Ocurrio un error al escribir el laberitno en la salida estandar");
            System.exit(1);
        }
    }
}
