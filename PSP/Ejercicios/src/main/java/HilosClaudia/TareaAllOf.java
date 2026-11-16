package HilosClaudia;

import java.util.concurrent.CompletableFuture;

public class TareaAllOf {

    static void dormir(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    static void main(String[] args) {
        long inicio = System.currentTimeMillis();

        CompletableFuture<String> pizza = CompletableFuture.supplyAsync(() -> {
            dormir(2000);
            return "pizza";
        });

        CompletableFuture<String> bebida = CompletableFuture.supplyAsync(() -> {
            dormir(3000);
            return "refresco";
        });

        CompletableFuture<String> postre = CompletableFuture.supplyAsync(() -> {
            dormir(5000);
            return "tarta";
        });

        CompletableFuture.allOf(pizza, bebida, postre).join();

        System.out.println("Pizza: " + pizza.join());
        System.out.println("Bebida: " + bebida.join());
        System.out.println("Postre: " + postre.join());

        System.out.println("Tardó " + (System.currentTimeMillis() - inicio) + " ms");
    }
}