package main.java.HilosJava25.Ejercicios7.Ejercicio7_2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

    void main(String[] args) throws Exception {

        try (ExecutorService executor = Executors.newFixedThreadPool(5)) {

            List<Callable<String>> tareas = new ArrayList<>();

            for (int i = 1; i <= 5; i++) {

                int numero = i;

                tareas.add(() -> {

                    int tiempo = (int) (Math.random() * 3) + 1;

                    Thread.sleep(tiempo * 1000);

                    return "API " + numero + " ha respondido";
                });
            }

            List<Future<String>> resultados = executor.invokeAll(tareas);

            for (Future<String> resultado : resultados) {
                System.out.println(resultado.get());
            }
        }
    }
}
