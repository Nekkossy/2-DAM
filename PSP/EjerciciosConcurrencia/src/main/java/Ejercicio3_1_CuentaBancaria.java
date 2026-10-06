public class Ejercicio3_1_CuentaBancaria implements Runnable {
    int saldo = 0;
    @Override
    public void run() {

    }
    public void depositar(int cantidad) {
        saldo = saldo + cantidad;
    }
}

void main(String[] args) {
    Ejercicio3_1_CuentaBancaria cuentaBancaria = new Ejercicio3_1_CuentaBancaria();

    Thread[] hilo = new Thread[100];

    for (int i = 0; i < 100; i++) {

        hilo[i] = new Thread(() -> {
            cuentaBancaria.depositar(1);
        });

        hilo[i].start();

    }
    System.out.println("Saldo atual: " + cuentaBancaria.saldo);
}
