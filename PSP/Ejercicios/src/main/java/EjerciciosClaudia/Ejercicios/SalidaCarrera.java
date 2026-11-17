package EjerciciosClaudia.Ejercicios;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

/*
 * EJERCICIO 7: SALIDA DE CARRERA
 *
 * Enunciado del material del profesor:
 *  - Cinco corredores calientan durante un tiempo aleatorio.
 *  - Ninguno empieza hasta que todos estan preparados.
 *  - El juez da la salida cuando el ultimo corredor esta listo.
 *
 * Conceptos: Thread, CountDownLatch, await(), countDown() y join().
 */
public class SalidaCarrera {

    public static void main(String[] args) throws InterruptedException {
        final int NUM_CORREDORES = 5;

        // El primer latch cuenta a los corredores que ya han terminado de calentar.
        CountDownLatch todosListos = new CountDownLatch(NUM_CORREDORES);

        // El segundo latch representa la senal de salida: comienza en 1 (cerrado).
        CountDownLatch senalSalida = new CountDownLatch(1);

        List<Thread> corredores = new ArrayList<>();

        for (int i = 1; i <= NUM_CORREDORES; i++) {
            final int dorsal = i;
            Thread corredor = Thread.ofVirtual().name("corredor-" + dorsal).start(() -> {
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(500, 2001));
                    System.out.println("Corredor " + dorsal + " esta preparado");

                    todosListos.countDown();
                    senalSalida.await(); // Espera a que el juez de la salida.

                    System.out.println("Corredor " + dorsal + " empieza a correr");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            corredores.add(corredor);
        }

        // El juez no continua hasta que todos hayan llegado al primer latch.
        todosListos.await();
        System.out.println("JUEZ: preparados... listos... YA!");
        senalSalida.countDown();

        // El main espera a que todos los corredores terminen.
        for (Thread corredor : corredores) {
            corredor.join();
        }
    }
}
