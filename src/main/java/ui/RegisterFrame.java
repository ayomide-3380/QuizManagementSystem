package ui;

import dao.UserDAO;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Modern student registration window with theme switching, branded icons,
 * and inline validation feedback.
 */
public class RegisterFrame extends JFrame {

    private final JTextField txtFullName = new JTextField();
    private final JTextField txtUsername = new JTextField();
    private final JPasswordField txtPassword = new JPasswordField();
    private final JPasswordField txtConfirm = new JPasswordField();
    private final JLabel lblFeedback = new JLabel(" ", SwingConstants.CENTER);

    private final JButton btnRegister = UITheme.createPrimaryButton("Create Account →");
    private final JButton btnBack = UITheme.createSecondaryButton("← Back to Login");

    public RegisterFrame() {
        setTitle("Quiz Management System - Register Student");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 640);
        setResizable(false);
        setLocationRelativeTo(null);
        UITheme.applyWindowIcon(this);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_APP);
        root.setBorder(new EmptyBorder(22, 38, 22, 38));

        // Top bar with Theme Toggle
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        topBar.setOpaque(false);
        topBar.add(UITheme.createThemeToggle(this));
        root.add(topBar, BorderLayout.NORTH);

        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // Header Section
        JLabel badge = UITheme.createChip("STUDENT ENROLLMENT", UITheme.PRIMARY_LIGHT, UITheme.PRIMARY);
        badge.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Create an Account");
        title.setFont(UITheme.FONT_TITLE_LARGE);
        title.setForeground(UITheme.TEXT_TITLE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Sign up to take assessments and track your scores");
        subtitle.setFont(UITheme.FONT_SUBTITLE);
        subtitle.setForeground(UITheme.TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(badge);
        card.add(Box.createVerticalStrut(8));
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(14));

        lblFeedback.setFont(UITheme.FONT_SMALL);
        lblFeedback.setForeground(UITheme.DANGER);
        lblFeedback.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblFeedback);
        card.add(Box.createVerticalStrut(6));

        // Form Fields
        UITheme.styleInput(txtFullName);
        UITheme.styleInput(txtUsername);
        UITheme.styleInput(txtPassword);
        UITheme.styleInput(txtConfirm);

        txtFullName.putClientProperty("JTextField.placeholderText", "e.g. John Doe");
        txtUsername.putClientProperty("JTextField.placeholderText", "Choose a unique username");
        txtPassword.putClientProperty("JTextField.placeholderText", "At least 4 characters");
        txtConfirm.putClientProperty("JTextField.placeholderText", "Re-enter your password");

        card.add(createFieldRow("Full Name", txtFullName));
        card.add(Box.createVerticalStrut(10));
        card.add(createFieldRow("Username", txtUsername));
        card.add(Box.createVerticalStrut(10));
        card.add(createFieldRow("Password", txtPassword));
        card.add(Box.createVerticalStrut(10));
        card.add(createFieldRow("Confirm Password", txtConfirm));
        card.add(Box.createVerticalStrut(20));

        // Actions
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnRegister.setPreferredSize(new Dimension(320, 40));

        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBack.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnBack.setPreferredSize(new Dimension(320, 38));

        btnRegister.addActionListener(e -> doRegister());
        btnBack.addActionListener(e -> backToLogin());

        KeyAdapter enterListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    doRegister();
                }
            }
        };
        txtFullName.addKeyListener(enterListener);
        txtUsername.addKeyListener(enterListener);
        txtPassword.addKeyListener(enterListener);
        txtConfirm.addKeyListener(enterListener);

        card.add(btnRegister);
        card.add(Box.createVerticalStrut(10));
        card.add(btnBack);

        root.add(card, BorderLayout.CENTER);
        setContentPane(root);
        getRootPane().setDefaultButton(btnRegister);
    }

    private JPanel createFieldRow(String label, JComponent comp) {
        JPanel p = UITheme.createFormField(label, comp);
        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        return p;
    }

    private void doRegister() {
        String fullName = txtFullName.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirm = new String(txtConfirm.getPassword());

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            lblFeedback.setText("⚠ Please complete all required fields.");
            lblFeedback.setForeground(UITheme.DANGER);
            return;
        }
        if (username.length() < 3) {
            lblFeedback.setText("⚠ Username must be at least 3 characters.");
            lblFeedback.setForeground(UITheme.DANGER);
            return;
        }
        if (password.length() < 4) {
            lblFeedback.setText("⚠ Password must be at least 4 characters.");
            lblFeedback.setForeground(UITheme.DANGER);
            return;
        }
        if (!password.equals(confirm)) {
            lblFeedback.setText("⚠ Passwords do not match.");
            lblFeedback.setForeground(UITheme.DANGER);
            return;
        }

        UserDAO dao = new UserDAO();
        if (dao.usernameExists(username)) {
            lblFeedback.setText("✖ That username is already taken. Please pick another.");
            lblFeedback.setForeground(UITheme.DANGER);
            return;
        }

        if (dao.register(fullName, username, password)) {
            JOptionPane.showMessageDialog(this,
                    "Account registered successfully! You can now log in.",
                    "Registration Complete", JOptionPane.INFORMATION_MESSAGE);
            backToLogin();
        } else {
            lblFeedback.setText("✖ Could not register account. Database error occurred.");
            lblFeedback.setForeground(UITheme.DANGER);
        }
    }

    private void backToLogin() {
        new LoginFrame().setVisible(true);
        dispose();
    }
}