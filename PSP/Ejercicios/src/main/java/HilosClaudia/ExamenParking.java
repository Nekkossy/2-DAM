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



package HilosClaudia;

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
        TipoSolicitud tipo;
        int idCaravana;
        int cantidadCoches;
        CountDownLatch atendida = new CountDownLatch(1);

        Solicitud(TipoSolicitud tipo, int idCaravana, int cantidadCoches) {
            this.tipo = tipo;
            this.idCaravana = idCaravana;
            this.cantidadCoches = cantidadCoches;
        }
    }

    static final int PLAZAS_NORMALES = 20;
    static final int PLAZAS_VIP = 5;
    static final int CARAVANAS = 200;
    static final long LLEGADA_CARAVANA_MS = 150;
    static final long BARRERA_MS = 1000;
    static final int ESPERA_MAXIMA_SEGUNDOS = 60;
    static final int ESTANCIA_MIN_S = 10;
    static final int ESTANCIA_MAX_S = 30;

    static final Semaphore plazasNormales = new Semaphore(PLAZAS_NORMALES, true);
    static final Semaphore plazasVip = new Semaphore(PLAZAS_VIP, true);

    static final BlockingQueue<Solicitud> colaEntradas = new ArrayBlockingQueue<>(CARAVANAS + 1);
    static final BlockingQueue<Solicitud> colaSalidas = new ArrayBlockingQueue<>(CARAVANAS + 1);
    static final Semaphore solicitudesPendientes = new Semaphore(0);

    static final AtomicInteger caravanasAtendidas = new AtomicInteger();
    static final AtomicInteger caravanasRechazadas = new AtomicInteger();
    static final AtomicInteger cochesProcesados = new AtomicInteger();
    static final AtomicInteger cochesAtendidos = new AtomicInteger();
    static final AtomicInteger cochesRechazados = new AtomicInteger();
    static final AtomicLong estanciaTotalMs = new AtomicLong();
    static final AtomicInteger ocupacionMaxima = new AtomicInteger();
    static final AtomicLong ingresosVIP = new AtomicLong();
    static final AtomicLong ingresosNormales = new AtomicLong();
    static final CountDownLatch finJornada = new CountDownLatch(CARAVANAS);

    static String ahora() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    static int ocupacionActual() {
        return (PLAZAS_NORMALES - plazasNormales.availablePermits())
                + (PLAZAS_VIP - plazasVip.availablePermits());
    }

    static void acumularIngresos(TipoVehiculo tipo, long importe) {
        if (tipo == TipoVehiculo.VIP) {
            ingresosVIP.addAndGet(importe);
        } else {
            acumularIngresos(TipoVehiculo.NORMAL, importe);
        }
    }

    static long ingresosTotales() {
        return ingresosVIP.get() + ingresosNormales.get();
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== PARKING INTELIGENTE: CARAVANAS ===\n");

        Thread hiloBarrera = Thread.ofPlatform().name("barrera").start(ExamenParking::funcionarBarrera);

        ScheduledExecutorService dashboard = Executors.newSingleThreadScheduledExecutor();
        dashboard.scheduleAtFixedRate(ExamenParking::pintarDashboard,
                5, 10, TimeUnit.SECONDS);

        List<Thread> hilosCaravanas = new ArrayList<>();
        Random random = new Random();

        for (int i = 1; i <= CARAVANAS; i++) {
            final int id = i;
            final int cantidadCoches = 1 + random.nextInt(3);

            Thread hilo = Thread.ofVirtual().name("caravana-" + id)
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
        System.out.println("Caravanas procesadas: " + CARAVANAS);
        System.out.println("Coches procesados: " + cochesProcesados.get());
        System.out.println("Caravanas atendidas: " + caravanasAtendidas.get());
        System.out.println("Coches atendidos: " + cochesAtendidos.get());
        System.out.println("Caravanas rechazadas: " + caravanasRechazadas.get());
        System.out.println("Coches rechazados: " + cochesRechazados.get());
        System.out.println("Tiempo promedio de estancia: "
                + (cochesAtendidos.get() == 0 ? 0
                : estanciaTotalMs.get() / 1000.0 / cochesAtendidos.get()) + " s");
        System.out.println("Ingresos VIP: " + (ingresosVIP.get() / 100.0) + " €");
        System.out.println("Ingresos NORMALES: " + (ingresosNormales.get() / 100.0) + " €");
        System.out.println("Ingresos totales: " + (ingresosTotales() / 100.0) + " €");
        System.out.println("Ocupacion maxima: " + ocupacionMaxima.get() + "/"
                + (PLAZAS_NORMALES + PLAZAS_VIP) + " plazas");
    }

    static void simularCaravana(int id, int cantidadCoches) {
        cochesProcesados.addAndGet(cantidadCoches);
        boolean tienePlazas = false;

        try {
            System.out.println("[" + ahora() + "] Caravana-" + id + " llega con "
                    + cantidadCoches + " coches");

            boolean consiguePlazas = plazasNormales.tryAcquire(
                    cantidadCoches, ESPERA_MAXIMA_SEGUNDOS, TimeUnit.SECONDS);

            if (!consiguePlazas) {
                caravanasRechazadas.incrementAndGet();
                cochesRechazados.addAndGet(cantidadCoches);
                System.out.println("[" + ahora() + "] Caravana-" + id
                        + " se VA: no ha conseguido sus " + cantidadCoches + " plazas");
                return;
            }

            tienePlazas = true;

            solicitarPaso(TipoSolicitud.ENTRADA, id, cantidadCoches);

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

            solicitarPaso(TipoSolicitud.SALIDA, id, cantidadCoches);

            plazasNormales.release(cantidadCoches);
            tienePlazas = false;

            long importePorCoche = Math.round(
                    TipoVehiculo.NORMAL.tarifaPorMinuto * (estanciaMs / 60_000.0) * 100);
            long importe = importePorCoche * cantidadCoches;
            acumularIngresos(TipoVehiculo.NORMAL, importe);

            System.out.println("[" + ahora() + "] Caravana-" + id + " SALE - Pago: "
                    + (importe / 100.0) + " €");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (tienePlazas) {
                plazasNormales.release(cantidadCoches);
            }
            finJornada.countDown();
        }
    }

    static void solicitarPaso(TipoSolicitud tipo, int id, int cantidadCoches) {
        Solicitud solicitud = new Solicitud(tipo, id, cantidadCoches);

        if (tipo == TipoSolicitud.SALIDA) {
            colaSalidas.add(solicitud);
        } else {
            colaEntradas.add(solicitud);
        }

        solicitudesPendientes.release();

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

    static void funcionarBarrera() {
        while (true) {
            try {
                solicitudesPendientes.acquire();

                Solicitud solicitud = colaSalidas.poll();
                if (solicitud == null) {
                    solicitud = colaEntradas.poll();
                }

                if (solicitud == null) {
                    continue;
                }

                if (solicitud.tipo == TipoSolicitud.PARAR) {
                    break;
                }

                String accion = solicitud.tipo == TipoSolicitud.ENTRADA ? "ENTRADA" : "SALIDA";
                System.out.println("[" + ahora() + "] Barrera atiende " + accion
                        + " de caravana-" + solicitud.idCaravana);

                try {
                    for (int i = 0; i < solicitud.cantidadCoches; i++) {
                        Thread.sleep(BARRERA_MS);
                    }
                } finally {
                    solicitud.atendida.countDown();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    static void cerrarBarrera() {
        Solicitud parar = new Solicitud(TipoSolicitud.PARAR, 0, 0);
        colaEntradas.add(parar);
        solicitudesPendientes.release();
    }

    static void pintarDashboard() {
        int libresNormales = plazasNormales.availablePermits();
        int libresVip = plazasVip.availablePermits();
        int ocupadasNormales = PLAZAS_NORMALES - libresNormales;
        int ocupadasVip = PLAZAS_VIP - libresVip;
        int esperando = plazasNormales.getQueueLength();

        System.out.println("\nPARKING - Estado actual: [" + ahora() + "]");
        System.out.println("┌──────────────────────────────────────┐");
        System.out.println("│ PLAZAS NORMALES: " + barra(ocupadasNormales, PLAZAS_NORMALES)
                + " " + ocupadasNormales + "/" + PLAZAS_NORMALES + " │");
        System.out.println("│ PLAZAS VIP:      " + barra(ocupadasVip, PLAZAS_VIP)
                + " " + ocupadasVip + "/" + PLAZAS_VIP + "   │");
        System.out.println("│ ESPERANDO PLAZA: " + esperando + " caravanas          │");
        System.out.println("│ INGRESOS: " + String.format("%8.2f", ingresosTotales() / 100.0) + " €             │");
        System.out.println("└──────────────────────────────────────┘");
    }

    static String barra(int usadas, int total) {
        int celdas = 8;
        int llenas = (int) Math.round(usadas * (celdas * 1.0 / total));
        return "█".repeat(llenas) + "░".repeat(celdas - llenas);
    }

    static void dormir(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}