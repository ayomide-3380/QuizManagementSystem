package ui;

import dao.QuizDAO;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Quiz;
import model.User;

public class ManageQuizzesFrame extends JFrame {

    private final User user;
    private final QuizDAO dao = new QuizDAO();
    private List<Quiz> quizzes = new ArrayList<>();

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Title", "Description"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JTextField txtTitle = new JTextField();
    private final JTextField txtDescription = new JTextField();

    public ManageQuizzesFrame(User user) {
        this.user = user;

        setTitle("Quiz Management System - Manage Quizzes");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 520);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 15));
        root.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("Manage quizzes", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        root.add(title, BorderLayout.NORTH);

        // Table of quizzes
        table.setRowHeight(26);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFields();
        });
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        // Form + buttons
        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.add(new JLabel("Title:"));        form.add(txtTitle);
        form.add(new JLabel("Description:"));  form.add(txtDescription);

        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear");
        JButton btnBack = new JButton("Back");

        btnAdd.addActionListener(e -> addQuiz());
        btnUpdate.addActionListener(e -> updateQuiz());
        btnDelete.addActionListener(e -> deleteQuiz());
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

    // Reloads the table from the database
    private void refresh() {
        quizzes = dao.getAllQuizzes();
        model.setRowCount(0);
        for (Quiz q : quizzes) {
            model.addRow(new Object[]{q.getId(), q.getTitle(), q.getDescription()});
        }
    }

    // Copies the selected row into the two boxes so it can be edited
    private void fillFields() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        Quiz q = quizzes.get(row);
        txtTitle.setText(q.getTitle());
        txtDescription.setText(q.getDescription());
    }

    private void clearForm() {
        table.clearSelection();
        txtTitle.setText("");
        txtDescription.setText("");
    }

    private Quiz selectedQuiz() {
        int row = table.getSelectedRow();
        if (row < 0) {
            warn("Please select a quiz in the table first.");
            return null;
        }
        return quizzes.get(row);
    }

    private void addQuiz() {
        String title = txtTitle.getText().trim();
        if (title.isEmpty()) {
            warn("Please enter a title.");
            return;
        }
        if (dao.addQuiz(title, txtDescription.getText().trim(), user.getId()) == -1) {
            warn("Could not save the quiz. Is MySQL running?");
            return;
        }
        clearForm();
        refresh();
    }

    private void updateQuiz() {
        Quiz q = selectedQuiz();
        if (q == null) return;

        String title = txtTitle.getText().trim();
        if (title.isEmpty()) {
            warn("The title cannot be empty.");
            return;
        }
        q.setTitle(title);
        q.setDescription(txtDescription.getText().trim());

        if (!dao.updateQuiz(q)) {
            warn("Could not update the quiz.");
            return;
        }
        clearForm();
        refresh();
    }

    private void deleteQuiz() {
        Quiz q = selectedQuiz();
        if (q == null) return;

        int choice = JOptionPane.showConfirmDialog(this,
                "Delete \"" + q.getTitle() + "\" and all its questions?",
                "Delete quiz", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;

        if (!dao.deleteQuiz(q.getId())) {
            warn("Could not delete this quiz. Students may already have taken it.");
            return;
        }
        clearForm();
        refresh();
    }

    private void warn(String message) {
        JOptionPane.showMessageDialog(this, message, "Manage Quizzes", JOptionPane.WARNING_MESSAGE);
    }

    private void goBack() {
        new DashboardFrame(user).setVisible(true);
        dispose();
    }
}