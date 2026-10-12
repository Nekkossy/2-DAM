package main.java.HilosJava25.Ejercicios5.Ejercicio5_1_Parking;

import java.util.concurrent.Semaphore;

public class Parking {

    private Semaphore plazas = new Semaphore(3);

    public void entrar(int coche) {

        try {
            if (plazas.availablePermits() == 0) {
                System.out.println("Coche " + coche + " espera porque no hay plazas");
            }

            plazas.acquire();

            System.out.println("Coche " + coche + " ha entrado al parking");

            Thread.sleep((long) (Math.random() * 3000 + 1000));

            System.out.println("Coche " + coche + " ha salido del parking");

            plazas.release();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
