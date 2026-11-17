package Ejerciciosde0Claudia.src.main.java.Ejercicios;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * EJERCICIO 2: JUEGO DEL PANUELO
 *
 * Hay dos equipos. En cada ronda sale un jugador de cada equipo.
 * Los dos esperan la señal del arbitro y corren hacia el pañuelo.
 * Un Semaphore con un permiso hace que solo uno pueda conseguirlo.
 * El equipo ganador de la ronda suma un punto.
 */
public class JuegoPanuelo {

    static final int JUGADORES_POR_EQUIPO = 5;
    static final int RONDAS = 5;

    static class Jugador implements Runnable {
        private final String equipo;
        private final int numero;
        private final Semaphore panuelo;
        private final CountDownLatch preparados;
        private final CountDownLatch salida;
        private final AtomicBoolean conseguido;

        Jugador(String equipo, int numero, Semaphore panuelo,
                CountDownLatch preparados, CountDownLatch salida,
                AtomicBoolean conseguido) {
            this.equipo = equipo;
            this.numero = numero;
            this.panuelo = panuelo;
            this.preparados = preparados;
            this.salida = salida;
            this.conseguido = conseguido;
        }

        @Override
        public void run() {
            preparados.countDown();

            try {
                // Los dos jugadores esperan al mismo aviso de salida.
                salida.await();

                // Simulamos el tiempo que tarda cada jugador en llegar.
                Thread.sleep(ThreadLocalRandom.current().nextInt(300, 1201));

                // Solo el primero puede adquirir el unico permiso del panuelo.
                if (panuelo.tryAcquire()) {
                    conseguido.set(true);
                    System.out.println(equipo + " - jugador " + numero
                            + " consigue el pañuelo.");
                } else {
                    System.out.println(equipo + " - jugador " + numero
                            + " llega despues.");
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        String getEquipo() {
            return equipo;
        }

        boolean haConseguidoPanuelo() {
            return conseguido.get();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        AtomicInteger puntosRojo = new AtomicInteger();
        AtomicInteger puntosAzul = new AtomicInteger();

        for (int ronda = 1; ronda <= RONDAS; ronda++) {
            System.out.println("\n========== RONDA " + ronda + " ==========");

            // Un Semaphore(1) representa el unico pañuelo disponible.
            Semaphore panuelo = new Semaphore(1);
            CountDownLatch preparados = new CountDownLatch(2);
            CountDownLatch salida = new CountDownLatch(1);

            AtomicBoolean rojoLoConsigue = new AtomicBoolean(false);
            AtomicBoolean azulLoConsigue = new AtomicBoolean(false);

            Jugador rojo = new Jugador("Equipo rojo", ronda, panuelo,
                    preparados, salida, rojoLoConsigue);
            Jugador azul = new Jugador("Equipo azul", ronda, panuelo,
                    preparados, salida, azulLoConsigue);

            Thread hiloRojo = Thread.ofVirtual().name("rojo-" + ronda).start(rojo);
            Thread hiloAzul = Thread.ofVirtual().name("azul-" + ronda).start(azul);

            // Esperamos a que ambos jugadores esten preparados.
            preparados.await();
            System.out.println("El arbitro da la salida.");
            salida.countDown();

            hiloRojo.join();
            hiloAzul.join();

            if (rojo.haConseguidoPanuelo()) {
                puntosRojo.incrementAndGet();
            } else if (azul.haConseguidoPanuelo()) {
                puntosAzul.incrementAndGet();
            }
        }

        System.out.println("\n========== RESULTADO FINAL ==========");
        System.out.println("Equipo rojo: " + puntosRojo.get() + " puntos");
        System.out.println("Equipo azul: " + puntosAzul.get() + " puntos");

        if (puntosRojo.get() > puntosAzul.get()) {
            System.out.println("Gana el equipo rojo.");
        } else if (puntosAzul.get() > puntosRojo.get()) {
            System.out.println("Gana el equipo azul.");
        } else {
            System.out.println("La partida termina en empate.");
        }
    }
}
