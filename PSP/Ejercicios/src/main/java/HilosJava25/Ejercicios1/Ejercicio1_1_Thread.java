public class Ejercicio1_1 extends Thread {
    @Override
    public void run(){
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
    Ejercicio1_1 c1 = new Ejercicio1_1();
    Ejercicio1_1 c2 = new Ejercicio1_1();
    Ejercicio1_1 c3 = new Ejercicio1_1();
    Ejercicio1_1 c4 = new Ejercicio1_1();

    c1.setName("descarga-1");
    c2.setName("descarga-2");
    c3.setName("descarga-3");
    c4.setName("descarga-4");

    c1.start();
    c2.start();
    c3.start();
    c4.start();

    try {
        c1.join();
        c2.join();
        c3.join();
        c4.join();
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
}
//