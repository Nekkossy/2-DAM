package main.java.TareasAsyncronas;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Practica2 {

    void main(String[] args) throws Exception {

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Callable<String> tarea = () -> {

            System.out.println("Hilo: " + Thread.currentThread().getName());

            Thread.sleep(5000);

            return "Tarea terminada";
        };

        Future<String> futuro = executor.submit(tarea);

        while (!futuro.isDone()) {
            System.out.println("Esperando...");
            Thread.sleep(1000);
        }

        System.out.println(futuro.get());

        executor.shutdown();
    }
}
