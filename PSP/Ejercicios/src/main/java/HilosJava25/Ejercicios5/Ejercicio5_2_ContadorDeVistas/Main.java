package main.java.HilosJava25.Ejercicios5.Ejercicio5_2_ContadorDeVistas;

import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    void main(String[] args) {

        AtomicInteger visitas = new AtomicInteger(0);

        Thread[] hilos = new Thread[1000];

        for (int i = 0; i < 1000; i++) {

            hilos[i] = new Thread(() -> {
                visitas.incrementAndGet();
            });

            hilos[i].start();
        }

        for (int i = 0; i < 1000; i++) {

            try {
                hilos[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Número de visitas: " + visitas.get());
    }
}