public class Ejercicio2_1 extends Thread {
    @Override
    public void run(){
        for (int i = 10; i > 0; i--) {
            System.out.println(i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

void main(String[] args){
    Ejercicio2_1 cohete = new Ejercicio2_1();
    cohete.start();
    try {
        cohete.join();
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
    System.out.println("¡Despegue!");
}
