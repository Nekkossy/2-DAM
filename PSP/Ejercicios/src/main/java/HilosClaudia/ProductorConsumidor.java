package HilosClaudia;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ProductorConsumidor {

    static BlockingQueue<String> cola = new ArrayBlockingQueue<>(2);

    static void main(String[] args) throws InterruptedException {

        Thread productor = new Thread(() -> {

            try {
                for (int i = 1; i <= 5; i++) {
                    String pedido = "Pedido-" + i;
                    System.out.println("Productor crea " + pedido);
                    cola.put(pedido);
                    System.out.println("Productor ha dejado " + pedido);
                    Thread.sleep(1500);
                }
            } catch (InterruptedException e) {
                System.out.println("Productor interrumpido");
            }
        });

        Thread consumidor = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    String pedido = cola.take();
                    System.out.println("      Consumidor coge " + pedido);
                    Thread.sleep(200);
                    System.out.println("      Consumidor termina " + pedido);
                }
            } catch (InterruptedException e) {
                System.out.println("Consumidor interrumpido");
            }
        });

        productor.start();
        consumidor.start();
        productor.join();
        consumidor.join();

        System.out.println("Fin");
    }
}