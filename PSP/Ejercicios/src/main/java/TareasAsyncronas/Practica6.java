package main.java.TareasAsyncronas;

import java.util.concurrent.CompletableFuture;

public class Practica6 {

    void main(String[] args) {

        CompletableFuture
                .supplyAsync(() -> 10)
                .thenApply(n -> n * 2)
                .thenApply(n -> n + 5)
                .thenApply(String::valueOf)
                .thenAccept(resultado ->
                        System.out.println(
                                "Resultado: " + resultado
                        )
                )
                .join();
    }
}
