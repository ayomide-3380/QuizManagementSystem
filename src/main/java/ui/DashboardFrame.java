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
        setSize(420, 460);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel lblWelcome = new JLabel(
                "Welcome, " + user.getFullName() + " (" + user.getRole() + ")",
                SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 16));
        root.add(lblWelcome, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(0, 1, 0, 10));

        if (user.isAdmin()) {
            buttons.add(makeButton("Manage Quizzes"));
            buttons.add(makeButton("Manage Questions"));
            buttons.add(makeButton("Manage Users"));
        }
        JButton btnTakeQuiz = new JButton("Take Quiz");
        btnTakeQuiz.addActionListener(e -> {
            new QuizTakeFrame(currentUser).setVisible(true);
            dispose();
        });
        buttons.add(btnTakeQuiz);
        buttons.add(makeButton("My Results"));

        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        buttons.add(btnLogout);

        root.add(buttons, BorderLayout.CENTER);
        setContentPane(root);
    }

    // Each button shows a placeholder for now.
    // Teammates later replace the message with code that opens their window.
    private JButton makeButton(String text) {
        JButton b = new JButton(text);
        b.addActionListener(e -> JOptionPane.showMessageDialog(this, text + " window coming soon."));
        return b;
    }
}