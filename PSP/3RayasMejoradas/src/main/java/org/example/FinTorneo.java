package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@WebServlet("/finTorneo")
public class FinTorneo extends HttpServlet {

    private List<Jugador> podio = new ArrayList<>();
    private int llamadas = 0;

    @Override
    protected synchronized void doGet(HttpServletRequest req,
                                      HttpServletResponse resp)
            throws ServletException, IOException {

        List<Jugador> jugadores =
                DatosTorneo.getJugadores(getServletContext());

        List<Partida> partidas =
                DatosTorneo.getPartidas(getServletContext());

        if (llamadas == 0) {

            if (jugadores.size() < 3) {
                resp.getWriter().println(
                        "<h1>No hay suficientes jugadores para formar el podio</h1>");
                return;
            }

            podio = new ArrayList<>(jugadores);

            podio.sort(Comparator
                    .comparingInt((Jugador j) ->
                            contarVictorias(j, partidas))
                    .reversed()
                    .thenComparing(Jugador::getNombre,
                            String.CASE_INSENSITIVE_ORDER));
        }

        String posicion;
        Jugador jugador;

        if (llamadas == 0) {

            posicion = "Tercer puesto";
            jugador = podio.get(2);

        } else if (llamadas == 1) {

            posicion = "Segundo puesto";
            jugador = podio.get(1);

        } else {

            posicion = "¡Campeón del torneo!";
            jugador = podio.get(0);
        }

        resp.getWriter().println("<html>");
        resp.getWriter().println("<head><title>Fin del torneo</title></head>");
        resp.getWriter().println("<body>");
        resp.getWriter().println("<h1>" + posicion + "</h1>");
        resp.getWriter().println("<h2>" + jugador.getNombre() + "</h2>");
        resp.getWriter().println("</body>");
        resp.getWriter().println("</html>");

        llamadas++;

        if (llamadas == 3) {
            llamadas = 0;
            podio.clear();
        }
    }

    private int contarVictorias(Jugador jugador, List<Partida> partidas) {

        int victorias = 0;

        for (Partida p : partidas) {

            if (p.isFinalizada() && p.getGanador() == jugador) {
                victorias++;
            }
        }

        return victorias;
    }
}