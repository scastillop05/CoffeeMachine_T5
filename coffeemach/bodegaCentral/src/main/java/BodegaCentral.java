import guiInventario.Interfaz;
import javax.swing.SwingUtilities;

public class BodegaCentral {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Interfaz frame = new Interfaz();
            frame.setVisible(true);
        });
    }
}
