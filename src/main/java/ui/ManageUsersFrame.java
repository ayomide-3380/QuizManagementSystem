package ui;

import dao.UserDAO;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import model.User;

/**
 * Modern, clean user management frame.
 */
public class ManageUsersFrame extends JFrame {

    private final User admin;
    private final UserDAO dao = new UserDAO();
    private List<User> users = new ArrayList<>();

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Full Name", "Username", "Role"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);

    private final JTextField txtSearch = new JTextField();
    private final JTextField txtFullName = new JTextField();
    private final JTextField txtUsername = new JTextField();
    private final JPasswordField txtPassword = new JPasswordField();
    private final JComboBox<String> cmbRole = new JComboBox<>(new String[]{"STUDENT", "ADMIN"});

    private final JButton btnAdd = UITheme.createPrimaryButton("➕ Add User");
    private final JButton btnUpdate = UITheme.createSecondaryButton("✏ Update User");
    private final JButton btnDelete = UITheme.createDangerButton("🗑 Delete User");
    private final JButton btnClear = UITheme.createSecondaryButton("Clear Form");
    private final JButton btnBack = UITheme.createSecondaryButton("← Back to Dashboard");

    public ManageUsersFrame(User admin) {
        this.admin = admin;

        setTitle("Quiz Management System - Manage Users");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(920, 680);
        setMinimumSize(new Dimension(840, 600));
        setLocationRelativeTo(null);
        UITheme.applyWindowIcon(this);

        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(UITheme.BG_APP);
        root.setBorder(new EmptyBorder(20, 26, 20, 26));

        // 1. Header with theme toggle & back button
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(UITheme.createHeader("User Management", "View and administer student and admin accounts"), BorderLayout.WEST);

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topRight.setOpaque(false);
        topRight.add(UITheme.createThemeToggle(this));
        topRight.add(btnBack);
        top.add(topRight, BorderLayout.EAST);
        root.add(top, BorderLayout.NORTH);

        // 2. Middle Section: Search + Table Card
        JPanel centerCard = UITheme.createCardPanel();
        centerCard.setLayout(new BorderLayout(0, 12));

        // Search Bar
        JPanel searchBar = new JPanel(new BorderLayout(10, 0));
        searchBar.setOpaque(false);
        JLabel lblSearch = new JLabel("🔍 Search:");
        lblSearch.setFont(UITheme.FONT_BOLD);
        lblSearch.setForeground(UITheme.TEXT_MUTED);

        UITheme.styleInput(txtSearch);
        txtSearch.putClientProperty("JTextField.placeholderText", "Search by name, username, or role...");
        searchBar.add(lblSearch, BorderLayout.WEST);
        searchBar.add(txtSearch, BorderLayout.CENTER);
        centerCard.add(searchBar, BorderLayout.NORTH);

        // Table
        UITheme.styleTable(table);
        table.setRowSorter(sorter);
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(260);
        table.getColumnModel().getColumn(2).setPreferredWidth(180);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFields();
        });

        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSel, boolean hasFoc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(tbl, val, isSel, hasFoc, row, col);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(UITheme.FONT_BADGE);
                if (!isSel && val != null) {
                    if ("ADMIN".equalsIgnoreCase(val.toString())) {
                        lbl.setForeground(new Color(99, 102, 241));
                        lbl.setBackground(UITheme.PURPLE_LIGHT);
                    } else {
                        lbl.setForeground(UITheme.SUCCESS_DARK);
                        lbl.setBackground(UITheme.SUCCESS_LIGHT);
                    }
                }
                return lbl;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        centerCard.add(scrollPane, BorderLayout.CENTER);

        root.add(centerCard, BorderLayout.CENTER);

        // 3. Bottom Section: User Details Form Card
        JPanel formCard = UITheme.createCardPanel();
        formCard.setLayout(new BorderLayout(0, 14));

        JLabel formHeader = new JLabel("User Account Information");
        formHeader.setFont(UITheme.FONT_SECTION);
        formHeader.setForeground(UITheme.TEXT_TITLE);
        formCard.add(formHeader, BorderLayout.NORTH);

        UITheme.styleInput(txtFullName);
        UITheme.styleInput(txtUsername);
        UITheme.styleInput(txtPassword);
        cmbRole.setFont(UITheme.FONT_BODY);
        cmbRole.setPreferredSize(new Dimension(cmbRole.getPreferredSize().width, 34));

        JPanel formGrid = new JPanel(new GridLayout(2, 2, 16, 10));
        formGrid.setOpaque(false);
        formGrid.add(UITheme.createFormField("Full Name", txtFullName));
        formGrid.add(UITheme.createFormField("Username", txtUsername));
        formGrid.add(UITheme.createFormField("Password (leave empty to keep unchanged)", txtPassword));
        formGrid.add(UITheme.createFormField("System Role", cmbRole));
        formCard.add(formGrid, BorderLayout.CENTER);

        // Buttons
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonBar.setOpaque(false);
        buttonBar.add(btnClear);
        buttonBar.add(btnDelete);
        buttonBar.add(btnUpdate);
        buttonBar.add(btnAdd);
        formCard.add(buttonBar, BorderLayout.SOUTH);

        root.add(formCard, BorderLayout.SOUTH);

        // Actions
        btnAdd.addActionListener(e -> addUser());
        btnUpdate.addActionListener(e -> updateUser());
        btnDelete.addActionListener(e -> deleteUser());
        btnClear.addActionListener(e -> clearForm());
        btnBack.addActionListener(e -> goBack());

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filter(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filter(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String text = txtSearch.getText().trim();
                sorter.setRowFilter(text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + text));
            }
        });

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

    private void fillFields() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return;
        int modelRow = table.convertRowIndexToModel(viewRow);
        User u = users.get(modelRow);

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
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            warn("Please select a user from the table first.");
            return null;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        return users.get(modelRow);
    }

    private void addUser() {
        String fullName = txtFullName.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String role = (String) cmbRole.getSelectedItem();

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            warn("Please fill in the full name, username, and password.");
            return;
        }
        if (password.length() < 4) {
            warn("Password must be at least 4 characters.");
            return;
        }
        if (dao.usernameExists(username)) {
            warn("That username is already taken. Please choose another.");
            return;
        }
        if (!dao.addUser(fullName, username, password, role)) {
            warn("Could not save the user.");
            return;
        }
        clearForm();
        refresh();
        JOptionPane.showMessageDialog(this, "User created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
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
            warn("You cannot change your own administrator role.");
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
            warn("Could not update user details.");
            return;
        }
        clearForm();
        refresh();
        JOptionPane.showMessageDialog(this, "User updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteUser() {
        User u = selectedUser();
        if (u == null) return;

        if (u.getId() == admin.getId()) {
            warn("You cannot delete your currently logged-in account.");
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this,
                "Delete the account \"" + u.getUsername() + "\" and its quiz history?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;

        if (!dao.deleteUser(u.getId())) {
            warn("Could not delete this user.");
            return;
        }
        clearForm();
        refresh();
        JOptionPane.showMessageDialog(this, "User deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
    }

    private void warn(String message) {
        JOptionPane.showMessageDialog(this, message, "Notice", JOptionPane.WARNING_MESSAGE);
    }

    private void goBack() {
        new DashboardFrame(admin).setVisible(true);
        dispose();
    }
}