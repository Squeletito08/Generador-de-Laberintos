package mx.unam.ciencias.edd;

/**
 * Clase para métodos estáticos con dispersores de bytes.
 */
public class Dispersores {

    /* Constructor privado para evitar instanciación. */
    private Dispersores() {}

    /**
     * Función de dispersión XOR.
     * @param llave la llave a dispersar.
     * @return la dispersión de XOR de la llave.
     */
    public static int dispersaXOR(byte[] llave) {
        int r = 0; 
        int i = 0; 
        int l = llave.length; 
        while(l >= 4){
            r ^= bigEndian(llave[i], llave[i+1], llave[i+2], llave[i+3]);
            i += 4; 
            l -= 4;
        }
        int t = 0; 
        switch(l){
            case 3: t |= (llave[i+2] & 0xFF) << 8;
            case 2: t |= (llave[i+1] & 0xFF) << 16;
            case 1: t |= (llave[i] & 0xFF) << 24;
        }
        return (r ^ t);
    }

    /**
     * Función de dispersión de Bob Jenkins.
     * @param llave la llave a dispersar.
     * @return la dispersión de Bob Jenkins de la llave.
     */
    public static int dispersaBJ(byte[] llave) {
        int a = 0x9E3779B9;
        int b = 0x9E3779B9;
        int c = 0xFFFFFFFF;

        int l = llave.length; 
        int i = 0; 

        int[] arreglo = new int[3];

        while(l >= 12){
            a += littleEndian(llave[i],llave[i+1],llave[i+2],llave[i+3]);
            b += littleEndian(llave[i+4],llave[i+5],llave[i+6],llave[i+7]);
            c += littleEndian(llave[i+8],llave[i+9],llave[i+10],llave[i+11]);

            i+=12; 

            arreglo = mezclaBJ(a, b, c);

            a = arreglo[0];
            b = arreglo[1];
            c = arreglo[2];

            l -= 12; 
        }

        int a_aux = 0; 
        int b_aux = 0;
        int c_aux = 0; 

        /*El primer byte más significativo de c debe
        * de ser la longitud del arreglo */
        c += llave.length;  

        switch(l){
            case 11: c_aux |= (llave[i+10] & 0xFF) << 24; 
            case 10: c_aux |= (llave[i+9] & 0xFF) << 16; 
            case 9: c_aux |= (llave[i+8] & 0xFF) << 8; 

            case 8: b_aux |= (llave[i+7] & 0xFF) << 24; 
            case 7: b_aux |= (llave[i+6] & 0xFF) << 16; 
            case 6: b_aux |= (llave[i+5] & 0xFF) << 8; 
            case 5: b_aux |= (llave[i+4] & 0xFF); 

            case 4: a_aux |= (llave[i+3] & 0xFF) << 24; 
            case 3: a_aux |= (llave[i+2] & 0xFF) << 16; 
            case 2: a_aux |= (llave[i+1] & 0xFF) << 8;
            case 1: a_aux |= (llave[i] & 0xFF); 
        }

        a += a_aux; 
        b += b_aux; 
        c += c_aux;

        arreglo = mezclaBJ(a, b, c);
        c = arreglo[2];

        return c;
    }

    /**
     * Algortimo auxiliar mezcla para la función de dispersión de BJ.
     * @param a el primer entero a mezclar.
     * @param b segundo entero a mezclar.
     * @param c tercer entero a mezclar.
     * @return un arreglo con los tres enteros ya mexclados entre si, para 
     *          que en el metodo que mande a llamar a este se puedan 
     *          reasignar los valores de las variables. 
     */
    private static int[] mezclaBJ(int a, int b, int c){
        a -= b;     a -= c;     a ^= (c >>> 13);
        b -= c;     b -= a;     b ^= (a << 8);
        c -= a;     c -= b;     c ^= (b >>> 13);

        a -= b;     a -= c;     a ^= (c >>> 12);  
        b -= c;     b -= a;     b ^= (a << 16); 
        c -= a;     c -= b;     c ^= (b >>> 5); 

        a -= b;     a -= c;     a ^= (c >>> 3);  
        b -= c;     b -= a;     b ^= (a << 10); 
        c -= a;     c -= b;     c ^= (b >>> 15);

        int[] arreglo = {a,b,c}; 
        return arreglo; 
    }

    /**
     * Función de dispersión Daniel J. Bernstein.
     * @param llave la llave a dispersar.
     * @return la dispersión de Daniel Bernstein de la llave.
     */
    public static int dispersaDJB(byte[] llave) {
        int h = 5381;

        for(byte n: llave)
            h += (h << 5) + (n & 0xFF); 
        
        return h;
    }

    /**
     * Algoritmo para combinar 4 bytes en big-endian.
     * @param a primer byte (el más significativo).
     * @param b segundo byte (el segundo más siginificativo).
     * @param c tercer byte (el tercer más significativo).
     * @param d cuarto byte (el cuarto más significativo).
     * @return un entero de 32 bits, el cual corresponde a los 
     *          4 bytes anteriores combinados en big-endian.
     */
    private static int bigEndian(byte a, byte b, byte c, byte d){
        return ((a & 0xFF) << 24) | ((b & 0xFF) << 16) |
                ((c & 0xFF) << 8) | ((d & 0xFF));  
    }

    /**
     * Algoritmo para combinar 4 bytes en little-endian.
     * @param a primer byte (cuarto más signifiativo).
     * @param b segundo byte (segundo más significativo).
     * @param c tercer byte (tercero más significativo).
     * @param d cuarto byte (primer más significativo).
     * @return un entero de 32 bits, el cual corresponde a los 
     *          4 bytes anteriores combinados en little-endian.
     */
    private static int littleEndian(byte a, byte b, byte c, byte d){
        return ((a & 0xFF)) | ((b & 0xFF) << 8) | 
        ((c & 0xFF) << 16) | ((d & 0xFF) << 24); 
    } 

}
