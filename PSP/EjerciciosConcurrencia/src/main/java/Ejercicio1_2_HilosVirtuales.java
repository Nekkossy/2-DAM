public class Ejercicio1_2_HilosVirtuales implements Runnable {
    @Override
    public void run() {
        for (int i = 0; i <=100; i++) {
            System.out.println(Thread.currentThread().getName() + " -> " +i+ "%");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

void main(String[] args) {
    Thread[] hilos = new Thread[10000];

    for (int i = 0; i < 10000; i++) {
        hilos[i] = Thread.ofVirtual().name("descarga-" + i+1).start(new Ejercicio1_2_HilosVirtuales());
    }
    for (Thread hilo : hilos) {
        try {
            hilo.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

//