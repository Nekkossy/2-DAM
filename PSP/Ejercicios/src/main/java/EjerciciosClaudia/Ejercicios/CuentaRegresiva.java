package EjerciciosClaudia.Ejercicios;

/*
 * EJERCICIO 11: CUENTA REGRESIVA DE COHETE
 *
 * Enunciado del material del profesor:
 *  - Un hilo cuenta de 10 a 0, esperando un segundo entre numeros.
 *  - El hilo principal debe esperar con join() antes de imprimir el despegue.
 *
 * Conceptos: Thread, sleep() y join().
 */
public class CuentaRegresiva {

    public static void main(String[] args) throws InterruptedException {
        Thread cuenta = Thread.ofPlatform().name("cuenta-regresiva").start(() -> {
            try {
                for (int numero = 10; numero >= 0; numero--) {
                    System.out.println(numero);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Sin join(), el mensaje de despegue podria aparecer antes de acabar la cuenta.
        cuenta.join();
        System.out.println("DESPEGUE!");
    }
}
