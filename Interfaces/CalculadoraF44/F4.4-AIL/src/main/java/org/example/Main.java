package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        Calculadora calculadora = new Calculadora();

        Scene scene = new Scene(calculadora, 500, 680);

        scene.getStylesheets().add(
                getClass().getResource("/estilos.css").toExternalForm()
        );

        stage.setTitle("CALCULADORA");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}