package EjerciciosClaudia.Ejercicios;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;

/*
 * EJERCICIO 8: TRABAJO EN FASES
 *
 * Enunciado del material del profesor:
 *  - Cuatro trabajadores completan tres fases de un proyecto.
 *  - Nadie puede empezar la fase siguiente hasta que todos terminen la actual.
 *
 * Conceptos: CyclicBarrier, await(), hilos y join().
 */
public class TrabajoEnFases {

    public static void main(String[] args) throws InterruptedException {
        final int TRABAJADORES = 4;
        final int FASES = 3;

        // La barrera se libera cuando han llegado los cuatro trabajadores.
        // La accion se ejecuta una vez por cada fase, cuando llega el ultimo.
        CyclicBarrier barrera = new CyclicBarrier(TRABAJADORES,
                () -> System.out.println(">> Todos han terminado la fase. Pueden avanzar. <<"));

        List<Thread> equipo = new ArrayList<>();

        for (int i = 1; i <= TRABAJADORES; i++) {
            final int id = i;
            Thread trabajador = Thread.ofVirtual().name("trabajador-" + id).start(() -> {
                try {
                    for (int fase = 1; fase <= FASES; fase++) {
                        Thread.sleep(ThreadLocalRandom.current().nextInt(300, 1001));
                        System.out.println("Trabajador " + id + " termina fase " + fase);

                        // No puede continuar hasta que los cuatro hayan llegado aqui.
                        barrera.await();
                    }
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            });
            equipo.add(trabajador);
        }

        for (Thread trabajador : equipo) {
            trabajador.join();
        }

        System.out.println("Proyecto terminado por todo el equipo.");
    }
}
