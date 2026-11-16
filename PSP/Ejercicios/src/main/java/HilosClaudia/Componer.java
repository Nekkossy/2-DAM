package HilosClaudia;

import java.util.concurrent.CompletableFuture;

public class Componer {

    static void dormir(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    static CompletableFuture<String> buscarUsuario() {
        return CompletableFuture.supplyAsync(() -> {
            dormir(1000);
            return "Ana";
        });
    }

    static CompletableFuture<String> buscarPedido(String usuario) {
        return CompletableFuture.supplyAsync(() -> {
            dormir(1000);
            return "pizza de " + usuario;
        });
    }

    static void main(String[] args) {
        long inicio = System.currentTimeMillis();

        CompletableFuture<String> resultado =
                buscarUsuario()
                        .thenCompose(usuario -> buscarPedido(usuario));

        System.out.println(resultado.join());
        System.out.println("Tardó " + (System.currentTimeMillis() - inicio) + " ms");
    }
}