package EjerciciosClaudia.Ejercicios;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/*
 * EJERCICIO 2: BANCO VIRTUAL
 *
 * Enunciado resumido:
 *  - Una cuenta empieza con 10.000 euros.
 *  - 50 clientes realizan 10 operaciones cada uno.
 *  - Un retiro solo se acepta si hay saldo suficiente.
 *  - Se guarda un historial de operaciones.
 *  - Comparar ReentrantLock y synchronized.
 *
 * Conceptos: seccion critica, exclusion mutua, ReentrantLock, synchronized y join.
 * En esta solucion se muestran dos implementaciones correctas; no se usa volatile
 * para proteger operaciones compuestas como comprobar saldo y despues retirar.
 */
public class BancoVirtual {

    static final int SALDO_INICIAL = 10000;
    static final int CLIENTES = 50;
    static final int OPERACIONES_POR_CLIENTE = 10;

    interface Cuenta {
        boolean retirar(int cantidad);
        void ingresar(int cantidad);
        int consultarSaldo();
        List<String> obtenerHistorial();
    }

    // VERSION 1: ReentrantLock. Hay que liberar el lock siempre en finally.
    static class CuentaConLock implements Cuenta {
        private int saldo = SALDO_INICIAL;
        private final ReentrantLock lock = new ReentrantLock();
        private final List<String> historial = new ArrayList<>();

        public boolean retirar(int cantidad) {
            lock.lock();
            try {
                if (saldo < cantidad) {
                    historial.add("Retiro rechazado de " + cantidad + " euros");
                    return false;
                }
                saldo -= cantidad;
                historial.add("Retirada de " + cantidad + " euros. Saldo: " + saldo);
                return true;
            } finally {
                lock.unlock();
            }
        }

        public void ingresar(int cantidad) {
            lock.lock();
            try {
                saldo += cantidad;
                historial.add("Ingreso de " + cantidad + " euros. Saldo: " + saldo);
            } finally {
                lock.unlock();
            }
        }

        public int consultarSaldo() {
            lock.lock();
            try {
                return saldo;
            } finally {
                lock.unlock();
            }
        }

        public List<String> obtenerHistorial() {
            lock.lock();
            try {
                return new ArrayList<>(historial);
            } finally {
                lock.unlock();
            }
        }
    }

    // VERSION 2: synchronized. El monitor de this protege todo el metodo.
    static class CuentaSincronizada implements Cuenta {
        private int saldo = SALDO_INICIAL;
        private final List<String> historial = new ArrayList<>();

        public synchronized boolean retirar(int cantidad) {
            if (saldo < cantidad) {
                historial.add("Retiro rechazado de " + cantidad + " euros");
                return false;
            }
            saldo -= cantidad;
            historial.add("Retirada de " + cantidad + " euros. Saldo: " + saldo);
            return true;
        }

        public synchronized void ingresar(int cantidad) {
            saldo += cantidad;
            historial.add("Ingreso de " + cantidad + " euros. Saldo: " + saldo);
        }

        public synchronized int consultarSaldo() {
            return saldo;
        }

        public synchronized List<String> obtenerHistorial() {
            return new ArrayList<>(historial);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ejecutarPrueba("REENTRANTLOCK", new CuentaConLock());
        ejecutarPrueba("SYNCHRONIZED", new CuentaSincronizada());
    }

    static void ejecutarPrueba(String nombre, Cuenta cuenta) throws InterruptedException {
        AtomicInteger operacionesCorrectas = new AtomicInteger();
        AtomicInteger operacionesRechazadas = new AtomicInteger();
        List<Thread> hilos = new ArrayList<>();

        for (int i = 1; i <= CLIENTES; i++) {
            final int idCliente = i;
            Thread hilo = Thread.ofVirtual().name("cliente-" + idCliente).start(() -> {
                for (int j = 0; j < OPERACIONES_POR_CLIENTE; j++) {
                    int cantidad;
                    boolean esRetirada = ThreadLocalRandom.current().nextInt(100) < 60;

                    if (esRetirada) {
                        cantidad = ThreadLocalRandom.current().nextInt(1, 101);
                        if (cuenta.retirar(cantidad)) {
                            operacionesCorrectas.incrementAndGet();
                        } else {
                            operacionesRechazadas.incrementAndGet();
                        }
                    } else {
                        cantidad = ThreadLocalRandom.current().nextInt(1, 51);
                        cuenta.ingresar(cantidad);
                        operacionesCorrectas.incrementAndGet();
                    }

                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            });
            hilos.add(hilo);
        }

        for (Thread hilo : hilos) {
            hilo.join();
        }

        System.out.println("--- " + nombre + " ---");
        System.out.println("Saldo final: " + cuenta.consultarSaldo() + " euros");
        System.out.println("Operaciones correctas: " + operacionesCorrectas.get());
        System.out.println("Operaciones rechazadas: " + operacionesRechazadas.get());
        System.out.println("Nunca debe haber saldo negativo.\n");
    }
}
