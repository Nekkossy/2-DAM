package main.java.Problemas;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

public class Problema3 {

    final int NUM_COCINEROS = 3;
    final int NUM_CAMAREROS = 5;
    final int NUM_CLIENTES = 100;
    final int NUM_MESAS = 15;
    final int CAPACIDAD_MESA_PEDIDOS = 10;
    final int INTERVALO_LLEGADA_MS = 500;
    final int TOMA_PEDIDO_MS = 1000;

    // Multiplica todas las esperas. 1.0 = tiempo real, 0.1 = 10 veces más rápido
    double escala = 1.0;


    void pausa(long ms) {
        try {
            Thread.sleep(Math.max(1, Math.round(ms * escala)));
        } catch (InterruptedException e) {
            // Dejamos puesta la señal de "parar" para que el siguiente take()/put() la vea.
            Thread.currentThread().interrupt();
        }
    }

    // Escribe un mensaje con la hora: [14:32:15] Camarero-2 toma pedido PIZZA
    void log(String texto, Object... args) {
        String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.println("[" + hora + "] " + String.format(texto, args));
    }

    // Convierte nanosegundos a segundos
    double seg(long ns) {
        return ns / 1_000_000_000.0;
    }


    enum TipoPlato {
        ENSALADA(2000, 8.00),   // 2 segs
        PASTA(3000, 12.00),     // 3 segs
        PIZZA(4000, 14.00),     // 4 segs
        CARNE(5000, 20.00);     // 5 segs

        private final int tiempoMs;
        private final double precio;

        TipoPlato(int tiempoMs, double precio) {
            this.tiempoMs = tiempoMs;
            this.precio = precio;
        }

        int tiempoMs() {
            return tiempoMs;
        }

        double precio() {
            return precio;
        }
    }

    record Mesa(int numero) {
    }

    record Pedido(Cliente cliente, TipoPlato tipo, String camarero) {
    }

    // Lo que guardamos de cada día para el resumen final
    record ResultadoDia(int dia, int atendidos, double ganancias, double esperaMediaS) {
    }

    //Los dos trabajos de un camarero
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

    // Contadores seguros para muchos hilos a la vez
    final class Estadisticas {
        final AtomicInteger clientesAtendidos = new AtomicInteger();
        final AtomicInteger platosServidos = new AtomicInteger();

        final LongAdder mesaLlenaVeces = new LongAdder();

        // Tiempos acumulados en nanosegundos
        final LongAdder nsEsperaSentarse = new LongAdder();
        final LongAdder nsEsperaPlato = new LongAdder();
        final LongAdder nsCocinerosEsperando = new LongAdder();
        final LongAdder nsCocinerosCocinando = new LongAdder();

        final DoubleAdder ganancias = new DoubleAdder();
    }

    final class Restaurante {

        final int dia;
        final Estadisticas stats = new Estadisticas();

        // Contador (seq)
        final AtomicLong secuencia = new AtomicLong();

        // Fila de clientes esperando mesa (sin límite)
        final BlockingQueue<Cliente> colaEspera = new LinkedBlockingQueue<>();

        // Mesas libres (máximo 15)
        final BlockingQueue<Mesa> mesasLibres = new ArrayBlockingQueue<>(NUM_MESAS);

        // Mostrador camareros → cocineros (máximo 10)
        final BlockingQueue<Pedido> mesaPedidos = new ArrayBlockingQueue<>(CAPACIDAD_MESA_PEDIDOS, true);

        // Trabajo de los camareros, ordenado por prioridad y luego por seq
        final BlockingQueue<TareaCamarero> tareasCamarero = new PriorityBlockingQueue<>(
                64, Comparator.comparingInt(TareaCamarero::prioridad).thenComparingLong(TareaCamarero::seq));

        Restaurante(int dia) {
            this.dia = dia;
            for (int i = 1; i <= NUM_MESAS; i++) mesasLibres.add(new Mesa(i));
        }

