package HilosClaudia;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PoolSimple {

    static void main(String[] args) throws InterruptedException {

        ExecutorService pool = Executors.newFixedThreadPool(3);

        for (int i = 1; i <= 8; i++) {
            final int numero = i;
            pool.submit(() -> {
                String hilo = Thread.currentThread().getName();
                System.out.println(hilo + " empieza la tarea " + numero);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println(hilo + " interrumpido");
                }
                System.out.println(hilo + " termina la tarea " + numero);
            });
        }

        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.MINUTES);

        System.out.println("Fin");
    }
}