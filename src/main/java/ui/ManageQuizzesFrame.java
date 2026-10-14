package ui;

import dao.QuizDAO;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import model.Quiz;
import model.User;

/**
 * Modern, clean management window for quizzes with live filtering.
 */
public class ManageQuizzesFrame extends JFrame {

    private final User user;
    private final QuizDAO dao = new QuizDAO();
    private List<Quiz> quizzes = new ArrayList<>();

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Quiz Title", "Description"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);

    private final JTextField txtSearch = new JTextField();
    private final JTextField txtTitle = new JTextField();
    private final JTextField txtDescription = new JTextField();

    private final JButton btnAdd = UITheme.createPrimaryButton("➕ Add Quiz");
    private final JButton btnUpdate = UITheme.createSecondaryButton("✏ Update");
    private final JButton btnDelete = UITheme.createDangerButton("🗑 Delete");
    private final JButton btnClear = UITheme.createSecondaryButton("Clear Selection");
    private final JButton btnBack = UITheme.createSecondaryButton("← Back to Dashboard");

    public ManageQuizzesFrame(User user) {
        this.user = user;

        setTitle("Quiz Management System - Manage Quizzes");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(880, 640);
        setMinimumSize(new Dimension(800, 560));
        setLocationRelativeTo(null);
        UITheme.applyWindowIcon(this);

        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(UITheme.BG_APP);
        root.setBorder(new EmptyBorder(20, 26, 20, 26));

        // 1. Header with theme toggle & back button
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(UITheme.createHeader("Quiz Management", "Create, edit, and organize quizzes"), BorderLayout.WEST);

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topRight.setOpaque(false);
        topRight.add(UITheme.createThemeToggle(this));
        topRight.add(btnBack);
        top.add(topRight, BorderLayout.EAST);
        root.add(top, BorderLayout.NORTH);

        // 2. Middle: Search bar + Table card
        JPanel centerCard = UITheme.createCardPanel();
        centerCard.setLayout(new BorderLayout(0, 12));

        // Search Bar
        JPanel searchBar = new JPanel(new BorderLayout(10, 0));
        searchBar.setOpaque(false);
        JLabel lblSearch = new JLabel("🔍 Search:");
        lblSearch.setFont(UITheme.FONT_BOLD);
        lblSearch.setForeground(UITheme.TEXT_MUTED);

        UITheme.styleInput(txtSearch);
        txtSearch.putClientProperty("JTextField.placeholderText", "Filter quizzes by title or keyword...");
        searchBar.add(lblSearch, BorderLayout.WEST);
        searchBar.add(txtSearch, BorderLayout.CENTER);
        centerCard.add(searchBar, BorderLayout.NORTH);

        // Table setup
        UITheme.styleTable(table);
        table.setRowSorter(sorter);
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(260);
        table.getColumnModel().getColumn(2).setPreferredWidth(450);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFields();
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        centerCard.add(scrollPane, BorderLayout.CENTER);

        root.add(centerCard, BorderLayout.CENTER);

        // 3. Bottom: Form card for Adding / Updating
        JPanel formCard = UITheme.createCardPanel();
        formCard.setLayout(new BorderLayout(0, 14));

        JLabel formTitle = new JLabel("Quiz Details");
        formTitle.setFont(UITheme.FONT_SECTION);
        formTitle.setForeground(UITheme.TEXT_TITLE);
        formCard.add(formTitle, BorderLayout.NORTH);

        UITheme.styleInput(txtTitle);
        UITheme.styleInput(txtDescription);
        txtTitle.putClientProperty("JTextField.placeholderText", "e.g. Java Fundamentals");
        txtDescription.putClientProperty("JTextField.placeholderText", "Brief description of quiz scope...");

        JPanel fields = new JPanel(new GridLayout(1, 2, 16, 0));
        fields.setOpaque(false);
        fields.add(UITheme.createFormField("Quiz Title", txtTitle));
        fields.add(UITheme.createFormField("Description", txtDescription));
        formCard.add(fields, BorderLayout.CENTER);

        // Buttons
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonBar.setOpaque(false);
        buttonBar.add(btnClear);
        buttonBar.add(btnDelete);
        buttonBar.add(btnUpdate);
        buttonBar.add(btnAdd);
        formCard.add(buttonBar, BorderLayout.SOUTH);

        root.add(formCard, BorderLayout.SOUTH);

        // Handlers
        btnAdd.addActionListener(e -> addQuiz());
        btnUpdate.addActionListener(e -> updateQuiz());
        btnDelete.addActionListener(e -> deleteQuiz());
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
        quizzes = dao.getAllQuizzes();
        model.setRowCount(0);
        for (Quiz q : quizzes) {
            model.addRow(new Object[]{q.getId(), q.getTitle(), q.getDescription()});
        }
    }

    private void fillFields() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return;
        int modelRow = table.convertRowIndexToModel(viewRow);
        Quiz q = quizzes.get(modelRow);
        txtTitle.setText(q.getTitle());
        txtDescription.setText(q.getDescription());
    }

    private void clearForm() {
        table.clearSelection();
        txtTitle.setText("");
        txtDescription.setText("");
    }

    private Quiz selectedQuiz() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            warn("Please select a quiz from the table first.");
            return null;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        return quizzes.get(modelRow);
    }

    private void addQuiz() {
        String title = txtTitle.getText().trim();
        if (title.isEmpty()) {
            warn("Please enter a quiz title.");
            txtTitle.requestFocus();
            return;
        }
        if (dao.addQuiz(title, txtDescription.getText().trim(), user.getId()) == -1) {
            warn("Could not save the quiz. Please check database connection.");
            return;
        }
        clearForm();
        refresh();
        JOptionPane.showMessageDialog(this, "Quiz created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateQuiz() {
        Quiz q = selectedQuiz();
        if (q == null) return;

        String title = txtTitle.getText().trim();
        if (title.isEmpty()) {
            warn("Quiz title cannot be empty.");
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
        JOptionPane.showMessageDialog(this, "Quiz updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteQuiz() {
        Quiz q = selectedQuiz();
        if (q == null) return;

        int choice = JOptionPane.showConfirmDialog(this,
                "Delete \"" + q.getTitle() + "\" and all associated questions and attempts?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;

        if (!dao.deleteQuiz(q.getId())) {
            warn("Could not delete this quiz.");
            return;
        }
        clearForm();
        refresh();
        JOptionPane.showMessageDialog(this, "Quiz deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
    }

    private void warn(String message) {
        JOptionPane.showMessageDialog(this, message, "Notice", JOptionPane.WARNING_MESSAGE);
    }

    private void goBack() {
        new DashboardFrame(user).setVisible(true);
        dispose();
    }
}