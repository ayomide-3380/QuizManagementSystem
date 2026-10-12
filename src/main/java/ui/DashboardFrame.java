package ui;

import java.awt.*;
import javax.swing.*;
import model.User;

public class DashboardFrame extends JFrame {

    private final User currentUser;

    public DashboardFrame(User user) {
        this.currentUser = user;

        setTitle("Quiz Management System - Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 560);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel lblWelcome = new JLabel(
                "Welcome, " + user.getFullName() + " (" + user.getRole() + ")",
                SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 16));
        root.add(lblWelcome, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(0, 1, 0, 10));

        // Admin-only buttons
        if (user.isAdmin()) {
            buttons.add(makeButton("Manage Quizzes",
                    () -> new ManageQuizzesFrame(currentUser)));
            buttons.add(makeButton("Manage Questions",
                    () -> new ManageQuestionsFrame(currentUser)));
            buttons.add(makeButton("Manage Users",
                    () -> new ManageUsersFrame(currentUser)));
        }

        // Everyone
        buttons.add(makeButton("Take Quiz",
                () -> new QuizTakeFrame(currentUser)));
        buttons.add(makeButton(user.isAdmin() ? "All Results" : "My Results",
                () -> new ResultsFrame(currentUser)));
        buttons.add(makeButton("Settings",
                () -> new SettingsFrame(currentUser)));
        buttons.add(makeButton("Logout",
                () -> new LoginFrame()));

        root.add(buttons, BorderLayout.CENTER);
        setContentPane(root);
    }

    // Makes a button that opens the given window and closes the dashboard
    private JButton makeButton(String text, java.util.function.Supplier<JFrame> windowToOpen) {
        JButton b = new JButton(text);
        b.addActionListener(e -> {
            windowToOpen.get().setVisible(true);
            dispose();
        });
        return b;
    }
}