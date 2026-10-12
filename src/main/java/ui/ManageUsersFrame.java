package ui;

import dao.UserDAO;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.User;

public class ManageUsersFrame extends JFrame {

    private final User admin;
    private final UserDAO dao = new UserDAO();
    private List<User> users = new ArrayList<>();

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Full name", "Username", "Role"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);

    private final JTextField txtFullName = new JTextField();
    private final JTextField txtUsername = new JTextField();
    private final JPasswordField txtPassword = new JPasswordField();
    private final JComboBox<String> cmbRole = new JComboBox<>(new String[]{"STUDENT", "ADMIN"});

    public ManageUsersFrame(User admin) {
        this.admin = admin;

        setTitle("Quiz Management System - Manage Users");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(720, 580);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 15));
        root.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("Manage users", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        root.add(title, BorderLayout.NORTH);

        // Table of users
        table.setRowHeight(26);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFields();
        });
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        // Form
        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.add(new JLabel("Full name:"));                              form.add(txtFullName);
        form.add(new JLabel("Username:"));                               form.add(txtUsername);
        form.add(new JLabel("Password (leave blank to keep it):"));      form.add(txtPassword);
        form.add(new JLabel("Role:"));                                   form.add(cmbRole);

        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear");
        JButton btnBack = new JButton("Back");

        btnAdd.addActionListener(e -> addUser());
        btnUpdate.addActionListener(e -> updateUser());
        btnDelete.addActionListener(e -> deleteUser());
        btnClear.addActionListener(e -> clearForm());
        btnBack.addActionListener(e -> goBack());

        JPanel buttons = new JPanel(new GridLayout(1, 5, 10, 0));
        buttons.add(btnAdd);
        buttons.add(btnUpdate);
        buttons.add(btnDelete);
        buttons.add(btnClear);
        buttons.add(btnBack);

        JPanel south = new JPanel(new BorderLayout(0, 12));
        south.add(form, BorderLayout.CENTER);
        south.add(buttons, BorderLayout.SOUTH);
        root.add(south, BorderLayout.SOUTH);

        setContentPane(root);
        refresh();
    }

    private void refresh() {
        users = dao.getAllUsers();
        model.setRowCount(0);
        for (User u : users) {
            model.addRow(new Object[]{u.getId(), u.getFullName(), u.getUsername(), u.getRole()});
        }
    }

    // Copies the selected user into the form. The username cannot be changed after creation.
    private void fillFields() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        User u = users.get(row);
        txtFullName.setText(u.getFullName());
        txtUsername.setText(u.getUsername());
        txtUsername.setEditable(false);
        txtPassword.setText("");
        cmbRole.setSelectedItem(u.getRole());
    }

    private void clearForm() {
        table.clearSelection();
        txtFullName.setText("");
        txtUsername.setText("");
        txtUsername.setEditable(true);
        txtPassword.setText("");
        cmbRole.setSelectedItem("STUDENT");
    }

    private User selectedUser() {
        int row = table.getSelectedRow();
        if (row < 0) {
            warn("Please select a user in the table first.");
            return null;
        }
        return users.get(row);
    }

    private void addUser() {
        String fullName = txtFullName.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String role = (String) cmbRole.getSelectedItem();

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            warn("Please fill in the name, username and password.");
            return;
        }
        if (password.length() < 4) {
            warn("Password must be at least 4 characters.");
            return;
        }
        if (dao.usernameExists(username)) {
            warn("That username is already taken.");
            return;
        }
        if (!dao.addUser(fullName, username, password, role)) {
            warn("Could not save the user. Is MySQL running?");
            return;
        }
        clearForm();
        refresh();
    }

    private void updateUser() {
        User u = selectedUser();
        if (u == null) return;

        String fullName = txtFullName.getText().trim();
        String role = (String) cmbRole.getSelectedItem();
        String password = new String(txtPassword.getPassword());

        if (fullName.isEmpty()) {
            warn("The name cannot be empty.");
            return;
        }
        if (u.getId() == admin.getId() && !role.equals(u.getRole())) {
            warn("You cannot change your own role.");
            return;
        }
        if (!password.isEmpty() && password.length() < 4) {
            warn("The new password must be at least 4 characters.");
            return;
        }

        boolean ok = dao.updateUser(u.getId(), fullName, role);
        if (ok && !password.isEmpty()) {
            ok = dao.resetPassword(u.getId(), password);
        }
        if (!ok) {
            warn("Could not update the user.");
            return;
        }
        clearForm();
        refresh();
    }

    private void deleteUser() {
        User u = selectedUser();
        if (u == null) return;

        if (u.getId() == admin.getId()) {
            warn("You cannot delete the account you are logged in with.");
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this,
                "Delete the account \"" + u.getUsername() + "\"?",
                "Delete user", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;

        if (!dao.deleteUser(u.getId())) {
            warn("Could not delete this user. They may have taken quizzes or created quizzes.");
            return;
        }
        clearForm();
        refresh();
    }

    private void warn(String message) {
        JOptionPane.showMessageDialog(this, message, "Manage Users", JOptionPane.WARNING_MESSAGE);
    }

    private void goBack() {
        new DashboardFrame(admin).setVisible(true);
        dispose();
    }
}