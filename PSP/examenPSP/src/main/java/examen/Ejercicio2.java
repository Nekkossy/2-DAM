package examen;

import java.util.concurrent.CompletableFuture;

/*
## Ejercicio 2. Entender un programa asíncrono defectuoso (1 puntos)

Una aplicación consulta dos servicios independientes: temperatura y humedad.
Se desea que ambas consultas comiencen sin esperar a que termine la otra y que,
cuando las dos hayan acabado, se muestre el resultado.

 */
public class Ejercicio2 {

    static CompletableFuture<Integer> temperatura() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 25;
        });
    }

    static CompletableFuture<Integer> humedad() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 60;
        });
    }

    static void main(String[] args) {

        String resultado = temperatura()
                .thenCombine(humedad(), (temperatura, humedad) ->
                        "Temperatura: " + temperatura + " ºC\nHumedad: " + humedad + " %")
                .join();

        System.out.println(resultado);
    }
}

//He creado 3 clases y he cambiado el CompletableFuture<String>
// por CompletableFuture<Integer> para así hacer el thenCombine
