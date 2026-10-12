package main.java.HilosJava25.Ejercicios5.Ejercicio5_3;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    void main(String[] args) {

        ReentrantLock lock = new ReentrantLock();

        Thread hilo1 = new Thread(() -> {

            lock.lock();

            try {
                System.out.println("Hilo 1 está usando el recurso");

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("Hilo 1 ha terminado");

            } finally {
                lock.unlock();
            }
        });

        Thread hilo2 = new Thread(() -> {

            boolean conseguido = false;

            while (!conseguido) {

                try {
                    conseguido = lock.tryLock(500, TimeUnit.MILLISECONDS);

                    if (conseguido) {

                        try {
                            System.out.println("Hilo 2 está usando el recurso");

                        } finally {
                            lock.unlock();
                        }

                    } else {
                        System.out.println("Ocupado, lo intento más tarde");
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        hilo1.start();
        hilo2.start();
    }
}