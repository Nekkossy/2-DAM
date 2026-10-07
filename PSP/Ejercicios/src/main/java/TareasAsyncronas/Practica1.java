package main.java.TareasAsyncronas;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Practica1 {

    void main(String[] args) throws Exception {

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Callable<Integer> tarea = () -> {

            Thread.sleep(2000);

            return 42;
        };

        Future<Integer> futuro = executor.submit(tarea);

        System.out.println("Tarea enviada");

        System.out.println("Esperando resultado...");

        int resultado = futuro.get();

        System.out.println("Resultado: " + resultado);

        executor.shutdown();
    }
}
