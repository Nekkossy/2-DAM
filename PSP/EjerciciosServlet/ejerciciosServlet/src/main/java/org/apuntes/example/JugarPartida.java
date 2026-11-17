package org.example;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.model.Participante;
import org.example.model.Partida;

import java.io.IOException;
@WebServlet("/jugar")
public class JugarPartida extends HttpServlet{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Partida partida = (Partida) req.getSession().getAttribute("partida");
        resp.setContentType("text/plain;charset=UTF-8");

        if (partida == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Primero crea la partida");
            return;
        }
        if (partida.getParticipantes().isEmpty()){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No hay participantes");
            return;
        }
        if (!partida.isJugable()){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "La partida no es jugable todavia, comienzala");
            return;
        }

        String nombreJugador=req.getParameter("nombreJugador");


        if (!partida.getParticipantes().containsKey(nombreJugador)){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "jugador no registrado");
            return;
        }

        synchronized (partida) {
            if (partida.isTerminada()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "La partida ya ha terminado");
                return;
            }

            Participante jugadorActual= partida.getParticipantes().get(nombreJugador);
            if (jugadorActual.isTurnoCumplido()){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "este jugador ya ha jugado este turno");
                return;
            }

            int tirada = partida.tirarDado();
            jugadorActual.sumarPosiciones(tirada);
            jugadorActual.setTurnoCumplido(true);

            resp.getWriter().println("Turno " + partida.getContadorTurnos()
                    + ": " + jugadorActual.getNombre() + " ha sacado " + tirada);
            resp.getWriter().println("JUGADOR "+ jugadorActual.toString()
                    +" va por la casilla "+jugadorActual.getPosicionesAvanzadas());

            for (Participante participante:partida.getParticipantes().values()){
                resp.getWriter().println(participante.getNombre()
                        + " lleva " + participante.getPosicionesAvanzadas() + " casillas");
            }

            if (jugadorActual.getPosicionesAvanzadas()>=partida.getNumCasillas()){
                partida.setTerminada(true);
                partida.setJugable(false);
                resp.getWriter().println("JUGADOR "+ jugadorActual.getNombre()+" ha ganado");
                return;
            }

            boolean todosHanJugado = true;
            for (Participante participante:partida.getParticipantes().values()){
                if (!participante.isTurnoCumplido()){
                    todosHanJugado = false;
                    break;
                }
            }

            if (todosHanJugado) {
                for (Participante participante:partida.getParticipantes().values()){
                    participante.setTurnoCumplido(false);
                }
                partida.setContadorTurnos(partida.getContadorTurnos() + 1);
                resp.getWriter().println("Turno completado. Comienza el siguiente turno");
            }
        }



    }
}
