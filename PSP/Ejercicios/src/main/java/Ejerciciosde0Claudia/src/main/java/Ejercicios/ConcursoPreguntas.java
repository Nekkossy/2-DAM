package Ejerciciosde0Claudia.src.main.java.Ejercicios;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * EJERCICIO 3: CONCURSO DE PREGUNTAS
 *
 * Varios concursantes compiten por pulsar primero el boton.
 * AtomicBoolean.compareAndSet permite que solo uno lo consiga.
 * Si responde correctamente suma un punto; si falla, queda eliminado.
 * Se realizan varias preguntas o se termina cuando queda un concursante.
 */
public class ConcursoPreguntas {

    static final int NUMERO_CONCURSANTES = 8;
    static final int NUMERO_PREGUNTAS = 12;
    static final int TIEMPO_REACCION_MIN_MS = 100;
    static final int TIEMPO_REACCION_MAX_MS = 900;

    static class Concursante implements Runnable {
        private final int id;
        private final AtomicBoolean botonPulsado;
        private final CountDownLatch preparados;
        private final CountDownLatch pregunta;
        private boolean haPulsado;
        private boolean respuestaCorrecta;

        Concursante(int id, AtomicBoolean botonPulsado,
                    CountDownLatch preparados, CountDownLatch pregunta) {
            this.id = id;
            this.botonPulsado = botonPulsado;
            this.preparados = preparados;
            this.pregunta = pregunta;
        }

        @Override
        public void run() {
            preparados.countDown();

            try {
                // Todos esperan a que el presentador formule la pregunta.
                pregunta.await();

                // Simulamos el tiempo de reaccion antes de pulsar.
                Thread.sleep(ThreadLocalRandom.current().nextInt(
                        TIEMPO_REACCION_MIN_MS, TIEMPO_REACCION_MAX_MS + 1));

                // compareAndSet solo cambia false a true una vez.
                if (botonPulsado.compareAndSet(false, true)) {
                    haPulsado = true;
                    respuestaCorrecta = ThreadLocalRandom.current().nextInt(100) < 70;

                    if (respuestaCorrecta) {
                        System.out.println("Concursante " + id
                                + " pulsa primero y acierta.");
                    } else {
                        System.out.println("Concursante " + id
                                + " pulsa primero, pero falla.");
                    }
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        int getIdConcursante() {
            return id;
        }

        boolean haPulsado() {
            return haPulsado;
        }

        boolean esCorrecta() {
            return respuestaCorrecta;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        List<Integer> participantes = new ArrayList<>();
        Map<Integer, Integer> puntos = new HashMap<>();

        for (int i = 1; i <= NUMERO_CONCURSANTES; i++) {
            participantes.add(i);
            puntos.put(i, 0);
        }

        int preguntaNumero = 1;

        while (participantes.size() > 1 && preguntaNumero <= NUMERO_PREGUNTAS) {
            System.out.println("\n========== PREGUNTA " + preguntaNumero + " ==========");
            System.out.println("Concursantes: " + participantes);

            AtomicBoolean botonPulsado = new AtomicBoolean(false);
            CountDownLatch preparados = new CountDownLatch(participantes.size());
            CountDownLatch pregunta = new CountDownLatch(1);

            List<Concursante> concursantes = new ArrayList<>();
            List<Thread> hilos = new ArrayList<>();

            // Cada concursante tiene un hilo que intenta pulsar el boton.
            for (int id : participantes) {
                Concursante concursante = new Concursante(
                        id, botonPulsado, preparados, pregunta);
                concursantes.add(concursante);

                Thread hilo = Thread.ofVirtual()
                        .name("concursante-" + id)
                        .start(concursante);
                hilos.add(hilo);
            }

            preparados.await();
            System.out.println("El presentador formula la pregunta.");
            pregunta.countDown();

            // Esperamos a que todos terminen su intento.
            for (Thread hilo : hilos) {
                hilo.join();
            }

            Concursante quienPulso = null;
            for (Concursante concursante : concursantes) {
                if (concursante.haPulsado()) {
                    quienPulso = concursante;
                    break;
                }
            }

            if (quienPulso == null) {
                System.out.println("Nadie ha pulsado el boton.");
            } else if (quienPulso.esCorrecta()) {
                // Sumamos un punto al concursante que ha respondido correctamente.
                int idGanadorPregunta = quienPulso.getIdConcursante();
                puntos.put(idGanadorPregunta, puntos.get(idGanadorPregunta) + 1);
                System.out.println("Concursante " + quienPulso.getIdConcursante()
                        + " suma un punto y continua.");
            } else {
                System.out.println("Concursante " + quienPulso.getIdConcursante()
                        + " queda eliminado.");
                participantes.remove(Integer.valueOf(quienPulso.getIdConcursante()));
                puntos.remove(quienPulso.getIdConcursante());
            }

            preguntaNumero++;
        }

        System.out.println("\n========== RESULTADO FINAL ==========");
        if (participantes.isEmpty()) {
            System.out.println("No queda ningun concursante.");
        } else {
            // Si se llega al maximo de preguntas, gana quien tenga mas puntos.
            int mejorPuntuacion = -1;
            List<Integer> ganadores = new ArrayList<>();

            for (int i = 0; i < participantes.size(); i++) {
                int puntuacionActual = puntos.get(participantes.get(i));
                if (puntuacionActual > mejorPuntuacion) {
                    mejorPuntuacion = puntuacionActual;
                    ganadores.clear();
                    ganadores.add(participantes.get(i));
                } else if (puntuacionActual == mejorPuntuacion) {
                    ganadores.add(participantes.get(i));
                }
            }

            System.out.println("Ganador(es): " + ganadores);
            System.out.println("Puntuacion maxima: " + mejorPuntuacion);
        }
    }
}
