package org.example.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Partida {
    private String[] tablero;

    private Map<String, Participante> participantes;
    private int numCasillas;
    private int numParticipantes;
    private boolean terminada;
    private boolean jugable;
    private int contadorTurnos;
    private boolean ganada;

    public Partida(int casillasTablero) {
        tablero = new String[casillasTablero];
        participantes=new HashMap<>();
        numCasillas=0;
        numParticipantes=0;
        contadorTurnos=0;
        jugable=false;
        terminada=false;
        ganada=false;

        for (int i = 0; i <tablero.length; i++) {
            tablero[i]="-";
            
        }
    }


    public int tirarDado(){
        Random rand=new Random();
        return rand.nextInt(1,7);
    }


    public String[] getTablero() {
        return tablero;
    }

    public void setTablero(String[] tablero) {
        this.tablero = tablero;
    }

    public int getNumCasillas() {
        return numCasillas;
    }

    public void setNumCasillas(int numCasillas) {
        this.numCasillas = numCasillas;
    }

    public int getNumParticipantes() {
        return numParticipantes;
    }

    public void setNumParticipantes(int numParticipantes) {
        this.numParticipantes = numParticipantes;
    }

    public boolean isTerminada() {
        return terminada;
    }

    public void setTerminada(boolean terminada) {
        this.terminada = terminada;
    }

    public boolean isGanada() {
        return ganada;
    }

    public void setGanada(boolean ganada) {
        this.ganada = ganada;
    }

    public Map<String, Participante> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(Map<String, Participante> participantes) {
        this.participantes = participantes;
    }

    public boolean isJugable() {
        return jugable;
    }

    public void setJugable(boolean jugable) {
        this.jugable = jugable;
    }

    public int getContadorTurnos() {
        return contadorTurnos;
    }

    public void setContadorTurnos(int contadorTurnos) {
        this.contadorTurnos = contadorTurnos;
    }
}
