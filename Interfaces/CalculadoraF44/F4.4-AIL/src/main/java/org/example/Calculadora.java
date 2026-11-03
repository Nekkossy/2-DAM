package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Calculadora extends BorderPane {

    private TextField pantalla;
    private LogicaCalculadora logica;
    private boolean resultadoMostrado;

    public Calculadora() {

        logica = new LogicaCalculadora();
        resultadoMostrado = false;

        setPadding(new Insets(30));
        getStyleClass().add("fondo");

        pantalla = new TextField("0");
        pantalla.setEditable(false);
        pantalla.setAlignment(Pos.CENTER_RIGHT);
        pantalla.getStyleClass().add("pantalla");

        Button igual = crearBoton("=", "igual");
        igual.setOnAction(e -> calcular());

        HBox zonaPantalla = new HBox(15, pantalla, igual);
        zonaPantalla.setAlignment(Pos.CENTER);
        zonaPantalla.setPadding(new Insets(20, 0, 30, 0));

        GridPane botones = new GridPane();
        botones.setHgap(12);
        botones.setVgap(12);
        botones.setAlignment(Pos.CENTER);

        String[][] teclas = {
                {"C", "()", "%", "DEL"},
                {"7", "8", "9", "÷"},
                {"4", "5", "6", "×"},
                {"1", "2", "3", "−"},
                {"+/−", "0", ",", "+"}
        };

        for (int fila = 0; fila < teclas.length; fila++) {

            for (int columna = 0; columna < teclas[fila].length; columna++) {

                String texto = teclas[fila][columna];

                String estilo = "numero";

                if (texto.equals("C") || texto.equals("()")
                        || texto.equals("%") || texto.equals("+/−")
                        || texto.equals(",")) {
                    estilo = "especial";
                }

                if (texto.equals("DEL")) {
                    estilo = "borrar";
                }

                if (texto.equals("÷") || texto.equals("×")
                        || texto.equals("−") || texto.equals("+")) {
                    estilo = "operacion";
                }

                Button boton = crearBoton(texto, estilo);

                boton.setOnAction(e -> pulsar(texto));

                botones.add(boton, columna, fila);
            }
        }

        VBox contenido = new VBox(15, zonaPantalla, botones);
        contenido.setAlignment(Pos.CENTER);

        setCenter(contenido);
    }

    private Button crearBoton(String texto, String estilo) {

        Button boton = new Button(texto);

        boton.setPrefSize(85, 78);
        boton.setMinSize(85, 78);
        boton.setMaxSize(85, 78);

        boton.getStyleClass().add(estilo);

        return boton;
    }

    private void pulsar(String texto) {

        String actual = pantalla.getText();

        switch (texto) {

            case "C":
                pantalla.setText("0");
                resultadoMostrado = false;
                break;

            case "DEL":
                if (resultadoMostrado) {
                    pantalla.setText("0");
                    resultadoMostrado = false;
                } else if (actual.length() > 1) {
                    pantalla.setText(actual.substring(0, actual.length() - 1));
                } else {
                    pantalla.setText("0");
                }
                break;

            case "()":
                if (resultadoMostrado) {
                    pantalla.setText("(");
                    resultadoMostrado = false;
                } else {
                    long abiertos = actual.chars().filter(c -> c == '(').count();
                    long cerrados = actual.chars().filter(c -> c == ')').count();

                    if (abiertos > cerrados && actual.matches(".*[0-9)]$")) {
                        pantalla.appendText(")");
                    } else {
                        if (actual.equals("0")) {
                            pantalla.setText("(");
                        } else {
                            pantalla.appendText("(");
                        }
                    }
                }
                break;

            case "+/−":
                cambiarSigno();
                break;

            case ",":
                if (resultadoMostrado) {
                    pantalla.setText("0,");
                    resultadoMostrado = false;
                } else {
                    String[] partes = actual.split("[+−×÷()%()]");

                    String ultimo = partes[partes.length - 1];

                    if (!ultimo.contains(",")) {
                        if (actual.equals("0")) {
                            pantalla.setText("0,");
                        } else if (actual.matches(".*[+−×÷(]$")) {
                            pantalla.appendText("0,");
                        } else {
                            pantalla.appendText(",");
                        }
                    }
                }
                break;

            default:
                if ("0123456789".contains(texto)) {

                    if (resultadoMostrado || actual.equals("0")) {
                        pantalla.setText(texto);
                        resultadoMostrado = false;
                    } else {
                        pantalla.appendText(texto);
                    }

                } else {

                    if (resultadoMostrado) {
                        resultadoMostrado = false;
                    }

                    if (actual.equals("0") && !texto.equals("−")) {
                        if (texto.equals("×") || texto.equals("÷")) {
                            return;
                        }
                        pantalla.setText(texto);
                    } else {
                        pantalla.appendText(texto);
                    }
                }
                break;
        }
    }

    private void cambiarSigno() {

        String actual = pantalla.getText();

        if (actual.equals("0")) {
            return;
        }

        int inicio = actual.length() - 1;

        while (inicio >= 0 &&
                (Character.isDigit(actual.charAt(inicio))
                        || actual.charAt(inicio) == ',')) {
            inicio--;
        }

        if (inicio < actual.length() - 1) {

            String numero = actual.substring(inicio + 1);

            if (inicio >= 0 && actual.charAt(inicio) == '−') {
                pantalla.setText(
                        actual.substring(0, inicio) + numero
                );
            } else {
                pantalla.setText(
                        actual.substring(0, inicio + 1) + "−" + numero
                );
            }
        }
    }

    private void calcular() {

        try {
            String expresion = pantalla.getText();

            double resultado = logica.calcular(expresion);

            if (!Double.isFinite(resultado)) {
                throw new ArithmeticException();
            }

            if (resultado == (long) resultado) {
                pantalla.setText(String.valueOf((long) resultado));
            } else {
                pantalla.setText(
                        String.format(java.util.Locale.US, "%.8f", resultado)
                                .replaceAll("0+$", "")
                                .replaceAll("\\.$", "")
                                .replace(".", ",")
                );
            }

            resultadoMostrado = true;

        } catch (Exception e) {
            pantalla.setText("Error");
            resultadoMostrado = true;
        }
    }
}