package HilosClaudia;

public class Ejercicio1 {
    static void main(String[] args) throws InterruptedException {
        Thread hilo1 = new Thread(() -> {
            for (int i = 1; i < 6; i++) {
                System.out.println(i);
                try {
                    Thread.sleep(300);
                    System.out.println("Mi nombre es: " + Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "Hilo-A");

        Thread hilo2 = new Thread(() -> {
            for (int i = 1; i < 6; i++) {
                System.out.println(i);
                try {
                    Thread.sleep(300);
                    System.out.println("Mi nombre es: " + Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "Hilo-B");

        hilo1.start();
        hilo2.start();
        hilo1.join();
        hilo2.join();

        System.out.println("Fin");
    }
}
