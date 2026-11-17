
package apuntes.otroEjercicio;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import apuntes.otroEjercicio.modelo.Taquilla;

import java.io.IOException;

@WebServlet("/abrirT")
public class AbrirTaquilla extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1. Recoger el numero
        String numero = req.getParameter("numero");

        // 2. Comprobar que existe la taquilla
        Taquilla taquilla = (Taquilla) req.getSession().getAttribute("taquilla");

        if (taquilla == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Primero configura la taquilla");
            return;
        }

        // 3. Comprobar que llega el parametro
        if (numero == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el numero");
            return;
        }

        // 4. Convertir a entero
        int digito;

        try {
            digito = Integer.parseInt(numero);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "El numero debe ser entero");
            return;
        }

        // 5. Comprobar si ya esta abierta
        if (taquilla.isAbierta()) {
            resp.getWriter().println("La taquilla ya esta abierta");
            return;
        }

        // 6. Comprobar el numero introducido
        int posicion = taquilla.getPosicion();

        if (digito == taquilla.getCombinacion()[posicion]) {

            posicion++;
            taquilla.setPosicion(posicion);

            // 7. Comprobar si hemos completado la combinacion
            if (posicion == 4) {
                taquilla.setAbierta(true);
                resp.getWriter().println("Caja abierta correctamente");
            } else {
                resp.getWriter().println("Numero correcto. Introduce el siguiente");
            }

        } else {

            // Reiniciar la secuencia
            taquilla.setPosicion(0);

            // Si coincide con el primer numero, contamos ese intento
            if (digito == taquilla.getCombinacion()[0]) {
                taquilla.setPosicion(1);
            }

            resp.getWriter().println("Numero incorrecto. Vuelve a empezar");
        }
    }
}
