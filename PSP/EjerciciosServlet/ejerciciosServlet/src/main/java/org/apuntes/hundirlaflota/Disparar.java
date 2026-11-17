package apuntes.hundirlaflota;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import apuntes.hundirlaflota.modelo.Partida;

import java.io.IOException;

@WebServlet("/disparar")
public class Disparar extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String fila = req.getParameter("fila");
        String columna = req.getParameter("columna");

        if (fila == null || columna == null
                || fila.isBlank() || columna.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Faltan coordenadas");
            return;
        }

        Partida partida = (Partida) req.getSession().getAttribute("partida");

        if (partida == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Primero crea una partida");
            return;
        }

        if (partida.isTerminada()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "La partida ha terminado");
            return;
        }

        try {

            int filaInt = Integer.parseInt(fila);
            int columnaInt = Integer.parseInt(columna);

            if (filaInt < 1 || filaInt > 5 || columnaInt < 1 || columnaInt > 5) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Coordenadas entre 1 y 5");
                return;
            }

            String resultado = partida.disparar(filaInt - 1, columnaInt - 1);

            if (resultado.equals("REPETIDO")) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ya has disparado en esa casilla");
                return;
            }

            resp.setContentType("text/plain;charset=UTF-8");

            switch (resultado) {

                case "AGUA":
                    resp.getWriter().println("¡Agua!");
                    break;

                case "TOCADO":
                    resp.getWriter().println("¡Barco tocado!");
                    break;

                case "VICTORIA":
                    resp.getWriter().println("¡Has hundido todos los barcos! Has ganado.");
                    break;

                case "DERROTA":
                    resp.getWriter().println("Te has quedado sin disparos. Has perdido.");
                    break;
            }

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Las coordenadas deben ser numeros");
        }
    }
}
