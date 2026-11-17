
package apuntes.cajaFuerte;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import apuntes.cajaFuerte.modelo.CajaFuerte;

import java.io.IOException;

@WebServlet("/abrir")
public class AbrirCaja extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1. Recoger el numero
        String numero = req.getParameter("numero");

        // 2. Comprobar que existe la caja
        CajaFuerte caja = (CajaFuerte) req.getSession().getAttribute("cajaFuerte");

        if (caja == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Primero configura la caja");
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
        if (caja.isAbierta()) {
            resp.getWriter().println("La caja ya esta abierta");
            return;
        }

        // 6. Comprobar el numero introducido
        int posicion = caja.getPosicion();

        if (digito == caja.getCombinacion()[posicion]) {

            posicion++;
            caja.setPosicion(posicion);

            // 7. Comprobar si hemos completado la combinacion
            if (posicion == 3) {
                caja.setAbierta(true);
                resp.getWriter().println("Caja abierta correctamente");
            } else {
                resp.getWriter().println("Numero correcto. Introduce el siguiente");
            }

        } else {

            // Reiniciar la secuencia
            caja.setPosicion(0);

            // Si coincide con el primer numero, contamos ese intento
            if (digito == caja.getCombinacion()[0]) {
                caja.setPosicion(1);
            }

            resp.getWriter().println("Numero incorrecto. Vuelve a empezar");
        }
    }
}
