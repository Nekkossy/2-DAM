package apuntes.tienda;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import apuntes.tienda.modelo.Inventario;

import java.io.IOException;
@WebServlet("/crearInv")
public class CrearInventario extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Inventario inventario = (Inventario) req.getSession().getAttribute("inventario");

        resp.setContentType("text/plain;charset=UTF-8");

        if (inventario != null) {
            resp.getWriter().println("Ya existe un inventario");
            return;
        }

        inventario = new Inventario();

        req.getSession().setAttribute("inventario", inventario);

        resp.getWriter().println("Inventario creado correctamente");
    }
}