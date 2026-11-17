package EjerciciosClaudia.Ejercicios;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * EJERCICIO 3: RESTAURANTE CONCURRENTE
 *
 * Enunciado resumido:
 *  - 100 clientes generan pedidos.
 *  - 5 camareros trasladan pedidos a una cola de cocina de capacidad 10.
 *  - 3 cocineros consumen la cola y preparan los platos.
 *  - Los clientes terminan cuando su pedido se ha cocinado.
 *
 * Conceptos: patron productor-consumidor, BlockingQueue, put(), take(),
 * CountDownLatch, AtomicInteger y cierre ordenado mediante pedidos especiales.
 */
public class RestauranteConcurrente {

    static final int NUM_CLIENTES = 100;
    static final int NUM_CAMAREROS = 5;
    static final int NUM_COCINEROS = 3;
    static final int CAPACIDAD_COCINA = 10;

    enum TipoPlato {
        ENSALADA(200), PASTA(300), PIZZA(400), CARNE(500);
        final int tiempoMs;
        TipoPlato(int tiempoMs) { this.tiempoMs = tiempoMs; }
    }

    static class Pedido {
        final int idCliente;
        final TipoPlato plato;
        final CountDownLatch terminado;
        final boolean fin;

        Pedido(int idCliente, TipoPlato plato) {
            this.idCliente = idCliente;
            this.plato = plato;
            this.terminado = new CountDownLatch(1);
            this.fin = false;
        }

        // Pedido especial para indicar que un trabajador debe terminar.
        Pedido() {
            this.idCliente = -1;
            this.plato = null;
            this.terminado = null;
            this.fin = true;
        }
    }

    // Los clientes producen pedidos para los camareros.
    static final BlockingQueue<Pedido> colaClientes = new ArrayBlockingQueue<>(NUM_CLIENTES);

    // Los camareros producen pedidos para los cocineros. La capacidad es limitada.
    static final BlockingQueue<Pedido> colaCocina = new ArrayBlockingQueue<>(CAPACIDAD_COCINA);

    static final AtomicInteger platosPreparados = new AtomicInteger();

    public static void main(String[] args) throws InterruptedException {
        List<Thread> cocineros = new ArrayList<>();
        List<Thread> camareros = new ArrayList<>();
        List<Thread> clientes = new ArrayList<>();

        CountDownLatch pedidosEnviados = new CountDownLatch(NUM_CLIENTES);

        // 1. Iniciar consumidores de la cola de cocina.
        for (int i = 1; i <= NUM_COCINEROS; i++) {
            final int id = i;
            Thread cocinero = Thread.ofPlatform().name("cocinero-" + id).start(() -> {
                try {
                    while (true) {
                        Pedido pedido = colaCocina.take(); // Espera si la cola esta vacia.
                        if (pedido.fin) {
                            break;
                        }

                        Thread.sleep(pedido.plato.tiempoMs);
                        platosPreparados.incrementAndGet();
                        System.out.println("Cocinero-" + id + " prepara " + pedido.plato
                                + " para cliente " + pedido.idCliente);
                        pedido.terminado.countDown();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            cocineros.add(cocinero);
        }

        // 2. Iniciar productores/intermediarios: los camareros.
        for (int i = 1; i <= NUM_CAMAREROS; i++) {
            final int id = i;
            Thread camarero = Thread.ofPlatform().name("camarero-" + id).start(() -> {
                try {
                    while (true) {
                        Pedido pedido = colaClientes.take(); // Espera si no hay pedidos de clientes.
                        if (pedido.fin) {
                            break;
                        }

                        Thread.sleep(30); // Tiempo que tarda el camarero en tomar el pedido.
                        colaCocina.put(pedido); // Espera si la cocina esta llena.
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            camareros.add(camarero);
        }

        // 3. Crear los clientes como hilos virtuales.
        for (int i = 1; i <= NUM_CLIENTES; i++) {
            final int id = i;
            Thread cliente = Thread.ofVirtual().name("cliente-" + id).start(() -> {
                TipoPlato[] platos = TipoPlato.values();
                TipoPlato elegido = platos[ThreadLocalRandom.current().nextInt(platos.length)];
                Pedido pedido = new Pedido(id, elegido);

                try {
                    colaClientes.put(pedido);
                    pedidosEnviados.countDown();
                    pedido.terminado.await(); // El cliente espera a que su plato este preparado.
                    System.out.println("Cliente-" + id + " recibe su " + elegido);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            clientes.add(cliente);
        }

        // Todos los pedidos normales ya estan en la cola antes de enviar los cierres.
        pedidosEnviados.await();

        // 4. Cerrar primero camareros: los pedidos anteriores se procesan por orden FIFO.
        for (int i = 0; i < NUM_CAMAREROS; i++) {
            colaClientes.put(new Pedido());
        }
        for (Thread camarero : camareros) {
            camarero.join();
        }

        // 5. Cuando todos los camareros han terminado, ya no entraran mas pedidos en cocina.
        for (int i = 0; i < NUM_COCINEROS; i++) {
            colaCocina.put(new Pedido());
        }
        for (Thread cocinero : cocineros) {
            cocinero.join();
        }

        // 6. Esperar tambien a los clientes, que ya habran recibido sus pedidos.
        for (Thread cliente : clientes) {
            cliente.join();
        }

        System.out.println("\nRestaurante cerrado. Platos preparados: " + platosPreparados.get());
    }
}
