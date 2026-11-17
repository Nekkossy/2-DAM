package apuntes.hundirlaflota;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import apuntes.hundirlaflota.modelo.Partida;

import java.io.IOException;

@WebServlet("/crear")
public class CrearPartida extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Partida partida = (Partida) req.getSession().getAttribute("partida");

        resp.setContentType("text/plain;charset=UTF-8");

        if (partida != null) {
            if (!partida.isTerminada()) {
                resp.getWriter().println("Ya tienes una partida en curso");
            } else {
                resp.getWriter().println("La partida ha terminado. Utiliza reiniciar");
            }
            return;
        }

        partida = new Partida();

        req.getSession().setAttribute("partida", partida);

        resp.getWriter().println("Partida creada correctamente");
    }
}
