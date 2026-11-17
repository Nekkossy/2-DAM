
package apuntes.tienda;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import apuntes.tienda.modelo.Inventario;
import apuntes.tienda.modelo.Producto;

import java.io.IOException;

@WebServlet("/inventario")
public class ConsultarInventario extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Inventario inventario = (Inventario) req.getSession().getAttribute("inventario");

        if (inventario == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Primero crea el inventario");
            return;
        }

        resp.setContentType("text/plain;charset=UTF-8");

        if (inventario.getProductos().isEmpty()) {
            resp.getWriter().println("El inventario está vacío");
            return;
        }

        for (Producto producto : inventario.getProductos().values()) {
            resp.getWriter().println(
                    "Codigo: " + producto.getCodigo()
                            + " | Nombre: " + producto.getNombre()
                            + " | Cantidad: " + producto.getCantidad()
            );
        }

    }
}
