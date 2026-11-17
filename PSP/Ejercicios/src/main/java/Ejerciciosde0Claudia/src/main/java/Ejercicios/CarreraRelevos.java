package Ejerciciosde0Claudia.src.main.java.Ejercicios;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * EJERCICIO 4: CARRERA DE RELEVOS
 *
 * Hay varios equipos con tres corredores cada uno.
 * Cada corredor es un hilo. El siguiente no puede correr hasta recibir
 * el testigo del corredor anterior. Una cola de capacidad uno representa
 * el testigo. El ultimo corredor registra la llegada de su equipo.
 */
public class CarreraRelevos {

    static final int NUMERO_EQUIPOS = 4;
    static final int CORREDORES_POR_EQUIPO = 3;
    static final int TIEMPO_MIN_TRAMO_MS = 300;
    static final int TIEMPO_MAX_TRAMO_MS = 1000;

    static class Equipo {
        private final int id;
        private final BlockingQueue<Integer> testigo = new ArrayBlockingQueue<>(1);
        private int posicionLlegada;

        Equipo(int id) {
            this.id = id;
            // El primer corredor recibe el testigo para iniciar el relevo.
            testigo.offer(1);
        }

        int getId() {
            return id;
        }

        int getPosicionLlegada() {
            return posicionLlegada;
        }

        void setPosicionLlegada(int posicionLlegada) {
            this.posicionLlegada = posicionLlegada;
        }
    }

    static class Corredor implements Runnable {
        private final Equipo equipo;
        private final int numero;
        private final CountDownLatch salida;
        private final AtomicInteger ordenLlegada;

        Corredor(Equipo equipo, int numero, CountDownLatch salida,
                 AtomicInteger ordenLlegada) {
            this.equipo = equipo;
            this.numero = numero;
            this.salida = salida;
            this.ordenLlegada = ordenLlegada;
        }

        @Override
        public void run() {
            try {
                // Todos los corredores esperan la salida general.
                salida.await();

                // Los corredores 2 y 3 se bloquean aqui hasta recibir el testigo.
                equipo.testigo.take();

                System.out.println("Equipo " + equipo.getId()
                        + ": corredor " + numero + " empieza su tramo.");

                Thread.sleep(ThreadLocalRandom.current().nextInt(
                        TIEMPO_MIN_TRAMO_MS, TIEMPO_MAX_TRAMO_MS + 1));

                if (numero < CORREDORES_POR_EQUIPO) {
                    // Entregamos el testigo al siguiente corredor del mismo equipo.
                    equipo.testigo.put(1);
                    System.out.println("Equipo " + equipo.getId()
                            + ": corredor " + numero + " entrega el testigo.");
                } else {
                    // El ultimo corredor cruza la meta y registra la posicion.
                    int posicion = ordenLlegada.incrementAndGet();
                    equipo.setPosicionLlegada(posicion);
                    System.out.println("Equipo " + equipo.getId()
                            + " termina en posicion " + posicion + ".");
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch salida = new CountDownLatch(1);
        AtomicInteger ordenLlegada = new AtomicInteger();

        List<Equipo> equipos = new ArrayList<>();
        List<Thread> hilos = new ArrayList<>();

        // Creamos los equipos y sus tres corredores.
        for (int idEquipo = 1; idEquipo <= NUMERO_EQUIPOS; idEquipo++) {
            Equipo equipo = new Equipo(idEquipo);
            equipos.add(equipo);

            for (int numeroCorredor = 1;
                 numeroCorredor <= CORREDORES_POR_EQUIPO;
                 numeroCorredor++) {

                Corredor corredor = new Corredor(
                        equipo, numeroCorredor, salida, ordenLlegada);

                Thread hilo = Thread.ofVirtual()
                        .name("equipo-" + idEquipo + "-corredor-" + numeroCorredor)
                        .start(corredor);
                hilos.add(hilo);
            }
        }

        System.out.println("Todos los equipos estan preparados.");
        System.out.println("El juez da la salida.");
        salida.countDown();

        // Esperamos a que terminen los doce corredores.
        for (Thread hilo : hilos) {
            hilo.join();
        }

        equipos.sort((a, b) -> Integer.compare(
                a.getPosicionLlegada(), b.getPosicionLlegada()));

        System.out.println("\n========== CLASIFICACION FINAL ==========");
        for (Equipo equipo : equipos) {
            System.out.println("Equipo " + equipo.getId()
                    + ": posicion " + equipo.getPosicionLlegada());
        }

        System.out.println("Ganador: equipo " + equipos.get(0).getId());
    }
}
