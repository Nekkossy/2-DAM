package HilosClaudia;

import java.util.concurrent.CompletableFuture;

public class Combinar2 {

    static void dormir(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    static void main(String[] args) {

        CompletableFuture<String> pizza = CompletableFuture.supplyAsync(() -> {
            dormir(2000);
            return "pizza";
        });

        CompletableFuture<String> bebida = CompletableFuture.supplyAsync(() -> {
            dormir(3000);
            return "refresco";
        });

        CompletableFuture.allOf(pizza, bebida).join();

        System.out.println("Pizza: " + pizza.join());
        System.out.println("Bebida: " + bebida.join());
    }
}