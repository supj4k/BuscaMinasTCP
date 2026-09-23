package org.galaga;

import org.galaga.Controllers.TCPController;
import org.galaga.services.ServicesImpl;

public class Main {

    public static void main(String[] args) {
        // Capa de servicios: internamente crea el BoardGame
        ServicesImpl serv = new ServicesImpl();

        // Puerto por defecto (coincide con el constructor de TCPController)
        int port = 12345;

        // Si se paso un puerto por linea de comandos, lo usamos.
        // Ej: .\gradlew :server:run --args="9000"
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Puerto invalido, usando 12345 por defecto.");
            }
        }

        // Arranca el servidor TCP en el puerto elegido.
        // Usa el constructor de dos parametros (services, port).
        TCPController controller = new TCPController(serv, port);
        controller.startService();
    }
}