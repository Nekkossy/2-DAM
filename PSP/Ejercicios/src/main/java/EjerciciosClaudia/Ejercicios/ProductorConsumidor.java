package EjerciciosClaudia.Ejercicios;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/*
 * EJERCICIO 9: PRODUCTOR-CONSUMIDOR CON ARRAYBLOCKINGQUEUE
 *
 * Enunciado del material del profesor:
 *  - Un productor genera 20 numeros.
 *  - Un consumidor los recoge y procesa.
 *  - La cola tiene capacidad 5.
 *  - El productor espera si la cola esta llena y el consumidor espera si esta vacia.
 *
 * Conceptos: BlockingQueue, put(), take(), sleep() y join().
 */
public class ProductorConsumidor {

    static final int CAPACIDAD = 5;
    static final int TOTAL = 20;
    static final BlockingQueue<Integer> cola = new ArrayBlockingQueue<>(CAPACIDAD);

    public static void main(String[] args) throws InterruptedException {
        Thread productor = Thread.ofVirtual().name("productor").start(() -> {
            try {
                for (int i = 1; i <= TOTAL; i++) {
                    cola.put(i); // Si esta llena, el productor espera sin hacer sondeo.
                    System.out.println("Productor produce " + i + " (cola: " + cola.size() + ")");
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumidor = Thread.ofVirtual().name("consumidor").start(() -> {
            try {
                for (int i = 1; i <= TOTAL; i++) {
                    Integer valor = cola.take(); // Si esta vacia, el consumidor espera.
                    System.out.println("Consumidor recoge " + valor + " (cola: " + cola.size() + ")");
                    Thread.sleep(250);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        productor.join();
        consumidor.join();
        System.out.println("Productor y consumidor han terminado.");
    }
}
