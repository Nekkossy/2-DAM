package main.java.HilosJava25.Ejercicios8.Ejercicio8_1;

import java.util.concurrent.CountDownLatch;

public class Main {

    void main(String[] args) {

        CountDownLatch preparados = new CountDownLatch(5);
        CountDownLatch salida = new CountDownLatch(1);

        for (int i = 1; i <= 5; i++) {

            int corredor = i;

            Thread hilo = new Thread(() -> {

                try {

                    int tiempo = (int) (Math.random() * 4) + 1;

                    System.out.println("Corredor " + corredor + " se está preparando...");

                    Thread.sleep(tiempo * 1000);

                    System.out.println("Corredor " + corredor + " está listo");

                    preparados.countDown();

                    salida.await();

                    System.out.println("Corredor " + corredor + " empieza a correr");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            hilo.start();
        }

        Thread juez = new Thread(() -> {

            try {

                preparados.await();

                System.out.println("Todos los corredores están listos");

                Thread.sleep(1000);

                System.out.println("¡¡¡SALIDA!!!");

                salida.countDown();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        juez.start();
    }
}