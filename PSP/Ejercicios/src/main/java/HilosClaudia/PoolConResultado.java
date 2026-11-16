package HilosClaudia;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PoolConResultado {

    static void main(String[] args) throws Exception {

        ExecutorService pool = Executors.newFixedThreadPool(3);
        List<Future<Integer>> futuros = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            final int numero = i;
            Future<Integer> futuro = pool.submit(() -> {
                Thread.sleep(numero == 1? 4000 : 1000);
                return numero * numero;
            });
            futuros.add(futuro);
        }

        System.out.println("Todas las tareas enviadas");

        for (Future<Integer> futuro : futuros) {
            System.out.println("Resultado: " + futuro.get());
        }

        pool.shutdown();
    }
}