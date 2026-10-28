package org.example;

import javax.swing.*;
import java.awt.*;

public class Calculadora extends JFrame {

    private JTextField pantalla;

    private final Color fondo = new Color(18, 15, 20);
    private final Color negro = new Color(32, 27, 35);
    private final Color rosa = new Color(236, 110, 165);
    private final Color rosaClaro = new Color(255, 145, 190);
    private final Color rosaSuave = new Color(255, 205, 225);
    private final Color rosaOscuro = new Color(105, 48, 75);
    private final Color textoClaro = new Color(255, 240, 247);

    public Calculadora() {
        setTitle("CALCULADORA");
        setSize(500, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));
        panelPrincipal.setBackground(fondo);
        panelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        );

        pantalla = new JTextField();
        pantalla.setFont(new Font("Arial", Font.PLAIN, 30));
        pantalla.setForeground(rosaSuave);
        pantalla.setBackground(negro);
        pantalla.setHorizontalAlignment(JTextField.RIGHT);
        pantalla.setEditable(false);
        pantalla.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(rosa, 3),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)
                )
        );

        JButton igual = new JButton("=");
        igual.setFont(new Font("Arial", Font.BOLD, 25));
        igual.setForeground(Color.WHITE);
        igual.setBackground(rosa);
        igual.setFocusPainted(false);
        igual.setOpaque(true);
        igual.setBorder(
                BorderFactory.createLineBorder(rosaClaro, 2)
        );
        igual.setCursor(new Cursor(Cursor.HAND_CURSOR));
        igual.setPreferredSize(new Dimension(95, 55));

        igual.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                igual.setBackground(rosaClaro);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                igual.setBackground(rosa);
            }
        });

        JPanel panelPantalla = new JPanel(new BorderLayout(10, 0));
        panelPantalla.setBackground(fondo);
        panelPantalla.add(pantalla, BorderLayout.CENTER);
        panelPantalla.add(igual, BorderLayout.EAST);

        panelPrincipal.add(panelPantalla, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new GridLayout(6, 4, 10, 10));
        panelBotones.setBackground(fondo);

        String[] textos = {
                "C", "()", "%", "DEL",
                "7", "8", "9", "÷",
                "4", "5", "6", "×",
                "1", "2", "3", "−",
                "+/−", "0", ",", "+",
                "", "", "", ""
        };

        for (String texto : textos) {
            JButton boton = new JButton(texto);
            boton.setFont(new Font("Arial", Font.BOLD, 23));
            boton.setForeground(textoClaro);
            boton.setBackground(negro);
            boton.setFocusPainted(false);
            boton.setOpaque(true);
            boton.setBorder(
                    BorderFactory.createLineBorder(rosaOscuro, 2)
            );
            boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            if (texto.isEmpty()) {
                boton.setEnabled(false);
                boton.setVisible(false);
            } else if (texto.equals("C") || texto.equals("DEL")) {
                boton.setBackground(rosaOscuro);
                boton.setForeground(Color.WHITE);
                boton.setBorder(
                        BorderFactory.createLineBorder(rosa, 2)
                );
            } else if ("÷×−+".contains(texto) && texto.length() == 1) {
                boton.setBackground(new Color(65, 43, 57));
                boton.setForeground(rosaClaro);
                boton.setBorder(
                        BorderFactory.createLineBorder(rosa, 2)
                );
            }

            Color colorOriginal = boton.getBackground();
            Color textoOriginal = boton.getForeground();

            boton.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    if (boton.isEnabled()) {
                        boton.setBackground(rosa);
                        boton.setForeground(Color.WHITE);
                        boton.setBorder(
                                BorderFactory.createLineBorder(rosaSuave, 2)
                        );
                    }
                }

                public void mouseExited(java.awt.event.MouseEvent e) {
                    boton.setBackground(colorOriginal);
                    boton.setForeground(textoOriginal);

                    if (texto.equals("C") || texto.equals("DEL")
                            || ("÷×−+".contains(texto)
                            && texto.length() == 1)) {
                        boton.setBorder(
                                BorderFactory.createLineBorder(rosa, 2)
                        );
                    } else {
                        boton.setBorder(
                                BorderFactory.createLineBorder(rosaOscuro, 2)
                        );
                    }
                }
            });

            boton.addActionListener(e -> gestionarBoton(texto));
            panelBotones.add(boton);
        }

        igual.addActionListener(e -> calcularResultado());

        panelPrincipal.add(panelBotones, BorderLayout.CENTER);

        add(panelPrincipal);
    }

    private void gestionarBoton(String texto) {
        String contenido = pantalla.getText();

        if (texto.equals("C")) {
            pantalla.setText("");
        } else if (texto.equals("DEL")) {
            if (!contenido.isEmpty()) {
                pantalla.setText(
                        contenido.substring(0, contenido.length() - 1)
                );
            }
        } else if (texto.equals("()")) {
            int abiertos = 0;
            int cerrados = 0;

            for (char c : contenido.toCharArray()) {
                if (c == '(') abiertos++;
                if (c == ')') cerrados++;
            }

            if (contenido.isEmpty()
                    || "+−×÷(".contains(
                    contenido.substring(contenido.length() - 1))) {
                pantalla.setText(contenido + "(");
            } else if (abiertos > cerrados) {
                pantalla.setText(contenido + ")");
            } else {
                pantalla.setText(contenido + "×(");
            }
        } else if (texto.equals("+/−")) {
            cambiarSigno();
        } else if (texto.equals(",")) {
            if (contenido.isEmpty()
                    || "+−×÷(".contains(
                    contenido.substring(contenido.length() - 1))) {
                pantalla.setText(contenido + "0,");
            } else {
                int inicio = contenido.length() - 1;

                while (inicio >= 0
                        && (Character.isDigit(contenido.charAt(inicio))
                        || contenido.charAt(inicio) == ',')) {
                    inicio--;
                }

                String numero = contenido.substring(inicio + 1);

                if (!numero.contains(",")) {
                    pantalla.setText(contenido + ",");
                }
            }
        } else if (texto.matches("[0-9]")) {
            pantalla.setText(contenido + texto);
        } else if (texto.equals("%")) {
            if (!contenido.isEmpty()
                    && Character.isDigit(
                    contenido.charAt(contenido.length() - 1))) {
                pantalla.setText(contenido + "%");
            }
        } else if ("+−×÷".contains(texto)) {
            if (contenido.isEmpty()) {
                if (texto.equals("−")) {
                    pantalla.setText("−");
                }
            } else {
                char ultimo = contenido.charAt(contenido.length() - 1);

                if ("+−×÷".indexOf(ultimo) >= 0) {
                    pantalla.setText(
                            contenido.substring(0, contenido.length() - 1)
                                    + texto
                    );
                } else if (ultimo != '(' && ultimo != ',') {
                    pantalla.setText(contenido + texto);
                }
            }
        }
    }

    private void cambiarSigno() {
        String contenido = pantalla.getText();

        if (contenido.isEmpty()) {
            pantalla.setText("−");
            return;
        }

        int inicio = contenido.length() - 1;

        while (inicio >= 0
                && (Character.isDigit(contenido.charAt(inicio))
                || contenido.charAt(inicio) == ',')) {
            inicio--;
        }

        String numero = contenido.substring(inicio + 1);

        if (numero.isEmpty()) {
            if (contenido.endsWith("−")) {
                pantalla.setText(
                        contenido.substring(0, contenido.length() - 1)
                );
            } else {
                pantalla.setText(contenido + "−");
            }
        } else if (inicio >= 0 && contenido.charAt(inicio) == '−'
                && (inicio == 0
                || "+−×÷(".indexOf(contenido.charAt(inicio - 1)) >= 0)) {
            pantalla.setText(
                    contenido.substring(0, inicio) + numero
            );
        } else {
            pantalla.setText(
                    contenido.substring(0, inicio + 1) + "−" + numero
            );
        }
    }

    private void calcularResultado() {
        String expresion = pantalla.getText();

        if (expresion.isEmpty()) {
            return;
        }

        try {
            Parser parser = new Parser(expresion);
            double resultado = parser.calcular();

            if (!Double.isFinite(resultado)) {
                throw new ArithmeticException();
            }

            if (resultado == Math.rint(resultado)
                    && Math.abs(resultado) < 1e15) {
                pantalla.setText(String.valueOf((long) resultado));
            } else {
                pantalla.setText(
                        String.valueOf(resultado).replace('.', ',')
                );
            }
        } catch (Exception e) {
            pantalla.setText("Error");
        }
    }

    private class Parser {

        private final String expresion;
        private int posicion = 0;

        public Parser(String expresion) {
            this.expresion = expresion
                    .replace("×", "*")
                    .replace("÷", "/")
                    .replace("−", "-")
                    .replace(",", ".");
        }

        public double calcular() {
            double resultado = expresion();

            if (posicion < expresion.length()) {
                throw new IllegalArgumentException();
            }

            return resultado;
        }

        private double expresion() {
            double resultado = termino();

            while (posicion < expresion.length()) {
                char operador = expresion.charAt(posicion);

                if (operador == '+') {
                    posicion++;
                    resultado += termino();
                } else if (operador == '-') {
                    posicion++;
                    resultado -= termino();
                } else {
                    break;
                }
            }

            return resultado;
        }

        private double termino() {
            double resultado = factor();

            while (posicion < expresion.length()) {
                char operador = expresion.charAt(posicion);

                if (operador == '*') {
                    posicion++;
                    resultado *= factor();
                } else if (operador == '/') {
                    posicion++;
                    double divisor = factor();

                    if (divisor == 0) {
                        throw new ArithmeticException();
                    }

                    resultado /= divisor;
                } else {
                    break;
                }
            }

            return resultado;
        }

        private double factor() {
            if (posicion < expresion.length()
                    && expresion.charAt(posicion) == '-') {
                posicion++;
                return -factor();
            }

            if (posicion < expresion.length()
                    && expresion.charAt(posicion) == '+') {
                posicion++;
                return factor();
            }

            double resultado;

            if (posicion < expresion.length()
                    && expresion.charAt(posicion) == '(') {
                posicion++;
                resultado = expresion();

                if (posicion >= expresion.length()
                        || expresion.charAt(posicion) != ')') {
                    throw new IllegalArgumentException();
                }

                posicion++;
            } else {
                resultado = numero();
            }

            if (posicion < expresion.length()
                    && expresion.charAt(posicion) == '%') {
                posicion++;
                resultado /= 100;
            }

            return resultado;
        }

        private double numero() {
            int inicio = posicion;

            while (posicion < expresion.length()
                    && (Character.isDigit(expresion.charAt(posicion))
                    || expresion.charAt(posicion) == '.')) {
                posicion++;
            }

            if (inicio == posicion) {
                throw new IllegalArgumentException();
            }

            return Double.parseDouble(
                    expresion.substring(inicio, posicion)
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Calculadora calculadora = new Calculadora();
            calculadora.setVisible(true);
        });
    }
}