package HilosClaudia;
/*
 PROBLEMA 4 - El Parking Inteligente de clase dado como solucion
 20 plazas normales + 5 VIP (Semaphore), cola de espera maxima de 10 coches
 (ArrayBlockingQueue), barrera de entrada (1 coche cada 2 s) y de salida (1 coche cada 1 s),
 200 coches (hilos virtuales), dashboard periodico (ScheduledExecutorService)
 y estadisticas finales.

 con los siguientes cambios

 1 Pto   El cierre del parking debera de ser mas controlado: quita el System.exit(0).
         El programa no termina hasta haber parado el dashboard (shutdown + awaitTermination)
         y haber salido el ultimo coche.

 0,5 Pto Acumula por separado lo recaudado con los coches VIP y con los NORMALES
         y muestralo al final junto al total.

 2 Pto   Flujo de un coche (hilo virtual):
         llega -> espera plaza durante un tiempo maximo (elige el tiempo para que no se vayan todos)
         -> si la consigue, pasa la barrera de entrada y aparca; si no, se va
         -> estancia -> barrera de salida -> libera la plaza -> se va.
         Elimina la cola de espera con sondeo (dormir(500)) y usa la espera con tiempo del semaforo.
         (Simplificacion: un VIP prueba primero una plaza VIP sin esperar; si no hay,
          espera una plaza normal con paciencia.)

 2 Pto   La barrera tiene PRIORIDAD para dejar salir: sustituye las dos barreras (synchronized + sleep)
         por un unico hilo "barrera" que procesa 1 coche por segundo. Cada coche, para entrar o salir,
         deja una solicitud y espera a que la barrera la atienda. Antes de atender una entrada,
         la barrera mira SIEMPRE si hay salidas pendientes y las atiende primero.

 2 Pto   Cambia los coches por caravanas de 1 a 3 coches NORMALES que necesitan sus plazas juntas.
         La primera parte hasta que aparcan es igual que un coche solo: o consiguen
         TODAS las plazas a la vez o se van. Pasan la barrera, estan su estancia, salen y devuelven todas las plazas.

 NOTA para probar rapido: reduce COCHES, BARRERA_ENTRADA_MS y BARRERA_SALIDA_MS.
*/
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ExamenParking {

    enum TipoVehiculo {
        NORMAL(1.0), VIP(2.0); // tarifa €/minuto
        final double tarifaPorMinuto;
        TipoVehiculo(double t) { this.tarifaPorMinuto = t; }
    }

    // --- Infraestructura ---
    static final int PLAZAS_NORMALES = 20;
    static final int PLAZAS_VIP = 5;
    static final int MAX_COLA = 10;
    static final int COCHES = 200;
    static final long BARRERA_ENTRADA_MS = 2000; // 1 coche cada 2 s
    static final long BARRERA_SALIDA_MS = 1000;  // 1 coche cada 1 s
    static final int ESTANCIA_MIN_S = 10;
    static final int ESTANCIA_MAX_S = 30;

    static final Semaphore plazasNormales = new Semaphore(PLAZAS_NORMALES);
    static final Semaphore plazasVip = new Semaphore(PLAZAS_VIP);

    // Cola de espera fuera del parking (los coches que no encuentran plaza)
    static final BlockingQueue<Object> colaEspera = new ArrayBlockingQueue<>(MAX_COLA);

    // --- Barreras: procesan UN coche cada X ms (cerrojo compartido + sleep) ---
    static final Object barreraEntrada = new Object();
    static final Object barreraSalida = new Object();

    // --- Estadisticas ---
    static final AtomicInteger procesados = new AtomicInteger();
    static final AtomicInteger atendidos = new AtomicInteger();
    static final AtomicInteger rechazados = new AtomicInteger();
    static final AtomicLong estanciaTotalMs = new AtomicLong();
    static final AtomicInteger ocupacionMaxima = new AtomicInteger();
    static final AtomicLong ingresos = new AtomicLong(); // centimos
    static final CountDownLatch finJornada = new CountDownLatch(COCHES);

    static String ahora() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    static int ocupacionActual() {
        return (PLAZAS_NORMALES - plazasNormales.availablePermits())
                + (PLAZAS_VIP - plazasVip.availablePermits());
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== PARKING INTELIGENTE ===\n");

        // --- Dashboard en tiempo real ---
        ScheduledExecutorService dashboard = Executors.newSingleThreadScheduledExecutor();
        dashboard.scheduleAtFixedRate(ExamenParking::pintarDashboard,
                5, 10, TimeUnit.SECONDS);

        // --- Llegada de 200 coches (hilos virtuales, llegada escalonada) ---
        for (int i = 1; i <= COCHES; i++) {
            final int id = i;
            final TipoVehiculo tipo = new Random().nextDouble() < 0.2
                    ? TipoVehiculo.VIP : TipoVehiculo.NORMAL;
            Thread.ofVirtual().name("coche-" + id).start(() -> simularCoche(id, tipo));
            Thread.sleep(150); // llegada escalonada para no saturar la barrera
        }

        finJornada.await();
        dashboard.shutdownNow();

        System.out.println("\n--- RESUMEN DEL DIA ---");
        System.out.println("Vehiculos procesados: " + procesados.get());
        System.out.println("Vehiculos atendidos: " + atendidos.get()
                + " (" + (atendidos.get() * 100.0 / COCHES) + "%)");
        System.out.println("Vehiculos rechazados: " + rechazados.get()
                + " (parking+cola llenos)");
        System.out.println("Tiempo promedio de estancia: "
                + (atendidos.get() == 0 ? 0 : estanciaTotalMs.get() / 1000.0 / atendidos.get())
                + "s");
        System.out.println("Ingresos totales: " + (ingresos.get() / 100.0) + "€");
        System.out.println("Ocupacion maxima: " + ocupacionMaxima.get() + "/"
                + (PLAZAS_NORMALES + PLAZAS_VIP) + " plazas");
        System.exit(0);
    }

    static void simularCoche(int id, TipoVehiculo tipo) {
        procesados.incrementAndGet();
        Object token = new Object();

        // 1) Buscar plaza (los VIP pueden usar cualquiera, los normales solo normales)
        Semaphore plaza = buscarPlaza(tipo);
        if (plaza == null) {
            // 2) No hay plaza -> esperar en la cola (si cabe) o irse
            if (!colaEspera.offer(token)) {
                rechazados.incrementAndGet();
                System.out.println("[" + ahora() + "] Coche-" + id + " (" + tipo
                        + ") se VA: parking y cola llenos");
                finJornada.countDown();
                return;
            }
            System.out.println("[" + ahora() + "] Coche-" + id + " (" + tipo
                    + ") esperando en cola (" + colaEspera.size() + "/" + MAX_COLA + ")");
            plaza = esperarEnCola(token, tipo);
        }

        // 3) Barrera de entrada: 1 coche cada 2 s
        synchronized (barreraEntrada) {
            dormir(BARRERA_ENTRADA_MS);
        }
        atendidos.incrementAndGet();
        int ocupacion = ocupacionActual();
        ocupacionMaxima.accumulateAndGet(ocupacion, Math::max);
        System.out.println("[" + ahora() + "] " + (tipo == TipoVehiculo.VIP ? "⭐" : "🚗")
                + " Coche-" + id + " (" + tipo + ") entra - ocupacion " + ocupacion + "/25");

        // 4) Estancia de 10-30 s
        long estanciaMs = ESTANCIA_MIN_S * 1000L
                + new Random().nextInt((ESTANCIA_MAX_S - ESTANCIA_MIN_S) * 1000);
        dormir(estanciaMs);
        estanciaTotalMs.addAndGet(estanciaMs);

        // 5) Barrera de salida: 1 coche cada 1 s
        synchronized (barreraSalida) {
            dormir(BARRERA_SALIDA_MS);
        }
        plaza.release();
        long importe = Math.round(tipo.tarifaPorMinuto * (estanciaMs / 60_000.0) * 100);
        ingresos.addAndGet(importe);
        System.out.println("[" + ahora() + "] Coche-" + id + " (" + tipo + ") sale - Pago: "
                + (importe / 100.0) + "€");

        finJornada.countDown();
    }

    // Intenta coger plaza: VIP -> VIP o normal; NORMAL -> solo normal
    static Semaphore buscarPlaza(TipoVehiculo tipo) {
        if (tipo == TipoVehiculo.VIP) {
            if (plazasVip.tryAcquire()) return plazasVip;
        }
        if (plazasNormales.tryAcquire()) return plazasNormales;
        return null;
    }

    // Espera en cola hasta que haya una plaza adecuada para su tipo
    static Semaphore esperarEnCola(Object token, TipoVehiculo tipo) {
        try {
            while (true) {
                Semaphore plaza = buscarPlaza(tipo);
                if (plaza != null) {
                    colaEspera.remove(token);
                    return plaza;
                }
                dormir(500); // sondeo: simple y suficiente para la simulacion
            }
        } finally {
            colaEspera.remove(token);
        }
    }

    static void pintarDashboard() {
        int libresN = plazasNormales.availablePermits();
        int libresV = plazasVip.availablePermits();
        System.out.println("\n🅿️  Estado actual: [" + ahora() + "]");
        System.out.println("┌─────────────────────────────────┐");
        System.out.println("│ PLAZAS NORMALES: " + barra(PLAZAS_NORMALES - libresN, PLAZAS_NORMALES)
                + " " + (PLAZAS_NORMALES - libresN) + "/" + PLAZAS_NORMALES);
        System.out.println("│ PLAZAS VIP:      " + barra(PLAZAS_VIP - libresV, PLAZAS_VIP)
                + " " + (PLAZAS_VIP - libresV) + "/" + PLAZAS_VIP);
        System.out.println("│ COLA DE ESPERA:  " + barra(colaEspera.size(), MAX_COLA)
                + " " + colaEspera.size() + "/" + MAX_COLA);
        System.out.println("│ INGRESOS:              "
                + String.format("%8.2f€", ingresos.get() / 100.0) + " │");
        System.out.println("└─────────────────────────────────┘");
    }

    static String barra(int usadas, int total) {
        int celdas = 8;
        int llenas = (int) Math.round(usadas * (celdas * 1.0 / total));
        return "█".repeat(llenas) + "░".repeat(celdas - llenas);
    }

    static void dormir(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}