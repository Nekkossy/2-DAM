package EjerciciosClaudia.Ejercicios;

/*
 * EJERCICIO 4: PARKING INTELIGENTE CON CARAVANAS
 *
 * Enunciado resumido (adaptado con las modificaciones que has compartido):
 *  - 20 plazas normales y 5 VIP. En esta ultima version las caravanas son normales.
 *  - Cada hilo representa una caravana de 1 a 3 coches que necesita plazas contiguas.
 *  - La caravana consigue todas las plazas juntas o se marcha tras esperar un maximo.
 *  - Un unico hilo controla la barrera y atiende SIEMPRE primero las salidas pendientes.
 *  - Dashboard periodico, estadisticas, ingresos separados y cierre ordenado.
 *
 * Conceptos: Semaphore, BlockingQueue, CountDownLatch, Atomic*, Thread virtual,
 * ScheduledExecutorService, join, shutdown/awaitTermination y synchronized para
 * reservar posiciones contiguas de forma atomica.
 *
 * NOTA: esta es la version modificada para caravanas; no es exactamente el parking
 * original que aparece en el ZIP del profesor.
 */

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
        NORMAL(1.0), VIP(2.0);

        final double tarifaPorMinuto;

        TipoVehiculo(double tarifa) {
            this.tarifaPorMinuto = tarifa;
        }
    }

    enum TipoSolicitud {
        ENTRADA, SALIDA, PARAR
    }

    static class Solicitud {
        final TipoSolicitud tipo;
        final int idCaravana;
        final int numeroCoche;
        final CountDownLatch atendida = new CountDownLatch(1);

        Solicitud(TipoSolicitud tipo, int idCaravana, int numeroCoche) {
            this.tipo = tipo;
            this.idCaravana = idCaravana;
            this.numeroCoche = numeroCoche;
        }
    }

    static final int PLAZAS_NORMALES = 20;
    static final int PLAZAS_VIP = 5;
    static final int CARAVANAS = 200;
    static final long LLEGADA_CARAVANA_MS = 150;
    static final long BARRERA_MS = 1000;
    static final int ESPERA_MAXIMA_PLAZA_S = 60;
    static final int ESTANCIA_MIN_S = 10;
    static final int ESTANCIA_MAX_S = 30;

    // El semaforo cuenta permisos; el array posterior permite localizar plazas contiguas.
    static final Semaphore plazasNormales = new Semaphore(PLAZAS_NORMALES, true);
    static final Semaphore plazasVip = new Semaphore(PLAZAS_VIP, true);
    static final boolean[] plazasOcupadas = new boolean[PLAZAS_NORMALES];
    static final Object bloqueoPlazas = new Object();

    static final BlockingQueue<Solicitud> colaEntradas =
            new ArrayBlockingQueue<>(CARAVANAS * 3 + 1);
    static final BlockingQueue<Solicitud> colaSalidas =
            new ArrayBlockingQueue<>(CARAVANAS * 3 + 1);
    static final Semaphore solicitudesPendientes = new Semaphore(0);
    static final Object bloqueoColas = new Object();

    static final AtomicInteger caravanasProcesadas = new AtomicInteger();
    static final AtomicInteger caravanasAtendidas = new AtomicInteger();
    static final AtomicInteger caravanasRechazadas = new AtomicInteger();
    static final AtomicInteger cochesProcesados = new AtomicInteger();
    static final AtomicInteger cochesAtendidos = new AtomicInteger();
    static final AtomicInteger cochesRechazados = new AtomicInteger();
    static final AtomicLong estanciaTotalMs = new AtomicLong();
    static final AtomicInteger ocupacionMaxima = new AtomicInteger();
    static final AtomicLong ingresos = new AtomicLong();
    static final AtomicLong ingresosVIP = new AtomicLong();
    static final AtomicLong ingresosNormales = new AtomicLong();
    static final CountDownLatch finJornada = new CountDownLatch(CARAVANAS);

    static String ahora() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    static int plazasNormalesOcupadas() {
        synchronized (bloqueoPlazas) {
            int ocupadas = 0;
            for (boolean ocupada : plazasOcupadas) {
                if (ocupada) {
                    ocupadas++;
                }
            }
            return ocupadas;
        }
    }

    static int ocupacionActual() {
        return plazasNormalesOcupadas()
                + (PLAZAS_VIP - plazasVip.availablePermits());
    }

    // Primero reserva la cantidad total de permisos y despues busca un bloque contiguo.
    // Si no logra reservarlo antes del limite, devuelve los permisos en el finally.
    static int reservarPlazasCaravana(int cantidad) throws InterruptedException {
        long limite = System.nanoTime() + TimeUnit.SECONDS.toNanos(ESPERA_MAXIMA_PLAZA_S);

        if (!plazasNormales.tryAcquire(
                cantidad, ESPERA_MAXIMA_PLAZA_S, TimeUnit.SECONDS)) {
            return -1;
        }

        boolean reservadas = false;

        try {
            synchronized (bloqueoPlazas) {
                while (true) {
                    int inicio = buscarHueco(cantidad);

                    if (inicio != -1) {
                        for (int i = inicio; i < inicio + cantidad; i++) {
                            plazasOcupadas[i] = true;
                        }
                        reservadas = true;
                        return inicio;
                    }

                    long restante = limite - System.nanoTime();
                    if (restante <= 0) {
                        return -1;
                    }

                    TimeUnit.NANOSECONDS.timedWait(bloqueoPlazas, restante);
                }
            }
        } finally {
            if (!reservadas) {
                plazasNormales.release(cantidad);
            }
        }
    }

    static int buscarHueco(int cantidad) {
        for (int inicio = 0; inicio <= PLAZAS_NORMALES - cantidad; inicio++) {
            boolean libre = true;

            for (int i = inicio; i < inicio + cantidad; i++) {
                if (plazasOcupadas[i]) {
                    libre = false;
                    break;
                }
            }

            if (libre) {
                return inicio;
            }
        }

        return -1;
    }

    static void liberarPlazasCaravana(int inicio, int cantidad) {
        synchronized (bloqueoPlazas) {
            for (int i = inicio; i < inicio + cantidad; i++) {
                plazasOcupadas[i] = false;
            }
            bloqueoPlazas.notifyAll();
        }

        plazasNormales.release(cantidad);
    }

    // MAIN: arranca la barrera y el dashboard, crea las caravanas y espera sus salidas.
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== PARKING INTELIGENTE: CARAVANAS ===\n");

        Thread hiloBarrera = Thread.ofPlatform()
                .name("barrera")
                .start(ExamenParking::funcionarBarrera);

        ScheduledExecutorService dashboard = Executors.newSingleThreadScheduledExecutor();
        dashboard.scheduleAtFixedRate(ExamenParking::pintarDashboard,
                5, 10, TimeUnit.SECONDS);

        List<Thread> hilosCaravanas = new ArrayList<>();
        Random random = new Random();

        for (int i = 1; i <= CARAVANAS; i++) {
            final int id = i;
            final int cantidadCoches = 1 + random.nextInt(3);

            Thread hilo = Thread.ofVirtual()
                    .name("caravana-" + id)
                    .start(() -> simularCaravana(id, cantidadCoches));

            hilosCaravanas.add(hilo);
            Thread.sleep(LLEGADA_CARAVANA_MS);
        }

        finJornada.await();

        for (Thread hilo : hilosCaravanas) {
            hilo.join();
        }

        cerrarBarrera();
        hiloBarrera.join();

        dashboard.shutdown();
        if (!dashboard.awaitTermination(10, TimeUnit.SECONDS)) {
            dashboard.shutdownNow();
            dashboard.awaitTermination(10, TimeUnit.SECONDS);
        }

        System.out.println("\n--- RESUMEN DEL DIA ---");
        System.out.println("Caravanas procesadas: " + caravanasProcesadas.get());
        System.out.println("Caravanas atendidas: " + caravanasAtendidas.get());
        System.out.println("Caravanas rechazadas: " + caravanasRechazadas.get());
        System.out.println("Coches procesados: " + cochesProcesados.get());
        System.out.println("Coches atendidos: " + cochesAtendidos.get());
        System.out.println("Coches rechazados: " + cochesRechazados.get());
        System.out.println("Tiempo promedio de estancia por coche: "
                + (cochesAtendidos.get() == 0 ? 0
                : estanciaTotalMs.get() / 1000.0 / cochesAtendidos.get()) + " s");
        System.out.println("Ingresos VIP: " + (ingresosVIP.get() / 100.0) + " €");
        System.out.println("Ingresos NORMALES: " + (ingresosNormales.get() / 100.0) + " €");
        System.out.println("Ingresos totales: " + (ingresos.get() / 100.0) + " €");
        System.out.println("Ocupacion maxima: " + ocupacionMaxima.get() + "/"
                + (PLAZAS_NORMALES + PLAZAS_VIP) + " plazas");
    }

    // Flujo de cada caravana: reservar -> entrar -> estancia -> salir -> liberar plazas.
    static void simularCaravana(int id, int cantidadCoches) {
        caravanasProcesadas.incrementAndGet();
        cochesProcesados.addAndGet(cantidadCoches);

        boolean tienePlazas = false;
        int primeraPlaza = -1;

        try {
            System.out.println("[" + ahora() + "] Caravana-" + id + " llega con "
                    + cantidadCoches + " coches NORMALES");

            primeraPlaza = reservarPlazasCaravana(cantidadCoches);

            if (primeraPlaza == -1) {
                caravanasRechazadas.incrementAndGet();
                cochesRechazados.addAndGet(cantidadCoches);
                System.out.println("[" + ahora() + "] Caravana-" + id
                        + " se VA: no ha conseguido sus " + cantidadCoches + " plazas");
                return;
            }

            tienePlazas = true;

            for (int i = 1; i <= cantidadCoches; i++) {
                solicitarPaso(TipoSolicitud.ENTRADA, id, i);
            }

            caravanasAtendidas.incrementAndGet();
            cochesAtendidos.addAndGet(cantidadCoches);

            int ocupacion = ocupacionActual();
            ocupacionMaxima.accumulateAndGet(ocupacion, Math::max);

            System.out.println("[" + ahora() + "] Caravana-" + id + " ENTRA con "
                    + cantidadCoches + " coches - ocupacion " + ocupacion + "/25");

            long estanciaMs = ESTANCIA_MIN_S * 1000L
                    + new Random().nextInt((ESTANCIA_MAX_S - ESTANCIA_MIN_S) * 1000);

            dormir(estanciaMs);
            estanciaTotalMs.addAndGet(estanciaMs * cantidadCoches);

            for (int i = 1; i <= cantidadCoches; i++) {
                solicitarPaso(TipoSolicitud.SALIDA, id, i);
            }

            liberarPlazasCaravana(primeraPlaza, cantidadCoches);
            tienePlazas = false;

            long importePorCoche = Math.round(TipoVehiculo.NORMAL.tarifaPorMinuto
                    * (estanciaMs / 60_000.0) * 100);
            long importeTotal = importePorCoche * cantidadCoches;
            registrarIngreso(TipoVehiculo.NORMAL, importeTotal);

            System.out.println("[" + ahora() + "] Caravana-" + id + " SALE - Pago: "
                    + (importeTotal / 100.0) + " €");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (tienePlazas) {
                liberarPlazasCaravana(primeraPlaza, cantidadCoches);
            }
            finJornada.countDown();
        }
    }

    // Cada coche deja una solicitud y espera a que el hilo unico de barrera la atienda.
    static void solicitarPaso(TipoSolicitud tipo, int idCaravana, int numeroCoche) {
        Solicitud solicitud = new Solicitud(tipo, idCaravana, numeroCoche);

        synchronized (bloqueoColas) {
            if (tipo == TipoSolicitud.SALIDA) {
                colaSalidas.add(solicitud);
            } else {
                colaEntradas.add(solicitud);
            }
            solicitudesPendientes.release();
        }

        boolean interrumpido = false;

        while (true) {
            try {
                solicitud.atendida.await();
                break;
            } catch (InterruptedException e) {
                interrumpido = true;
            }
        }

        if (interrumpido) {
            Thread.currentThread().interrupt();
        }
    }

    // La barrera consulta primero la cola de salidas; solo si esta vacia atiende entradas.
    static void funcionarBarrera() {
        while (true) {
            try {
                solicitudesPendientes.acquire();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            Solicitud solicitud;

            synchronized (bloqueoColas) {
                solicitud = colaSalidas.poll();
                if (solicitud == null) {
                    solicitud = colaEntradas.poll();
                }
            }

            if (solicitud == null) {
                continue;
            }

            if (solicitud.tipo == TipoSolicitud.PARAR) {
                return;
            }

            String accion = solicitud.tipo == TipoSolicitud.ENTRADA ? "ENTRADA" : "SALIDA";
            System.out.println("[" + ahora() + "] Barrera atiende " + accion
                    + " de caravana-" + solicitud.idCaravana
                    + ", coche " + solicitud.numeroCoche);

            try {
                Thread.sleep(BARRERA_MS);
            } catch (InterruptedException e) {
                solicitud.atendida.countDown();
                Thread.currentThread().interrupt();
                return;
            }

            solicitud.atendida.countDown();
        }
    }

    // Envia una solicitud especial para que el hilo de la barrera termine ordenadamente.
    static void cerrarBarrera() {
        Solicitud parar = new Solicitud(TipoSolicitud.PARAR, 0, 0);

        synchronized (bloqueoColas) {
            colaEntradas.add(parar);
            solicitudesPendientes.release();
        }
    }

    static void registrarIngreso(TipoVehiculo tipo, long importe) {
        ingresos.addAndGet(importe);

        if (tipo == TipoVehiculo.VIP) {
            ingresosVIP.addAndGet(importe);
        } else {
            ingresosNormales.addAndGet(importe);
        }
    }

    static void pintarDashboard() {
        int libresVip = plazasVip.availablePermits();
        int ocupadasNormales = plazasNormalesOcupadas();
        int ocupadasVip = PLAZAS_VIP - libresVip;
        int esperando = plazasNormales.getQueueLength();

        System.out.println("\nPARKING - Estado actual: [" + ahora() + "]");
        System.out.println("┌──────────────────────────────────────────────┐");
        System.out.println("│ PLAZAS NORMALES: " + barra(ocupadasNormales, PLAZAS_NORMALES)
                + " " + ocupadasNormales + "/" + PLAZAS_NORMALES + " │");
        System.out.println("│ PLAZAS VIP:      " + barra(ocupadasVip, PLAZAS_VIP)
                + " " + ocupadasVip + "/" + PLAZAS_VIP + "       │");
        System.out.println("│ ESPERANDO PLAZA: " + esperando + " caravanas              │");
        System.out.println("│ INGRESOS: " + String.format("%8.2f", ingresos.get() / 100.0)
                + " €                         │");
        System.out.println("└──────────────────────────────────────────────┘");
    }

    static String barra(int usadas, int total) {
        int celdas = 8;
        int llenas = (int) Math.round(usadas * (celdas * 1.0 / total));
        return "█".repeat(llenas) + "░".repeat(celdas - llenas);
    }

    static void dormir(long ms) {
        long fin = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(ms);
        boolean interrumpido = false;

        while (System.nanoTime() < fin) {
            long restante = fin - System.nanoTime();
            try {
                TimeUnit.NANOSECONDS.sleep(restante);
            } catch (InterruptedException e) {
                interrumpido = true;
            }
        }

        if (interrumpido) {
            Thread.currentThread().interrupt();
        }
    }
}
