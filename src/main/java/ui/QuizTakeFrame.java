package ui;

import dao.AttemptDAO;
import dao.QuestionDAO;
import dao.QuizDAO;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import model.Attempt;
import model.Option;
import model.Question;
import model.Quiz;
import model.User;

public class QuizTakeFrame extends JFrame {

    private final User user;
    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);

    // Screen 1: choose a quiz
    private final JComboBox<Quiz> cmbQuizzes = new JComboBox<>();
    private final JButton btnStart = new JButton("Start Quiz");

    // Screen 2: answer questions
    private final JLabel lblProgress = new JLabel("", SwingConstants.CENTER);
    private final JTextArea txtQuestion = new JTextArea(3, 30);
    private final JPanel optionsPanel = new JPanel(new GridLayout(0, 1, 0, 8));
    private final JButton btnPrev = new JButton("Previous");
    private final JButton btnNext = new JButton("Next");
    private final JButton btnSubmit = new JButton("Submit Quiz");

    // Quiz state
    private Quiz currentQuiz;
    private List<Question> questions = new ArrayList<>();
    private int index = 0;
    private final Map<Integer, Integer> selected = new HashMap<>();   // question id -> option id
    private final List<JRadioButton> radios = new ArrayList<>();

    public QuizTakeFrame(User user) {
        this.user = user;

        setTitle("Quiz Management System - Take Quiz");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(580, 460);
        setLocationRelativeTo(null);

        root.add(buildSelectPanel(), "select");
        root.add(buildQuizPanel(), "quiz");
        setContentPane(root);
        cards.show(root, "select");
    }

    // ---------- Screen 1 ----------

    private JPanel buildSelectPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 20));
        p.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("Choose a quiz", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        p.add(title, BorderLayout.NORTH);

        List<Quiz> quizzes = new QuizDAO().getAllQuizzes();
        for (Quiz q : quizzes) cmbQuizzes.addItem(q);

        JPanel center = new JPanel(new GridBagLayout());
        center.add(cmbQuizzes);
        p.add(center, BorderLayout.CENTER);

        if (quizzes.isEmpty()) {
            title.setText("No quizzes are available yet");
            btnStart.setEnabled(false);
        }

        JButton btnBack = new JButton("Back to dashboard");
        btnStart.addActionListener(e -> startQuiz());
        btnBack.addActionListener(e -> goBack());

        JPanel bottom = new JPanel(new GridLayout(1, 2, 10, 0));
        bottom.add(btnStart);
        bottom.add(btnBack);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private void startQuiz() {
        currentQuiz = (Quiz) cmbQuizzes.getSelectedItem();
        if (currentQuiz == null) return;

        questions = new QuestionDAO().getQuestionsByQuiz(currentQuiz.getId());
        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "This quiz has no questions yet.",
                    "Take Quiz", JOptionPane.WARNING_MESSAGE);
            return;
        }

        selected.clear();
        index = 0;
        showQuestion();
        cards.show(root, "quiz");
    }

    // ---------- Screen 2 ----------

    private JPanel buildQuizPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 15));
        p.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        lblProgress.setFont(new Font("Segoe UI", Font.BOLD, 14));
        p.add(lblProgress, BorderLayout.NORTH);

        txtQuestion.setEditable(false);
        txtQuestion.setLineWrap(true);
        txtQuestion.setWrapStyleWord(true);
        txtQuestion.setOpaque(false);
        txtQuestion.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JPanel center = new JPanel(new BorderLayout(0, 15));
        center.add(txtQuestion, BorderLayout.NORTH);
        center.add(optionsPanel, BorderLayout.CENTER);
        p.add(center, BorderLayout.CENTER);

        btnPrev.addActionListener(e -> { saveSelection(); index--; showQuestion(); });
        btnNext.addActionListener(e -> { saveSelection(); index++; showQuestion(); });
        btnSubmit.addActionListener(e -> submit());

        JPanel bottom = new JPanel(new GridLayout(1, 3, 10, 0));
        bottom.add(btnPrev);
        bottom.add(btnNext);
        bottom.add(btnSubmit);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private void showQuestion() {
        Question q = questions.get(index);

        lblProgress.setText(currentQuiz.getTitle() + "  -  Question "
                + (index + 1) + " of " + questions.size());
        txtQuestion.setText(q.getText());

        optionsPanel.removeAll();
        radios.clear();
        ButtonGroup group = new ButtonGroup();
        Integer previous = selected.get(q.getId());

        for (Option o : q.getOptions()) {
            JRadioButton rb = new JRadioButton(o.getText());
            rb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            if (previous != null && previous == o.getId()) rb.setSelected(true);
            group.add(rb);
            radios.add(rb);
            optionsPanel.add(rb);
        }

        btnPrev.setEnabled(index > 0);
        btnNext.setEnabled(index < questions.size() - 1);
        optionsPanel.revalidate();
        optionsPanel.repaint();
    }

    // Remembers which option is ticked for the question on screen
    private void saveSelection() {
        Question q = questions.get(index);
        for (int i = 0; i < radios.size(); i++) {
            if (radios.get(i).isSelected()) {
                selected.put(q.getId(), q.getOptions().get(i).getId());
                return;
            }
        }
    }

    private void submit() {
        saveSelection();

        int unanswered = questions.size() - selected.size();
        String message = unanswered > 0
                ? "You have " + unanswered + " unanswered question(s). Submit anyway?"
                : "Submit your answers now?";
        int choice = JOptionPane.showConfirmDialog(this, message, "Submit Quiz",
                JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;

        AttemptDAO attemptDAO = new AttemptDAO();
        int attemptId = attemptDAO.submitAttempt(user.getId(), currentQuiz.getId(), selected);

        if (attemptId == -1) {
            JOptionPane.showMessageDialog(this, "Could not save your result. Is MySQL running?",
                    "Submit Quiz", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (Attempt a : attemptDAO.getAttemptsByUser(user.getId())) {
            if (a.getId() == attemptId) {
                JOptionPane.showMessageDialog(this,
                        "You scored " + a.getScore() + " out of " + a.getTotal()
                        + " (" + Math.round(a.getPercentage()) + "%)",
                        "Result", JOptionPane.INFORMATION_MESSAGE);
                break;
            }
        }
        goBack();
    }

    private void goBack() {
        new DashboardFrame(user).setVisible(true);
        dispose();
    }
}