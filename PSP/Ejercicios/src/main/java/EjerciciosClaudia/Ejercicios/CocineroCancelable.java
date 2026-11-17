package EjerciciosClaudia.Ejercicios;

/*
 * EJERCICIO 10: COCINERO CANCELABLE
 *
 * Enunciado del material del profesor:
 *  - Un hilo simula la preparacion de un plato durante 10 segundos.
 *  - El hilo principal espera 3 segundos y cancela la preparacion.
 *  - El cocinero captura InterruptedException y termina de forma controlada.
 *
 * Conceptos: interrupt(), InterruptedException, sleep(), getState() y join().
 */
public class CocineroCancelable {

    public static void main(String[] args) throws InterruptedException {
        Thread cocinero = Thread.ofPlatform().name("cocinero").start(() -> {
            try {
                for (int segundo = 1; segundo <= 10; segundo++) {
                    System.out.println("Cocinando... " + segundo + "/10 segundos");
                    Thread.sleep(1000);
                }
                System.out.println("Plato terminado");
            } catch (InterruptedException e) {
                // sleep() lanza esta excepcion cuando el hilo es interrumpido.
                System.out.println("Coccion cancelada");
                Thread.currentThread().interrupt();
            }
        });

        System.out.println("Estado inicial: " + cocinero.getState());
        Thread.sleep(3000);
        System.out.println("Estado antes de cancelar: " + cocinero.getState());

        cocinero.interrupt(); // Solicita la cancelacion del hilo.
        cocinero.join();      // El main espera a que el cocinero termine.

        System.out.println("Estado final: " + cocinero.getState());
    }
}
