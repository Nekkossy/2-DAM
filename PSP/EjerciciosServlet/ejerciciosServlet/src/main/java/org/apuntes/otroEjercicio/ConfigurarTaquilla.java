package apuntes.otroEjercicio;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import apuntes.otroEjercicio.modelo.Taquilla;

import java.io.IOException;

@WebServlet("/configurarT")
public class ConfigurarTaquilla extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1. Recoger los tres parametros
        String n1 = req.getParameter("n1");
        String n2 = req.getParameter("n2");
        String n3 = req.getParameter("n3");
        String n4 = req.getParameter("n4");


        // 2. Comprobar que no falte ninguno
        if (n1 == null || n2 == null || n3 == null|| n4 == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametro requerido");
            return;
        }

        // 3. Convertir los parametros a int
        int digito1;
        int digito2;
        int digito3;
        int digito4;

        try {
            digito1 = Integer.parseInt(n1);
            digito2 = Integer.parseInt(n2);
            digito3 = Integer.parseInt(n3);
            digito4= Integer.parseInt(n4);

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Los numeros deben ser enteros");
            return;
        }

        // 4. Crear la taquilla fuerte
        Taquilla taquilla = new Taquilla(digito1, digito2, digito3, digito4);

        // 5. Guardarla en la sesion
        req.getSession().setAttribute("taquilla", taquilla);

        // 6. Responder al usuario
        resp.setContentType("text/plain;charset=UTF-8");
        resp.getWriter().println("Taquilla configurada correctamente");

    }
}
