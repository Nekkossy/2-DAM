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

@WebServlet("/tres")
public class TresEnRaya extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idTexto = req.getParameter("id");

        if (idTexto == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el ID de la partida");
            return;
        }

        int id;

        try {
            id = Integer.parseInt(idTexto);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de partida incorrecto");
            return;
        }

        List<Partida> partidas =
                DatosTorneo.getPartidas(getServletContext());

        Partida partida = buscarPartida(partidas, id);

        if (partida == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "No existe esa partida");
            return;
        }

        String[] tablero = partida.getTablero();

        String fila1 = tablero[0] + " " + tablero[1] + " " + tablero[2];
        String fila2 = tablero[3] + " " + tablero[4] + " " + tablero[5];
        String fila3 = tablero[6] + " " + tablero[7] + " " + tablero[8];

        var webExchange = JakartaServletWebApplication
                .buildApplication(getServletContext())
                .buildExchange(req, resp);

        WebContext ctx = new WebContext(webExchange);

        ctx.setVariable("fila1", fila1);
        ctx.setVariable("fila2", fila2);
        ctx.setVariable("fila3", fila3);
        ctx.setVariable("id", partida.getId());
        ctx.setVariable("turno", partida.getTurno().getNombre());
        ctx.setVariable("simboloTurno",
                partida.getSimbolo(partida.getTurno()));
        ctx.setVariable("finalizada", partida.isFinalizada());
        ctx.setVariable("empate", partida.isEmpate());
        ctx.setVariable("jugador1", partida.getJugador1().getNombre());
        ctx.setVariable("jugador2", partida.getJugador2().getNombre());

        if (partida.getGanador() != null) {
            ctx.setVariable("ganador",
                    partida.getGanador().getNombre());

            ctx.setVariable("perdedor",
                    partida.getPerdedor().getNombre());
        }

        TemplateEngine engine = (TemplateEngine)
                getServletContext().getAttribute(
                        ThymeleafConstants.TEMPLATE_ENGINE_ATTR);

        engine.process("tresEnRaya", ctx, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idTexto = req.getParameter("id");
        String posicionTexto = req.getParameter("posicion");

        if (idTexto == null || posicionTexto == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Faltan parametros");
            return;
        }

        int id;
        int posicion;

        try {
            id = Integer.parseInt(idTexto);
            posicion = Integer.parseInt(posicionTexto);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Los datos deben ser numeros");
            return;
        }

        if (posicion < 1 || posicion > 9) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "La posicion debe estar entre 1 y 9");
            return;
        }

        List<Partida> partidas =
                DatosTorneo.getPartidas(getServletContext());

        Partida partida = buscarPartida(partidas, id);

        if (partida == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "No existe esa partida");
            return;
        }

        synchronized (partida) {

            if (partida.isFinalizada()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "La partida ya ha terminado");
                return;
            }

            String[] tablero = partida.getTablero();

            if (!tablero[posicion - 1].equals("-")) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Esa casilla ya esta ocupada");
                return;
            }

            Jugador jugador = partida.getTurno();
            String simbolo = partida.getSimbolo(jugador);

            tablero[posicion - 1] = simbolo;

            int numeroMovimiento =
                    partida.getMovimientos().size() + 1;

            Movimiento movimiento = new Movimiento(
                    numeroMovimiento,
                    jugador.getNombre(),
                    simbolo,
                    posicion
            );

            partida.getMovimientos().add(movimiento);

            if (hayVictoria(tablero, simbolo)) {

                partida.setGanador(jugador);
                partida.setPerdedor(partida.getOtroJugador(jugador));
                partida.setFinalizada(true);

            } else if (tableroCompleto(tablero)) {

                partida.setEmpate(true);
                partida.setFinalizada(true);

            } else {

                partida.setTurno(partida.getOtroJugador(jugador));
            }
        }

        resp.sendRedirect(req.getContextPath() + "/tres?id=" + id);
    }

    private Partida buscarPartida(List<Partida> partidas, int id) {

        for (Partida p : partidas) {
            if (p.getId() == id) {
                return p;
            }
        }

        return null;
    }

    private boolean hayVictoria(String[] tablero, String simbolo) {

        int[][] combinaciones = {
                {0, 1, 2},
                {3, 4, 5},
                {6, 7, 8},
                {0, 3, 6},
                {1, 4, 7},
                {2, 5, 8},
                {0, 4, 8},
                {2, 4, 6}
        };

        for (int[] c : combinaciones) {

            if (tablero[c[0]].equals(simbolo) &&
                    tablero[c[1]].equals(simbolo) &&
                    tablero[c[2]].equals(simbolo)) {

                return true;
            }
        }

        return false;
    }

    private boolean tableroCompleto(String[] tablero) {

        for (String casilla : tablero) {

            if (casilla.equals("-")) {
                return false;
            }
        }

        return true;
    }
}