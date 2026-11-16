package HilosClaudia;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class prueba2 {
    static void main(String[] args) {
        long t0 = System.currentTimeMillis();
        ExecutorService ex = Executors.newFixedThreadPool(3);
        List<String> resultados = new ArrayList<>();
        List<CompletableFuture<String>> tickets = new ArrayList<>();

        for (String s : List.of("A", "B", "C")) {
            tickets.add(CompletableFuture.supplyAsync(()-> consultar(s), ex));
        }
        for (CompletableFuture<String> t : tickets) resultados.add(t.join());
        System.out.println(System.currentTimeMillis() - t0);
        System.out.println(resultados);

        ex.shutdown();
    }
    static String consultar(String s) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "resultado " + s;
    }

}
