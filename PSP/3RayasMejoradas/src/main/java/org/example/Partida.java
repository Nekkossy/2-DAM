package org.example;

import java.util.ArrayList;

public class Partida {

    private int id;
    private Jugador jugador1;
    private Jugador jugador2;
    private String simbolo1;
    private String simbolo2;
    private String[] tablero;
    private Jugador turno;
    private ArrayList<Movimiento> movimientos;
    private Jugador ganador;
    private Jugador perdedor;
    private boolean finalizada;
    private boolean empate;

    public Partida(int id, Jugador jugador1, Jugador jugador2,
                   String simbolo1, String simbolo2) {

        this.id = id;
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.simbolo1 = simbolo1;
        this.simbolo2 = simbolo2;

        tablero = new String[]{"-", "-", "-", "-", "-", "-", "-", "-", "-"};
        movimientos = new ArrayList<>();

        finalizada = false;
        empate = false;
    }

    public int getId() {
        return id;
    }

    public Jugador getJugador1() {
        return jugador1;
    }

    public Jugador getJugador2() {
        return jugador2;
    }

    public String getSimbolo1() {
        return simbolo1;
    }

    public String getSimbolo2() {
        return simbolo2;
    }

    public String[] getTablero() {
        return tablero;
    }

    public Jugador getTurno() {
        return turno;
    }

    public void setTurno(Jugador turno) {
        this.turno = turno;
    }

    public ArrayList<Movimiento> getMovimientos() {
        return movimientos;
    }

    public Jugador getGanador() {
        return ganador;
    }

    public void setGanador(Jugador ganador) {
        this.ganador = ganador;
    }

    public Jugador getPerdedor() {
        return perdedor;
    }

    public void setPerdedor(Jugador perdedor) {
        this.perdedor = perdedor;
    }

    public boolean isFinalizada() {
        return finalizada;
    }

    public void setFinalizada(boolean finalizada) {
        this.finalizada = finalizada;
    }

    public boolean isEmpate() {
        return empate;
    }

    public void setEmpate(boolean empate) {
        this.empate = empate;
    }

    public String getSimbolo(Jugador jugador) {
        if (jugador == jugador1) {
            return simbolo1;
        }
        return simbolo2;
    }

    public Jugador getOtroJugador(Jugador jugador) {
        if (jugador == jugador1) {
            return jugador2;
        }
        return jugador1;
    }
}