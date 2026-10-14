package ui;

import dao.QuestionDAO;
import dao.QuizDAO;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import model.Option;
import model.Question;
import model.Quiz;
import model.User;

/**
 * Modern, clean question and option editor.
 */
public class ManageQuestionsFrame extends JFrame {

    private static final int OPTION_COUNT = 4;

    private final User user;
    private final QuestionDAO dao = new QuestionDAO();
    private List<Question> questions = new ArrayList<>();

    private final JComboBox<Quiz> cmbQuizzes = new JComboBox<>();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"No.", "Question", "Correct Answer"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);

    private final JTextField txtQuestion = new JTextField();
    private final JTextField[] txtOptions = new JTextField[OPTION_COUNT];
    private final JRadioButton[] rdoCorrect = new JRadioButton[OPTION_COUNT];
    private final ButtonGroup group = new ButtonGroup();

    private final JButton btnAdd = UITheme.createPrimaryButton("➕ Add Question");
    private final JButton btnUpdate = UITheme.createSecondaryButton("✏ Update");
    private final JButton btnDelete = UITheme.createDangerButton("🗑 Delete");
    private final JButton btnClear = UITheme.createSecondaryButton("Clear Form");
    private final JButton btnBack = UITheme.createSecondaryButton("← Back to Dashboard");

    public ManageQuestionsFrame(User user) {
        this.user = user;

        setTitle("Quiz Management System - Manage Questions");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(960, 720);
        setMinimumSize(new Dimension(860, 640));
        setLocationRelativeTo(null);
        UITheme.applyWindowIcon(this);

        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(UITheme.BG_APP);
        root.setBorder(new EmptyBorder(20, 26, 20, 26));

        // 1. Top Section: Header + Theme toggle + Back
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(UITheme.createHeader("Question Management", "Add, edit, and configure multiple-choice options"), BorderLayout.WEST);

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topRight.setOpaque(false);
        topRight.add(UITheme.createThemeToggle(this));
        topRight.add(btnBack);
        top.add(topRight, BorderLayout.EAST);
        root.add(top, BorderLayout.NORTH);

        // 2. Middle Section: Split Table & Quiz Selector
        JPanel centerCard = UITheme.createCardPanel();
        centerCard.setLayout(new BorderLayout(0, 12));

        // Quiz Chooser row
        JPanel chooserRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        chooserRow.setOpaque(false);
        JLabel lblQuiz = new JLabel("Select Quiz:");
        lblQuiz.setFont(UITheme.FONT_BOLD);
        lblQuiz.setForeground(UITheme.TEXT_TITLE);

        cmbQuizzes.setPreferredSize(new Dimension(360, 34));
        cmbQuizzes.setFont(UITheme.FONT_BODY);

        List<Quiz> quizzes = new QuizDAO().getAllQuizzes();
        for (Quiz q : quizzes) cmbQuizzes.addItem(q);

        chooserRow.add(lblQuiz);
        chooserRow.add(cmbQuizzes);
        centerCard.add(chooserRow, BorderLayout.NORTH);

        // Table
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(520);
        table.getColumnModel().getColumn(2).setPreferredWidth(260);
        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSel, boolean hasFoc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(tbl, val, isSel, hasFoc, row, col);
                lbl.setFont(UITheme.FONT_BOLD);
                if (!isSel && val != null && !val.toString().isEmpty()) {
                    lbl.setForeground(UITheme.SUCCESS_DARK);
                    lbl.setBackground(UITheme.SUCCESS_LIGHT);
                }
                return lbl;
            }
        });
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFields();
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, 220));
        centerCard.add(scrollPane, BorderLayout.CENTER);

        root.add(centerCard, BorderLayout.CENTER);

        // 3. Bottom Section: Question & Options Editor
        JPanel formCard = UITheme.createCardPanel();
        formCard.setLayout(new BorderLayout(0, 12));

        JLabel formHeader = new JLabel("Question & Multiple-Choice Options");
        formHeader.setFont(UITheme.FONT_SECTION);
        formHeader.setForeground(UITheme.TEXT_TITLE);
        formCard.add(formHeader, BorderLayout.NORTH);

        JPanel formFields = new JPanel();
        formFields.setLayout(new BoxLayout(formFields, BoxLayout.Y_AXIS));
        formFields.setOpaque(false);

        // Question text input
        UITheme.styleInput(txtQuestion);
        JPanel qRow = UITheme.createFormField("Question Prompt", txtQuestion);
        formFields.add(qRow);
        formFields.add(Box.createVerticalStrut(10));

        // Option instructions
        JLabel optHint = new JLabel("Options (Mark the radio button next to the correct answer):");
        optHint.setFont(UITheme.FONT_BOLD);
        optHint.setForeground(UITheme.TEXT_MUTED);
        formFields.add(optHint);
        formFields.add(Box.createVerticalStrut(6));

        // Options 2x2 grid
        JPanel optGrid = new JPanel(new GridLayout(2, 2, 16, 8));
        optGrid.setOpaque(false);

        for (int i = 0; i < OPTION_COUNT; i++) {
            txtOptions[i] = new JTextField();
            UITheme.styleInput(txtOptions[i]);

            char letter = (char) ('A' + i);
            rdoCorrect[i] = new JRadioButton("Option " + letter + ":");
            rdoCorrect[i].setFont(UITheme.FONT_BOLD);
            rdoCorrect[i].setOpaque(false);
            rdoCorrect[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            group.add(rdoCorrect[i]);

            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setOpaque(false);
            row.add(rdoCorrect[i], BorderLayout.WEST);
            row.add(txtOptions[i], BorderLayout.CENTER);
            optGrid.add(row);
        }
        formFields.add(optGrid);
        formCard.add(formFields, BorderLayout.CENTER);

        // Action Buttons
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonBar.setOpaque(false);
        buttonBar.add(btnClear);
        buttonBar.add(btnDelete);
        buttonBar.add(btnUpdate);
        buttonBar.add(btnAdd);
        formCard.add(buttonBar, BorderLayout.SOUTH);

        root.add(formCard, BorderLayout.SOUTH);

        // Listeners
        btnAdd.addActionListener(e -> addQuestion());
        btnUpdate.addActionListener(e -> updateQuestion());
        btnDelete.addActionListener(e -> deleteQuestion());
        btnClear.addActionListener(e -> clearForm());
        btnBack.addActionListener(e -> goBack());

        if (quizzes.isEmpty()) {
            btnAdd.setEnabled(false);
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);
        }

        cmbQuizzes.addActionListener(e -> {
            clearForm();
            refresh();
        });

        setContentPane(root);
        refresh();
    }

    private Quiz currentQuiz() {
        return (Quiz) cmbQuizzes.getSelectedItem();
    }

    private void refresh() {
        model.setRowCount(0);
        Quiz quiz = currentQuiz();
        if (quiz == null) {
            questions = new ArrayList<>();
            return;
        }
        questions = dao.getQuestionsByQuiz(quiz.getId());
        int number = 1;
        for (Question q : questions) {
            String correct = "";
            for (Option o : q.getOptions()) {
                if (o.isCorrect()) correct = o.getText();
            }
            model.addRow(new Object[]{number++, q.getText(), correct});
        }
    }

    private void fillFields() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= questions.size()) return;

        Question q = questions.get(row);
        txtQuestion.setText(q.getText());
        group.clearSelection();

        for (int i = 0; i < OPTION_COUNT; i++) {
            txtOptions[i].setText("");
            if (i < q.getOptions().size()) {
                Option o = q.getOptions().get(i);
                txtOptions[i].setText(o.getText());
                if (o.isCorrect()) rdoCorrect[i].setSelected(true);
            }
        }
    }

    private void clearForm() {
        table.clearSelection();
        txtQuestion.setText("");
        group.clearSelection();
        for (JTextField t : txtOptions) t.setText("");
    }

    private Question selectedQuestion() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= questions.size()) {
            warn("Please select a question from the table first.");
            return null;
        }
        return questions.get(row);
    }

    private List<Option> readOptions() {
        List<Option> list = new ArrayList<>();
        boolean correctFound = false;

        for (int i = 0; i < OPTION_COUNT; i++) {
            String text = txtOptions[i].getText().trim();
            boolean correct = rdoCorrect[i].isSelected();

            if (text.isEmpty()) {
                if (correct) {
                    warn("The option marked as correct cannot be empty.");
                    return null;
                }
                continue;
            }
            if (correct) correctFound = true;
            list.add(new Option(text, correct));
        }

        if (list.size() < 2) {
            warn("Please provide at least 2 non-empty options.");
            return null;
        }
        if (!correctFound) {
            warn("Please select the radio button for the correct option.");
            return null;
        }
        return list;
    }

    private void addQuestion() {
        Quiz quiz = currentQuiz();
        if (quiz == null) return;

        String text = txtQuestion.getText().trim();
        if (text.isEmpty()) {
            warn("Please enter a question prompt.");
            txtQuestion.requestFocus();
            return;
        }
        List<Option> options = readOptions();
        if (options == null) return;

        if (dao.addQuestion(quiz.getId(), text, options) == -1) {
            warn("Could not save the question.");
            return;
        }
        clearForm();
        refresh();
        JOptionPane.showMessageDialog(this, "Question added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateQuestion() {
        Question q = selectedQuestion();
        if (q == null) return;

        String text = txtQuestion.getText().trim();
        if (text.isEmpty()) {
            warn("The question prompt cannot be empty.");
            return;
        }
        List<Option> options = readOptions();
        if (options == null) return;

        if (!dao.updateQuestion(q.getId(), text, options)) {
            warn("Could not update the question.");
            return;
        }
        clearForm();
        refresh();
        JOptionPane.showMessageDialog(this, "Question updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteQuestion() {
        Question q = selectedQuestion();
        if (q == null) return;

        int choice = JOptionPane.showConfirmDialog(this,
                "Delete this question and its options?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;

        if (!dao.deleteQuestion(q.getId())) {
            warn("Could not delete question.");
            return;
        }
        clearForm();
        refresh();
        JOptionPane.showMessageDialog(this, "Question deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
    }

    private void warn(String message) {
        JOptionPane.showMessageDialog(this, message, "Notice", JOptionPane.WARNING_MESSAGE);
    }

    private void goBack() {
        new DashboardFrame(user).setVisible(true);
        dispose();
    }
}