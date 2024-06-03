package mx.unam.ciencias.edd.laberinto;

import mx.unam.ciencias.edd.Diccionario;

/*Clase que funciona como esquema para crear un laberinto */
public abstract class CreadorLaberinto {

    /* Clase interna protegida para los cuartos de un laberitno */
    protected class Cuarto implements CuartoLaberinto {

        /* el valor de la pared Este */
        protected boolean paredEste;

        /* el valor de la pared Norte */
        protected boolean paredNorte;

        /* el valor de la pared Oeste */
        protected boolean paredOeste;

        /* el valor de la pared Sur */
        protected boolean paredSur;

        /* el valor del punaje del cuarto */
        protected int puntaje;

        /*
         * la fila donde se encuentra el cuarto respecto a
         * la matriz de cuartos del laberinto
         */
        protected int fila;

        /*
         * la columna donde se encuentra el cuarto respecto a
         * la matriz de cuartos del laberinto
         */
        protected int columna;

        /* diccionario de los vecinos del cuarto */
        protected Diccionario<Integer, CuartoVecino> vecinos;

        /*
         * el indice del cuarto respecto a la matriz de cuartos
         * del laberinto
         */
        protected int indice;

        /* para saber si el cuarto ya fue visitado o no */
        protected ColorCuarto color;

        /**
         * Constructor.
         * 
         * @param fila    la fila dodne se encuentra el cuarto.
         * @param columna la columna donde se encuentra el cuarto.
         * @param puntaje el valor del puntaje del cuarto.
         */
        protected Cuarto(int fila, int columna, int puntaje) {

            this.fila = fila;
            this.columna = columna;
            this.puntaje = puntaje;

            paredEste = true;
            paredNorte = true;
            paredOeste = true;
            paredSur = true;

            indice = (fila * columnas) + columna;

            color = ColorCuarto.NO_VISITADO;

            vecinos = new Diccionario<Integer, CuartoVecino>();
        }

        /*
         * Constructor vacío
         */
        protected Cuarto() {
        }

        /**
         * Constructor.
         * 
         * @param dato    el byte con el que se contruirá el cuarto.
         * @param fila    la fila donde se encuentra el cuarto.
         * @param columna la columna donde se encuentra el cuarto.
         */
        protected Cuarto(byte dato, int fila, int columna) {

            this.fila = fila;
            this.columna = columna;

            setParedEste(dato);
            setParedNorte(dato);
            setParedOeste(dato);
            setParedSur(dato);

            setPuntajeCuarto(dato);

            setNumeroCuarto(fila, columna);

            color = ColorCuarto.NO_VISITADO;

            vecinos = new Diccionario<Integer, CuartoVecino>();
        }

        /**
         * Regresa el valor del puntaje del cuarto.
         */
        @Override
        public int getPuntaje() {
            return puntaje;
        }

        /*
         * Regresa el valor de la paredEste del cuarto.
         */
        @Override
        public boolean getParedEste() {
            return paredEste;
        }

        /**
         * Regresa el valor de la paredNorte del cuarto.
         */
        @Override
        public boolean getParedNorte() {
            return paredNorte;
        }

        /**
         * Regresa el valor de la pared Oeste del cuarto.
         */
        @Override
        public boolean getParedOeste() {
            return paredOeste;
        }

        /**
         * Regresa el valor de la pared Sur del cuarto.
         */
        @Override
        public boolean getParedSur() {
            return paredSur;
        }

        /**
         * Regresa la columna donde se encuentra el cuarto.
         */
        @Override
        public int getColumna() {
            return columna;
        }

        /**
         * Regresa la fila donde se encuentra el cuarto.
         */
        @Override
        public int getFila() {
            return fila;
        }

        /**
         * Regresa el índice del cuarto.
         */
        @Override
        public int getIndice() {
            return indice;
        }

        /**
         * Verifica que la pared Este del cuarto recibdo
         * (coorespondiente al bit en la posición número 0) exista.
         * 
         * @param numero el byte a analizar.
         */
        public void setParedEste(byte numero) {
            paredEste = ((numero & 1) > 0) ? true : false;
        }

        /**
         * Verifica que la pared Norte del cuarto recibdo
         * (coorespondiente al bit en la posición número 1) exista.
         * 
         * @param numero el byte a analizar.
         */
        public void setParedNorte(byte numero) {
            paredNorte = ((numero & 2) > 0) ? true : false;
        }

