
package apuntes.escaperoom;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import apuntes.escaperoom.modelo.Partida;

import java.io.IOException;

@WebServlet("/responder")
public class ResponderPrueba extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String respuesta = req.getParameter("respuesta");

        if (respuesta == null || respuesta.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta la respuesta");
            return;
        }

        Partida partida = (Partida) req.getSession().getAttribute("partida");

        if (partida == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Primero crea una partida");
            return;
        }

        resp.setContentType("text/plain;charset=UTF-8");

        if (partida.isTerminada()) {
            resp.getWriter().println("La partida ya ha terminado");
            return;
        }

        if (respuesta.trim().equalsIgnoreCase(partida.getRespuestaActual())) {

            partida.setPruebasSuperadas(partida.getPruebasSuperadas() + 1);
            partida.setPruebaActual(partida.getPruebaActual() + 1);

            if (partida.getPruebaActual() == 3) {
                partida.setTerminada(true);
                partida.setGanada(true);
                resp.getWriter().println("¡Has escapado! Has ganado.");
            } else {
                resp.getWriter().println("Respuesta correcta. Pasas a la siguiente prueba.");
            }

        } else {

            partida.setVidas(partida.getVidas() - 1);

            if (partida.getVidas() == 0) {
                partida.setTerminada(true);
                partida.setGanada(false);
                resp.getWriter().println("Te has quedado sin vidas. Has perdido.");
            } else {
                resp.getWriter().println("Respuesta incorrecta. Vidas restantes: " + partida.getVidas());
            }
        }
    }
}
