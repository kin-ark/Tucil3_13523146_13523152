package com.kinan.rushhour;

import com.kinan.rushhour.gui.GameSolverGUI;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameSolverGUI::new);
    }
}