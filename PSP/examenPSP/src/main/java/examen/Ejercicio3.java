package examen;
/*
 PROBLEMA 3 - El Restaurante Concurrente de clase dado como solucion
 Patron Productor-Consumidor con 5 camareros (productores), 3 cocineros
 (consumidores) y 100 clientes (hilos virtuales).
 con los siguientes cambios
 1Pto El cierre del resturante deberá de ser más controlado, y no salir hasta parar a los cocineros y camareros.
 0,5 Pto  Tb pon precio a los platos y acumula cuanto ha ganado el restaurente en el servicio.
 2Pto Flujo de un cliente (hilo virtual):
   llega -> espera mesa libre durante un tiempo(elige el tiempo para que no se vayan todos) -> se sienta si la consigue y si no se va-> pide -> espera el plato
         -> se lo come -> libera la mesa -> se va
 2Pto Los camareros tienen PRIORIDAD para servir platos: antes de tomar un pedido nuevo
     o de meter uno en cocina, siempre miran si hay platos listos para entregarlos a los clientes.
 2Pto Cambia los clientes por un grupo de clientes.  Flujo de un grupo:
       la primera parte hasta que se sientan es igual que un cliente solo -> el camarero toma la comanda (un plato por persona)
             -> espera a que le sirvan TODOS los platos -> comen -> liberan la mesa -> se van

*/


//Se que es un caos y que la mayoria de cosas están mal, pero me pondré las pilas jurao

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

public class Ejercicio3 {

    enum TipoPlato {
        ENSALADA(2000, 8.00),
        PASTA(3000, 12.00),
        PIZZA(4000, 14.00),
        CARNE(5000, 20.00);

        private final int tiempoMs;
        private final double precio;

        TipoPlato(int tiempoMs, double precio) {
            this.tiempoMs = tiempoMs;
            this.precio = precio;
        }

        double precio() {
            return precio;
        }

        static examen.Ejercicio3.TipoPlato aleatorio(Random r) {
            return values()[r.nextInt(values().length)];
        }
    }

    record Pedido(int cliente, examen.Ejercicio3.TipoPlato plato, long horaCreacionMs) {
    }

    record Mesa(int numero) {
    }


    // --- Constantes del enunciado ---
    static final int COCINEROS = 3;
    static final int CAMAREROS = 5;
    static final int CLIENTES = 100;
    static final int NUM_MESAS = 15;
    static final int CAPACIDAD_MESA = 10;
    static final long LLEGADA_CLIENTE_MS = 500;
    static final long TIEMPO_CAMARERO_MS = 1000;

    // --- Recursos compartidos ---
    static final BlockingQueue<examen.Ejercicio3.Pedido> mesa = new ArrayBlockingQueue<>(CAPACIDAD_MESA);
    static final CountDownLatch clientesAtendidos = new CountDownLatch(CLIENTES);
    // Cola intermedia clientes -> camareros (ilimitada: los clientes nunca se quedan sin atender)
    static final BlockingQueue<Pedido> colaClientes = new ArrayBlockingQueue<>(CLIENTES);

    // --- Estadisticas ---
    static final AtomicInteger platosServidos = new AtomicInteger();
    static final AtomicLong esperaTotalMs = new AtomicLong();
    static final AtomicInteger mesaLlenaCount = new AtomicInteger();
    static final AtomicLong cocinerosEsperandoMs = new AtomicLong();

    static String ahora() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    static double escala = 1.0;

