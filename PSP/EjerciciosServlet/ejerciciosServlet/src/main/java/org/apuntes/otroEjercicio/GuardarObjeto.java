package apuntes.otroEjercicio;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import apuntes.otroEjercicio.modelo.Taquilla;

import java.io.IOException;

@WebServlet("/guardarT")
public class GuardarObjeto extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1. Recuperar la taquilla fuerte
        Taquilla taquilla = (Taquilla) req.getSession().getAttribute("taquilla");

        // 2. Comprobar que existe
        if (taquilla == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Primero configura la taquilla");
            return;
        }

        // 3. Comprobar que esta abierta
        if (!taquilla.isAbierta()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Primero abre la taquilla");
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
        taquilla.getObjetos().put(nombre, valor);

        // 7. Responder
        resp.setContentType("text/plain;charset=UTF-8");
        resp.getWriter().println("Objeto guardado correctamente");

    }
}
