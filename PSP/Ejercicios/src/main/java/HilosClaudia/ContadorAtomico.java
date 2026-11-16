package HilosClaudia;

import java.util.concurrent.locks.ReentrantLock;

public class ContadorAtomico {

    static int contador = 0;
    static ReentrantLock lock = new ReentrantLock();

    static void main(String[] args) throws InterruptedException {

        Thread[] hilos = new Thread[10];

        for (int i = 0; i < 10; i++) {
            hilos[i] = new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    lock.lock();
                    try {
                        contador++;
                        if (j == 5) throw new RuntimeException("fallo");
                    } finally {
                        lock.unlock();
                    }
                }
            });
        }

        for (int i = 0; i < 10; i++) {
            hilos[i].start();
        }

        for (int i = 0; i < 10; i++) {
            hilos[i].join();
        }

        System.out.println("Resultado: " + contador);
    }
}