package ui;

import dao.UserDAO;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.User;

/**
 * Modern account and profile settings frame.
 */
public class SettingsFrame extends JFrame {

    private final User user;
    private final UserDAO dao = new UserDAO();

    private final JTextField txtFullName = new JTextField();
    private final JPasswordField txtCurrent = new JPasswordField();
    private final JPasswordField txtNew = new JPasswordField();
    private final JPasswordField txtConfirm = new JPasswordField();

    private final JButton btnSave = UITheme.createPrimaryButton("💾 Save Changes");
    private final JButton btnBack = UITheme.createSecondaryButton("← Back to Dashboard");

    public SettingsFrame(User user) {
        this.user = user;

        setTitle("Quiz Management System - Account Settings");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(560, 640);
        setResizable(false);
        setLocationRelativeTo(null);
        UITheme.applyWindowIcon(this);

        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(UITheme.BG_APP);
        root.setBorder(new EmptyBorder(24, 34, 24, 34));

        // Header with theme toggle & back button
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(UITheme.createHeader("Account Settings", "Manage your profile information and security"), BorderLayout.WEST);

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topRight.setOpaque(false);
        topRight.add(UITheme.createThemeToggle(this));
        topRight.add(btnBack);
        top.add(topRight, BorderLayout.EAST);
        root.add(top, BorderLayout.NORTH);

        // Card Container
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // Section 1: Profile Info
        JLabel sec1 = new JLabel("Profile Information");
        sec1.setFont(UITheme.FONT_SECTION);
        sec1.setForeground(UITheme.TEXT_TITLE);
        card.add(sec1);
        card.add(Box.createVerticalStrut(12));

        JTextField txtUsername = new JTextField(user.getUsername());
        txtUsername.setEditable(false);
        UITheme.styleInput(txtUsername);

        JTextField txtRole = new JTextField(user.getRole());
        txtRole.setEditable(false);
        UITheme.styleInput(txtRole);

        txtFullName.setText(user.getFullName());
        UITheme.styleInput(txtFullName);

        JPanel infoGrid = new JPanel(new GridLayout(1, 2, 14, 0));
        infoGrid.setOpaque(false);
        infoGrid.add(UITheme.createFormField("Username", txtUsername));
        infoGrid.add(UITheme.createFormField("System Role", txtRole));
        infoGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        card.add(infoGrid);
        card.add(Box.createVerticalStrut(10));

        JPanel namePanel = UITheme.createFormField("Full Name", txtFullName);
        namePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        card.add(namePanel);
        card.add(Box.createVerticalStrut(20));

        // Section 2: Password
        JLabel sec2 = new JLabel("Change Password");
        sec2.setFont(UITheme.FONT_SECTION);
        sec2.setForeground(UITheme.TEXT_TITLE);
        card.add(sec2);
        card.add(Box.createVerticalStrut(4));

        JLabel hint = new JLabel("Leave password fields blank if you do not wish to change it.");
        hint.setFont(UITheme.FONT_SMALL);
        hint.setForeground(UITheme.TEXT_MUTED);
        card.add(hint);
        card.add(Box.createVerticalStrut(12));

        UITheme.styleInput(txtCurrent);
        UITheme.styleInput(txtNew);
        UITheme.styleInput(txtConfirm);

        JPanel curPass = UITheme.createFormField("Current Password", txtCurrent);
        curPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        card.add(curPass);
        card.add(Box.createVerticalStrut(10));

        JPanel newPassGrid = new JPanel(new GridLayout(1, 2, 14, 0));
        newPassGrid.setOpaque(false);
        newPassGrid.add(UITheme.createFormField("New Password (min 4)", txtNew));
        newPassGrid.add(UITheme.createFormField("Confirm New Password", txtConfirm));
        newPassGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        card.add(newPassGrid);
        card.add(Box.createVerticalStrut(26));

        // Save Button
        btnSave.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        btnSave.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnSave.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSave.addActionListener(e -> save());
        card.add(btnSave);

        root.add(card, BorderLayout.CENTER);

        btnBack.addActionListener(e -> goBack(user));
        setContentPane(root);
    }

    private void save() {
        String fullName = txtFullName.getText().trim();
        String current = new String(txtCurrent.getPassword());
        String newPass = new String(txtNew.getPassword());
        String confirm = new String(txtConfirm.getPassword());

        if (fullName.isEmpty()) {
            warn("Your full name cannot be empty.");
            txtFullName.requestFocus();
            return;
        }

        boolean changingPassword = !current.isEmpty() || !newPass.isEmpty() || !confirm.isEmpty();

        if (changingPassword) {
            if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
                warn("To change your password, please fill in all 3 password fields.");
                return;
            }
            if (dao.login(user.getUsername(), current) == null) {
                warn("Your current password is incorrect.");
                txtCurrent.requestFocus();
                return;
            }
            if (newPass.length() < 4) {
                warn("The new password must be at least 4 characters long.");
                txtNew.requestFocus();
                return;
            }
            if (!newPass.equals(confirm)) {
                warn("The new password and confirm password do not match.");
                txtConfirm.requestFocus();
                return;
            }
        }

        if (!dao.updateUser(user.getId(), fullName, user.getRole())) {
            warn("Could not save your profile changes. Is MySQL running?");
            return;
        }

        if (changingPassword && !dao.resetPassword(user.getId(), newPass)) {
            warn("Your name was saved, but the password could not be updated.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Your settings have been saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        User updatedUser = new User(user.getId(), fullName, user.getUsername(), user.getRole());
        goBack(updatedUser);
    }

    private void warn(String message) {
        JOptionPane.showMessageDialog(this, message, "Notice", JOptionPane.WARNING_MESSAGE);
    }

    private void goBack(User u) {
        new DashboardFrame(u).setVisible(true);
        dispose();
    }
}