package HilosClaudia;

import java.util.concurrent.CompletableFuture;

public class Errores {

    static String consultarServicio() {
        throw new RuntimeException("el servicio ha fallado");
    }

    static void main(String[] args) {
        CompletableFuture<String> resultado =
                CompletableFuture.supplyAsync(() -> consultarServicio())
                        .thenApply(s -> s.toUpperCase());

        System.out.println(resultado.join());
    }
}