        // El cocinero: consume pedidos
        void cocinero() {
            String yo = Thread.currentThread().getName();
            try {
                while (true) {
                    long esperaIni = System.nanoTime();
                    Pedido p = mesaPedidos.take();
                    stats.nsCocinerosEsperando.add(System.nanoTime() - esperaIni);

                    log("%s empieza %s para %s", yo, p.tipo(), p.cliente().nombre());
                    long cocinaIni = System.nanoTime();
                    pausa(p.tipo().tiempoMs());
                    stats.nsCocinerosCocinando.add(System.nanoTime() - cocinaIni);
                    log("%s termina %s para %s", yo, p.tipo(), p.cliente().nombre());

                    tareasCamarero.put(new ServirPlato(p, secuencia.incrementAndGet()));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        //Camarero: produce pedidos
        void camarero() {
            try {
                while (true) {
                    TareaCamarero tarea = tareasCamarero.take();
                    switch (tarea) {
                        case TomarPedido t -> tomarPedido(t.cliente());
                        case ServirPlato s -> servirPlato(s.pedido());
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        void tomarPedido(Cliente c) {
            String yo = Thread.currentThread().getName();
            log("%s toma pedido %s de %s", yo, c.plato, c.nombre());
            pausa(TOMA_PEDIDO_MS);

            Pedido pedido = new Pedido(c, c.plato, yo);

            // offer() no espera: si devuelve false, el mostrador está lleno
            if (!mesaPedidos.offer(pedido)) {
                stats.mesaLlenaVeces.increment();
                log("Mesa de pedidos LLENA: %s espera para dejar %s", yo, c.plato);
                try {
                    mesaPedidos.put(pedido);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            log("%s deja pedido %s en el mostrador (%d/%d)", yo, c.plato,
                    mesaPedidos.size(), CAPACIDAD_MESA_PEDIDOS);
        }

        void servirPlato(Pedido p) {
            Cliente c = p.cliente();
            log("%s sirve %s a %s", Thread.currentThread().getName(), p.tipo(), c.nombre());
            stats.platosServidos.incrementAndGet();
            c.servido.complete(null);
        }

        void anfitrion() {
            try {
                while (true) {
                    Cliente c = colaEspera.take();
                    Mesa m = mesasLibres.take();
                    c.sentado.complete(m);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Estadísticas en vivo (se imprime cada 5 segundos)
        void estadoEnVivo() {
            log("ESTADO | Cola: %d | Mesas: %d/%d | Pedidos en mostrador: %d/%d | Atendidos: %d/%d | Caja: %.2f €",
                    colaEspera.size(),
                    NUM_MESAS - mesasLibres.size(), NUM_MESAS,
                    mesaPedidos.size(), CAPACIDAD_MESA_PEDIDOS,
                    stats.clientesAtendidos.get(), NUM_CLIENTES,
                    stats.ganancias.sum());
        }

        // Un día completo de servicio
        ResultadoDia abrir() {
            System.out.println("\n================ DÍA " + dia + " ================");

            // 1. Arrancar al personal (hilos normales). Se guardan para poder despedirlos al final
            List<Thread> personal = new ArrayList<>();
            personal.add(Thread.ofPlatform().name("Anfitrion").start(this::anfitrion));
            for (int i = 1; i <= NUM_COCINEROS; i++)
                personal.add(Thread.ofPlatform().name("Cocinero-" + i).start(this::cocinero));
            for (int i = 1; i <= NUM_CAMAREROS; i++)
                personal.add(Thread.ofPlatform().name("Camarero-" + i).start(this::camarero));

            // 2. Reloj que imprime el estado cada 5 segundos
            ScheduledExecutorService monitor = Executors.newSingleThreadScheduledExecutor();
            monitor.scheduleAtFixedRate(this::estadoEnVivo, 5, 5, TimeUnit.SECONDS);

            long inicio = System.nanoTime();

            // 3. Clientes: un hilo virtual cada uno, llega uno cada 500 ms.
            //    Al cerrarse este bloque try se espera a que se vayan los 100
            try (ExecutorService clientes = Executors.newVirtualThreadPerTaskExecutor()) {
                for (int i = 1; i <= NUM_CLIENTES; i++) {
                    TipoPlato plato = TipoPlato.values()[ThreadLocalRandom.current().nextInt(TipoPlato.values().length)];
                    clientes.submit(new Cliente(i, plato, this));
                    if (i < NUM_CLIENTES) pausa(INTERVALO_LLEGADA_MS);
                }
            }

            long duracionNs = System.nanoTime() - inicio;

            // 4. Cerrar: parar el reloj y despedir al personal
            monitor.shutdownNow();
            for (Thread t : personal) t.interrupt();
            for (Thread t : personal) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            return imprimirFinal(duracionNs);
        }

        // Resumen final del día
        ResultadoDia imprimirFinal(long duracionNs) {
            int atendidos = stats.clientesAtendidos.get();

            double esperaSentarse = atendidos == 0 ? 0 : seg(stats.nsEsperaSentarse.sum()) / atendidos;
            double esperaPlato = atendidos == 0 ? 0 : seg(stats.nsEsperaPlato.sum()) / atendidos;
            double esperaTotal = esperaSentarse + esperaPlato;

            // Eficiencia = tiempo cocinando / (nº cocineros x duración del día)
            double eficiencia = 100.0 * stats.nsCocinerosCocinando.sum()
                    / ((double) NUM_COCINEROS * duracionNs);

            System.out.println();
            System.out.println("--- ESTADÍSTICAS FINALES (Día " + dia + ") ---");
            System.out.println("Clientes atendidos: " + atendidos + "/" + NUM_CLIENTES
                    + (atendidos == NUM_CLIENTES ? " ✅" : " ❌"));
            System.out.println("Platos servidos: " + stats.platosServidos.get());
            System.out.printf("Tiempo promedio de espera: %.1fs (sentarse: %.1fs + plato: %.1fs)%n",
                    esperaTotal, esperaSentarse, esperaPlato);
            System.out.println("Mesa llena (veces): " + stats.mesaLlenaVeces.sum());
            System.out.printf("Cocineros esperando (tiempo): %.0fs total%n", seg(stats.nsCocinerosEsperando.sum()));
            System.out.printf("Eficiencia: %.0f%%%n", eficiencia);
            System.out.printf("GANANCIAS DEL DÍA %d: %.2f €%n", dia, stats.ganancias.sum());

            return new ResultadoDia(dia, atendidos, stats.ganancias.sum(), esperaTotal);
        }
    }

    //CLIENTE (hilo virtual)
    final class Cliente implements Runnable {
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

    // MAIN
    void main(String[] args) {
        // Primer argumento: número de días. Segundo: escala de tiempo
        int dias = args.length > 0 ? Integer.parseInt(args[0]) : 1;
        if (args.length > 1) escala = Double.parseDouble(args[1]);

        System.out.println("=== RESTAURANTE CONCURRENTE ===");
        System.out.printf("Iniciando servicio con %d cocineros y %d camareros...%n",
                NUM_COCINEROS, NUM_CAMAREROS);

        // Un restaurante nuevo por cada día
        List<ResultadoDia> resultados = new ArrayList<>();
        for (int d = 1; d <= dias; d++) {
            resultados.add(new Restaurante(d).abrir());
        }

        // Resumen de ganancias por día
        System.out.println("\n=== RESUMEN DE GANANCIAS POR DÍA ===");
        double total = 0;
        for (ResultadoDia r : resultados) {
            System.out.printf("Día %d: %.2f €  (clientes: %d, espera media: %.1fs)%n",
                    r.dia(), r.ganancias(), r.atendidos(), r.esperaMediaS());
            total += r.ganancias();
        }
        System.out.printf("TOTAL: %.2f €%n", total);
    }
}