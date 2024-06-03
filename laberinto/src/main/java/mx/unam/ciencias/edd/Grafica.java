package mx.unam.ciencias.edd;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para gráficas. Una gráfica es un conjunto de vértices y aristas, tales
 * que las aristas son un subconjunto del producto cruz de los vértices.
 */
public class Grafica<T> implements Coleccion<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Iterador auxiliar. */
        private Iterator<Vertice> iterador;

        /* Construye un nuevo iterador, auxiliándose de la lista de vértices. */
        public Iterador() {
            iterador = vertices.iterator(); 
        }

        /* Nos dice si hay un siguiente elemento. */
        @Override public boolean hasNext() {
            return iterador.hasNext(); 
        }

        /* Regresa el siguiente elemento. */
        @Override public T next() {
            return iterador.next().elemento; 
        }
    }

    /* Clase interna privada para vértices. */
    private class Vertice implements VerticeGrafica<T>,
                          ComparableIndexable<Vertice> {

        /* El elemento del vértice. */
        private T elemento;
        /* El color del vértice. */
        private Color color;
        /* La distancia del vértice. */
        private double distancia;
        /* El índice del vértice. */
        private int indice;
        /* El diccionario de vecinos del vértice. */
        private Diccionario<T, Vecino> vecinos;

        /* Crea un nuevo vértice a partir de un elemento. */
        public Vertice(T elemento) {
            this.elemento = elemento; 
            color = Color.NINGUNO; 
            vecinos = new Diccionario<T, Vecino>();
        }

        /* Regresa el elemento del vértice. */
        @Override public T get() {
            return elemento; 
        }

        /* Regresa el grado del vértice. */
        @Override public int getGrado() {
            return vecinos.getElementos(); 
        }

        /* Regresa el color del vértice. */
        @Override public Color getColor() {
            return color; 
        }

        /* Regresa un iterable para los vecinos. */
        @Override public Iterable<? extends VerticeGrafica<T>> vecinos() {
            return vecinos; 
        }

        /* Define el índice del vértice. */
        @Override public void setIndice(int indice) {
            this.indice = indice;
        }

        /* Regresa el índice del vértice. */
        @Override public int getIndice() {
            return indice; 
        }

        /* Compara dos vértices por distancia. */
        @Override public int compareTo(Vertice vertice) {

            if(distancia > vertice.distancia)
                return 1; 

            if(distancia < vertice.distancia)
                return -1; 
            
            return 0;
        }
    }

    /* Clase interna privada para vértices vecinos. */
    private class Vecino implements VerticeGrafica<T> {

        /* El vértice vecino. */
        public Vertice vecino;
        /* El peso de la arista conectando al vértice con su vértice vecino. */
        public double peso;

        /* Construye un nuevo vecino con el vértice recibido como vecino y el
         * peso especificado. */
        public Vecino(Vertice vecino, double peso) {
            this.vecino = vecino; 
            this.peso = peso; 
        }

        /* Regresa el elemento del vecino. */
        @Override public T get() {
            return vecino.elemento; 
        }

        /* Regresa el grado del vecino. */
        @Override public int getGrado() {
            return vecino.getGrado(); 
        }

        /* Regresa el color del vecino. */
        @Override public Color getColor() {
            return vecino.color; 
        }

        /* Regresa un iterable para los vecinos del vecino. */
        @Override public Iterable<? extends VerticeGrafica<T>> vecinos() {
            return vecino.vecinos();
        }
    }

    /* Interface para poder usar lambdas al buscar el elemento que sigue al
     * reconstruir un camino. */
    @FunctionalInterface
    private interface BuscadorCamino<T> {
        /* Regresa true si el vértice se sigue del vecino. */
        public boolean seSiguen(Grafica<T>.Vertice v, Grafica<T>.Vecino a);
    }

    /* Vértices. */
    private Diccionario<T, Vertice> vertices;
    /* Número de aristas. */
    private int aristas;

    /**
     * Constructor único.
     */
    public Grafica() {
        vertices = new Diccionario<T, Vertice>();
    }

    /**
     * Regresa el número de elementos en la gráfica. El número de elementos es
     * igual al número de vértices.
     * @return el número de elementos en la gráfica.
     */
    @Override public int getElementos() {
        return vertices.getElementos();
    }

    /**
     * Regresa el número de aristas.
     * @return el número de aristas.
     */
    public int getAristas() {
        return aristas; 
    }

    /**
     * Agrega un nuevo elemento a la gráfica.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si el elemento ya había sido agregado a
     *         la gráfica.
     */
    @Override public void agrega(T elemento) {
        if(elemento == null)
            throw new IllegalArgumentException("El elemento es null");
        
        if(contiene(elemento))
            throw new IllegalArgumentException("El vertice ya está en la gráfica");
        
        vertices.agrega(elemento,new Vertice(elemento));
    }

    /**
     * Conecta dos elementos de la gráfica. Los elementos deben estar en la
     * gráfica. El peso de la arista que conecte a los elementos será 1.
     * @param a el primer elemento a conectar.
     * @param b el segundo elemento a conectar.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b ya están conectados, o si a es
     *         igual a b.
     */
    public void conecta(T a, T b) {
        conecta(a, b, 1);
    }

    /**
     * Conecta dos elementos de la gráfica. Los elementos deben estar en la
     * gráfica.
     * @param a el primer elemento a conectar.
     * @param b el segundo elemento a conectar.
     * @param peso el peso de la nueva vecino.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b ya están conectados, si a es
     *         igual a b, o si el peso es no positivo.
     */
    public void conecta(T a, T b, double peso) {

        if(a.equals(b))
            throw new IllegalArgumentException("No se puede conectar un vertice consigo mismo");

        if(peso <= 0)
            throw new IllegalArgumentException("No se pueden conectar 2 vertices con peso menor o igual a 0");

        Vertice verticeA = (Vertice)vertice(a);
        Vertice verticeB = (Vertice)vertice(b);

        if(verticeA.vecinos.contiene(b))
            throw new IllegalArgumentException("No se pueden conectar 2 vertices ya conectados");

        Vecino vecinoA = new Vecino(verticeA, peso);
        Vecino vecinoB = new Vecino(verticeB, peso);

        verticeA.vecinos.agrega(b,vecinoB);
        verticeB.vecinos.agrega(a,vecinoA);

        aristas++;
    }

    /**
     * Desconecta dos elementos de la gráfica. Los elementos deben estar en la
     * gráfica y estar conectados entre ellos.
     * @param a el primer elemento a desconectar.
     * @param b el segundo elemento a desconectar.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b no están conectados.
     */
    public void desconecta(T a, T b) {
            
        Vertice vA_vertice = (Vertice)vertice(a);
        Vertice vB_vertice = (Vertice)vertice(b);

        if(!(vA_vertice.vecinos.contiene(b)))
            throw new IllegalArgumentException("No se pueden desconectar 2 vertices ya desconectados");

        vA_vertice.vecinos.elimina(b);
        vB_vertice.vecinos.elimina(a);

        aristas--;
    }

    /**
     * Nos dice si el elemento está contenido en la gráfica.
     * @return <code>true</code> si el elemento está contenido en la gráfica,
     *         <code>false</code> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
        return vertices.contiene(elemento);
    }

    /**
     * Elimina un elemento de la gráfica. El elemento tiene que estar contenido
     * en la gráfica.
     * @param elemento el elemento a eliminar.
     * @throws NoSuchElementException si el elemento no está contenido en la
     *         gráfica.
     */
    @Override public void elimina(T elemento) {

        Vertice v_vertice = (Vertice)vertice(elemento);

        vertices.elimina(elemento);

        for(Vecino u: v_vertice.vecinos){
            u.vecino.vecinos.elimina(elemento);
            aristas--; 
        }

    }

    /**
     * Nos dice si dos elementos de la gráfica están conectados. Los elementos
     * deben estar en la gráfica.
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @return <code>true</code> si a y b son vecinos, <code>false</code> en otro caso.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     */
    public boolean sonVecinos(T a, T b) {

        if(!vertices.contiene(a))
            throw new NoSuchElementException("El vertice " + a + " no está en la gráfcia");

        if(!vertices.contiene(b))
            throw new NoSuchElementException("El vertice " + b + " no está en la gráfcia");
        
        Vertice vA_vertice = (Vertice)vertice(a);

        return vA_vertice.vecinos.contiene(b); 
    }

    /**
     * Regresa el peso de la arista que comparten los vértices que contienen a
     * los elementos recibidos.
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @return el peso de la arista que comparten los vértices que contienen a
     *         los elementos recibidos.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b no están conectados.
     */
    public double getPeso(T a, T b) {

        if(!sonVecinos(a, b))
            throw new IllegalArgumentException("Los vertices no están concectados");

        Vertice verticeA = (Vertice)vertice(a);
        Vecino vecinoDe_A = verticeA.vecinos.get(b);

        return vecinoDe_A.peso; 
    }

    /**
     * Define el peso de la arista que comparten los vértices que contienen a
     * los elementos recibidos.
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @param peso el nuevo peso de la arista que comparten los vértices que
     *        contienen a los elementos recibidos.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b no están conectados, o si peso
     *         es menor o igual que cero.
     */
    public void setPeso(T a, T b, double peso) {
        
        Vertice verticeA = (Vertice)vertice(a);
        Vertice verticeB = (Vertice)vertice(b);

        if(!sonVecinos(a, b))
            throw new IllegalArgumentException("Los vertices no están conectados");
        
        if(peso <= 0)
            throw new IllegalArgumentException("El peso es menor o igual a 0");

        Vecino vecinoDe_A = verticeA.vecinos.get(b);
        Vecino vecinoDe_B = verticeB.vecinos.get(a);

        vecinoDe_A.peso = peso; 
        vecinoDe_B.peso = peso;
    }

    /**
     * Regresa el vértice correspondiente el elemento recibido.
     * @param elemento el elemento del que queremos el vértice.
     * @throws NoSuchElementException si elemento no es elemento de la gráfica.
     * @return el vértice correspondiente el elemento recibido.
     */
    public VerticeGrafica<T> vertice(T elemento) {
        try{
            return vertices.get(elemento);
        }
        catch(NoSuchElementException e){
            throw new NoSuchElementException("El elemento " + elemento + " no está en la grafica");
        }
    }

    /**
     * Define el color del vértice recibido.
     * @param vertice el vértice al que queremos definirle el color.
     * @param color el nuevo color del vértice.
     * @throws IllegalArgumentException si el vértice no es válido.
     */
    public void setColor(VerticeGrafica<T> vertice, Color color) {
        if(vertice == null || 
            (vertice.getClass() != Vertice.class) &&  
            (vertice.getClass() !=  Vecino.class)){
                throw new IllegalArgumentException("El vertice no es instancia de vertice");
            }

        if(vertice.getClass() == Vertice.class){
            Vertice v = (Vertice)vertice; 
            v.color = color; 
        }

        if(vertice.getClass() == Vecino.class){
            Vecino v = (Vecino)vertice;
            v.vecino.color = color; 
        }
    }

    /**
     * Nos dice si la gráfica es conexa.
     * @return <code>true</code> si la gráfica es conexa, <code>false</code> en
     *         otro caso.
     */
    public boolean esConexa() {
        Vertice vAux = null; 
        for(Vertice v: vertices){
            vAux = v;
            break; 
        }

        bfs(vAux.elemento, v -> {});

        for(Vertice vertice: vertices)
            if(vertice.color == Color.ROJO)
                return false; 
        
        return true; 
    }

    /**
     * Realiza la acción recibida en cada uno de los vértices de la gráfica, en
     * el orden en que fueron agregados.
     * @param accion la acción a realizar.
     */
    public void paraCadaVertice(AccionVerticeGrafica<T> accion) {
        for(Vertice vertice: vertices)
            accion.actua(vertice);
    }

    /**
     * Realiza la acción recibida en todos los vértices de la gráfica, en el
     * orden determinado por BFS, comenzando por el vértice correspondiente al
     * elemento recibido. Al terminar el método, todos los vértices tendrán
     * color {@link Color#NINGUNO}.
     * @param elemento el elemento sobre cuyo vértice queremos comenzar el
     *        recorrido.
     * @param accion la acción a realizar.
     * @throws NoSuchElementException si el elemento no está en la gráfica.
     */
    public void bfs(T elemento, AccionVerticeGrafica<T> accion) {
        Vertice vertice = (Vertice)vertice(elemento);
        Cola<Vertice> cola = new Cola<Vertice>(); 

        recorreGrafica(vertice, cola, accion);
    }

    /**
     * Realiza la acción recibida en todos los vértices de la gráfica, en el
     * orden determinado por DFS, comenzando por el vértice correspondiente al
     * elemento recibido. Al terminar el método, todos los vértices tendrán
     * color {@link Color#NINGUNO}.
     * @param elemento el elemento sobre cuyo vértice queremos comenzar el
     *        recorrido.
     * @param accion la acción a realizar.
     * @throws NoSuchElementException si el elemento no está en la gráfica.
     */
    public void dfs(T elemento, AccionVerticeGrafica<T> accion) {
        Vertice vertice = (Vertice)vertice(elemento);
        Pila<Vertice> pila = new Pila<Vertice>(); 

        recorreGrafica(vertice, pila, accion);
    }

    /**
     * Metodo auxiliar para implementar BFS o DFS
     * Recorre la grafica con una estructucura de datos, pintando cada 
     * vertice en el recorrido para saber si este ya fue visitado o no.
     * @param vertice el vertice con el que se comienza el recorrido.
     * @param estructura una instancia de MeteSaca<T> (una pila o cola).
     * @param accion la accion a realizar para cada vertice de la gráfica.
     */
    private void recorreGrafica(Vertice vertice, MeteSaca<Vertice> estructura,
                 AccionVerticeGrafica<T> accion){
        for(Vertice v: vertices)
            v.color = Color.ROJO; 
        
        vertice.color = Color.NINGUNO; 
        estructura.mete(vertice);

        while(!estructura.esVacia()){
            vertice = estructura.saca();
            accion.actua(vertice);
            for(Vecino u: vertice.vecinos){
                if(u.vecino.color == Color.ROJO){
                    u.vecino.color = Color.NINGUNO; 
                    estructura.mete(u.vecino);
                }
            }
        }
    }

    /**
     * Nos dice si la gráfica es vacía.
     * @return <code>true</code> si la gráfica es vacía, <code>false</code> en
     *         otro caso.
     */
    @Override public boolean esVacia() {
        return vertices.esVacia(); 
    }

    /**
     * Limpia la gráfica de vértices y aristas, dejándola vacía.
     */
    @Override public void limpia() {
        vertices.limpia();
        aristas = 0; 
    }

    /**
     * Regresa una representación en cadena de la gráfica.
     * @return una representación en cadena de la gráfica.
     */
    @Override public String toString() {
        String s = "{";
    
        for(Vertice vertice: vertices)
            s += vertice.elemento.toString() + ", ";
        
        s += "}, {";

        for(Vertice u: vertices){
            for(Vecino w: u.vecinos){
                if(s.contains("(" + w.vecino.elemento.toString()))
                    continue;
                s += "(" + u.elemento.toString() + ", " + w.vecino.elemento.toString() + "), ";
            }
        }

        s += "}";
        return s; 
    }

    /**
     * Nos dice si la gráfica es igual al objeto recibido.
     * @param objeto el objeto con el que hay que comparar.
     * @return <code>true</code> si la gráfica es igual al objeto recibido;
     *         <code>false</code> en otro caso.
     */
    @Override public boolean equals(Object objeto) {
        if (objeto == null || getClass() != objeto.getClass())
            return false;
        @SuppressWarnings("unchecked") Grafica<T> grafica = (Grafica<T>)objeto;
        
        return (getElementos() == grafica.getElementos() &&
               aristas == grafica.getAristas() &&
               verificaElementosIguales(grafica) &&
               verificaAristasIguales(grafica));
    }

    /**
     * Nos dice si las graficas comparadas contienen los mismos 
     * elementos, no necesariamente en el mismo orden.
     * @param grafica una gráfica para comparar elementos.
     * @return <code>true</code> si los elmentos de las 2 graficas son iguales,
     *         <code>false</code> en otro caso. 
     */
    private boolean verificaElementosIguales(Grafica<T> grafica){
        
        for(Vertice vertice: vertices){
            try{
                grafica.vertice(vertice.elemento);
            }
            catch(NoSuchElementException e){
                return false; 
            }
        }
        return true; 
    }

    /**
     * Verifica que las conexiones de un vertice se mantengan en la grafica reibida.
     * @param grafica la grafica a comparar
     * @return true si las aristas de la grafica recibida son iguales a las aristas
     *          de la gráfica que manda a llamar el metodo, false en otro caso.
     */
    private boolean verificaAristasIguales(Grafica<T> grafica){ 
        for(Vertice vertice: vertices){
            for(Vecino vecino_vertice: vertice.vecinos){
                if(!grafica.sonVecinos(vertice.elemento, vecino_vertice.vecino.elemento))
                    return false;
            }
        }
        return true; 
    }


    /**
     * Regresa un iterador para iterar la gráfica. La gráfica se itera en el
     * orden en que fueron agregados sus elementos.
     * @return un iterador para iterar la gráfica.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }

    /**
     * Calcula una trayectoria de distancia mínima entre dos vértices.
     * @param origen el vértice de origen.
     * @param destino el vértice de destino.
     * @return Una lista con vértices de la gráfica, tal que forman una
     *         trayectoria de distancia mínima entre los vértices <code>a</code> y
     *         <code>b</code>. Si los elementos se encuentran en componentes conexos
     *         distintos, el algoritmo regresa una lista vacía.
     * @throws NoSuchElementException si alguno de los dos elementos no está en
     *         la gráfica.
     */
    public Lista<VerticeGrafica<T>> trayectoriaMinima(T origen, T destino) {
        if(!contiene(origen))
            throw new NoSuchElementException("El vertice " + origen + " no está en la gráfica");

        if(!contiene(destino))
            throw new NoSuchElementException("El vertice " + destino + " no está en la gráfica");

        Vertice verticeOrigen = (Vertice)vertice(origen);
        Vertice verticeDestino = (Vertice)vertice(destino);

        Lista<VerticeGrafica<T>> trayectoria = new Lista<>();

        if(origen.equals(destino)){
            trayectoria.agregaFinal((VerticeGrafica<T>)verticeOrigen);
            return trayectoria; 
        }

        for(Vertice vertice: vertices)
            vertice.distancia = Double.MAX_VALUE; 
            
        verticeOrigen.distancia = 0; 
        
        Cola<Vertice> cola = new Cola<>(); 
        cola.mete(verticeOrigen);

        Vertice vertice;

        while(!cola.esVacia()){
            vertice = cola.saca(); 
            for(Vecino v: vertice.vecinos){
                if(v.vecino.distancia == Double.MAX_VALUE){
                    v.vecino.distancia = vertice.distancia + 1;
                    cola.mete(v.vecino);
                }
            }
        }

        if(verticeDestino.distancia == Double.MAX_VALUE)
            return trayectoria; 
        
        Vertice u = verticeDestino;
        trayectoria.agregaInicio(u);

        while(!(u.elemento.equals(origen))){
            for(Vecino v: u.vecinos){
                if(u.distancia == v.vecino.distancia + 1){
                    trayectoria.agregaInicio(v.vecino);
                    u = v.vecino;
                    break; 
                }
            }
        }
        
        return trayectoria;      
    }

    /**
     * Calcula la ruta de peso mínimo entre el elemento de origen y el elemento
     * de destino.
     * @param origen el vértice origen.
     * @param destino el vértice destino.
     * @return una trayectoria de peso mínimo entre el vértice <code>origen</code> y
     *         el vértice <code>destino</code>. Si los vértices están en componentes
     *         conexas distintas, regresa una lista vacía.
     * @throws NoSuchElementException si alguno de los dos elementos no está en
     *         la gráfica.
     */
    public Lista<VerticeGrafica<T>> dijkstra(T origen, T destino) {
        
        if(!contiene(origen))
            throw new NoSuchElementException("El vertice " + origen + " no está en la gráfica");

        if(!contiene(destino))
            throw new NoSuchElementException("El vertice " + destino + " no está en la gráfica");

        Vertice verticeOrigen = (Vertice)vertice(origen);
        Vertice verticeDestino = (Vertice)vertice(destino);

        Lista<VerticeGrafica<T>> trayectoria = new Lista<>();

        if(origen.equals(destino)){
            trayectoria.agregaFinal((VerticeGrafica<T>)verticeOrigen);
            return trayectoria; 
        }

        for(Vertice vertice: vertices)
            vertice.distancia = Double.MAX_VALUE; 

        verticeOrigen.distancia = 0; 

        MonticuloDijkstra<Vertice> monticulo = null;

        double n = getElementos();
        double cota = ( (n*(n-1)) /2 ) - n; 

        if(aristas > cota)
            monticulo = new MonticuloArreglo<Vertice>(vertices,vertices.getElementos());
        
        else
            monticulo = new MonticuloMinimo<Vertice>(vertices,vertices.getElementos());
        
        Vertice vertice; 

        while(!monticulo.esVacia()){
            vertice = monticulo.elimina(); 
            for(Vecino v: vertice.vecinos){
                if(v.vecino.distancia > vertice.distancia + v.peso){
                    v.vecino.distancia = vertice.distancia + v.peso;
                    monticulo.reordena(v.vecino);
                }
            }
        }

        if(verticeDestino.distancia == Double.MAX_VALUE)
            return trayectoria; 

        Vertice u = verticeDestino; 
        trayectoria.agregaInicio(u);

        while(!(u.elemento.equals(origen))){
            for(Vecino v: u.vecinos){
                if(u.distancia == v.vecino.distancia + v.peso){
                    trayectoria.agregaInicio(v.vecino);
                    u = v.vecino;
                    break; 
                }
            }
        }
        
        return trayectoria;  

    }
}
