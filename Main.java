import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CardManagementGui gui = new CardManagementGui();
            gui.setVisible(true);
        });
    }
}
