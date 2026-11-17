
package apuntes.tienda;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import apuntes.tienda.modelo.Inventario;
import apuntes.tienda.modelo.Producto;

import java.io.IOException;

@WebServlet("/aniadir")
public class AniadirProducto extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String codigo = req.getParameter("codigo");
        String nombre = req.getParameter("nombre");
        String cantidad = req.getParameter("cantidad");

        if (codigo == null || nombre == null || cantidad == null
                || codigo.isBlank() || nombre.isBlank() || cantidad.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Faltan parametros");
            return;
        }

        Inventario inventario = (Inventario) req.getSession().getAttribute("inventario");

        if (inventario == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Primero crea el inventario");
            return;
        }

        try {
            int codigoInt = Integer.parseInt(codigo);
            int cantidadInt = Integer.parseInt(cantidad);

            if (cantidadInt < 0) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cantidad incorrecta");
                return;
            }

            if (inventario.getProductos().containsKey(codigoInt)) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ese codigo ya existe");
                return;
            }

            Producto producto = new Producto(codigoInt, nombre, cantidadInt);

            inventario.getProductos().put(producto.getCodigo(), producto);


            resp.setContentType("text/plain;charset=UTF-8");
            resp.getWriter().println("Producto añadido correctamente");

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Codigo y cantidad deben ser numeros");
        }
    }
}
