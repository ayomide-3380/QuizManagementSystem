package ui;

import dao.UserDAO;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import model.User;

/**
 * Modern, high-conversion Login Window featuring branded application icon,
 * theme toggle, password visibility switcher, and instant demo account buttons.
 */
public class LoginFrame extends JFrame {

    private final JTextField txtUsername = new JTextField();
    private final JPasswordField txtPassword = new JPasswordField();
    private final JButton btnLogin = UITheme.createPrimaryButton("Sign In to Portal →");
    private final JButton btnRegister = UITheme.createSecondaryButton("Create Student Account");
    private final JLabel lblFeedback = new JLabel(" ", SwingConstants.CENTER);
    private boolean showPassword = false;

    public LoginFrame() {
        setTitle("Quiz Management System - Secure Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 630);
        setResizable(false);
        setLocationRelativeTo(null);
        UITheme.applyWindowIcon(this);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_APP);
        root.setBorder(new EmptyBorder(22, 38, 22, 38));

        // Top bar with Theme Toggle
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        topBar.setOpaque(false);
        JButton btnTheme = UITheme.createThemeToggle(this);
        topBar.add(btnTheme);
        root.add(topBar, BorderLayout.NORTH);

        // Card Container
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // Brand Banner
        JLabel iconLbl = new JLabel("🎓", SwingConstants.CENTER);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 44));
        iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel badge = new JLabel("  ENTERPRISE ASSESSMENT PORTAL  ");
        badge.setFont(UITheme.FONT_BADGE);
        badge.setForeground(UITheme.PRIMARY);
        badge.setBackground(UITheme.PRIMARY_LIGHT);
        badge.setOpaque(true);
        badge.setBorder(new LineBorder(new Color(191, 219, 254), 1, true));
        badge.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Welcome Back");
        title.setFont(UITheme.FONT_HERO);
        title.setForeground(UITheme.TEXT_TITLE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Enter your credentials to access your dashboard");
        subtitle.setFont(UITheme.FONT_SUBTITLE);
        subtitle.setForeground(UITheme.TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(iconLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(badge);
        card.add(Box.createVerticalStrut(8));
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(16));

        // Inline Feedback Notification
        lblFeedback.setFont(UITheme.FONT_SMALL);
        lblFeedback.setForeground(UITheme.DANGER);
        lblFeedback.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblFeedback);
        card.add(Box.createVerticalStrut(6));

        // Inputs Section
        UITheme.styleInput(txtUsername);
        UITheme.styleInput(txtPassword);
        txtUsername.putClientProperty("JTextField.placeholderText", "Enter your username");
        txtPassword.putClientProperty("JTextField.placeholderText", "Enter your password");

        JPanel userPanel = UITheme.createFormField("Username", txtUsername);
        userPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        // Password field with toggle visibility button
        JPanel passContainer = new JPanel(new BorderLayout(6, 0));
        passContainer.setOpaque(false);
        passContainer.add(txtPassword, BorderLayout.CENTER);

        JButton btnTogglePass = new JButton("👁");
        btnTogglePass.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        btnTogglePass.setFocusPainted(false);
        btnTogglePass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTogglePass.setPreferredSize(new Dimension(42, 36));
        btnTogglePass.setBackground(UITheme.BG_CARD);
        btnTogglePass.setBorder(new LineBorder(UITheme.BORDER, 1, true));
        btnTogglePass.addActionListener(e -> {
            showPassword = !showPassword;
            txtPassword.setEchoChar(showPassword ? (char) 0 : '•');
            btnTogglePass.setText(showPassword ? "🔒" : "👁");
        });
        passContainer.add(btnTogglePass, BorderLayout.EAST);

        JPanel passPanel = UITheme.createFormField("Password", passContainer);
        passPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        card.add(userPanel);
        card.add(Box.createVerticalStrut(10));
        card.add(passPanel);
        card.add(Box.createVerticalStrut(18));

        // Buttons Section
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnLogin.setPreferredSize(new Dimension(340, 42));

        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnRegister.setPreferredSize(new Dimension(340, 38));

        btnLogin.addActionListener(e -> doLogin());
        btnRegister.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });

        KeyAdapter enterListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin();
            }
        };
        txtUsername.addKeyListener(enterListener);
        txtPassword.addKeyListener(enterListener);

        card.add(btnLogin);
        card.add(Box.createVerticalStrut(10));
        card.add(btnRegister);
        card.add(Box.createVerticalStrut(16));

        // Quick Fill Demo Accounts
        JPanel quickDemo = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        quickDemo.setOpaque(false);
        JLabel lblQuick = new JLabel("Quick login demo:");
        lblQuick.setFont(UITheme.FONT_SMALL);
        lblQuick.setForeground(UITheme.TEXT_MUTED);
        quickDemo.add(lblQuick);

        JButton chipAdmin = makeDemoChip("👑 Admin", "admin", "admin123");
        JButton chipSarah = makeDemoChip("🎓 Sarah", "sarah", "student123");
        JButton chipAlex = makeDemoChip("🧪 Alex", "alex", "student123");
        quickDemo.add(chipAdmin);
        quickDemo.add(chipSarah);
        quickDemo.add(chipAlex);

        quickDemo.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(quickDemo);

        root.add(card, BorderLayout.CENTER);
        setContentPane(root);
        getRootPane().setDefaultButton(btnLogin);
    }

    private JButton makeDemoChip(String label, String username, String password) {
        JButton btn = new JButton(label);
        btn.setFont(UITheme.FONT_BADGE);
        btn.setForeground(UITheme.PRIMARY);
        btn.setBackground(UITheme.PRIMARY_LIGHT);
        btn.setBorder(new LineBorder(new Color(191, 219, 254), 1, true));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            txtUsername.setText(username);
            txtPassword.setText(password);
            lblFeedback.setText(" ");
            txtPassword.requestFocus();
        });
        return btn;
    }

    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            lblFeedback.setText("⚠ Please enter both your username and password.");
            lblFeedback.setForeground(UITheme.DANGER);
            return;
        }

        User user = new UserDAO().login(username, password);
        if (user == null) {
            lblFeedback.setText("✖ Invalid username or password. Please try again.");
            lblFeedback.setForeground(UITheme.DANGER);
            txtPassword.setText("");
            txtPassword.requestFocus();
            return;
        }

        new DashboardFrame(user).setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        UITheme.setupTheme();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
