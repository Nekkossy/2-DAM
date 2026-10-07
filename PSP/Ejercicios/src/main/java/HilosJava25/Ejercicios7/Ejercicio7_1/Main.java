package main.java.HilosJava25.Ejercicios7.Ejercicio7_1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

    void main(String[] args) throws Exception {

        int[] numeros = new int[10_000_000];

        for (int i = 0; i < numeros.length; i++) {
            numeros[i] = 1;
        }

        ExecutorService executor = Executors.newFixedThreadPool(4);

        Future<Long>[] resultados = new Future[4];

        int tamaño = numeros.length / 4;

        for (int i = 0; i < 4; i++) {

            int inicio = i * tamaño;
            int fin = (i + 1) * tamaño;

            resultados[i] = executor.submit(() -> {

                long suma = 0;

                for (int j = inicio; j < fin; j++) {
                    suma += numeros[j];
                }

                return suma;
            });
        }

        long sumaTotal = 0;

        for (int i = 0; i < 4; i++) {
            sumaTotal += resultados[i].get();
        }

        System.out.println("Suma total: " + sumaTotal);

        executor.shutdown();
    }
}
