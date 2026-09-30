public class Ejercicio1_1 extends Thread {
    @Override
    public void run(){
        for (int i = 0; i <=100; i++) {
            System.out.println(getName() + " -> " +i+ "%");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

void main(String[] args) throws InterruptedException {
    Ejercicio1_1 c1 = new Ejercicio1_1();
    c1.start();
    c1.join();
    Ejercicio1_1 c2 = new Ejercicio1_1();
    Ejercicio1_1 c3 = new Ejercicio1_1();
    Ejercicio1_1 c4 = new Ejercicio1_1();
    c2.start();
    c3.start();
    c4.start();
}
