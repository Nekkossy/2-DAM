package main.java.TareasAsyncronas;

import java.util.concurrent.CompletableFuture;

public class Practica7 {

    void main(String[] args) {

        CompletableFuture<String> resultado = obtenerUsuario()
                .thenCompose(usuario -> obtenerEmail(usuario));

        resultado.thenAccept(email ->
                System.out.println("Email: " + email)
        ).join();
    }

    CompletableFuture<String> obtenerUsuario() {

        return CompletableFuture.supplyAsync(() -> {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            return "Oscar";
        });
    }

    CompletableFuture<String> obtenerEmail(String usuario) {

        return CompletableFuture.supplyAsync(() -> {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            return "imageusuario@example.com";
        });
    }
}
