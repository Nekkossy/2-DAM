package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class Participante {
    private String nombre;
    private int id;
    private boolean turnoCumplido;
    private int posicionesAvanzadas;
    private List<Integer> tiradas;

    public Participante(String nombre, int id) {
        this.nombre = nombre;
        this.id = id;
        turnoCumplido=false;
        posicionesAvanzadas=0;
        tiradas=new ArrayList<>();
    }

    public void sumarPosiciones(int numPosicionesAvanzadas){
        this.posicionesAvanzadas+=numPosicionesAvanzadas;
        tiradas.add(numPosicionesAvanzadas);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPosicionesAvanzadas() {
        return posicionesAvanzadas;
    }

    public void setPosicionesAvanzadas(int posicionesAvanzadas) {
        this.posicionesAvanzadas = posicionesAvanzadas;
    }

    public boolean isTurnoCumplido() {
        return turnoCumplido;
    }

    public void setTurnoCumplido(boolean turnoCumplido) {
        this.turnoCumplido = turnoCumplido;
    }

    public List<Integer> getTiradas() {
        return tiradas;
    }

    @Override
    public String toString() {
        return
                "nombre='" + nombre + '\'' +
                ", id=" + id +
                ", posicionesAvanzadas=" + posicionesAvanzadas;
    }
}
