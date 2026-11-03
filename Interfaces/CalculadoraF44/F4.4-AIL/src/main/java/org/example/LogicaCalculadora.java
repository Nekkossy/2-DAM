package org.example;

public class LogicaCalculadora {

    private String expresion;
    private int posicion;
    private double numero;

    public double calcular(String expresion) {
        this.expresion = expresion.replace(",", ".")
                .replace("×", "*")
                .replace("÷", "/")
                .replace("−", "-");

        posicion = -1;
        siguienteCaracter();

        double resultado = expresion();

        if (posicion < this.expresion.length()) {
            throw new RuntimeException("Expresión incorrecta");
        }

        return resultado;
    }

    private void siguienteCaracter() {
        numero = (++posicion < expresion.length())
                ? expresion.charAt(posicion) : -1;
    }

    private boolean aceptar(int caracter) {
        while (numero == ' ') {
            siguienteCaracter();
        }

        if (numero == caracter) {
            siguienteCaracter();
            return true;
        }

        return false;
    }

    private double expresion() {
        double resultado = termino();

        while (true) {
            if (aceptar('+')) {
                resultado += termino();
            } else if (aceptar('-')) {
                resultado -= termino();
            } else {
                return resultado;
            }
        }
    }

    private double termino() {
        double resultado = factor();

        while (true) {
            if (aceptar('*')) {
                resultado *= factor();
            } else if (aceptar('/')) {
                double divisor = factor();

                if (divisor == 0) {
                    throw new ArithmeticException("No se puede dividir entre cero");
                }

                resultado /= divisor;
            } else {
                return resultado;
            }
        }
    }

    private double factor() {

        if (aceptar('+')) {
            return factor();
        }

        if (aceptar('-')) {
            return -factor();
        }

        double resultado;
        int inicio = posicion;

        if (aceptar('(')) {
            resultado = expresion();

            if (!aceptar(')')) {
                throw new RuntimeException("Falta un paréntesis");
            }

        } else if ((numero >= '0' && numero <= '9') || numero == '.') {

            while ((numero >= '0' && numero <= '9') || numero == '.') {
                siguienteCaracter();
            }

            resultado = Double.parseDouble(
                    expresion.substring(inicio, posicion)
            );

        } else {
            throw new RuntimeException("Número incorrecto");
        }

        while (aceptar('%')) {
            resultado /= 100;
        }

        return resultado;
    }
}