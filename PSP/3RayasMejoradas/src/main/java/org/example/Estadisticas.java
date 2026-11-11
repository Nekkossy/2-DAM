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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/estadisticas")
public class Estadisticas extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        List<Jugador> jugadores =
                DatosTorneo.getJugadores(getServletContext());

        List<Partida> partidas =
                DatosTorneo.getPartidas(getServletContext());

        var webExchange = JakartaServletWebApplication
                .buildApplication(getServletContext())
                .buildExchange(req, resp);

        WebContext ctx = new WebContext(webExchange);

        String nombre = req.getParameter("nombre");
        String idTexto = req.getParameter("id");

        if (idTexto != null) {

            int id;

            try {
                id = Integer.parseInt(idTexto);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "El ID debe ser un numero");
                return;
            }

            Partida partidaEncontrada = null;

            for (Partida p : partidas) {
                if (p.getId() == id) {
                    partidaEncontrada = p;
                    break;
                }
            }

            if (partidaEncontrada == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No existe esa partida");
                return;
            }

            ctx.setVariable("partida", partidaEncontrada);
            ctx.setVariable("tipo", "movimientos");

        } else if (nombre != null && !nombre.isBlank()) {

            Jugador jugador = DatosTorneo.buscarJugador(
                    getServletContext(), nombre.trim());

            if (jugador == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No existe ese jugador");
                return;
            }

            List<Partida> partidasJugador = new ArrayList<>();

            for (Partida p : partidas) {

                if (p.getJugador1() == jugador ||
                        p.getJugador2() == jugador) {

                    partidasJugador.add(p);
                }
            }

            ctx.setVariable("jugador", jugador);
            ctx.setVariable("partidasJugador", partidasJugador);
            ctx.setVariable("tipo", "jugador");

        } else {

            List<ResultadoJugador> resultados = new ArrayList<>();

            for (Jugador jugador : jugadores) {

                int victorias = 0;

                for (Partida p : partidas) {

                    if (p.isFinalizada() &&
                            p.getGanador() == jugador) {

                        victorias++;
                    }
                }

                resultados.add(
                        new ResultadoJugador(
                                jugador.getNombre(), victorias));
            }

            ctx.setVariable("resultados", resultados);
            ctx.setVariable("tipo", "general");
        }

        TemplateEngine engine = (TemplateEngine)
                getServletContext().getAttribute(
                        ThymeleafConstants.TEMPLATE_ENGINE_ATTR);

        engine.process("estadisticas", ctx, resp.getWriter());
    }
}