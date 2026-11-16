//Tenemos 10 jugadores y queremos simular el juego de las sillas.
//
//        Las reglas son:
//        1. Todos los jugadores empiezan participando.
//        2. Empieza a sonar la música.
//        3. Mientras suena, todos los jugadores bailan.
//        4. Cuando se detiene, todos intentan sentarse.
//        5. Hay una silla menos que jugadores.
//        6. El que no consigue silla queda eliminado.
//        7. Se elimina una silla.
//        8. Empieza otra ronda.
//        9. Cuando queda un único jugador, gana.


package HilosClaudia;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class JuegoSillas {

    static final int NUMERO_JUGADORES = 10;
    static final int DURACION_MUSICA_MS = 3000;

    static class Jugador implements Runnable {
        private final int id;
        private final BlockingQueue<Integer> colaSillas;
        private final AtomicBoolean musicaSonando;
        private final CountDownLatch listos;
        private final CountDownLatch inicio;
        private boolean sentado;

        Jugador(int id, BlockingQueue<Integer> colaSillas,
                AtomicBoolean musicaSonando, CountDownLatch listos,
                CountDownLatch inicio) {
            this.id = id;
            this.colaSillas = colaSillas;
            this.musicaSonando = musicaSonando;
            this.listos = listos;
            this.inicio = inicio;
        }

        @Override
        public void run() {
            listos.countDown();

            try {
                inicio.await();
                System.out.println("Jugador " + id + " está bailando");

                while (musicaSonando.get()) {
                    Thread.sleep(100);
                }

                sentado = colaSillas.offer(id);

                if (sentado) {
                    System.out.println("Jugador " + id + " consigue sentarse");
                } else {
                    System.out.println("Jugador " + id + " se queda sin silla");
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        boolean estaSentado() {
            return sentado;
        }

        int getId() {
            return id;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        List<Integer> jugadores = new ArrayList<>();

        for (int i = 1; i <= NUMERO_JUGADORES; i++) {
            jugadores.add(i);
        }

        int ronda = 1;

        while (jugadores.size() > 1) {
            System.out.println("\n========== RONDA " + ronda + " ==========");

            int numeroSillas = jugadores.size() - 1;
            System.out.println("Jugadores: " + jugadores.size());
            System.out.println("Sillas: " + numeroSillas);

            BlockingQueue<Integer> colaSillas =
                    new ArrayBlockingQueue<>(numeroSillas);

            AtomicBoolean musicaSonando = new AtomicBoolean(true);

            CountDownLatch listos =
                    new CountDownLatch(jugadores.size());

            CountDownLatch inicio = new CountDownLatch(1);

            List<Thread> hilos = new ArrayList<>();
            List<Jugador> jugadoresRonda = new ArrayList<>();

            for (int id : jugadores) {
                Jugador jugador = new Jugador(id, colaSillas,
                        musicaSonando, listos, inicio);

                jugadoresRonda.add(jugador);

                Thread hilo = Thread.ofVirtual()
                        .name("jugador-" + id)
                        .start(jugador);

                hilos.add(hilo);
            }

            listos.await();

            System.out.println("La música comienza");
            inicio.countDown();

            Thread hiloMusica = Thread.ofVirtual().name("musica").start(() -> {
                try {
                    Thread.sleep(DURACION_MUSICA_MS);
                    musicaSonando.set(false);
                    System.out.println("La música se detiene");
                } catch (InterruptedException e) {
                    musicaSonando.set(false);
                    Thread.currentThread().interrupt();
                }
            });

            hiloMusica.join();

            for (Thread hilo : hilos) {
                hilo.join();
            }

            Jugador eliminado = null;
            List<Integer> siguientesJugadores = new ArrayList<>();

            for (Jugador jugador : jugadoresRonda) {
                if (jugador.estaSentado()) {
                    siguientesJugadores.add(jugador.getId());
                } else {
                    eliminado = jugador;
                }
            }

            if (eliminado != null) {
                System.out.println("Jugador eliminado: " + eliminado.getId());
            }

            jugadores = siguientesJugadores;
            ronda++;
        }

        System.out.println("\n========== FIN DEL JUEGO ==========");
        System.out.println("¡El ganador es el jugador " + jugadores.get(0) + "!");
    }
}