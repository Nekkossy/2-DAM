package HilosClaudia;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CamareroConPrioridad {

    static BlockingQueue<String> platosListos = new ArrayBlockingQueue<>(10);
    static BlockingQueue<String> pedidosNuevos = new ArrayBlockingQueue<>(10);
    static CountDownLatch acciones = new CountDownLatch(4);   // 3 pedidos + 1 entrega

    static void main(String[] args) throws InterruptedException {
        pedidosNuevos.put("pedido-1");
        pedidosNuevos.put("pedido-2");
        pedidosNuevos.put("pedido-3");

        Thread camarero = new Thread(() -> {
            try {
                while (true) {
                    String listo = platosListos.poll();
                    if (listo != null) {
                        System.out.println("Camarero ENTREGA " + listo);
                        acciones.countDown();
                        continue;
                    }

                    String nuevo = pedidosNuevos.poll(100, TimeUnit.MILLISECONDS);
                    if (nuevo != null) {
                        System.out.println("Camarero toma " + nuevo);
                        Thread.sleep(1000);
                        acciones.countDown();
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("Camarero se va a casa");
            }
        }, "camarero");

        Thread cocinero = new Thread(() -> {
            try {
                Thread.sleep(1500);
                platosListos.put("plato-listo");
            } catch (InterruptedException e) {
                System.out.println("Cocinero interrumpido");
            }
        }, "cocinero");

        camarero.start();
        cocinero.start();

        acciones.await();         // el main espera a que el camarero haga las 4 cosas
        camarero.interrupt();     // ahora le aviso de que se vaya
        camarero.join();          // espero a que se vaya de verdad
        System.out.println("Fin");
    }
}