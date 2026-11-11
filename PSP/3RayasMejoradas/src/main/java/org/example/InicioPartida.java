package org.example;

import config.ThymeleafConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.util.List;
import java.util.Random;

@WebServlet("/inicio")
public class InicioPartida extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        var webExchange = JakartaServletWebApplication
                .buildApplication(getServletContext())
                .buildExchange(req, resp);

        WebContext ctx = new WebContext(webExchange);

        TemplateEngine engine = (TemplateEngine)
                getServletContext().getAttribute(
                        ThymeleafConstants.TEMPLATE_ENGINE_ATTR);

        engine.process("inicio", ctx, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String nombre1 = req.getParameter("jugador1");
        String nombre2 = req.getParameter("jugador2");
        String simbolo1 = req.getParameter("simbolo1");
        String simbolo2 = req.getParameter("simbolo2");

        if (nombre1 == null || nombre2 == null ||
                simbolo1 == null || simbolo2 == null ||
                nombre1.isBlank() || nombre2.isBlank() ||
                simbolo1.isBlank() || simbolo2.isBlank()) {

            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Faltan datos de los jugadores");
            return;
        }

        nombre1 = nombre1.trim();
        nombre2 = nombre2.trim();
        simbolo1 = simbolo1.trim();
        simbolo2 = simbolo2.trim();

        if (nombre1.equalsIgnoreCase(nombre2)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Los jugadores deben ser diferentes");
            return;
        }

        if (simbolo1.length() != 1 || simbolo2.length() != 1 ||
                simbolo1.equals(simbolo2) ||
                simbolo1.equals("-") || simbolo2.equals("-")) {

            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Los simbolos deben ser distintos y de un caracter");
            return;
        }

        List<Jugador> jugadores =
                DatosTorneo.getJugadores(getServletContext());

        List<Partida> partidas =
                DatosTorneo.getPartidas(getServletContext());

        Jugador jugador1 = DatosTorneo.buscarJugador(
                getServletContext(), nombre1);

        if (jugador1 == null) {
            jugador1 = new Jugador(nombre1);
            jugadores.add(jugador1);
        }

        Jugador jugador2 = DatosTorneo.buscarJugador(
                getServletContext(), nombre2);

        if (jugador2 == null) {
            jugador2 = new Jugador(nombre2);
            jugadores.add(jugador2);
        }

        int id = 1;

        for (Partida p : partidas) {
            if (p.getId() >= id) {
                id = p.getId() + 1;
            }
        }

        Partida partida = new Partida(
                id, jugador1, jugador2, simbolo1, simbolo2);

        Random random = new Random();

        if (random.nextBoolean()) {
            partida.setTurno(jugador1);
        } else {
            partida.setTurno(jugador2);
        }

        partidas.add(partida);

        resp.sendRedirect(req.getContextPath() + "/tres?id=" + id);
    }
}