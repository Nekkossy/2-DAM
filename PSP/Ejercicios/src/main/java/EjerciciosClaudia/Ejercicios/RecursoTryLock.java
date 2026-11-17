package EjerciciosClaudia.Ejercicios;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/*
 * EJERCICIO 12: RECURSO COMPARTIDO CON TRYLOCK
 *
 * Enunciado basado en el ejercicio 5.3 de los apuntes:
 *  - Dos hilos quieren utilizar un recurso protegido por ReentrantLock.
 *  - El segundo espera como maximo 500 ms para conseguir el lock.
 *  - Si no lo consigue, muestra un mensaje en vez de quedarse bloqueado indefinidamente.
 *
 * Conceptos: ReentrantLock, tryLock(timeout), unlock() y finally.
 */
public class RecursoTryLock {

    static final ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        Thread primero = Thread.ofPlatform().name("hilo-1").start(() -> {
            lock.lock();
            try {
                System.out.println("Hilo 1 utiliza el recurso durante 2 segundos");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
                System.out.println("Hilo 1 libera el recurso");
            }
        });

        Thread.sleep(100); // Da tiempo al primer hilo para conseguir el lock.

        Thread segundo = Thread.ofPlatform().name("hilo-2").start(() -> {
            boolean conseguido = false;
            try {
                conseguido = lock.tryLock(500, TimeUnit.MILLISECONDS);
                if (conseguido) {
                    System.out.println("Hilo 2 consigue el recurso");
                } else {
                    System.out.println("Hilo 2: recurso ocupado, lo intentare mas tarde");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                if (conseguido) {
                    lock.unlock();
                }
            }
        });

        primero.join();
        segundo.join();
    }
}
