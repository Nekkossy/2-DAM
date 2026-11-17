package EjerciciosClaudia.Ejercicios;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * EJERCICIO 1: CONTADOR DE VISITAS WEB
 *
 * Enunciado resumido:
 *  - Simular 1000 visitas concurrentes a una web.
 *  - Cada hilo incrementa el contador una sola vez.
 *  - Comparar una versión insegura, synchronized y AtomicInteger.
 *
 * Conceptos: condicion de carrera, synchronized, AtomicInteger, hilos virtuales y join.
 */
public class ContadorVisitas {

    static final int VISITANTES = 1000;

    // Todas las implementaciones cumplen el mismo contrato.
    interface Contador {
        void incrementarVisita();
        int getVisitas();
    }

    // VERSION 1: insegura. Sirve para observar una condicion de carrera.
    static class ContadorInseguro implements Contador {
        private int visitas;

        public void incrementarVisita() {
            // La operacion visitas++ realmente implica leer, sumar y escribir.
            visitas++;
        }

        public int getVisitas() {
            return visitas;
        }
    }

    // VERSION 2: synchronized permite que solo un hilo entre a la vez.
    static class ContadorSincronizado implements Contador {
        private int visitas;

        public synchronized void incrementarVisita() {
            visitas++;
        }

        public synchronized int getVisitas() {
            return visitas;
        }
    }

    // VERSION 3: AtomicInteger hace el incremento de forma atomica.
    static class ContadorAtomico implements Contador {
        private final AtomicInteger visitas = new AtomicInteger(0);

        public void incrementarVisita() {
            visitas.incrementAndGet();
        }

        public int getVisitas() {
            return visitas.get();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        probar("SIN SINCRONIZACION", new ContadorInseguro());
        probar("CON SYNCHRONIZED", new ContadorSincronizado());
        probar("CON ATOMICINTEGER", new ContadorAtomico());
    }

    static void probar(String nombre, Contador contador) throws InterruptedException {
        List<Thread> hilos = new ArrayList<>();
        long inicio = System.currentTimeMillis();

        // Cada visita se representa mediante un hilo virtual.
        for (int i = 0; i < VISITANTES; i++) {
            Thread hilo = Thread.ofVirtual().start(() -> {
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(5, 16));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                contador.incrementarVisita();
            });
            hilos.add(hilo);
        }

        // join garantiza que todos los visitantes han terminado antes de leer el contador.
        for (Thread hilo : hilos) {
            hilo.join();
        }

        long tiempo = System.currentTimeMillis() - inicio;
        System.out.println("--- " + nombre + " ---");
        System.out.println("Visitas esperadas: " + VISITANTES);
        System.out.println("Visitas contadas:  " + contador.getVisitas());
        System.out.println("Tiempo: " + tiempo + " ms\n");
    }
}
