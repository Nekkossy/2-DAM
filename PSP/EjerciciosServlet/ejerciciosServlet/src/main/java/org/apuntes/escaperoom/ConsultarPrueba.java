
package apuntes.escaperoom;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import apuntes.escaperoom.modelo.Partida;

import java.io.IOException;

@WebServlet("/prueba")
public class ConsultarPrueba extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Partida partida = (Partida) req.getSession().getAttribute("partida");

        if (partida == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Primero crea una partida");
            return;
        }

        resp.setContentType("text/plain;charset=UTF-8");

        if (partida.isTerminada()) {
            resp.getWriter().println("La partida ha terminado");
            return;
        }

        resp.getWriter().println("Prueba: " + (partida.getPruebaActual() + 1));
        resp.getWriter().println("Pregunta: " + partida.getPreguntaActual());
        resp.getWriter().println("Vidas: " + partida.getVidas());
    }
}
