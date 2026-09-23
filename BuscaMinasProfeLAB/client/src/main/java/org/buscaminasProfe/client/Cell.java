package org.buscaminasProfe.client;

// Copia de la Cell del server. El cliente necesita su propia version
// porque los dos modulos Gradle no comparten codigo (se hablan por red).
// Los nombres de los campos deben ser IDENTICOS a los del JSON del server,
// porque Gson los rellena buscando por nombre.
public class Cell {
    private boolean isLandMine;  // true si la celda es una mina
    private int value;           // numero de minas vecinas (0 a 8)
    private boolean hide;        // true = celda tapada (no destapada aun)
    private boolean showAll;     // true = fin de partida, se revela todo
    private boolean isMarked;    // true = el jugador puso bandera aqui

    // Getters: el renderizador los necesita porque los campos son private.
    // No hay setters: el cliente solo LEE el estado que manda el server.
    public boolean isMarked()   { return isMarked; }
    public boolean isHide()     { return hide; }
    public boolean isLandMine() { return isLandMine; }
    public boolean isShowAll()  { return showAll; }
    public int getValue()       { return value; }

    // toString: cada celda sabe dibujarse sola. Orden: marcada > oculta > mina > numero.
    // Los \u001B[..m son codigos ANSI de color de terminal.
    @Override
    public String toString() {
        if (isMarked) {
            return "\u001B[33mM\u001B[0m";  // M amarilla (33=amarillo, 0=reset)
        }
        // Si esta oculta y no es fin de partida -> "."
        // Si no, si es mina -> * roja (31=rojo); si no -> el numero
        return hide && !showAll ? "." : (isLandMine ? "\u001B[31m*\u001B[0m" : value + "");
    }
}
