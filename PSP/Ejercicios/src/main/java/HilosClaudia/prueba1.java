package HilosClaudia;

import java.util.concurrent.CompletableFuture;

public class prueba1 {
    static void main(String[] args) {
        CompletableFuture<Void> tarea = //la cadena acaba en thenAccept, que no devuelve resultado, así que el tipo es CompletableFuture<Void>.
                CompletableFuture.supplyAsync(() -> "ana")
                        .thenApply(s -> s.toUpperCase())
                        //thenAccept consume el valor y no devuelve nada (Void), así que la etapa siguiente no recibía
                        // ningún String y s.length() no compilaba. thenApply transforma y devuelve el valor
                        .thenApply(s -> s + " (" + s.length() + ")")
                        .thenAccept(r -> System.out.println(r));
        tarea.join();
    }
}
