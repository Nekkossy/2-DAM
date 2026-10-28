package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Random;

public class AdivinarFecha extends JFrame {

    private JTextField campoDia;
    private JTextField campoMes;
    private JTextField campoAnio;

    private JLabel resultadoDia;
    private JLabel resultadoMes;
    private JLabel resultadoAnio;
    private JLabel contadorIntentos;
    private JLabel mensaje;

    private LocalDate fechaSecreta;
    private int intentos;

    private final Color rosaFondo = new Color(255, 241, 247);
    private final Color rosaClaro = new Color(255, 226, 238);
    private final Color rosaMedio = new Color(238, 139, 177);
    private final Color rosaOscuro = new Color(173, 65, 112);
    private final Color morado = new Color(119, 83, 143);
    private final Color blanco = Color.WHITE;

    private final Color verde = new Color(190, 235, 201);
    private final Color rojo = new Color(255, 193, 201);
    private final Color azul = new Color(190, 219, 250);

    public AdivinarFecha() {

        setTitle("Adivina la fecha");
        setSize(650, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        generarFecha();

        JPanel principal = new JPanel();
        principal.setBackground(rosaFondo);
        principal.setLayout(new BorderLayout(15, 15));
        principal.setBorder(new EmptyBorder(25, 35, 25, 35));

        JPanel cabecera = new JPanel();
        cabecera.setBackground(rosaFondo);
        cabecera.setLayout(new BoxLayout(cabecera, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Adivina la fecha");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 30));
        titulo.setForeground(rosaOscuro);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Descubre el día, el mes y el año");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subtitulo.setForeground(morado);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        cabecera.add(titulo);
        cabecera.add(Box.createVerticalStrut(8));
        cabecera.add(subtitulo);

        JPanel contenido = new JPanel();
        contenido.setBackground(blanco);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel textoFecha = new JLabel("Introduce tu fecha");
        textoFecha.setFont(new Font("SansSerif", Font.BOLD, 19));
        textoFecha.setForeground(rosaOscuro);
        textoFecha.setAlignmentX(Component.CENTER_ALIGNMENT);

        contenido.add(textoFecha);
        contenido.add(Box.createVerticalStrut(20));

        JPanel campos = new JPanel(new GridLayout(1, 3, 12, 0));
        campos.setBackground(blanco);

        campoDia = crearCampo();
        campoMes = crearCampo();
        campoAnio = crearCampo();

        campoDia.setToolTipText("Día: del 1 al 31");
        campoMes.setToolTipText("Mes: del 1 al 12");
        campoAnio.setToolTipText("Año: del 2020 al 2029");

        campoDia.setText("");
        campoMes.setText("");
        campoAnio.setText("");

        campos.add(crearGrupo("Día (1-31)", campoDia));
        campos.add(crearGrupo("Mes (1-12)", campoMes));
        campos.add(crearGrupo("Año (2020-2029)", campoAnio));

        contenido.add(campos);
        contenido.add(Box.createVerticalStrut(22));

        JButton botonComprobar = crearBoton("Comprobar fecha");
        botonComprobar.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonComprobar.addActionListener(e -> comprobarFecha());

        JButton botonNueva = crearBoton("Nueva partida");
        botonNueva.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonNueva.addActionListener(e -> nuevaPartida());

        contenido.add(botonComprobar);
        contenido.add(Box.createVerticalStrut(10));
        contenido.add(botonNueva);
        contenido.add(Box.createVerticalStrut(22));

        JLabel tituloResultados = new JLabel("Resultado de tu intento");
        tituloResultados.setFont(new Font("SansSerif", Font.BOLD, 18));
        tituloResultados.setForeground(rosaOscuro);
        tituloResultados.setAlignmentX(Component.CENTER_ALIGNMENT);

        contenido.add(tituloResultados);
        contenido.add(Box.createVerticalStrut(12));

        JPanel resultados = new JPanel(new GridLayout(1, 3, 10, 0));
        resultados.setBackground(blanco);

        resultadoDia = crearIndicador("Día");
        resultadoMes = crearIndicador("Mes");
        resultadoAnio = crearIndicador("Año");

        resultados.add(resultadoDia);
        resultados.add(resultadoMes);
        resultados.add(resultadoAnio);

        contenido.add(resultados);
        contenido.add(Box.createVerticalStrut(15));

        contadorIntentos = new JLabel("Intentos: 0");
        contadorIntentos.setFont(new Font("SansSerif", Font.BOLD, 16));
        contadorIntentos.setForeground(morado);
        contadorIntentos.setAlignmentX(Component.CENTER_ALIGNMENT);

        contenido.add(contadorIntentos);
        contenido.add(Box.createVerticalStrut(10));

        mensaje = new JLabel(" ");
        mensaje.setFont(new Font("SansSerif", Font.BOLD, 14));
        mensaje.setForeground(rosaOscuro);
        mensaje.setAlignmentX(Component.CENTER_ALIGNMENT);

        contenido.add(mensaje);

        JPanel leyenda = new JPanel(new GridLayout(3, 1, 0, 5));
        leyenda.setBackground(rosaFondo);
        leyenda.setBorder(new EmptyBorder(5, 5, 5, 5));

        leyenda.add(crearLeyenda(verde, "Verde: has acertado el número"));
        leyenda.add(crearLeyenda(rojo, "Rojo: el número es demasiado alto, baja"));
        leyenda.add(crearLeyenda(azul, "Azul: el número es demasiado bajo, sube"));

        principal.add(cabecera, BorderLayout.NORTH);
        principal.add(contenido, BorderLayout.CENTER);
        principal.add(leyenda, BorderLayout.SOUTH);

        setContentPane(principal);
    }

    private void generarFecha() {
        Random random = new Random();

        LocalDate inicio = LocalDate.of(2020, 1, 1);
        LocalDate fin = LocalDate.of(2029, 12, 31);

        long dias = ChronoUnit.DAYS.between(inicio, fin);

        fechaSecreta = inicio.plusDays(random.nextInt((int) dias + 1));
    }

    private JTextField crearCampo() {
        JTextField campo = new JTextField();
        campo.setFont(new Font("SansSerif", Font.BOLD, 20));
        campo.setForeground(rosaOscuro);
        campo.setBackground(new Color(255, 248, 251));
        campo.setHorizontalAlignment(JTextField.CENTER);
        campo.setCaretColor(rosaOscuro);
        campo.setPreferredSize(new Dimension(100, 45));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(rosaMedio, 2, true),
                new EmptyBorder(5, 5, 5, 5)
        ));
        return campo;
    }

    private JPanel crearGrupo(String nombre, JTextField campo) {
        JPanel grupo = new JPanel();
        grupo.setBackground(blanco);
        grupo.setLayout(new BoxLayout(grupo, BoxLayout.Y_AXIS));

        JLabel etiqueta = new JLabel(nombre);
        etiqueta.setFont(new Font("SansSerif", Font.BOLD, 14));
        etiqueta.setForeground(morado);
        etiqueta.setAlignmentX(Component.CENTER_ALIGNMENT);

        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        grupo.add(etiqueta);
        grupo.add(Box.createVerticalStrut(8));
        grupo.add(campo);

        return grupo;
    }

    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("SansSerif", Font.BOLD, 14));
        boton.setForeground(Color.WHITE);
        boton.setBackground(rosaMedio);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(rosaMedio, 1, true),
                new EmptyBorder(10, 20, 10, 20)
        ));
        boton.setMaximumSize(new Dimension(280, 45));
        boton.setPreferredSize(new Dimension(240, 45));
        boton.setOpaque(true);
        return boton;
    }

    private JLabel crearIndicador(String nombre) {
        JLabel indicador = new JLabel(nombre, SwingConstants.CENTER);
        indicador.setFont(new Font("SansSerif", Font.BOLD, 15));
        indicador.setForeground(morado);
        indicador.setBackground(rosaClaro);
        indicador.setOpaque(true);
        indicador.setPreferredSize(new Dimension(120, 55));
        indicador.setBorder(BorderFactory.createLineBorder(rosaMedio, 1, true));
        return indicador;
    }

    private JPanel crearLeyenda(Color color, String texto) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        panel.setBackground(rosaFondo);

        JLabel muestra = new JLabel("  ");
        muestra.setOpaque(true);
        muestra.setBackground(color);
        muestra.setBorder(BorderFactory.createLineBorder(color.darker()));
        muestra.setPreferredSize(new Dimension(22, 18));

        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font("SansSerif", Font.PLAIN, 13));
        etiqueta.setForeground(morado);

        panel.add(muestra);
        panel.add(etiqueta);

        return panel;
    }

    private void comprobarFecha() {

        int dia;
        int mes;
        int anio;

        try {
            dia = Integer.parseInt(campoDia.getText().trim());
            mes = Integer.parseInt(campoMes.getText().trim());
            anio = Integer.parseInt(campoAnio.getText().trim());

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Introduce números en los tres campos.",
                    "Datos incorrectos",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        LocalDate fechaIntroducida;

        try {
            fechaIntroducida = LocalDate.of(anio, mes, dia);

        } catch (DateTimeException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "La fecha no es válida. Comprueba el día, el mes y el año.",
                    "Fecha incorrecta",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (anio < 2020 || anio > 2029) {
            JOptionPane.showMessageDialog(
                    this,
                    "El año debe estar entre 2020 y 2029.",
                    "Año fuera de rango",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        intentos++;
        contadorIntentos.setText("Intentos: " + intentos);

        compararCampo(resultadoDia, dia, fechaSecreta.getDayOfMonth());
        compararCampo(resultadoMes, mes, fechaSecreta.getMonthValue());
        compararCampo(resultadoAnio, anio, fechaSecreta.getYear());

        if (fechaIntroducida.equals(fechaSecreta)) {

            mensaje.setText("¡Correcto! Has adivinado la fecha.");
            mensaje.setForeground(new Color(40, 140, 75));

            JOptionPane.showMessageDialog(
                    this,
                    "¡Enhorabuena! Has acertado la fecha en "
                            + intentos + " intentos.",
                    "Fecha acertada",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } else {
            mensaje.setText("Sigue intentándolo.");
            mensaje.setForeground(rosaOscuro);
        }
    }

    private void compararCampo(JLabel indicador, int valor, int secreto) {

        String nombre = indicador.getText().split(":")[0];

        if (valor == secreto) {
            indicador.setBackground(verde);
            indicador.setForeground(new Color(35, 105, 55));
            indicador.setText(nombre + ": ¡Correcto!");

        } else if (valor > secreto) {
            indicador.setBackground(rojo);
            indicador.setForeground(new Color(150, 40, 55));
            indicador.setText(nombre + ": Baja");

        } else {
            indicador.setBackground(azul);
            indicador.setForeground(new Color(35, 75, 140));
            indicador.setText(nombre + ": Sube");
        }
    }

    private void nuevaPartida() {
        generarFecha();

        intentos = 0;
        contadorIntentos.setText("Intentos: 0");

        campoDia.setText("");
        campoMes.setText("");
        campoAnio.setText("");

        resultadoDia.setText("Día");
        resultadoMes.setText("Mes");
        resultadoAnio.setText("Año");

        resultadoDia.setBackground(rosaClaro);
        resultadoMes.setBackground(rosaClaro);
        resultadoAnio.setBackground(rosaClaro);

        resultadoDia.setForeground(morado);
        resultadoMes.setForeground(morado);
        resultadoAnio.setForeground(morado);

        mensaje.setText("Nueva partida. ¡Inténtalo!");
        mensaje.setForeground(rosaOscuro);

        campoDia.requestFocus();
    }
}