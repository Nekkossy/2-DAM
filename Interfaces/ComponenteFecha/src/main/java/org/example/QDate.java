package org.example;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.Date;

public class QDate extends JPanel {

    private JComboBox<Integer> campoDia;
    private JComboBox<String> campoMes;
    private JTextField campoAnio;

    private final Color rosa = new Color(255, 235, 243);
    private final Color rosaFuerte = new Color(224, 126, 164);
    private final Color morado = new Color(104, 72, 110);

    public QDate() {
        setLayout(new GridLayout(2, 3, 10, 7));
        setBackground(rosa);
        setOpaque(true);

        campoDia = new JComboBox<>();
        campoMes = new JComboBox<>();
        campoAnio = new JTextField();

        JLabel etiquetaDia = new JLabel("Día");
        JLabel etiquetaMes = new JLabel("Mes");
        JLabel etiquetaAnio = new JLabel("Año");

        JLabel[] etiquetas = {etiquetaDia, etiquetaMes, etiquetaAnio};

        for (JLabel etiqueta : etiquetas) {
            etiqueta.setForeground(morado);
            etiqueta.setFont(new Font("SansSerif", Font.BOLD, 13));
        }

        add(etiquetaDia);
        add(etiquetaMes);
        add(etiquetaAnio);

        add(campoDia);
        add(campoMes);
        add(campoAnio);

        for (int dia = 1; dia <= 31; dia++) {
            campoDia.addItem(dia);
        }

        String[] meses = {
                "Enero", "Febrero", "Marzo", "Abril",
                "Mayo", "Junio", "Julio", "Agosto",
                "Septiembre", "Octubre", "Noviembre", "Diciembre"
        };

        for (String mes : meses) {
            campoMes.addItem(mes);
        }

        campoDia.setSelectedItem(1);
        campoMes.setSelectedIndex(0);
        campoAnio.setText("1900");

        JComboBox<?>[] selectores = {campoDia, campoMes};

        for (JComboBox<?> selector : selectores) {
            selector.setBackground(Color.WHITE);
            selector.setForeground(morado);
            selector.setFont(new Font("SansSerif", Font.PLAIN, 13));
            selector.setBorder(new LineBorder(rosaFuerte, 1, true));
        }

        campoAnio.setBackground(Color.WHITE);
        campoAnio.setForeground(morado);
        campoAnio.setCaretColor(rosaFuerte);
        campoAnio.setFont(new Font("SansSerif", Font.PLAIN, 13));
        campoAnio.setHorizontalAlignment(JTextField.CENTER);
        campoAnio.setBorder(new CompoundBorder(
                new LineBorder(rosaFuerte, 1, true),
                new EmptyBorder(4, 6, 4, 6)
        ));

        try {
            setDate(1900, 1, 1);
        } catch (QDateException e) {
            throw new RuntimeException(e);
        }
    }

    private LocalDate validarFecha() throws QDateException {

        if (campoDia.getSelectedItem() == null ||
                campoMes.getSelectedItem() == null ||
                campoAnio.getText().trim().isEmpty()) {
            throw new QDateException("La fecha está incompleta");
        }

        int dia = (int) campoDia.getSelectedItem();
        int mes = campoMes.getSelectedIndex() + 1;
        int anio;

        try {
            anio = Integer.parseInt(campoAnio.getText().trim());
        } catch (NumberFormatException e) {
            throw new QDateException("El año debe ser un número");
        }

        if (anio <= 0) {
            throw new QDateException("El año debe ser mayor que cero");
        }

        try {
            return LocalDate.of(anio, mes, dia);
        } catch (DateTimeException e) {
            throw new QDateException(
                    "La fecha no existe en el calendario"
            );
        }
    }

    public Date getDate() throws QDateException {
        LocalDate fecha = validarFecha();

        return Date.from(
                fecha.atStartOfDay(ZoneId.systemDefault()).toInstant()
        );
    }

    public void setDate(int year, int month, int day)
            throws QDateException {

        try {
            LocalDate fecha = LocalDate.of(year, month, day);

            campoDia.setSelectedItem(fecha.getDayOfMonth());
            campoMes.setSelectedIndex(fecha.getMonthValue() - 1);
            campoAnio.setText(String.valueOf(fecha.getYear()));

        } catch (DateTimeException e) {
            throw new QDateException("La fecha no es válida");
        }
    }
}
