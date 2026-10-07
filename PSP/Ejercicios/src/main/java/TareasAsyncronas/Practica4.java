package main.java.TareasAsyncronas;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Practica4 {

    void main(String[] args) throws Exception {

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

        Future<Integer> f1 = executor.submit(tarea1);
        Future<Integer> f2 = executor.submit(tarea2);
        Future<Integer> f3 = executor.submit(tarea3);

        int resultado2 = f2.get();
        int resultado1 = f1.get();
        int resultado3 = f3.get();

        int total = resultado1 + resultado2 + resultado3;

        System.out.println("Resultado total: " + total);

        executor.shutdown();
    }
}
