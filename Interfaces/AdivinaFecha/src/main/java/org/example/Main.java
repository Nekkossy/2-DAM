import org.example.AdivinarFecha;

import javax.swing.*;

public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        AdivinarFecha ventana = new AdivinarFecha();
        ventana.setVisible(true);
    });
}