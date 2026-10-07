public class Ejercicio3_2_Deadlock {
    int saldo;
    int id;

    public Ejercicio3_2_Deadlock(int id, int saldo) {
        this.id = id;
        this.saldo = saldo;
    }

    public void transferir(Ejercicio3_2_Deadlock destino, int cantidad) {
        Ejercicio3_2_Deadlock primero;
        Ejercicio3_2_Deadlock segundo;

        if (this.id < destino.id) {
            primero = this;
            segundo = destino;
        } else {
            primero = destino;
            segundo = this;
        }

        synchronized (primero) {

            synchronized (segundo) {

                System.out.println("Transfiriendo de " + id + " a " + destino.id);

                saldo = saldo - cantidad;
                destino.saldo = destino.saldo + cantidad;
            }
        }
    }

    static void main(String[] args) {
        Ejercicio3_2_Deadlock cuentaA =
                new Ejercicio3_2_Deadlock(1, 100);

        Ejercicio3_2_Deadlock cuentaB =
                new Ejercicio3_2_Deadlock(2, 100);

        Thread hilo1 = new Thread(() -> {
            cuentaA.transferir(cuentaB, 10);
        });

        Thread hilo2 = new Thread(() -> {
            cuentaB.transferir(cuentaA, 20);
        });

        hilo1.start();
        hilo2.start();

        System.out.println("Transferencias iniciadas");
    }
}
//