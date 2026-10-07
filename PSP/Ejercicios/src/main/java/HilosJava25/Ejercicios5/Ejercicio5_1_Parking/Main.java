package main.java.HilosJava25.Ejercicios5.Ejercicio5_1_Parking;

public class Main {

    void main(String[] args) {

        Parking parking = new Parking();

        for (int i = 1; i <= 10; i++) {

            int coche = i;

            Thread hilo = new Thread(() -> {
                parking.entrar(coche);
            });

            hilo.start();
        }
    }
}
