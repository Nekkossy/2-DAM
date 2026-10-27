package org.example;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class PruebaQDate extends JFrame {

    private QDate fechaInicio;
    private QDate fechaFin;
    private JLabel resultado;

    private final Color fondo = new Color(255, 241, 247);
    private final Color rosa = new Color(224, 126, 164);
    private final Color rosaClaro = new Color(255, 235, 243);
    private final Color morado = new Color(104, 72, 110);
    private final Color blanco = Color.WHITE;

    public PruebaQDate() {
        setTitle("QDate | Selector de fechas");
        setSize(540, 440);
        setMinimumSize(new Dimension(500, 550));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel principal = new JPanel(new BorderLayout(18, 18));
        principal.setBackground(fondo);
        principal.setBorder(new EmptyBorder(25, 28, 25, 28));

        JLabel titulo = new JLabel("Comparador de fechas", JLabel.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titulo.setForeground(morado);

        JLabel subtitulo = new JLabel(
                "Selecciona las fechas que quieres comparar",
                JLabel.CENTER
        );
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitulo.setForeground(new Color(140, 104, 143));

        JPanel cabecera = new JPanel(new GridLayout(2, 1, 0, 5));
        cabecera.setOpaque(false);
        cabecera.add(titulo);
        cabecera.add(subtitulo);

        fechaInicio = new QDate();
        fechaFin = new QDate();

        JPanel tarjetaInicio = crearTarjeta("Fecha de inicio", fechaInicio);
        JPanel tarjetaFin = crearTarjeta("Fecha de fin", fechaFin);

        JPanel panelFechas = new JPanel(new GridLayout(2, 1, 0, 12));
        panelFechas.setOpaque(false);
        panelFechas.add(tarjetaInicio);
        panelFechas.add(tarjetaFin);

        JButton comprobar = new JButton("Comprobar fechas");
        comprobar.setFont(new Font("SansSerif", Font.BOLD, 14));
        comprobar.setForeground(blanco);
        comprobar.setBackground(rosa);
        comprobar.setFocusPainted(false);
        comprobar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        comprobar.setBorder(new CompoundBorder(
                new LineBorder(rosa, 1, true),
                new EmptyBorder(10, 18, 10, 18)
        ));
        comprobar.setOpaque(true);
        comprobar.addActionListener(e -> comprobarFechas());

        resultado = new JLabel(
                "Selecciona las dos fechas",
                JLabel.CENTER
        );
        resultado.setFont(new Font("SansSerif", Font.BOLD, 13));
        resultado.setForeground(morado);
        resultado.setBorder(new EmptyBorder(8, 5, 0, 5));

        JPanel inferior = new JPanel(new BorderLayout(8, 8));
        inferior.setOpaque(false);
        inferior.add(comprobar, BorderLayout.CENTER);
        inferior.add(resultado, BorderLayout.SOUTH);

        principal.add(cabecera, BorderLayout.NORTH);
        principal.add(panelFechas, BorderLayout.CENTER);
        principal.add(inferior, BorderLayout.SOUTH);

        setContentPane(principal);
    }

    private JPanel crearTarjeta(String titulo, QDate selector) {
        JPanel tarjeta = new JPanel(new BorderLayout(8, 8));
        tarjeta.setBackground(blanco);
        tarjeta.setBorder(new CompoundBorder(
                new LineBorder(new Color(246, 202, 220), 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setForeground(rosa);
        etiqueta.setFont(new Font("SansSerif", Font.BOLD, 14));

        tarjeta.add(etiqueta, BorderLayout.NORTH);
        tarjeta.add(selector, BorderLayout.CENTER);

        return tarjeta;
    }

    private void comprobarFechas() {
        try {
            Date inicio = fechaInicio.getDate();
            Date fin = fechaFin.getDate();

            LocalDate fecha1 = inicio.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            LocalDate fecha2 = fin.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            if (fecha1.isAfter(fecha2)) {
                resultado.setForeground(new Color(185, 60, 100));
                resultado.setText(
                        "Error: la fecha de inicio es posterior a la fecha de fin"
                );
            } else {
                long diferencia = ChronoUnit.DAYS.between(fecha1, fecha2);

                resultado.setForeground(morado);
                resultado.setText(
                        "Diferencia: " + diferencia +
                                (diferencia == 1 ? " día" : " días")
                );
            }

        } catch (QDateException e) {
            resultado.setForeground(new Color(185, 60, 100));
            resultado.setText("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PruebaQDate ventana = new PruebaQDate();
            ventana.setVisible(true);
        });
    }
}
