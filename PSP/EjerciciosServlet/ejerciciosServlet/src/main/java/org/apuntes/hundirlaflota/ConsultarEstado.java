package apuntes.hundirlaflota;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import apuntes.hundirlaflota.modelo.Partida;

import java.io.IOException;

@WebServlet("/estado")
public class ConsultarEstado extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Partida partida = (Partida) req.getSession().getAttribute("partida");

        if (partida == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Primero crea una partida");
            return;
        }

        resp.setContentType("text/plain;charset=UTF-8");

        resp.getWriter().println("Disparos restantes: " + partida.getDisparosRestantes());
        resp.getWriter().println("Barcos hundidos: " + partida.getBarcosHundidos());
        resp.getWriter().println("Barcos restantes: " + partida.getBarcosRestantes());

        if (partida.isTerminada()) {

            if (partida.isGanada()) {
                resp.getWriter().println("Estado: VICTORIA");
            } else {
                resp.getWriter().println("Estado: DERROTA");
            }

        } else {
            resp.getWriter().println("Estado: EN CURSO");
        }
    }
}
