
package apuntes.tienda;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import apuntes.tienda.modelo.Inventario;
import apuntes.tienda.modelo.Producto;

import java.io.IOException;

@WebServlet("/vender")
public class VenderProducto extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String codigo = req.getParameter("codigo");
        String cantidad = req.getParameter("cantidad");

        if (codigo == null || cantidad == null
                || codigo.isBlank() || cantidad.isBlank()) {
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

            if (cantidadInt <= 0) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cantidad incorrecta");
                return;
            }

            Producto producto = inventario.getProductos().get(codigoInt);

            if (producto == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Producto no encontrado");
                return;
            }

            if (producto.getCantidad() < cantidadInt) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No hay existencias suficientes");
                return;
            }

            producto.setCantidad(producto.getCantidad() - cantidadInt);

            resp.setContentType("text/plain;charset=UTF-8");
            resp.getWriter().println("Venta realizada correctamente");

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Codigo y cantidad deben ser numeros");
        }
    }
}
