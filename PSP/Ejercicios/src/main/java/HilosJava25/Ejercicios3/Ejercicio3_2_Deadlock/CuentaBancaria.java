package main.java.HilosJava25.Ejercicios3.Ejercicio3_2_Deadlock;

public class CuentaBancaria {

    private int saldo;
    private int id;

    public CuentaBancaria(int id, int saldo) {
        this.id = id;
        this.saldo = saldo;
    }

    public void transferir(CuentaBancaria destino, int cantidad) {

        CuentaBancaria primero;
        CuentaBancaria segundo;

        if (this.id < destino.id) {
            primero = this;
            segundo = destino;
        } else {
            primero = destino;
            segundo = this;
        }

        synchronized (primero) {

            synchronized (segundo) {

                System.out.println("Transfiriendo de "
                        + this.id + " a " + destino.id);

                this.saldo -= cantidad;
                destino.saldo += cantidad;
            }
        }
    }

    public int getSaldo() {
        return saldo;
    }

    public int getId() {
        return id;
    }
}