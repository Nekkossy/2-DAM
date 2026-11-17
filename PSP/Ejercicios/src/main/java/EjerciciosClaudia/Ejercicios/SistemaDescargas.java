package EjerciciosClaudia.Ejercicios;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/*
 * EJERCICIO 5: SISTEMA DE DESCARGAS
 *
 * Enunciado resumido:
 *  - 50 usuarios solicitan archivos de distintos tamanos.
 *  - El servidor admite como maximo 5 descargas simultaneas.
 *  - Los usuarios premium tienen prioridad en la cola.
 *  - Cada descarga puede reintentarse hasta 3 veces.
 *  - Mostrar progreso y estadisticas finales.
 *
 * Conceptos: ThreadPoolExecutor, PriorityBlockingQueue, CompletableFuture,
 * hilos virtuales, ConcurrentHashMap, ScheduledExecutorService y AtomicInteger.
 * Los tiempos se han reducido para que puedas probarlo en clase sin esperar minutos.
 */
public class SistemaDescargas {

    static final int USUARIOS = 50;
    static final int MAX_DESCARGAS = 5;
    static final int MAX_INTENTOS = 3;

    enum TipoArchivo {
        DOCUMENTO(10, 300), IMAGEN(50, 500), VIDEO(500, 800), JUEGO(2000, 1000);
        final int megas;
        final int tiempoMs;
        TipoArchivo(int megas, int tiempoMs) {
            this.megas = megas;
            this.tiempoMs = tiempoMs;
        }
    }

    static class Resultado {
        final boolean correcto;
        final int intentos;
        Resultado(boolean correcto, int intentos) {
            this.correcto = correcto;
            this.intentos = intentos;
        }
    }

    // Cada tarea de descarga es comparable para que las solicitudes premium salgan antes.
    static class TareaDescarga implements Runnable, Comparable<TareaDescarga> {
        final String usuario;
        final TipoArchivo archivo;
        final boolean premium;
        final int orden;
        final CompletableFuture<Resultado> resultado = new CompletableFuture<>();

        TareaDescarga(String usuario, TipoArchivo archivo, boolean premium, int orden) {
            this.usuario = usuario;
            this.archivo = archivo;
            this.premium = premium;
            this.orden = orden;
        }

        public int compareTo(TareaDescarga otra) {
            // true (premium) debe quedar delante de false (normal).
            int prioridad = Boolean.compare(otra.premium, this.premium);
            if (prioridad != 0) {
                return prioridad;
            }
            return Integer.compare(this.orden, otra.orden);
        }

        public void run() {
            boolean exito = false;
            int intentos = 0;

            while (!exito && intentos < MAX_INTENTOS) {
                intentos++;
                try {
                    // Diez pasos permiten actualizar el progreso durante la descarga.
                    for (int paso = 1; paso <= 10; paso++) {
                        Thread.sleep(archivo.tiempoMs / 10);
                        progreso.put(usuario, paso * 10);
                    }

                    // Simulamos un fallo de red ocasional para practicar los reintentos.
                    exito = ThreadLocalRandom.current().nextInt(100) >= 10;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                if (!exito && intentos < MAX_INTENTOS) {
                    reintentos.incrementAndGet();
                    System.out.println(usuario + " reintenta la descarga (" + (intentos + 1) + ")");
                }
            }

            progreso.remove(usuario);
            if (exito) {
                completadas.incrementAndGet();
                datosTransferidos.addAndGet(archivo.megas);
            } else {
                fallidas.incrementAndGet();
            }
            resultado.complete(new Resultado(exito, intentos));
        }
    }

    // El ThreadPoolExecutor tiene cinco trabajadores y una cola con prioridad.
    static final ThreadPoolExecutor servidor = new ThreadPoolExecutor(
            MAX_DESCARGAS, MAX_DESCARGAS, 0, TimeUnit.MILLISECONDS,
            new PriorityBlockingQueue<>());

    static final ConcurrentHashMap<String, Integer> progreso = new ConcurrentHashMap<>();
    static final AtomicInteger ordenTareas = new AtomicInteger();
    static final AtomicInteger completadas = new AtomicInteger();
    static final AtomicInteger fallidas = new AtomicInteger();
    static final AtomicInteger reintentos = new AtomicInteger();
    static final AtomicLong datosTransferidos = new AtomicLong();

    public static void main(String[] args) throws InterruptedException {
        ScheduledExecutorService monitor = Executors.newSingleThreadScheduledExecutor();
        monitor.scheduleAtFixedRate(SistemaDescargas::mostrarProgreso, 1, 1, TimeUnit.SECONDS);

        CountDownLatch usuariosTerminados = new CountDownLatch(USUARIOS);
        List<Thread> hilosUsuarios = new ArrayList<>();
        TipoArchivo[] archivos = TipoArchivo.values();

        for (int i = 1; i <= USUARIOS; i++) {
            final int id = i;
            final boolean premium = i % 5 == 0;

            Thread usuario = Thread.ofVirtual().name("usuario-" + id).start(() -> {
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(10, 100));
                    TipoArchivo archivo = archivos[ThreadLocalRandom.current().nextInt(archivos.length)];
                    TareaDescarga tarea = new TareaDescarga("usuario-" + id, archivo,
                            premium, ordenTareas.incrementAndGet());

                    // El usuario envia su solicitud al servidor y espera el resultado futuro.
                    servidor.execute(tarea);
                    Resultado resultado = tarea.resultado.get();
                    System.out.println(tarea.usuario + (resultado.correcto
                            ? " descarga terminada" : " descarga fallida"));
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                } finally {
                    usuariosTerminados.countDown();
                }
            });
            hilosUsuarios.add(usuario);
        }

        usuariosTerminados.await();
        for (Thread hilo : hilosUsuarios) {
            hilo.join();
        }

        // Cierre ordenado: primero dejamos de aceptar trabajo y esperamos a los cinco trabajadores.
        servidor.shutdown();
        servidor.awaitTermination(30, TimeUnit.SECONDS);

        monitor.shutdown();
        if (!monitor.awaitTermination(5, TimeUnit.SECONDS)) {
            monitor.shutdownNow();
        }

        System.out.println("\n--- ESTADISTICAS ---");
        System.out.println("Solicitudes: " + USUARIOS);
        System.out.println("Completadas: " + completadas.get());
        System.out.println("Fallidas: " + fallidas.get());
        System.out.println("Reintentos: " + reintentos.get());
        System.out.println("Datos transferidos: " + datosTransferidos.get() + " MB");
    }

    static void mostrarProgreso() {
        System.out.println("\nDescargas activas: " + progreso.size() + "/" + MAX_DESCARGAS);
        progreso.forEach((usuario, porcentaje) ->
                System.out.println(usuario + " - " + porcentaje + "%"));
    }
}
