package HilosClaudia;

import java.util.ArrayList;
import java.util.List;

public class DiezMilVirtuales {

    static void main(String[] args) throws InterruptedException {
        long inicio = System.currentTimeMillis();
        List<Thread> hilos = new ArrayList<>();

        for (int i = 0; i < 10_000; i++) {
            Thread h = Thread.ofPlatform().start(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            hilos.add(h);
        }

        for (Thread h : hilos) {
            h.join();
        }

        System.out.println("10000 hilos virtuales tardaron "
                + (System.currentTimeMillis() - inicio) + " ms");
    }
}