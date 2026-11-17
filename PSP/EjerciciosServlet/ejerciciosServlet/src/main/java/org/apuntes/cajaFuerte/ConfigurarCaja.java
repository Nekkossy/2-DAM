
package apuntes.cajaFuerte;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import apuntes.cajaFuerte.modelo.CajaFuerte;

import java.io.IOException;

@WebServlet("/configurar")
public class ConfigurarCaja extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1. Recoger los tres parametros
        String n1 = req.getParameter("n1");
        String n2 = req.getParameter("n2");
        String n3 = req.getParameter("n3");

        // 2. Comprobar que no falte ninguno
        if (n1 == null || n2 == null || n3 == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametro requerido");
            return;
        }

        // 3. Convertir los parametros a int
        int digito1;
        int digito2;
        int digito3;

        try {
            digito1 = Integer.parseInt(n1);
            digito2 = Integer.parseInt(n2);
            digito3 = Integer.parseInt(n3);

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Los numeros deben ser enteros");
            return;
        }

        // 4. Crear la caja fuerte
        CajaFuerte caja = new CajaFuerte(digito1, digito2, digito3);

        // 5. Guardarla en la sesion
        req.getSession().setAttribute("cajaFuerte", caja);

        // 6. Responder al usuario
        resp.setContentType("text/plain;charset=UTF-8");
        resp.getWriter().println("Caja fuerte configurada correctamente");

    }
}
