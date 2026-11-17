package apuntes.tienda.modelo;
import java.util.HashMap;
import java.util.Map;

public class Inventario {
    private Map<Integer, Producto> productos;

    public Inventario(Map<Integer, Producto> productos) {
        this.productos = productos;
    }

    public Inventario() {
        this.productos = new HashMap<>();
    }

    public Map<Integer, Producto> getProductos() {
        return productos;
    }
    public void setProductos(Map<Integer, Producto> productos) {
        this.productos = productos;
    }
}
