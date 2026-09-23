package org.buscaminasProfe.client;

import com.google.gson.Gson;
import java.io.*;
import java.net.Socket;

// Clase encargada de TODA la comunicacion de red con el server.
public class BuscaminasTCPCliente {

    // Gson: convierte objetos Java <-> texto JSON en ambos sentidos.
    private final Gson gson = new Gson();

    // Envia una peticion y devuelve la respuesta. Abre y cierra un socket
    // por cada llamada (conexion corta: 1 comando = 1 conexion).
    public Response sendRequest(String host, int port, Request request) throws IOException {
        // try-with-resources: cierra socket, reader y writer solos al terminar,
        // incluso si hay error. Asi nunca dejamos sockets abiertos (lo pide la rubrica).
        try (Socket socket = new Socket(host, port);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            // 1. Objeto -> texto JSON
            String jsonOut = gson.toJson(request);
            writer.write(jsonOut);
            writer.newLine();  // el \n OBLIGATORIO: el server lee con readLine(),
            // que espera hasta ver un salto de linea (TCP framing)
            writer.flush();    // empuja los datos ya; sin flush se quedan en el buffer

            // 2. Espera la respuesta (una linea, tambien terminada en \n por el server)
            String jsonIn = reader.readLine();

            // 3. Texto JSON -> objeto Response
            return gson.fromJson(jsonIn, Response.class);
        }
    }

    // Extrae el tablero de la respuesta. AQUI se resuelve la trampa:
    // response.data.get("board") NO es Cell[][], es una lista generica de mapas
    // (ArrayList<ArrayList<LinkedTreeMap>>), porque data es Map<String, Object>
    // y Object no le dice a Gson que tipo construir.
    public Cell[][] extractBoard(Response response) {
        return gson.fromJson(
                gson.toJsonTree(response.data.get("board")),  // vuelve el objeto generico a JSON
                Cell[][].class                                 // ahora SI le decimos el tipo exacto
        );
    }
}
