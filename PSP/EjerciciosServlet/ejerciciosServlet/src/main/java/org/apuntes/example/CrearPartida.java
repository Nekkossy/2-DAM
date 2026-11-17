package org.example;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.model.Partida;

import java.io.IOException;

@WebServlet("/crear")
public class CrearPartida extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Partida partida = (Partida) req.getSession().getAttribute("partida");
        String admin = req.getParameter("admin");
        String contrasenia=req.getParameter("contrasenia");
        String numCasillas= req.getParameter("numCasillas");

        resp.setContentType("text/plain;charset=UTF-8");

        if (partida != null) {
            if (!partida.isTerminada()) {
                resp.getWriter().println("Ya tienes una partida en curso");
            }
            return;
        }
        if (numCasillas==null||admin==null||contrasenia==null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametros requeridos");
            return;
        }

        if (!"123".equals(contrasenia)){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Contraseña de administrador incorrecta");
            return;
        }

        int casillas;
        try {
            casillas=Integer.parseInt(numCasillas);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "La longitud debe ser un numero");
            return;
        }
        if (casillas <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "La longitud debe ser mayor que cero");
            return;
        }


        partida = new Partida(casillas);
        partida.setNumCasillas(casillas);

        req.getSession().setAttribute("partida", partida);

        resp.getWriter().println("Partida creada correctamente");
    }
}
