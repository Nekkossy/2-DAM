package apuntes.hundirlaflota.modelo;

import java.util.Random;

public class Partida {

    private boolean[][] barcos;
    private char[][] tablero;

    private int disparosRestantes;
    private int barcosHundidos;
    private boolean terminada;
    private boolean ganada;

    public Partida() {

        barcos = new boolean[5][5];
        tablero = new char[5][5];

        disparosRestantes = 10;
        barcosHundidos = 0;
        terminada = false;
        ganada = false;

        // Inicializamos el tablero con casillas desconocidas
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                tablero[i][j] = '?';
            }
        }

        // Colocamos 3 barcos en posiciones aleatorias
        Random random = new Random();

        int colocados = 0;

        while (colocados < 3) {

            int fila = random.nextInt(5);
            int columna = random.nextInt(5);

            if (!barcos[fila][columna]) {
                barcos[fila][columna] = true;
                colocados++;
            }
        }
    }

    public String disparar(int fila, int columna) {

        if (tablero[fila][columna] != '?') {
            return "REPETIDO";
        }

        disparosRestantes--;

        if (barcos[fila][columna]) {

            tablero[fila][columna] = 'X';
            barcosHundidos++;

            if (barcosHundidos == 3) {
                terminada = true;
                ganada = true;
                return "VICTORIA";
            }

            if (disparosRestantes == 0) {
                terminada = true;
                ganada = false;
                return "DERROTA";
            }

            return "TOCADO";

        } else {

            tablero[fila][columna] = 'A';

            if (disparosRestantes == 0) {
                terminada = true;
                ganada = false;
                return "DERROTA";
            }

            return "AGUA";
        }
    }

    public char[][] getTablero() {
        return tablero;
    }

    public int getDisparosRestantes() {
        return disparosRestantes;
    }

    public int getBarcosHundidos() {
        return barcosHundidos;
    }

    public int getBarcosRestantes() {
        return 3 - barcosHundidos;
    }

    public boolean isTerminada() {
        return terminada;
    }

    public boolean isGanada() {
        return ganada;
    }
}
