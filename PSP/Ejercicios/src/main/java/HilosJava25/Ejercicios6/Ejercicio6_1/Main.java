package main.java.HilosJava25.Ejercicios6.Ejercicio6_1;

import java.util.concurrent.ArrayBlockingQueue;

public class Main {

    void main(String[] args) {

        ArrayBlockingQueue<Integer> cola = new ArrayBlockingQueue<>(5);

        Thread productor = new Thread(() -> {

            try {

                for (int i = 1; i <= 10; i++) {

                    cola.put(i);

                    System.out.println("Productor añade: " + i);

                    Thread.sleep(500);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread consumidor = new Thread(() -> {

            try {

                for (int i = 1; i <= 10; i++) {

                    int numero = cola.take();

                    System.out.println("Consumidor recibe: " + numero);

                    Thread.sleep(1000);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        productor.start();
        consumidor.start();
    }
}
