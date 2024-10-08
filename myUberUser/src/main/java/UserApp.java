import java.util.Random;
import java.util.Scanner;

public class UserApp {
    public static void main(String[] args) {

        Random random = new Random();
        Scanner scanner = new Scanner(System.in);

        int id = random.nextInt(1, 999999);
        int posX = random.nextInt(10);
        int posY = random.nextInt(10);
        int tiempoHastaSolicitud = random.nextInt(10) + 1;
        boolean continueVar = true;

        User user = new User(id, posX, posY, tiempoHastaSolicitud);

        while (continueVar) {
            System.out.println("\nMenú aplicación.");
            System.out.println("1. Solicitar taxi");
            System.out.println("2. Solicitar datos usuario");
            System.out.println("3. Salir del sistema");
            System.out.print("Ingrese una opción: ");

            int opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    user.solicitarTaxi();
                    break;
                case 2:
                    System.out.println("\nID Usuario: " + id);
                    System.out.println("Posición actual: (" + posX + ", " + posY + ")");
                    break;
                case 3:
                    System.out.println("Saliendo del sistema...");
                    continueVar = false;
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
                    break;
            }
        }

        user.close();
        scanner.close();
    }
}