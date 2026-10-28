package org.example;

import config.ThymeleafConstants;
import config.UrlConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;

@WebServlet("/tres")
public class TresEnRaya extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String marca = req.getParameter("marca");
        String coordenada = req.getParameter("coordenada");

        if (marca == null || coordenada == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Faltan los parametros marca y coordenada");
            return;
        }

        if (!marca.equals("X") && !marca.equals("O")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "La marca debe ser X o O");
            return;
        }

        int posicion;

        try {
            posicion = Integer.parseInt(coordenada);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "La coordenada debe ser un numero del 1 al 9");
            return;
        }

        if (posicion < 1 || posicion > 9) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "La coordenada debe estar entre 1 y 9");
            return;
        }

        String[] tablero = (String[]) req.getSession().getAttribute("Tablero");

        if (tablero == null) {
            tablero = new String[]{"-", "-", "-", "-", "-", "-", "-", "-", "-"};
            req.getSession().setAttribute("Tablero", tablero);
        }

        String siguiente = (String) req.getSession().getAttribute("Siguiente");

        if (siguiente == null) {
            siguiente = "X";
        }

        if (!marca.equals(siguiente)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "No es tu turno. Le toca jugar a " + siguiente);
            return;
        }

        if (!tablero[posicion - 1].equals("-")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Esa casilla ya esta ocupada");
            return;
        }

        tablero[posicion - 1] = marca;

        if (marca.equals("X")) {
            siguiente = "O";
        } else {
            siguiente = "X";
        }

        req.getSession().setAttribute("Siguiente", siguiente);

        String fila1 = tablero[0] + " " + tablero[1] + " " + tablero[2];
        String fila2 = tablero[3] + " " + tablero[4] + " " + tablero[5];
        String fila3 = tablero[6] + " " + tablero[7] + " " + tablero[8];

        var webExchange = JakartaServletWebApplication
                .buildApplication(getServletContext())
                .buildExchange(req, resp);

        WebContext ctx = new WebContext(webExchange);

        ctx.setVariable("fila1", fila1);
        ctx.setVariable("fila2", fila2);
        ctx.setVariable("fila3", fila3);
        ctx.setVariable("siguiente", siguiente);

        resp.setContentType(ThymeleafConstants.CONTENT_TYPE);

        ((TemplateEngine) getServletContext()
                .getAttribute(ThymeleafConstants.TEMPLATE_ENGINE_ATTR))
                .process(UrlConstants.TEMPLATE_TRES_EN_RAYA, ctx, resp.getWriter());
    }
}