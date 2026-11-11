package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@WebServlet("/eliminarJugador")
public class EliminarJugador extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String nombre = req.getParameter("nombre");

        if (nombre == null || nombre.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Debes indicar el nombre del jugador");
            return;
        }

        nombre = nombre.trim();

        List<Jugador> jugadores =
                DatosTorneo.getJugadores(getServletContext());

        List<Partida> partidas =
                DatosTorneo.getPartidas(getServletContext());

        Jugador jugador = DatosTorneo.buscarJugador(
                getServletContext(), nombre);

        if (jugador == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No existe ese jugador");
            return;
        }

        Iterator<Partida> iteradorPartidas = partidas.iterator();

        while (iteradorPartidas.hasNext()) {

            Partida partida = iteradorPartidas.next();

            if (partida.getJugador1() == jugador ||
                    partida.getJugador2() == jugador) {

                iteradorPartidas.remove();
            }
        }

        jugadores.remove(jugador);

        resp.getWriter().println("<h1>Jugador eliminado correctamente</h1>");
        resp.getWriter().println("<p>Se han eliminado tambien sus partidas.</p>");
        resp.getWriter().println("<a href='" +
                req.getContextPath() + "/inicio'>Volver al inicio</a>");
    }
}