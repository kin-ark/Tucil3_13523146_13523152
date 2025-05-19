package rush_hour;

import rush_hour.gui.GameSolverGUI;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameSolverGUI::new);
    }
}