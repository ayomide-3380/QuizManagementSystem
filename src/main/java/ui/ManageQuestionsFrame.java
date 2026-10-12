package ui;

import dao.QuestionDAO;
import dao.QuizDAO;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Option;
import model.Question;
import model.Quiz;
import model.User;

public class ManageQuestionsFrame extends JFrame {

    private static final int OPTION_COUNT = 4;

    private final User user;
    private final QuestionDAO dao = new QuestionDAO();
    private List<Question> questions = new ArrayList<>();

    private final JComboBox<Quiz> cmbQuizzes = new JComboBox<>();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"No.", "Question", "Correct answer"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);

    private final JTextField txtQuestion = new JTextField();
    private final JTextField[] txtOptions = new JTextField[OPTION_COUNT];
    private final JRadioButton[] rdoCorrect = new JRadioButton[OPTION_COUNT];
    private final ButtonGroup group = new ButtonGroup();

    public ManageQuestionsFrame(User user) {
        this.user = user;

        setTitle("Quiz Management System - Manage Questions");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(820, 680);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 12));
        root.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // ----- Top: title and quiz chooser -----
        JLabel title = new JLabel("Manage questions", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        List<Quiz> quizzes = new QuizDAO().getAllQuizzes();
        for (Quiz q : quizzes) cmbQuizzes.addItem(q);

        JPanel chooser = new JPanel(new BorderLayout(10, 0));
        chooser.add(new JLabel("Quiz:"), BorderLayout.WEST);
        chooser.add(cmbQuizzes, BorderLayout.CENTER);

        JPanel top = new JPanel(new BorderLayout(0, 10));
        top.add(title, BorderLayout.NORTH);
        top.add(chooser, BorderLayout.CENTER);
        root.add(top, BorderLayout.NORTH);

        // ----- Middle: table of questions -----
        table.setRowHeight(26);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFields();
        });
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        // ----- Bottom: form and buttons -----
        JPanel form = new JPanel(new GridLayout(0, 1, 0, 6));

        JPanel qRow = new JPanel(new BorderLayout(10, 0));
        qRow.add(new JLabel("Question:"), BorderLayout.WEST);
        qRow.add(txtQuestion, BorderLayout.CENTER);
        form.add(qRow);

        form.add(new JLabel("Type the options. Tick the radio button beside the CORRECT one:"));

        for (int i = 0; i < OPTION_COUNT; i++) {
            txtOptions[i] = new JTextField();
            rdoCorrect[i] = new JRadioButton((char) ('A' + i) + ".");
            group.add(rdoCorrect[i]);

            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.add(rdoCorrect[i], BorderLayout.WEST);
            row.add(txtOptions[i], BorderLayout.CENTER);
            form.add(row);
        }

        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear");
        JButton btnBack = new JButton("Back");

        btnAdd.addActionListener(e -> addQuestion());
        btnUpdate.addActionListener(e -> updateQuestion());
        btnDelete.addActionListener(e -> deleteQuestion());
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

        if (quizzes.isEmpty()) {
            title.setText("No quizzes yet. Create a quiz first.");
            btnAdd.setEnabled(false);
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);
        }

        // Switching quiz reloads its questions
        cmbQuizzes.addActionListener(e -> { clearForm(); refresh(); });
        refresh();
    }

    private Quiz currentQuiz() {
        return (Quiz) cmbQuizzes.getSelectedItem();
    }

    // Reloads the table from the database
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

    // Copies the selected question into the form so it can be edited
    private void fillFields() {
        int row = table.getSelectedRow();
        if (row < 0) return;

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
        if (row < 0) {
            warn("Please select a question in the table first.");
            return null;
        }
        return questions.get(row);
    }

    // Reads and checks the four option boxes. Returns null (after a warning) if something is wrong.
    private List<Option> readOptions() {
        List<Option> list = new ArrayList<>();
        boolean correctFound = false;

        for (int i = 0; i < OPTION_COUNT; i++) {
            String text = txtOptions[i].getText().trim();
            boolean correct = rdoCorrect[i].isSelected();

            if (text.isEmpty()) {
                if (correct) {
                    warn("The option you marked as correct is empty.");
                    return null;
                }
                continue;
            }
            if (correct) correctFound = true;
            list.add(new Option(text, correct));
        }

        if (list.size() < 2) {
            warn("Please enter at least 2 options.");
            return null;
        }
        if (!correctFound) {
            warn("Please tick the radio button beside the correct answer.");
            return null;
        }
        return list;
    }

    private void addQuestion() {
        Quiz quiz = currentQuiz();
        if (quiz == null) return;

        String text = txtQuestion.getText().trim();
        if (text.isEmpty()) {
            warn("Please type the question.");
            return;
        }
        List<Option> options = readOptions();
        if (options == null) return;

        if (dao.addQuestion(quiz.getId(), text, options) == -1) {
            warn("Could not save the question. Is MySQL running?");
            return;
        }
        clearForm();
        refresh();
    }

    private void updateQuestion() {
        Question q = selectedQuestion();
        if (q == null) return;

        String text = txtQuestion.getText().trim();
        if (text.isEmpty()) {
            warn("The question cannot be empty.");
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
    }

    private void deleteQuestion() {
        Question q = selectedQuestion();
        if (q == null) return;

        int choice = JOptionPane.showConfirmDialog(this,
                "Delete this question and its options?",
                "Delete question", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;

        if (!dao.deleteQuestion(q.getId())) {
            warn("Could not delete this question. Students may already have answered it.");
            return;
        }
        clearForm();
        refresh();
    }

    private void warn(String message) {
        JOptionPane.showMessageDialog(this, message, "Manage Questions", JOptionPane.WARNING_MESSAGE);
    }

    private void goBack() {
        new DashboardFrame(user).setVisible(true);
        dispose();
    }
}