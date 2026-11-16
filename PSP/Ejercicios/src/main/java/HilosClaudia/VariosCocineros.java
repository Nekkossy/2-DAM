package HilosClaudia;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class VariosCocineros {

    static BlockingQueue<String> cola = new ArrayBlockingQueue<>(5);
    static CountDownLatch pedidosTerminados = new CountDownLatch(6);

    static void main(String[] args) throws InterruptedException {

        Thread productor = new Thread(() -> {
            try {
                for (int i = 1; i <= 6; i++) {
                    cola.put("Pedido-" + i);
                    System.out.println("Camarero deja Pedido-" + i);
                    Thread.sleep(300);
                }
            } catch (InterruptedException e) {
                System.out.println("Camarero interrumpido");
            }
        });

        Runnable cocinero = () -> {
            String nombre = Thread.currentThread().getName();
            try {
                while (true) {
                    String pedido = cola.take();
                    System.out.println("      " + nombre + " coge " + pedido);
                    Thread.sleep(1000);
                    System.out.println("      " + nombre + " termina " + pedido);
                    pedidosTerminados.countDown();
                }
            } catch (InterruptedException e) {
                System.out.println(nombre + " se va a casa");
            }
        };

        Thread cocinero1 = new Thread(cocinero, "Cocinero-1");
        Thread cocinero2 = new Thread(cocinero, "Cocinero-2");

        productor.start();
        cocinero1.start();
        cocinero2.start();

        productor.join();
        System.out.println("El camarero ha terminado");

        pedidosTerminados.await();
        cocinero1.interrupt();
        cocinero2.interrupt();
        cocinero1.join();
        cocinero2.join();

        System.out.println("Restaurante cerrado");
    }
}