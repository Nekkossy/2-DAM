package main.java.TareasAsyncronas;

import java.util.concurrent.CompletableFuture;

public class Practica5 {

    void main(String[] args) {

        CompletableFuture<Integer> futuro = CompletableFuture.supplyAsync(() -> {
            return 10 + 20;
        });

        futuro.thenApply(resultado -> {
            return resultado * 2;
        });

        futuro.thenAccept(resultado -> {
            System.out.println("Resultado: " + resultado);
        });
    }
}