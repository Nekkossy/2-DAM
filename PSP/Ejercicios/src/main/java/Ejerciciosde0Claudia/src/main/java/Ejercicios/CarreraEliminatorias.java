package Ejerciciosde0Claudia.src.main.java.Ejercicios;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * EJERCICIO 1: CARRERA DE ELIMINATORIAS
 *
 * Cada corredor es un hilo. Todos esperan hasta que el juez da la salida.
 * Corren durante un tiempo aleatorio y se registra el orden de llegada.
 * En cada ronda se elimina al ultimo corredor. Gana el ultimo que queda.
 */
public class CarreraEliminatorias {

    static final int PARTICIPANTES_INICIALES = 8;
    static final int TIEMPO_MIN_MS = 500;
    static final int TIEMPO_MAX_MS = 1800;

    static class Corredor implements Runnable {
        private final int id;
        private final CountDownLatch preparados;
        private final CountDownLatch salida;
        private final AtomicInteger ordenGlobal;
        private int posicionLlegada;

        Corredor(int id, CountDownLatch preparados,
                 CountDownLatch salida, AtomicInteger ordenGlobal) {
            this.id = id;
            this.preparados = preparados;
            this.salida = salida;
            this.ordenGlobal = ordenGlobal;
        }

        @Override
        public void run() {
            // Avisamos al hilo principal de que este corredor ya esta preparado.
            preparados.countDown();

            try {
                // Todos esperan aqui hasta que el juez de la salida.
                salida.await();

                int duracion = ThreadLocalRandom.current()
                        .nextInt(TIEMPO_MIN_MS, TIEMPO_MAX_MS + 1);

                System.out.println("Corredor " + id + " empieza a correr.");
                Thread.sleep(duracion);

                // Cada corredor obtiene un numero de llegada diferente.
                posicionLlegada = ordenGlobal.incrementAndGet();
                System.out.println("Corredor " + id + " llega en posicion "
                        + posicionLlegada + ".");

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        int getIdCorredor() {
            return id;
        }

        int getPosicionLlegada() {
            return posicionLlegada;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        List<Integer> participantes = new ArrayList<>();

        for (int i = 1; i <= PARTICIPANTES_INICIALES; i++) {
            participantes.add(i);
        }

        int ronda = 1;

        // Repetimos las carreras mientras quede mas de un participante.
        while (participantes.size() > 1) {
            System.out.println("\n========== RONDA " + ronda + " ==========");
            System.out.println("Participantes: " + participantes);

            CountDownLatch preparados = new CountDownLatch(participantes.size());
            CountDownLatch salida = new CountDownLatch(1);
            AtomicInteger ordenGlobal = new AtomicInteger();

            List<Corredor> corredores = new ArrayList<>();
            List<Thread> hilos = new ArrayList<>();

            // Creamos un corredor y un hilo por cada participante.
            for (int id : participantes) {
                Corredor corredor = new Corredor(
                        id, preparados, salida, ordenGlobal);
                corredores.add(corredor);

                Thread hilo = Thread.ofVirtual()
                        .name("corredor-" + id)
                        .start(corredor);
                hilos.add(hilo);
            }

            // No damos la salida hasta que todos esten preparados.
            preparados.await();
            System.out.println("El juez da la salida.");
            salida.countDown();

            // Esperamos a que terminen todos los corredores.
            for (Thread hilo : hilos) {
                hilo.join();
            }

            // El ultimo en llegar tiene el numero de posicion mas alto.
            Corredor ultimo = corredores.stream()
                    .max(Comparator.comparingInt(Corredor::getPosicionLlegada))
                    .orElseThrow();

            System.out.println("Eliminado: corredor " + ultimo.getIdCorredor());
            participantes.remove(Integer.valueOf(ultimo.getIdCorredor()));
            ronda++;
        }

        System.out.println("\n========== FIN DE LA COMPETICION ==========");
        System.out.println("Ganador: corredor " + participantes.get(0));
    }
}
