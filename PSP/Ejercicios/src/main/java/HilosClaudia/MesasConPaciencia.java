package HilosClaudia;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class MesasConPaciencia {

    static Semaphore mesas = new Semaphore(2);

    static void main(String[] args) throws InterruptedException {
        Thread[] clientes = new Thread[5];

        for (int i = 0; i < 5; i++) {
            final int id = i + 1;
            clientes[i] = Thread.ofVirtual().name("cliente-" + id).start(() -> {
                String nombre = Thread.currentThread().getName();
                try {
                    if (mesas.tryAcquire(1500, TimeUnit.MILLISECONDS)) {
                        try {
                            System.out.println(nombre + " se sienta");
                            Thread.sleep(1000);
                            System.out.println(nombre + " termina y se va");
                        } finally {
                            mesas.release();
                        }
                    } else {
                        System.out.println(nombre + " se cansa de esperar y se va");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        for (Thread c : clientes) {
            c.join();
        }
        System.out.println("Fin");
    }
}