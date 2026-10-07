public class Ejercicio1_1_2 implements Runnable{
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
    Ejercicio1_1_2 c1 = new Ejercicio1_1_2();
    Ejercicio1_1_2 c2 = new Ejercicio1_1_2();
    Ejercicio1_1_2 c3 = new Ejercicio1_1_2();
    Ejercicio1_1_2 c4 = new Ejercicio1_1_2();

    Thread t1 = new Thread(c1);
    Thread t2 = new Thread(c2);
    Thread t3 = new Thread(c3);
    Thread t4 = new Thread(c4);

    t1.setName("descarga-1");
    t2.setName("descarga-2");
    t3.setName("descarga-3");
    t4.setName("descarga-4");

    t1.start();
    t2.start();
    t3.start();
    t4.start();

    try {
        t1.join();
        t2.join();
        t3.join();
        t4.join();
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
}


//  Con Thread, la clase hereda directamente de Thread y podemos
//  usar start() para iniciar el hilo.
//
//  Con Runnable, la clase solo define lo que tiene que hacer el hilo
//  y después creamos un Thread para ejecutarlo.
//
//  Runnable es más flexible porque una clase puede implementar
//  Runnable y además heredar de otra clase.
//