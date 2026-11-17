package EjerciciosClaudia.Ejercicios;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * EJERCICIO 6: JUEGO DE LAS SILLAS (PROPUESTA BASADA EN LA PISTA DEL EXAMEN)
 *
 * Enunciado reconstruido, NO confirmado en los apuntes:
 *  - Empiezan 10 jugadores y se crean 9 sillas.
 *  - Cada jugador es un hilo y espera mientras suena la musica.
 *  - Al detenerse la musica, cada jugador intenta sentarse.
 *  - La cola representa las sillas: solo acepta 9 jugadores.
 *  - El jugador cuyo offer() devuelve false queda eliminado.
 *  - En cada ronda se quita una silla hasta que queda un ganador.
 *
 * Conceptos: Runnable, hilos virtuales, BlockingQueue, offer(), AtomicBoolean,
 * CountDownLatch, sleep y join.
 * Esta es una simulacion sencilla; el orden de llegada a la cola es no determinista.
 */
public class JuegoSillas {

    static final int NUMERO_JUGADORES = 10;
    static final int DURACION_MUSICA_MS = 1500;

    static class Jugador implements Runnable {
        private final int id;
        private final BlockingQueue<Integer> sillas;
        private final AtomicBoolean musicaSonando;
        private final CountDownLatch todosListos;
        private final CountDownLatch senalInicio;
        private boolean sentado;

        Jugador(int id, BlockingQueue<Integer> sillas, AtomicBoolean musicaSonando,
                CountDownLatch todosListos, CountDownLatch senalInicio) {
            this.id = id;
            this.sillas = sillas;
            this.musicaSonando = musicaSonando;
            this.todosListos = todosListos;
            this.senalInicio = senalInicio;
        }

        public void run() {
            // Avisamos de que este jugador ya esta preparado.
            todosListos.countDown();

            try {
                // Ningun jugador empieza hasta que el hilo principal de la salida.
                senalInicio.await();
                System.out.println("Jugador " + id + " esta bailando");

                // Mientras la musica siga activa, el jugador continua bailando.
                while (musicaSonando.get()) {
                    Thread.sleep(100);
                }

                // offer no bloquea: false significa que todas las sillas estan ocupadas.
                sentado = sillas.offer(id);
                if (sentado) {
                    System.out.println("Jugador " + id + " consigue silla");
                } else {
                    System.out.println("Jugador " + id + " queda eliminado");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        boolean estaSentado() { return sentado; }
        int getId() { return id; }
    }

    public static void main(String[] args) throws InterruptedException {
        List<Integer> jugadores = new ArrayList<>();
        for (int i = 1; i <= NUMERO_JUGADORES; i++) {
            jugadores.add(i);
        }

        int ronda = 1;
        while (jugadores.size() > 1) {
            System.out.println("\n===== RONDA " + ronda + " =====");

            // Siempre hay una silla menos que jugadores para que haya un eliminado.
            int numeroSillas = jugadores.size() - 1;
            BlockingQueue<Integer> sillas = new ArrayBlockingQueue<>(numeroSillas);
            AtomicBoolean musicaSonando = new AtomicBoolean(true);
            CountDownLatch todosListos = new CountDownLatch(jugadores.size());
            CountDownLatch senalInicio = new CountDownLatch(1);

            List<Thread> hilos = new ArrayList<>();
            List<Jugador> participantes = new ArrayList<>();

            // Crear un hilo virtual por jugador.
            for (int id : jugadores) {
                Jugador jugador = new Jugador(id, sillas, musicaSonando,
                        todosListos, senalInicio);
                participantes.add(jugador);
                hilos.add(Thread.ofVirtual().name("jugador-" + id).start(jugador));
            }

            // El hilo principal espera a que todos los jugadores esten preparados.
            todosListos.await();
            System.out.println("Empieza la musica");
            senalInicio.countDown();

            // Otro hilo controla la duracion de la musica.
            Thread musica = Thread.ofVirtual().name("musica").start(() -> {
                try {
                    Thread.sleep(DURACION_MUSICA_MS);
                    musicaSonando.set(false);
                    System.out.println("La musica se detiene");
                } catch (InterruptedException e) {
                    musicaSonando.set(false);
                    Thread.currentThread().interrupt();
                }
            });

            musica.join();
            for (Thread hilo : hilos) {
                hilo.join();
            }

            // Solo los jugadores sentados pasan a la siguiente ronda.
            List<Integer> siguen = new ArrayList<>();
            for (Jugador jugador : participantes) {
                if (jugador.estaSentado()) {
                    siguen.add(jugador.getId());
                }
            }

            for (int id : jugadores) {
                if (!siguen.contains(id)) {
                    System.out.println("Eliminado: jugador " + id);
                }
            }

            jugadores = siguen;
            ronda++;
        }

        System.out.println("\nGANADOR: jugador " + jugadores.get(0));
    }
}
