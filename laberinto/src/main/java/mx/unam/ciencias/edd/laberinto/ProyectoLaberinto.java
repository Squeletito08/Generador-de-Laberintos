package mx.unam.ciencias.edd.laberinto;

/* Proyecto: Creación de laberintos */
public class ProyectoLaberinto {

    public static void uso() {
        System.out.println("El uso correcto del programa es el siguiente: ");
        System.out.println(
                "Para generar un laberinto: java -jar target/laberinto.jar -g -s <semilla> -w <columnas> -h <filas>");
        System.out.println("Para resolver un laberinto: java -jar target/laberinto.jar < laberinto.mze");
        System.exit(1);
    }

    public static void main(String[] args) {
        try {
            AplicacionLaberintos app = new AplicacionLaberintos(args);
            app.ejecuta();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            uso();
        }
    }

}