    static void pausa(long ms) {
        try {
            Thread.sleep(Math.max(1, Math.round(ms * escala)));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    static void log(String texto, Object... args) {
        String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.println("[" + hora + "] " + String.format(texto, args));
    }

    sealed interface TareaCamarero permits TomarPedido, ServirPlato {
        long seq();

        int prioridad();
    }

    record TomarPedido(Cliente cliente, long seq) implements TareaCamarero {
        public int prioridad() {
            return 1;
        }
    }

    record ServirPlato(Pedido pedido, long seq) implements TareaCamarero {
        public int prioridad() {
            return 0;
        }
    }

    static final class Estadisticas {
        final AtomicInteger clientesAtendidos = new AtomicInteger();
        final LongAdder nsEsperaSentarse = new LongAdder();
        final LongAdder nsEsperaPlato = new LongAdder();
        final DoubleAdder ganancias = new DoubleAdder();
    }

    static final class Restaurante {

        final int dia;
        final Estadisticas stats = new Estadisticas();

        final AtomicLong secuencia = new AtomicLong();

        final BlockingQueue<Cliente> colaEspera = new LinkedBlockingQueue<>();

        final BlockingQueue<Mesa> mesasLibres = new ArrayBlockingQueue<>(NUM_MESAS);

        final BlockingQueue<TareaCamarero> tareasCamarero = new PriorityBlockingQueue<>(
                64, Comparator.comparingInt(TareaCamarero::prioridad).thenComparingLong(TareaCamarero::seq));

        Restaurante(int dia) {
            this.dia = dia;
            for (int i = 1; i <= NUM_MESAS; i++) mesasLibres.add(new Mesa(i));
        }
    }

    static final class Cliente implements Runnable {
        final int id;
        final TipoPlato plato;
        final Restaurante r;

        final CompletableFuture<Mesa> sentado = new CompletableFuture<>();
        final CompletableFuture<Void> servido = new CompletableFuture<>();

        Cliente(int id, TipoPlato plato, Restaurante r) {
            this.id = id;
            this.plato = plato;
            this.r = r;
        }

        String nombre() {
            return String.format("Cliente-%03d", id);
        }

        public void run() {
            try {
                long llegadaNs = System.nanoTime();
                r.colaEspera.put(this);
                log("%s llega al restaurante", nombre());

                Mesa mesa = sentado.join();
                r.stats.nsEsperaSentarse.add(System.nanoTime() - llegadaNs);
                log("%s se sienta en Mesa-%d", nombre(), mesa.numero());

                log("%s pide %s", nombre(), plato);
                long pideNs = System.nanoTime();
                r.tareasCamarero.put(new TomarPedido(this, r.secuencia.incrementAndGet()));

                servido.join();
                r.stats.nsEsperaPlato.add(System.nanoTime() - pideNs);

                log("%s empieza a comer su %s", nombre(), plato);
                pausa(ThreadLocalRandom.current().nextInt(1000, 3001));   // come entre 1 y 3 segs
                r.stats.ganancias.add(plato.precio());                    // paga
                log("%s paga %.2f € y se va", nombre(), plato.precio());

                r.mesasLibres.put(mesa);
                r.stats.clientesAtendidos.incrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== RESTAURANTE CONCURRENTE ===");
        System.out.println("Iniciando servicio con " + COCINEROS + " cocineros y "
                + CAMAREROS + " camareros...\n");


        // --- Cocineros (consumidores) ---
        for (int i = 1; i <= COCINEROS; i++) {
            final int id = i;
            Thread.ofPlatform().name("cocinero-" + id).start(() -> {
                try {
                    while (true) {
                        long t0 = System.currentTimeMillis();
                        Pedido p = mesa.take(); // espera si no hay pedidos
                        cocinerosEsperandoMs.addAndGet(System.currentTimeMillis() - t0);

                        Thread.sleep(p.plato().tiempoMs); // cocina el plato
                        esperaTotalMs.addAndGet(System.currentTimeMillis() - p.horaCreacionMs());
                        platosServidos.incrementAndGet();
                        clientesAtendidos.countDown();

                        System.out.println("[" + ahora() + "] Cocinero-" + id + " termina "
                                + p.plato() + " para Cliente-" + p.cliente());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // cierre del restaurante
                }
            });
        }

        // --- Camareros (productores) ---
        for (int i = 1; i <= CAMAREROS; i++) {
            final int id = i;
            Thread.ofPlatform().name("camarero-" + id).start(() -> {
                try {
                    while (true) {
                        Pedido p = colaClientes.take(); // espera a que llegue un cliente
                        Thread.sleep(TIEMPO_CAMARERO_MS); // toma el pedido
                        System.out.println("[" + ahora() + "] Camarero-" + id
                                + " toma pedido " + p.plato());
                        if (mesa.remainingCapacity() == 0) {
                            mesaLlenaCount.incrementAndGet();
                        }
                        mesa.put(p); // espera si la mesa esta llena
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // --- Clientes (hilos virtuales, llegan cada 500 ms) ---
        Thread generadorClientes = Thread.ofPlatform().name("puerta").start(() -> {
            for (int i = 1; i <= CLIENTES; i++) {
                final int id = i;
                TipoPlato plato = TipoPlato.aleatorio(new Random());
                System.out.println("[" + ahora() + "] Cliente-" + String.format("%03d", id)
                        + " pide " + plato);
                try {
                    colaClientes.put(new Pedido(id, plato, System.currentTimeMillis()));
                    Thread.sleep(LLEGADA_CLIENTE_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        clientesAtendidos.await(); // el restaurante no cierra hasta atender a los 100
        generadorClientes.join();


        System.out.println("\n--- ESTADISTICAS FINALES ---");
        System.out.println("Clientes atendidos: " + CLIENTES + "/" + CLIENTES + " ✅");
        System.out.println("Platos servidos: " + platosServidos.get());
        System.out.println("Tiempo promedio de espera: "
                + (esperaTotalMs.get() / 1000.0 / CLIENTES) + "s");
        System.out.println("Mesa llena (veces): " + mesaLlenaCount.get());
        System.out.println("Cocineros esperando (tiempo acumulado): "
                + (cocinerosEsperandoMs.get() / 1000) + "s total");
        System.out.println("Eficiencia: el sistema soporta " + CAMAREROS + " pedidos/s en entrada"
                + " y " + COCINEROS + " platos en paralelo en cocina.");
        System.exit(0); // cerrar los hilos camareros/cocineros (bucle infinito por disenyo)
    }

}

