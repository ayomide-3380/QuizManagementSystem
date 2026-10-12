package com.ayomide;

import com.formdev.flatlaf.FlatLightLaf;
import dao.DBConnection;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import ui.LoginFrame;

public class QuizManagementSystem {

    public static void main(String[] args) {
        UIManager.put("Button.arc", 12);
        UIManager.put("Component.arc", 12);
        UIManager.put("TextComponent.arc", 12);

        FlatLightLaf.setup();

        // Check the database before opening any window
        if (!DBConnection.isAvailable()) {
            JOptionPane.showMessageDialog(null,
                    "Cannot connect to the database.\n\n"
                  + "1. Open XAMPP and start the MySQL module.\n"
                  + "2. Make sure the quiz_db database exists.\n"
                  + "3. Run this application again.",
                    "Database not available",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}