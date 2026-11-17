
package apuntes.otroEjercicio;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import apuntes.otroEjercicio.modelo.Taquilla;

import java.io.IOException;

@WebServlet("/consultarT")
public class ConsultarObjeto extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1. Recuperar la taquilla fuerte
        Taquilla taquilla = (Taquilla) req.getSession()
                .getAttribute("taquilla");

        // 2. Comprobar que existe
        if (taquilla == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Primero configura la taquilla");
            return;
        }

        // 3. Comprobar que esta abierta
        if (!taquilla.isAbierta()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Primero abre la taquilla");
            return;
        }

        // 4. Recoger el nombre del objeto
        String nombre = req.getParameter("nombre");

        // 5. Comprobar que existe el parametro
        if (nombre == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Falta el nombre del objeto");
            return;
        }

        // 6. Consultar el objeto
        String objeto = taquilla.getObjetos().get(nombre);

        // 7. Comprobar que existe el objeto
        if (objeto == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "No existe ese objeto");
            return;
        }

        // 8. Responder
        resp.setContentType("text/plain;charset=UTF-8");
        resp.getWriter().println("Objeto: " + objeto);

    }
}
