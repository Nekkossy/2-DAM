public class Ejercicio2_2_Cocinero implements Runnable {
    @Override
    public void run() {
        System.out.println("Estado del cocinero: " + Thread.currentThread().getState());
        for (int i = 0; i <= 10; i++) {
            System.out.println("Cocinero cocinando..." + i + "´");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Cocción del cocinero interrumpida");
            }
        }
    }
}

void main(String[] args) {
    Thread cocinero = new Thread(new Ejercicio2_2_Cocinero());
    try {
        System.out.println("Estado inicial del cocinero: " + cocinero.getState());
        cocinero.start();
        Thread.sleep(3000);
        System.out.println("Estado ahora del cocinero: " + cocinero.getState());
        Thread.sleep(3000);
        cocinero.interrupt();
        cocinero.join();
        System.out.println("Estado final del cocinero: " + cocinero.getState());
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
}
