package HilosClaudia;

import java.util.concurrent.Semaphore;

public class ParkingSimple {

    static Semaphore plazas = new Semaphore(10);

    static void main(String[] args) throws InterruptedException {

        Thread[] coches = new Thread[10];

        for (int i = 0; i < 10; i++) {
            coches[i] = new Thread(() -> {
                String nombre = Thread.currentThread().getName();
                try {
                    plazas.acquire();
                    System.out.println(nombre + " aparca. Plazas libres: " + plazas.availablePermits());
                    Thread.sleep(2000);
                    System.out.println(nombre + " se va");
                    plazas.release();
                } catch (InterruptedException e) {
                    System.out.println(nombre + " ha sido interrumpido");
                }
            }, "Coche-" + (i + 1));
        }

        for (int i = 0; i < 10; i++) {
            coches[i].start();
        }

        for (int i = 0; i < 10; i++) {
            coches[i].join();
        }

        System.out.println("Fin");
    }
}