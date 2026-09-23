package org.buscaminasProfe.client;

import java.util.Map;

// DTO de peticion: la "caja" que el cliente ENVIA al server.
// Gson la convierte a JSON antes de mandarla por el socket.
public class Request {
    public String action;              // nombre de la accion: "INIT_GAME", "SELECT_CELL", etc.
    public Map<String, String> data;   // parametros. OJO: todo String ("i":"3"),
    // porque el server hace Integer.parseInt(...)

    // Constructor para crear la peticion comoda en una linea
    public Request(String action, Map<String, String> data) {
        this.action = action;
        this.data = data;
    }
}
