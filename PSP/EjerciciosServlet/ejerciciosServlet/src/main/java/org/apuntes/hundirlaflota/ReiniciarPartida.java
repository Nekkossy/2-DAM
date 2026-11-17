package apuntes.hundirlaflota;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import apuntes.hundirlaflota.modelo.Partida;

import java.io.IOException;

@WebServlet("/reiniciar")
public class ReiniciarPartida extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Partida partida = (Partida) req.getSession().getAttribute("partida");

        if (partida == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No existe ninguna partida");
            return;
        }

        if (!partida.isTerminada()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "La partida todavía está en curso");
            return;
        }

        partida = new Partida();

        req.getSession().setAttribute("partida", partida);

        resp.setContentType("text/plain;charset=UTF-8");
        resp.getWriter().println("Partida reiniciada correctamente");
    }
}
