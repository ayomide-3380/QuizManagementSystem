package ui;

import dao.UserDAO;
import java.awt.*;
import javax.swing.*;

public class RegisterFrame extends JFrame {

    private final JTextField txtFullName = new JTextField();
    private final JTextField txtUsername = new JTextField();
    private final JPasswordField txtPassword = new JPasswordField();
    private final JPasswordField txtConfirm = new JPasswordField();

    public RegisterFrame() {
        setTitle("Quiz Management System - Register");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 340);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 15));
        root.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("Create a student account", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 12));
        form.add(new JLabel("Full name:"));        form.add(txtFullName);
        form.add(new JLabel("Username:"));         form.add(txtUsername);
        form.add(new JLabel("Password:"));         form.add(txtPassword);
        form.add(new JLabel("Confirm password:")); form.add(txtConfirm);
        root.add(form, BorderLayout.CENTER);

        JButton btnRegister = new JButton("Register");
        JButton btnBack = new JButton("Back to login");
        btnRegister.addActionListener(e -> doRegister());
        btnBack.addActionListener(e -> backToLogin());

        JPanel bottom = new JPanel(new GridLayout(1, 2, 10, 0));
        bottom.add(btnRegister);
        bottom.add(btnBack);
        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void doRegister() {
        String fullName = txtFullName.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirm = new String(txtConfirm.getPassword());

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            warn("Please fill in every field.");
            return;
        }
        if (password.length() < 4) {
            warn("Password must be at least 4 characters.");
            return;
        }
        if (!password.equals(confirm)) {
            warn("The two passwords do not match.");
            return;
        }

        UserDAO dao = new UserDAO();
        if (dao.usernameExists(username)) {
            warn("That username is already taken. Try another one.");
            return;
        }
        if (dao.register(fullName, username, password)) {
            JOptionPane.showMessageDialog(this, "Account created! You can log in now.");
            backToLogin();
        } else {
            warn("Something went wrong. Is MySQL running?");
        }
    }

    private void warn(String message) {
        JOptionPane.showMessageDialog(this, message, "Register", JOptionPane.WARNING_MESSAGE);
    }

    private void backToLogin() {
        new LoginFrame().setVisible(true);
        dispose();
    }
}