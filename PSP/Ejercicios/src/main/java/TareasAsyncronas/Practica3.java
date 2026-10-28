package main.java.TareasAsyncronas;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Practica3 {

    void main(String[] args) throws Exception {

        long inicio = System.currentTimeMillis();

        ExecutorService executor = Executors.newFixedThreadPool(3);

        Callable<Integer> tarea1 = () -> {
            Thread.sleep(2000);
            return 10;
        };

        Callable<Integer> tarea2 = () -> {
            Thread.sleep(1000);
            return 20;
        };

        Callable<Integer> tarea3 = () -> {
            Thread.sleep(3000);
            return 30;
        };

        Future<Integer> futuro1 = executor.submit(tarea1);
        Future<Integer> futuro2 = executor.submit(tarea2);
        Future<Integer> futuro3 = executor.submit(tarea3);

        int resultado1 = futuro1.get();
        int resultado2 = futuro2.get();
        int resultado3 = futuro3.get();

        int total = resultado1 + resultado2 + resultado3;

        long fin = System.currentTimeMillis();

        System.out.println("Resultado total: " + total);
        System.out.println("Tiempo total: " + (fin - inicio) + " ms");

        executor.shutdown();
    }
}
