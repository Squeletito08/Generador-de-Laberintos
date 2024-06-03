package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * Clase para montículos mínimos (<i>min heaps</i>).
 */
public class MonticuloMinimo<T extends ComparableIndexable<T>>
    implements Coleccion<T>, MonticuloDijkstra<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Índice del iterador. */
        private int indice;

        /* Nos dice si hay un siguiente elemento. */
        @Override public boolean hasNext() {
            return indice >= 0 && indice < elementos; 
        }

        /* Regresa el siguiente elemento. */
        @Override public T next() {
            if(indice < 0 || indice >= elementos)
                throw new NoSuchElementException();  

            return arbol[indice++];
        }
    }

    /* Clase estática privada para adaptadores. */
    private static class Adaptador<T  extends Comparable<T>>
        implements ComparableIndexable<Adaptador<T>> {

        /* El elemento. */
        private T elemento;
        /* El índice. */
        private int indice;

        /* Crea un nuevo comparable indexable. */
        public Adaptador(T elemento) {
            this.elemento = elemento; 
            indice = -1; 
        }

        /* Regresa el índice. */
        @Override public int getIndice() {
            return indice; 
        }

        /* Define el índice. */
        @Override public void setIndice(int indice) {
            this.indice = indice; 
        }

        /* Compara un adaptador con otro. */
        @Override public int compareTo(Adaptador<T> adaptador) {
            return elemento.compareTo(adaptador.elemento);
        }
    }

    /* El número de elementos en el arreglo. */
    private int elementos;
    /* Usamos un truco para poder utilizar arreglos genéricos. */
    private T[] arbol;

    /* Truco para crear arreglos genéricos. Es necesario hacerlo así por cómo
       Java implementa sus genéricos; de otra forma obtenemos advertencias del
       compilador. */
    @SuppressWarnings("unchecked") private T[] nuevoArreglo(int n) {
        return (T[])(new ComparableIndexable[n]);
    }

    /**
     * Constructor sin parámetros. Es más eficiente usar {@link
     * #MonticuloMinimo(Coleccion)} o {@link #MonticuloMinimo(Iterable,int)},
     * pero se ofrece este constructor por completez.
     */
    public MonticuloMinimo() {
        arbol = nuevoArreglo(100);
    }

    /**
     * Constructor para montículo mínimo que recibe una colección. Es más barato
     * construir un montículo con todos sus elementos de antemano (tiempo
     * <i>O</i>(<i>n</i>)), que el insertándolos uno por uno (tiempo
     * <i>O</i>(<i>n</i> log <i>n</i>)).
     * @param coleccion la colección a partir de la cuál queremos construir el
     *                  montículo.
     */
    public MonticuloMinimo(Coleccion<T> coleccion) {
        this(coleccion, coleccion.getElementos());
    }

    /**
     * Constructor para montículo mínimo que recibe un iterable y el número de
     * elementos en el mismo. Es más barato construir un montículo con todos sus
     * elementos de antemano (tiempo <i>O</i>(<i>n</i>)), que el insertándolos
     * uno por uno (tiempo <i>O</i>(<i>n</i> log <i>n</i>)).
     * @param iterable el iterable a partir de la cuál queremos construir el
     *                 montículo.
     * @param n el número de elementos en el iterable.
     */
    public MonticuloMinimo(Iterable<T> iterable, int n) {
        arbol = nuevoArreglo(n);
        elementos = n; 

        int i = 0;
        for(T elemento: iterable){
            arbol[i] = elemento; 
            elemento.setIndice(i++);
        }

        int mitad = n/2; 

        for(int j = mitad-1; j >= 0; j--)
            acomodaHaciaAbajo(j);
        
    }

    /**
     * Verificamos si elemento recibido está en la posicion correcta
     * de acuerdo a la definición de Monticulo Minimo, y para esto
     * lo comparamos con su padre. Si su padre es mayor, los intercambiamos 
     * y hacemos recursión hacia arriba con el elemento inicial. 
     * @param indice del elemento que se modifico. 
     */
    private void acomodaHaciaArriba(int indice){

        if(!indiceValido(indice))
            return; 

        int indicePadre = (indice-1) / 2; 

        if((indice == 0))
            return;

        if(!indiceValido(indicePadre) || arbol[indicePadre].compareTo(arbol[indice]) < 0)
            return; 
        
        intercambia(indice, indicePadre);

        acomodaHaciaArriba(indicePadre);
    }

    /**
     * Verificamos si el elemento correspondiente al indice 
     * recibido está en la posición correcta de acuerdo a la definición de Monticulo Minimo, 
     * y para esto lo comparamos con sus dos hijos. Dependiendo de cual sea el menor, 
     * intercambiamos el elemeneto con el de dicho hijo, y hacemos recursión 
     * nuevamente con el elemento inicial. 
     * @param indice el indice del elemento que se modificó. 
     */
    private void acomodaHaciaAbajo(int indice){

        int indiceHijoIzquierdo = 2*indice + 1; 
        int indiceHijoDerecho = 2*indice + 2; 

        int indiceHijoMenor = indice; 

        if(indiceValido(indiceHijoIzquierdo) && 
        arbol[indiceHijoIzquierdo].compareTo(arbol[indiceHijoMenor]) < 0)
            indiceHijoMenor = indiceHijoIzquierdo;

        if(indiceValido(indiceHijoDerecho) && 
        arbol[indiceHijoDerecho].compareTo(arbol[indiceHijoMenor]) < 0)
            indiceHijoMenor = indiceHijoDerecho;

        if(indice == indiceHijoMenor)
            return; 

        intercambia(indice, indiceHijoMenor);

        acomodaHaciaAbajo(indiceHijoMenor);
    }

    /**
     * Verifica si el indice recibido está dentro del rango
     * de elementos.
     * @param indice el indice a verificar. 
     * @return true si el incide es valido, false en otro caso. 
     */
    private boolean indiceValido(int indice){
        return (indice >= 0 && indice < elementos);
    }

    /**
     * Agrega un nuevo elemento en el montículo.
     * @param elemento el elemento a agregar en el montículo.
     */
    @Override public void agrega(T elemento) {

        /*se crea un nuevo arreglo con el doble de elementos 
         * que el anterior */
        if(arbol.length == elementos){
            T[] arbol_nuevo = nuevoArreglo(elementos*2);

            for(T e: arbol)
                arbol_nuevo[e.getIndice()] = e;
            
            arbol = arbol_nuevo; 
        }

        elemento.setIndice(elementos);
        arbol[elementos] = elemento; 
        
        elementos++; 

        acomodaHaciaArriba(elemento.getIndice());
    }

    /**
     * Elimina el elemento mínimo del montículo.
     * @return el elemento mínimo del montículo.
     * @throws IllegalStateException si el montículo es vacío.
     */
    @Override public T elimina() {

        if(esVacia())
            throw new IllegalStateException("No se puede eliminar un elemento " +  
                                            "de un monticulo vacío");

        T raiz_inicial = arbol[0];

        /*se intercambia la raíz y el último elemeneto del arreglo */
        intercambia(0,elementos-1);

        /*A la raiz original se le asigna indice -1 
         * y se anula su entrada */
        arbol[elementos-1].setIndice(-1);
        arbol[elementos-1] = null;

        elementos--;

        /*se acomoda hacia abajo la nueva raíz */
        acomodaHaciaAbajo(0);

        return raiz_inicial;
    }

    /**
     * Intercambia dos elmentos del arreglo y 
     * sus indices.
     * @param indice1 el indice de un primer elemento.
     * @param indice2 el indice de un segundo elemento.
     */
    private void intercambia(int indice1, int indice2){

        arbol[indice1].setIndice(indice2);
        arbol[indice2].setIndice(indice1);

        T aux = arbol[indice1];

        arbol[indice1] = arbol[indice2];
        arbol[indice2] = aux; 

    }

    /**
     * Elimina un elemento del montículo.
     * @param elemento a eliminar del montículo.
     */
    @Override public void elimina(T elemento) {

        int indice = elemento.getIndice();

        if(elemento == null || esVacia() || !indiceValido(indice))
            return; 

        /*se intercambia el elemento recibido con el último */
        intercambia(indice, elementos-1);

        /*al elemento incial se le asigna indice -1
         * y se anula su entrada */
        elemento.setIndice(-1);
        arbol[elementos-1] = null; 

        elementos--;

        acomodaHaciaAbajo(indice);
        acomodaHaciaArriba(indice);
    }

    /**
     * Nos dice si un elemento está contenido en el montículo.
     * @param elemento el elemento que queremos saber si está contenido.
     * @return <code>true</code> si el elemento está contenido,
     *         <code>false</code> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
        int indice = elemento.getIndice();

        if(!indiceValido(indice))
            return false; 

        return arbol[indice].compareTo(elemento) == 0; 
    }

    /**
     * Nos dice si el montículo es vacío.
     * @return <code>true</code> si ya no hay elementos en el montículo,
     *         <code>false</code> en otro caso.
     */
    @Override public boolean esVacia() {
        return elementos == 0; 
    }

    /**
     * Limpia el montículo de elementos, dejándolo vacío.
     */
    @Override public void limpia() {
        elementos = 0;
        for(int i = 0; i < arbol.length; i++)
            arbol[i] = null; 
    }

   /**
     * Reordena un elemento en el árbol.
     * @param elemento el elemento que hay que reordenar.
     */
    @Override public void reordena(T elemento) {
        acomodaHaciaArriba(elemento.getIndice());
        acomodaHaciaAbajo(elemento.getIndice());
    }

    /**
     * Regresa el número de elementos en el montículo mínimo.
     * @return el número de elementos en el montículo mínimo.
     */
    @Override public int getElementos() {
        return elementos; 
    }

    /**
     * Regresa el <i>i</i>-ésimo elemento del árbol, por niveles.
     * @param i el índice del elemento que queremos, en <em>in-order</em>.
     * @return el <i>i</i>-ésimo elemento del árbol, por niveles.
     * @throws NoSuchElementException si i es menor que cero, o mayor o igual
     *         que el número de elementos.
     */
    @Override public T get(int i) {
        if(!indiceValido(i))
            throw new NoSuchElementException("El indice del elemento no está en " +
            "el arreglo (arbol completo)");
        
        return arbol[i];
    }


    /**
     * Regresa una representación en cadena del montículo mínimo.
     * @return una representación en cadena del montículo mínimo.
     */
    @Override public String toString() {
        String s = ""; 
        for(T elemento: arbol)
            s += elemento.toString() + ", "; 

        return s; 
    }

    /**
     * Nos dice si el montículo mínimo es igual al objeto recibido.
     * @param objeto el objeto con el que queremos comparar el montículo mínimo.
     * @return <code>true</code> si el objeto recibido es un montículo mínimo
     *         igual al que llama el método; <code>false</code> en otro caso.
     */
    @Override public boolean equals(Object objeto) {
        if (objeto == null || getClass() != objeto.getClass())
            return false;
        @SuppressWarnings("unchecked") MonticuloMinimo<T> monticulo =
            (MonticuloMinimo<T>)objeto;
        
        if(elementos != monticulo.elementos)
            return false;

        for(int i = 0; i < elementos; i++) 
            if(!arbol[i].equals(monticulo.arbol[i]))
                return false;
        
        return true;
    }

    /**
     * Regresa un iterador para iterar el montículo mínimo. El montículo se
     * itera en orden BFS.
     * @return un iterador para iterar el montículo mínimo.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }

    /**
     * Ordena la colección usando HeapSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param coleccion la colección a ordenar.
     * @return una lista ordenada con los elementos de la colección.
     */
    public static <T extends Comparable<T>>
    Lista<T> heapSort(Coleccion<T> coleccion) {
        Lista<Adaptador<T>> lista1 = new Lista<>(); 

        for(T elemento: coleccion)
            lista1.agregaFinal(new Adaptador<T>(elemento));

        Lista<T> lista2 = new Lista<>();

        MonticuloMinimo<Adaptador<T>> monticulo = new MonticuloMinimo<Adaptador<T>>(lista1);

        while(!monticulo.esVacia())
            lista2.agregaFinal(monticulo.elimina().elemento);

        return lista2;
    }
}
