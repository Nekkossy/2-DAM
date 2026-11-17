
package apuntes.cajaFuerte;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import apuntes.cajaFuerte.modelo.CajaFuerte;

import java.io.IOException;

@WebServlet("/consultar")
public class ConsultarSecreto extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1. Recuperar la caja fuerte
        CajaFuerte caja = (CajaFuerte) req.getSession()
                .getAttribute("cajaFuerte");

        // 2. Comprobar que existe
        if (caja == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Primero configura la caja");
            return;
        }

        // 3. Comprobar que esta abierta
        if (!caja.isAbierta()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Primero abre la caja");
            return;
        }

        // 4. Recoger el nombre del secreto
        String nombre = req.getParameter("nombre");

        // 5. Comprobar que existe el parametro
        if (nombre == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Falta el nombre del secreto");
            return;
        }

        // 6. Consultar el secreto
        String secreto = caja.getSecretos().get(nombre);

        // 7. Comprobar que existe el secreto
        if (secreto == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "No existe ese secreto");
            return;
        }

        // 8. Responder
        resp.setContentType("text/plain;charset=UTF-8");
        resp.getWriter().println("Secreto: " + secreto);

    }
}
