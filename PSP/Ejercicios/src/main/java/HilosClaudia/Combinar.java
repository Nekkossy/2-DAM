package HilosClaudia;

import java.util.concurrent.CompletableFuture;

public class Combinar {

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

        System.out.println("Pizza: " + pizza.join());

        CompletableFuture<String> bebida = CompletableFuture.supplyAsync(() -> {
            dormir(3000);
            return "refresco";
        });

        CompletableFuture<String> menu =
                pizza.thenCombine(bebida, (p, b) -> p + " + " + b);

        System.out.println("Menú: " + menu.join());
        System.out.println("Tardó " + (System.currentTimeMillis() - inicio) + " ms");
    }
}