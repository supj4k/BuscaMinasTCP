package org.buscaminasProfe.client;

import java.util.Map;
// DTO de respuesta: la "caja" que el cliente RECIBE del server.
// Gson llena estos campos al parsear el JSON de respuesta.
public class Response {
    public String status;             // "OK" o "ERROR"
    public Map<String, Object> data;  // contiene "board", "win", "gameEnd", "message".
    // Es Object (no un tipo concreto): por eso el
    // board hay que re-parsearlo (ver extractBoard).
}
