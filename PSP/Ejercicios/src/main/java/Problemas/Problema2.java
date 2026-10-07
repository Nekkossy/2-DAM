package main.java.Problemas;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Problema2 {

    void main(String[] args) throws Exception {

        System.out.println("=== BANCO VIRTUAL ===");
        System.out.println("Saldo inicial: 10000.00€");
        System.out.println("50 clientes realizando 500 operaciones totales...");

        // REENTRANTLOCK
        CuentaReentrantLock cuenta1 = new CuentaReentrantLock(10000);

        long inicio = System.currentTimeMillis();

        Thread[] clientes1 = crearClientes(cuenta1);
        iniciarYEsperar(clientes1);

        long fin = System.currentTimeMillis();

        System.out.println();
        System.out.println("--- CON REENTRANTLOCK ---");
        System.out.println("Saldo final: " + cuenta1.consultarSaldo() + "€");
        System.out.println("Operaciones registradas: " + cuenta1.obtenerHistorial().size());
        System.out.println("Tiempo total: " + (fin - inicio) + "ms");


        // SYNCHRONIZED
        CuentaSynchronized cuenta2 = new CuentaSynchronized(10000);

        inicio = System.currentTimeMillis();

        Thread[] clientes2 = crearClientes(cuenta2);
        iniciarYEsperar(clientes2);

        fin = System.currentTimeMillis();

        System.out.println();
        System.out.println("--- CON SYNCHRONIZED ---");
        System.out.println("Saldo final: " + cuenta2.consultarSaldo() + "€");
        System.out.println("Operaciones registradas: " + cuenta2.obtenerHistorial().size());
        System.out.println("Tiempo total: " + (fin - inicio) + "ms");
    }


    Thread[] crearClientes(OperacionBancaria cuenta) {

        Thread[] clientes = new Thread[50];

        for (int i = 0; i < 50; i++) {

            clientes[i] = new Thread(() -> {

                for (int j = 0; j < 10; j++) {

                    try {
                        Thread.sleep((long) (Math.random() * 201) + 100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    if (Math.random() < 0.6) {

                        double cantidad = (int) (Math.random() * 100) + 1;

                        cuenta.retirar(cantidad);

                    } else {

                        double cantidad = (int) (Math.random() * 50) + 1;

                        cuenta.ingresar(cantidad);
                    }
                }
            });

        }

        return clientes;
    }


    void iniciarYEsperar(Thread[] clientes) throws Exception {

        for (Thread cliente : clientes) {
            cliente.start();
        }

        for (Thread cliente : clientes) {
            cliente.join();
        }
    }
}


interface OperacionBancaria {

    boolean retirar(double cantidad);

    void ingresar(double cantidad);

    double consultarSaldo();

    List<String> obtenerHistorial();
}


class CuentaReentrantLock implements OperacionBancaria {

    private double saldo;

    private final ReentrantLock lock = new ReentrantLock();

    private List<String> historial = new ArrayList<>();


    CuentaReentrantLock(double saldoInicial) {
        saldo = saldoInicial;
    }


    public boolean retirar(double cantidad) {

        lock.lock();

        try {

            if (saldo >= cantidad) {

                saldo -= cantidad;

                historial.add(
                        System.currentTimeMillis()
                                + " - Retiro: " + cantidad
                );

                return true;
            }

            historial.add(
                    System.currentTimeMillis()
                            + " - Retiro rechazado: " + cantidad
            );

            return false;

        } finally {
            lock.unlock();
        }
    }


    public void ingresar(double cantidad) {

        lock.lock();

        try {

            saldo += cantidad;

            historial.add(
                    System.currentTimeMillis()
                            + " - Ingreso: " + cantidad
            );

        } finally {
            lock.unlock();
        }
    }


    public double consultarSaldo() {

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


class CuentaSynchronized implements OperacionBancaria {

    private double saldo;

    private List<String> historial = new ArrayList<>();


    CuentaSynchronized(double saldoInicial) {
        saldo = saldoInicial;
    }


    public synchronized boolean retirar(double cantidad) {

        if (saldo >= cantidad) {

            saldo -= cantidad;

            historial.add(
                    System.currentTimeMillis()
                            + " - Retiro: " + cantidad
            );

            return true;
        }

        historial.add(
                System.currentTimeMillis()
                        + " - Retiro rechazado: " + cantidad
        );

        return false;
    }


    public synchronized void ingresar(double cantidad) {

        saldo += cantidad;

        historial.add(
                System.currentTimeMillis()
                        + " - Ingreso: " + cantidad
        );
    }


    public synchronized double consultarSaldo() {
        return saldo;
    }


    public synchronized List<String> obtenerHistorial() {
        return new ArrayList<>(historial);
    }
}