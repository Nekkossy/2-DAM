package main.java.TareasAsyncronas;

import java.util.concurrent.CompletableFuture;

public class Practica8 {

    void main(String[] args) {

        CompletableFuture<Integer> temperatura = consultarTemperatura();

        CompletableFuture<Integer> humedad = consultarHumedad();

        temperatura.thenCombine(humedad, (temp, hum) -> {
            System.out.println("Temperatura: " + temp + " ºC");
            System.out.println("Humedad: " + hum + " %");
            return true;
        }).join();
    }

    CompletableFuture<Integer> consultarTemperatura() {

        return CompletableFuture.supplyAsync(() -> {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            return 25;
        });
    }

    CompletableFuture<Integer> consultarHumedad() {

        return CompletableFuture.supplyAsync(() -> {

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            return 60;
        });
    }
}
