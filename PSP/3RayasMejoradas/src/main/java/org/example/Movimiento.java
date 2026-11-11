package org.example;

public class Movimiento {

    private int numero;
    private String jugador;
    private String simbolo;
    private int posicion;

    public Movimiento(int numero, String jugador, String simbolo, int posicion) {
        this.numero = numero;
        this.jugador = jugador;
        this.simbolo = simbolo;
        this.posicion = posicion;
    }

    public int getNumero() {
        return numero;
    }

    public String getJugador() {
        return jugador;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public int getPosicion() {
        return posicion;
    }
}