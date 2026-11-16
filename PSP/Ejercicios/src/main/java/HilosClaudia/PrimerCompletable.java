package HilosClaudia;

import java.util.concurrent.CompletableFuture;

public class PrimerCompletable {

    static void main(String[] args) {

        CompletableFuture<Void> tarea =
                CompletableFuture.supplyAsync(() -> 10)
                        .thenApply(n -> n * 2)
                        .thenAccept(resultado ->
                                System.out.println("Resultado: " + resultado));

        tarea.join();
    }
}