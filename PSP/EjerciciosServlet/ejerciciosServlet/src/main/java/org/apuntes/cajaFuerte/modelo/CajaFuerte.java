
package apuntes.cajaFuerte.modelo;

import java.util.HashMap;
import java.util.Map;

public class CajaFuerte {

    private int[] combinacion;
    private int posicion;
    private boolean abierta;
    private Map<String, String> secretos;

    public CajaFuerte(int numero1, int numero2, int numero3) {
        this.combinacion = new int[]{numero1, numero2, numero3};
        this.posicion = 0;
        this.abierta = false;
        this.secretos = new HashMap<>();
    }

    public int[] getCombinacion() {
        return combinacion;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public boolean isAbierta() {
        return abierta;
    }

    public void setAbierta(boolean abierta) {
        this.abierta = abierta;
    }

    public Map<String, String> getSecretos() {
        return secretos;
    }
}
