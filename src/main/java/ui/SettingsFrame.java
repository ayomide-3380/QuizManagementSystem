package ui;

import dao.UserDAO;
import java.awt.*;
import javax.swing.*;
import model.User;

public class SettingsFrame extends JFrame {

    private final User user;
    private final UserDAO dao = new UserDAO();

    private final JTextField txtFullName = new JTextField();
    private final JPasswordField txtCurrent = new JPasswordField();
    private final JPasswordField txtNew = new JPasswordField();
    private final JPasswordField txtConfirm = new JPasswordField();

    public SettingsFrame(User user) {
        this.user = user;

        setTitle("Quiz Management System - Settings");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(480, 420);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 15));
        root.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("My settings", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 12));

        JTextField txtUsername = new JTextField(user.getUsername());
        txtUsername.setEditable(false);
        JTextField txtRole = new JTextField(user.getRole());
        txtRole.setEditable(false);

        txtFullName.setText(user.getFullName());

        form.add(new JLabel("Username:"));            form.add(txtUsername);
        form.add(new JLabel("Role:"));                form.add(txtRole);
        form.add(new JLabel("Full name:"));           form.add(txtFullName);
        form.add(new JLabel("Current password:"));    form.add(txtCurrent);
        form.add(new JLabel("New password:"));        form.add(txtNew);
        form.add(new JLabel("Confirm new password:")); form.add(txtConfirm);
        root.add(form, BorderLayout.CENTER);

        JLabel hint = new JLabel("To keep your password, leave the three password boxes empty.");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        JButton btnSave = new JButton("Save changes");
        JButton btnBack = new JButton("Back to dashboard");
        btnSave.addActionListener(e -> save());
        btnBack.addActionListener(e -> goBack(user));

        JPanel buttons = new JPanel(new GridLayout(1, 2, 10, 0));
        buttons.add(btnSave);
        buttons.add(btnBack);

        JPanel south = new JPanel(new BorderLayout(0, 10));
        south.add(hint, BorderLayout.NORTH);
        south.add(buttons, BorderLayout.SOUTH);
        root.add(south, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void save() {
        String fullName = txtFullName.getText().trim();
        String current = new String(txtCurrent.getPassword());
        String newPass = new String(txtNew.getPassword());
        String confirm = new String(txtConfirm.getPassword());

        if (fullName.isEmpty()) {
            warn("Your name cannot be empty.");
            return;
        }

        boolean changingPassword = !current.isEmpty() || !newPass.isEmpty() || !confirm.isEmpty();

        if (changingPassword) {
            if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
                warn("To change your password, fill in all three password boxes.");
                return;
            }
            // Check the current password the same way the login does
            if (dao.login(user.getUsername(), current) == null) {
                warn("Your current password is wrong.");
                return;
            }
            if (newPass.length() < 4) {
                warn("The new password must be at least 4 characters.");
                return;
            }
            if (!newPass.equals(confirm)) {
                warn("The new passwords do not match.");
                return;
            }
        }

        // Role stays the same, so a user cannot make themselves an admin here
        if (!dao.updateUser(user.getId(), fullName, user.getRole())) {
            warn("Could not save your changes. Is MySQL running?");
            return;
        }
        if (changingPassword && !dao.resetPassword(user.getId(), newPass)) {
            warn("Your name was saved, but the password could not be changed.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Your changes have been saved.");

        // Build a fresh User so the dashboard shows the new name
        User updated = new User(user.getId(), fullName, user.getUsername(), user.getRole());
        goBack(updated);
    }

    private void warn(String message) {
        JOptionPane.showMessageDialog(this, message, "Settings", JOptionPane.WARNING_MESSAGE);
    }

    private void goBack(User u) {
        new DashboardFrame(u).setVisible(true);
        dispose();
    }
}