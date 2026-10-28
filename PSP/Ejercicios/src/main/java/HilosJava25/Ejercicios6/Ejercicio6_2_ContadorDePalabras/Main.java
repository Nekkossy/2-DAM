package main.java.HilosJava25.Ejercicios6.Ejercicio6_2_ContadorDePalabras;

import java.util.concurrent.ConcurrentHashMap;

public class Main {

    void main(String[] args) {

        ConcurrentHashMap<String, Integer> palabras = new ConcurrentHashMap<>();

        String texto1 = "java es un lenguaje java es potente";
        String texto2 = "java permite trabajar con hilos";
        String texto3 = "los hilos permiten trabajar en paralelo";

        Thread hilo1 = new Thread(() -> contarPalabras(texto1, palabras));
        Thread hilo2 = new Thread(() -> contarPalabras(texto2, palabras));
        Thread hilo3 = new Thread(() -> contarPalabras(texto3, palabras));

        hilo1.start();
        hilo2.start();
        hilo3.start();

        try {
            hilo1.join();
            hilo2.join();
            hilo3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Frecuencia de palabras:");

        for (String palabra : palabras.keySet()) {
            System.out.println(palabra + ": " + palabras.get(palabra));
        }
    }

    void contarPalabras(String texto, ConcurrentHashMap<String, Integer> palabras) {

        String[] lista = texto.split(" ");

        for (String palabra : lista) {
            palabras.merge(palabra, 1, Integer::sum);
        }
    }
}
