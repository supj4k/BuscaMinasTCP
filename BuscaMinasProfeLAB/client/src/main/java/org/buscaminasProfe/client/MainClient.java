package org.buscaminasProfe.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.buscaminasProfe.client.*;

public class MainClient {

    // Dibuja el tablero en consola con cabeceras de fila y columna.
// static: porque se llama desde main(), que tambien es static.
    public static void printBoard(Cell[][] board) {
        // Si el tablero es null (ej: no has iniciado partida), avisamos y salimos
        if (board == null) {
            System.out.println("No hay tablero. Inicia una partida primero.");
            return;
        }

        // 1. Fila de cabecera con los indices de columna
        System.out.print("    ");  // espacio para alinear con los numeros de fila
        for (int j = 0; j < board[0].length; j++) {
            System.out.print(j + " ");
        }
        System.out.println();

        // 2. Recorremos cada fila
        for (int i = 0; i < board.length; i++) {
            System.out.print(i + " [ ");  // indice de fila + apertura
            // 3. Cada celda de la fila: su toString() ya sabe pintarse (color incluido)
            for (int j = 0; j < board[0].length; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println("]");  // cierre de la fila
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);           // lee lo que el usuario teclea
        BuscaminasTCPCliente client = new BuscaminasTCPCliente();  // el cartero de red
        String host = "localhost";   // el server esta en tu misma maquina
        int port = 12345;            // puerto donde escucha el server
        boolean flag = true;
        while(flag){
            System.out.println("=========================================================");
            System.out.println("      BUSCAMINAS DISTRIBUIDO - CLIENTE TCP  ");
            System.out.println("=========================================================");
            System.out.println("[1] Iniciar nueva partida ( Filas , Columnas , Minas )");
            System.out.println("[2] Destapar celda ( Fila , Columna )");
            System.out.println("[3] Marcar / Desmarcar bandera ( Fila , Columna ) ");
            System.out.println("[4]  Consultar estado actual del tablero");
            System.out.println("[5] Rendirse y revelar tablero completo");
            System.out.println("[6] Sair");
            System.out.println("=======================================================");
            System.out.print("Seleccione una Opcion:  ");
            int op = sc.nextInt();
            switch (op){

                case 1:
                    // 1. Pedimos los datos al usuario
                    System.out.print("Filas: ");
                    int n = sc.nextInt();
                    System.out.print("Columnas: ");
                    int m = sc.nextInt();
                    System.out.print("Minas: ");
                    int minas = sc.nextInt();

                    // 2. Armamos la data. OJO: todo como String, por eso el String.valueOf(...)
                    //    El server hara Integer.parseInt() para volverlos numero.
                    Map<String, String> dataInit = new HashMap<>();
                    dataInit.put("n", String.valueOf(n));
                    dataInit.put("m", String.valueOf(m));
                    dataInit.put("minas", String.valueOf(minas));

                    // 3. Creamos la peticion con la accion "INIT_GAME" y la enviamos
                    try {
                        Request reqInit = new Request("INIT_GAME", dataInit);
                        Response respInit = client.sendRequest(host, port, reqInit);

                        // 4. Extraemos el tablero de la respuesta (aqui se resuelve la trampa del board)
                        Cell[][] boardInit = client.extractBoard(respInit);

                        // 5. Lo dibujamos
                        printBoard(boardInit);
                    } catch (Exception e) {
                        // Si el server no esta encendido o hay error de red, avisamos sin crashear
                        System.out.println("Error al conectar con el servidor: " + e.getMessage());
                    }


                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    flag = false;
                    break;
                default:
                    System.out.println("Seleccione una opcion valida");

            }


        }

    }
}
