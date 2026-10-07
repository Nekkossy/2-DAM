package main.java.HilosJava25.Ejercicios8.Ejercicio8_2;

import java.util.concurrent.CyclicBarrier;

public class Main {

    void main(String[] args) {

        CyclicBarrier barrera = new CyclicBarrier(4, () -> {
            System.out.println("Fase completada, todos avanzan");
        });

        for (int i = 1; i <= 4; i++) {

            int trabajador = i;

            Thread hilo = new Thread(() -> {

                try {

                    for (int fase = 1; fase <= 3; fase++) {

                        System.out.println("Trabajador " + trabajador
                                + " empieza la fase " + fase);

                        int tiempo = (int) (Math.random() * 3) + 1;

                        Thread.sleep(tiempo * 1000);

                        System.out.println("Trabajador " + trabajador
                                + " termina la fase " + fase);

                        barrera.await();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            hilo.start();
        }
    }
}