        /**
         * Verifica que la pared Oeste del cuarto recibdo
         * (coorespondiente al bit en la posición número 2) exista.
         * 
         * @param numero el byte a analizar.
         */
        public void setParedOeste(byte numero) {
            paredOeste = ((numero & 4) > 0) ? true : false;
        }

        /**
         * Verifica que la pared Sur del cuarto recibdo
         * (coorespondiente al bit en la posición número 3) exista.
         * 
         * @param numero el byte a analizar.
         */
        public void setParedSur(byte numero) {
            paredSur = ((numero & 8) > 0) ? true : false;
        }

        /**
         * Recorre el byte 4 veces a la izquierda (sin tomar en
         * cuenta el signo) para quedarnos con los 4 bits más significatios
         * del byte y tomar ese valor como el puntaje del cuarto.
         * 
         * @param numero el byte.
         */
        protected void setPuntajeCuarto(byte numero) {
            puntaje = ((numero & 0xFF) >>> 4);
        }

        /**
         * Regresa el número de cuarto de un byte correspondiente
         * al que se encuentra en la fila i y columna j de la
         * matriz de cuartos.
         * 
         * @param i la fila del cuarto.
         * @param j la columna del cuarto.
         */
        protected void setNumeroCuarto(int i, int j) {
            indice = (columnas * i) + j;
        }

    }

    /**
     * Clase interna protegida para los cuartos Vecinos.
     */
    protected class CuartoVecino {

        /* un cuarto adyacente a el cuarto */
        Cuarto vecino;

        /*
         * la direccion en la que se encuentra este vecino
         * respecto al cuarto del que es vecino
         */
        Direccion direccion;

        /* para saber si el vecino ya fue visitado o no */
        ColorCuarto color;

        /**
         * Constructor.
         * 
         * @param vecino    el cuarto vecino.
         * @param direccion la direccion en la que está el cuarto vecino
         *                  respecto al cuarto del que es vecino.
         */
        public CuartoVecino(Cuarto vecino, Direccion direccion) {
            this.vecino = vecino;
            this.direccion = direccion;
            color = ColorCuarto.NO_VISITADO;
        }
    }

    /* los cuartos del laberinto puestos en una matriz */
    protected Cuarto[][] cuartos;

    /* el número de filas del laberinto */
    protected int filas;

    /* el número de columnas del laberitno */
    protected int columnas;

    /* el cuarto desde donde se comenzará a resolver el laberinto */
    protected Cuarto inicioLab;

    /* el cuarto donde se llegará al final del laberinto */
    protected Cuarto finalLab;

    /**
     * Constructor.
     * 
     * @param filas    el número de filas del laberinto.
     * @param columnas el número de columnas del laberinto.
     */
    public CreadorLaberinto(int filas, int columnas) {
        cuartos = new Cuarto[filas][columnas];
        this.filas = filas;
        this.columnas = columnas;
        inicioLab = new Cuarto();
        finalLab = new Cuarto();
    }

    /**
     * Regresa el número de filas del laberinto.
     * 
     * @return las filas del laberinto.
     */
    public int getFilas() {
        return filas;
    }

    /**
     * Regresa el número de columnas del laberinto.
     * 
     * @return las columnas del laberinto.
     */
    public int getColumnas() {
        return columnas;
    }

    /** Crea el laberinto */
    public abstract void creaLaberinto();

    /* Crea un laberitno vacío para que sea "moldeado" después */
    protected abstract void creaEsqueletoLaberinto();

    /**
     * Regresa el Cuarto donde se inicia la solucion del laberinto.
     * 
     * @return el inicio del laberinto visto como un cuarto de solo lectura.
     */
    public CuartoLaberinto getInicioLab() {
        return inicioLab;
    }

    /**
     * Regresa el Cuarto dónde se termina la solucion del laberinto.
     * 
     * @return el final del laberinto visto como un cuarto de solo lectura.
     */
    public CuartoLaberinto getFinalLab() {
        return finalLab;
    }

    /**
     * Regresa los cuartos del laberinto.
     * 
     * @return una matriz con los cuartos del laberinto vistos como
     *         cuartos de solo lectura.
     */
    public CuartoLaberinto[][] getCuartoLaberintos() {
        return cuartos;
    }

}
