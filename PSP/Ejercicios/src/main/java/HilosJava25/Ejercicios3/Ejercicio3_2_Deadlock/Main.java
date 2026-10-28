package main.java.HilosJava25.Ejercicios3.Ejercicio3_2_Deadlock;

public class Main {

    public static void main(String[] args) {

        CuentaBancaria cuentaA = new CuentaBancaria(1, 1000);
        CuentaBancaria cuentaB = new CuentaBancaria(2, 1000);

        Thread hilo1 = new Thread(() -> {
            cuentaA.transferir(cuentaB, 100);
        });

        Thread hilo2 = new Thread(() -> {
            cuentaB.transferir(cuentaA, 100);
        });

        hilo1.start();
        hilo2.start();
    }
}