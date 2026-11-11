package org.example;

import jakarta.servlet.ServletContext;

import java.util.ArrayList;
import java.util.List;

public class DatosTorneo {

    public static List<Jugador> getJugadores(ServletContext contexto) {

        if (contexto.getAttribute("jugadores") == null) {
            contexto.setAttribute("jugadores", new ArrayList<Jugador>());
        }

        return (List<Jugador>) contexto.getAttribute("jugadores");
    }

    public static List<Partida> getPartidas(ServletContext contexto) {

        if (contexto.getAttribute("partidas") == null) {
            contexto.setAttribute("partidas", new ArrayList<Partida>());
        }

        return (List<Partida>) contexto.getAttribute("partidas");
    }

    public static Jugador buscarJugador(ServletContext contexto, String nombre) {

        for (Jugador jugador : getJugadores(contexto)) {
            if (jugador.getNombre().equalsIgnoreCase(nombre)) {
                return jugador;
            }
        }

        return null;
    }
}