
package apuntes.cajaFuerte;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import apuntes.cajaFuerte.modelo.CajaFuerte;

import java.io.IOException;

@WebServlet("/guardar")
public class GuardarSecreto extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1. Recuperar la caja fuerte
        CajaFuerte caja = (CajaFuerte) req.getSession().getAttribute("cajaFuerte");

        // 2. Comprobar que existe
        if (caja == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Primero configura la caja");
            return;
        }

        // 3. Comprobar que esta abierta
        if (!caja.isAbierta()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Primero abre la caja");
            return;
        }

        // 4. Recoger los parametros
        String nombre = req.getParameter("nombre");
        String valor = req.getParameter("valor");

        // 5. Comprobar que no falten parametros
        if (nombre == null || valor == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Faltan parametros");
            return;
        }

        // 6. Guardar el secreto
        caja.getSecretos().put(nombre, valor);

        // 7. Responder
        resp.setContentType("text/plain;charset=UTF-8");
        resp.getWriter().println("Secreto guardado correctamente");

    }
}
