package org.example;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.model.Partida;

import java.io.IOException;

@WebServlet("/comenzar")
public class ComenzarPartida extends HttpServlet{
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
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Faltan Participantes por unirse");
            return;
        }

        String admin = req.getParameter("admin");
        String contrasenia=req.getParameter("contrasenia");

        if (admin == null || contrasenia == null || !"123".equals(contrasenia)){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Contraseña de administrador incorrecta");
            partida.setJugable(false);
            return;

        }else {
            resp.getWriter().println("Partida arrancada correctamente");
            partida.setJugable(true);
        }


    }
}
