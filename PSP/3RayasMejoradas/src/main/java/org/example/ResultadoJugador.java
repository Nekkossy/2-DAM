package org.example;

public class ResultadoJugador {

    private String nombre;
    private int victorias;

    public ResultadoJugador(String nombre, int victorias) {
        this.nombre = nombre;
        this.victorias = victorias;
    }

    public String getNombre() {
        return nombre;
    }

    public int getVictorias() {
        return victorias;
    }
}