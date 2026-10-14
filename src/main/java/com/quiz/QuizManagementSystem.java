package com.quiz;

import dao.DBConnection;
import javax.swing.JOptionPane;
import ui.LoginFrame;
import ui.UITheme;

public class QuizManagementSystem {

    public static void main(String[] args) {
        // Initialize modern theme tokens and fonts
        UITheme.setupTheme();

        // Check database connectivity before showing application window
        if (!DBConnection.isAvailable()) {
            JOptionPane.showMessageDialog(null,
                    "Cannot connect to the MySQL database.\n\n"
                  + "1. Ensure MySQL is running (e.g. in XAMPP on port 3306).\n"
                  + "2. Verify the 'quiz_db' database exists and database.sql is imported.\n"
                  + "3. Re-launch the application.",
                    "Database Not Available",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}