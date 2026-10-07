package main.java.Problemas;

import java.util.concurrent.atomic.AtomicInteger;

public class Problema1 {

    void main(String[] args) throws Exception {

        System.out.println("=== CONTADOR DE VISITAS WEB ===");
        System.out.println("Esperando 1000 visitantes...");

        System.out.println();

        // SIN SINCRONIZACIÓN
        ContadorVisitas contador1 = new ContadorVisitas();

        long inicio = System.currentTimeMillis();

        Thread[] hilos1 = new Thread[1000];

        for (int i = 0; i < 1000; i++) {

            hilos1[i] = new Thread(() -> {

                try {
                    Thread.sleep((long) (Math.random() * 101) + 50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                contador1.incrementarVisita();
            });

            hilos1[i].start();
        }

        for (int i = 0; i < 1000; i++) {
            hilos1[i].join();
        }

        long fin = System.currentTimeMillis();

        System.out.println("--- SIN SINCRONIZACIÓN ---");
        System.out.println("Visitas esperadas: 1000");
        System.out.println("Visitas contadas: " + contador1.getVisitas());
        System.out.println("Tiempo: " + (fin - inicio) + "ms");


        // CON SYNCHRONIZED
        ContadorVisitasSynchronized contador2 = new ContadorVisitasSynchronized();

        inicio = System.currentTimeMillis();

        Thread[] hilos2 = new Thread[1000];

        for (int i = 0; i < 1000; i++) {

            hilos2[i] = new Thread(() -> {

                try {
                    Thread.sleep((long) (Math.random() * 101) + 50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                contador2.incrementarVisita();
            });

            hilos2[i].start();
        }

        for (int i = 0; i < 1000; i++) {
            hilos2[i].join();
        }

        fin = System.currentTimeMillis();

        System.out.println();
        System.out.println("--- CON SYNCHRONIZED ---");
        System.out.println("Visitas esperadas: 1000");
        System.out.println("Visitas contadas: " + contador2.getVisitas());
        System.out.println("Tiempo: " + (fin - inicio) + "ms");


        // CON ATOMICINTEGER
        ContadorVisitasAtomic contador3 = new ContadorVisitasAtomic();

        inicio = System.currentTimeMillis();

        Thread[] hilos3 = new Thread[1000];

        for (int i = 0; i < 1000; i++) {

            hilos3[i] = new Thread(() -> {

                try {
                    Thread.sleep((long) (Math.random() * 101) + 50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                contador3.incrementarVisita();
            });

            hilos3[i].start();
        }

        for (int i = 0; i < 1000; i++) {
            hilos3[i].join();
        }

        fin = System.currentTimeMillis();

        System.out.println();
        System.out.println("--- CON ATOMICINTEGER ---");
        System.out.println("Visitas esperadas: 1000");
        System.out.println("Visitas contadas: " + contador3.getVisitas());
        System.out.println("Tiempo: " + (fin - inicio) + "ms");
    }
}


class ContadorVisitas {

    private int visitas = 0;

    void incrementarVisita() {
        visitas++;
    }

    int getVisitas() {
        return visitas;
    }
}


class ContadorVisitasSynchronized {

    private int visitas = 0;

    synchronized void incrementarVisita() {
        visitas++;
    }

    int getVisitas() {
        return visitas;
    }
}


class ContadorVisitasAtomic {

    private AtomicInteger visitas = new AtomicInteger(0);

    void incrementarVisita() {
        visitas.incrementAndGet();
    }

    int getVisitas() {
        return visitas.get();
    }
}