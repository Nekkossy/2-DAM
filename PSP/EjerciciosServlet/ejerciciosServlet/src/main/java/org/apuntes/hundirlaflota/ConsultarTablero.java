package apuntes.hundirlaflota;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import apuntes.hundirlaflota.modelo.Partida;

import java.io.IOException;

@WebServlet("/tablero")
public class ConsultarTablero extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Partida partida = (Partida) req.getSession().getAttribute("partida");

        if (partida == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Primero crea una partida");
            return;
        }

        resp.setContentType("text/plain;charset=UTF-8");

        char[][] tablero = partida.getTablero();

        resp.getWriter().println("    1  2  3  4  5");

        for (int i = 0; i < 5; i++) {

            resp.getWriter().print((i + 1) + "   ");

            for (int j = 0; j < 5; j++) {
                resp.getWriter().print(tablero[i][j] + "  ");
            }

            resp.getWriter().println();
        }

        resp.getWriter().println();
        resp.getWriter().println("? = Desconocido");
        resp.getWriter().println("A = Agua");
        resp.getWriter().println("X = Barco hundido");
    }
}
