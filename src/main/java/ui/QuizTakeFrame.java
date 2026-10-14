package ui;

import dao.AttemptDAO;
import dao.QuestionDAO;
import dao.QuizDAO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import model.Attempt;
import model.Option;
import model.Question;
import model.Quiz;
import model.User;

/**
 * Enterprise Quiz Taking Experience with dynamic topic gallery, live timer,
 * interactive question jump strip, card-based option selectors, and scorecard celebration.
 */
public class QuizTakeFrame extends JFrame {

    private final User user;
    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);

    // Screen 1: Quiz Gallery
    private final JTextField txtSearchQuiz = new JTextField();
    private final JPanel quizListPanel = new JPanel();
    private final JButton btnStartQuiz = UITheme.createPrimaryButton("🚀 Begin Assessment");
    private final JButton btnGalleryBack = UITheme.createSecondaryButton("← Back to Dashboard");
    private List<Quiz> allQuizzes = new ArrayList<>();
    private Quiz selectedQuiz = null;
    private final Map<Quiz, JPanel> quizCardMap = new HashMap<>();

    // Screen 2: Active Assessment
    private final JLabel lblQuizTitle = new JLabel();
    private final JLabel lblProgressText = new JLabel();
    private final JLabel lblTimer = new JLabel("⏱ 00:00");
    private final JProgressBar progressBar = new JProgressBar();
    private final JPanel questionNavStrip = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
    private final JTextArea txtQuestion = new JTextArea(3, 40);
    private final JPanel optionsContainer = new JPanel();
    private final JButton btnPrev = UITheme.createSecondaryButton("← Previous");
    private final JButton btnNext = UITheme.createSecondaryButton("Next →");
    private final JButton btnSubmit = UITheme.createSuccessButton("✓ Submit Answers");
    private final JLabel lblAnsweredCount = new JLabel();

    // Assessment State
    private List<Question> questions = new ArrayList<>();
    private int currentIndex = 0;
    private final Map<Integer, Integer> selectedAnswers = new HashMap<>(); // questionId -> optionId
    private final List<JRadioButton> optionRadios = new ArrayList<>();
    private Timer assessmentTimer;
    private int secondsElapsed = 0;

    public QuizTakeFrame(User user) {
        this.user = user;

        setTitle("Quiz Management System - Assessment Center");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(980, 740);
        setMinimumSize(new Dimension(880, 640));
        setLocationRelativeTo(null);
        UITheme.applyWindowIcon(this);

        root.add(buildGalleryPanel(), "gallery");
        root.add(buildQuizTakingPanel(), "taking");
        setContentPane(root);
        cards.show(root, "gallery");
    }

    // ==================== SCREEN 1: QUIZ GALLERY ====================

    private JPanel buildGalleryPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(UITheme.BG_APP);
        p.setBorder(new EmptyBorder(22, 30, 22, 30));

        // Top Header
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(UITheme.createHeader("Assessment Center", "Select an available topic from the curriculum to begin"), BorderLayout.WEST);

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topRight.setOpaque(false);
        topRight.add(UITheme.createThemeToggle(this));
        topRight.add(btnGalleryBack);
        top.add(topRight, BorderLayout.EAST);
        p.add(top, BorderLayout.NORTH);

        // Center Content Card with Search & Scrollable Grid
        JPanel centerCard = UITheme.createCardPanel();
        centerCard.setLayout(new BorderLayout(0, 14));

        // Search Bar
        JPanel searchBar = new JPanel(new BorderLayout(10, 0));
        searchBar.setOpaque(false);
        JLabel lblSearch = new JLabel("🔍 Filter Topics:");
        lblSearch.setFont(UITheme.FONT_BOLD);
        lblSearch.setForeground(UITheme.TEXT_MUTED);

        UITheme.styleInput(txtSearchQuiz);
        txtSearchQuiz.putClientProperty("JTextField.placeholderText", "Search quizzes by title or keywords...");
        searchBar.add(lblSearch, BorderLayout.WEST);
        searchBar.add(txtSearchQuiz, BorderLayout.CENTER);
        centerCard.add(searchBar, BorderLayout.NORTH);

        // Quiz Cards Container
        quizListPanel.setLayout(new BoxLayout(quizListPanel, BoxLayout.Y_AXIS));
        quizListPanel.setOpaque(false);

        allQuizzes = new QuizDAO().getAllQuizzes();
        renderQuizCards("");

        txtSearchQuiz.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filter(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filter(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() { renderQuizCards(txtSearchQuiz.getText().trim()); }
        });

        JScrollPane scrollPane = new JScrollPane(quizListPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        centerCard.add(scrollPane, BorderLayout.CENTER);

        // Bottom Bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottomBar.setOpaque(false);
        btnStartQuiz.setEnabled(false);
        bottomBar.add(btnStartQuiz);
        centerCard.add(bottomBar, BorderLayout.SOUTH);

        p.add(centerCard, BorderLayout.CENTER);

        btnStartQuiz.addActionListener(e -> launchQuiz());
        btnGalleryBack.addActionListener(e -> goBack());

        return p;
    }

    private void renderQuizCards(String query) {
        quizListPanel.removeAll();
        quizCardMap.clear();

        QuestionDAO questionDAO = new QuestionDAO();
        int matchingCount = 0;

        for (Quiz q : allQuizzes) {
            if (!query.isEmpty() && !q.getTitle().toLowerCase().contains(query.toLowerCase())
                    && (q.getDescription() == null || !q.getDescription().toLowerCase().contains(query.toLowerCase()))) {
                continue;
            }

            matchingCount++;
            int qCount = questionDAO.getQuestionCountByQuiz(q.getId());

            JPanel card = new JPanel(new BorderLayout(14, 6));
            card.setBackground(selectedQuiz != null && selectedQuiz.getId() == q.getId() ? UITheme.PRIMARY_LIGHT : UITheme.BG_CARD);
            card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(selectedQuiz != null && selectedQuiz.getId() == q.getId() ? UITheme.PRIMARY : UITheme.BORDER, 1, true),
                new EmptyBorder(14, 18, 14, 18)
            ));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 86));
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JPanel left = new JPanel();
            left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
            left.setOpaque(false);

            JLabel title = new JLabel(q.getTitle());
            title.setFont(UITheme.FONT_SECTION);
            title.setForeground(UITheme.TEXT_TITLE);

            String desc = q.getDescription() != null && !q.getDescription().isEmpty()
                    ? q.getDescription() : "Comprehensive topic evaluation assessment.";
            JLabel sub = new JLabel(desc);
            sub.setFont(UITheme.FONT_SMALL);
            sub.setForeground(UITheme.TEXT_MUTED);

            left.add(title);
            left.add(Box.createVerticalStrut(3));
            left.add(sub);

            // Right Badges
            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
            right.setOpaque(false);

            JLabel countBadge = UITheme.createChip(qCount + " Questions", UITheme.PRIMARY_LIGHT, UITheme.PRIMARY);
            right.add(countBadge);

            card.add(left, BorderLayout.CENTER);
            card.add(right, BorderLayout.EAST);

            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectedQuiz = q;
                    btnStartQuiz.setEnabled(qCount > 0);
                    btnStartQuiz.setText("🚀 Begin: " + q.getTitle());
                    updateCardSelectionVisuals();
                }
            });

            quizCardMap.put(q, card);
            quizListPanel.add(card);
            quizListPanel.add(Box.createVerticalStrut(10));
        }

        if (matchingCount == 0) {
            JLabel empty = new JLabel("🔍 No quizzes matching your search keyword.", SwingConstants.CENTER);
            empty.setFont(UITheme.FONT_SUBTITLE);
            empty.setForeground(UITheme.TEXT_MUTED);
            empty.setBorder(new EmptyBorder(30, 0, 30, 0));
            quizListPanel.add(empty);
        }

        quizListPanel.revalidate();
        quizListPanel.repaint();
    }

    private void updateCardSelectionVisuals() {
        for (Map.Entry<Quiz, JPanel> entry : quizCardMap.entrySet()) {
            boolean isSel = (selectedQuiz != null && selectedQuiz.getId() == entry.getKey().getId());
            JPanel c = entry.getValue();
            c.setBackground(isSel ? UITheme.PRIMARY_LIGHT : UITheme.BG_CARD);
            c.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(isSel ? UITheme.PRIMARY : UITheme.BORDER, isSel ? 2 : 1, true),
                new EmptyBorder(isSel ? 13 : 14, isSel ? 17 : 18, isSel ? 13 : 14, isSel ? 17 : 18)
            ));
        }
    }

    private void launchQuiz() {
        if (selectedQuiz == null) return;

        questions = new QuestionDAO().getQuestionsByQuiz(selectedQuiz.getId());
        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "This quiz currently contains no questions.",
                    "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        selectedAnswers.clear();
        currentIndex = 0;
        secondsElapsed = 0;

        if (assessmentTimer != null) assessmentTimer.stop();
        assessmentTimer = new Timer(1000, e -> {
            secondsElapsed++;
            int m = secondsElapsed / 60;
            int s = secondsElapsed % 60;
            lblTimer.setText(String.format("⏱ %02d:%02d", m, s));
        });
        assessmentTimer.start();

        showActiveQuestion();
        cards.show(root, "taking");
    }

    // ==================== SCREEN 2: ACTIVE ASSESSMENT ====================

    private JPanel buildQuizTakingPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 14));
        p.setBackground(UITheme.BG_APP);
        p.setBorder(new EmptyBorder(18, 28, 18, 28));

        // Top Header & Progress Bar
        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.setOpaque(false);

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);

        lblQuizTitle.setFont(UITheme.FONT_TITLE_LARGE);
        lblQuizTitle.setForeground(UITheme.TEXT_TITLE);

        JPanel progressEast = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        progressEast.setOpaque(false);

        lblTimer.setFont(UITheme.FONT_BOLD);
        lblTimer.setForeground(UITheme.TEXT_TITLE);
        lblTimer.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UITheme.BORDER, 1, true),
            new EmptyBorder(4, 10, 4, 10)
        ));

        lblProgressText.setFont(UITheme.FONT_BOLD);
        lblProgressText.setForeground(UITheme.PRIMARY);

        progressEast.add(lblTimer);
        progressEast.add(UITheme.createThemeToggle(this));
        progressEast.add(lblProgressText);

        titleRow.add(lblQuizTitle, BorderLayout.WEST);
        titleRow.add(progressEast, BorderLayout.EAST);

        progressBar.setStringPainted(false);
        progressBar.setPreferredSize(new Dimension(progressBar.getPreferredSize().width, 8));
        progressBar.setForeground(UITheme.PRIMARY);
        progressBar.setBackground(new Color(226, 232, 240));

        top.add(titleRow, BorderLayout.NORTH);
        top.add(progressBar, BorderLayout.SOUTH);
        p.add(top, BorderLayout.NORTH);

        // Center Question & Option Cards
        JPanel centerCard = UITheme.createCardPanel();
        centerCard.setLayout(new BorderLayout(0, 14));

        // Question Navigation Strip (Pills 1..N)
        questionNavStrip.setOpaque(false);
        JScrollPane navScroll = new JScrollPane(questionNavStrip);
        navScroll.setBorder(null);
        navScroll.setOpaque(false);
        navScroll.getViewport().setOpaque(false);
        navScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        navScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        centerCard.add(navScroll, BorderLayout.NORTH);

        // Question Statement
        txtQuestion.setEditable(false);
        txtQuestion.setLineWrap(true);
        txtQuestion.setWrapStyleWord(true);
        txtQuestion.setOpaque(false);
        txtQuestion.setFont(new Font("Segoe UI", Font.BOLD, 17));
        txtQuestion.setForeground(UITheme.TEXT_TITLE);
        txtQuestion.setBorder(new EmptyBorder(6, 6, 10, 6));

        JPanel middleStack = new JPanel(new BorderLayout(0, 12));
        middleStack.setOpaque(false);
        middleStack.add(txtQuestion, BorderLayout.NORTH);

        optionsContainer.setLayout(new BoxLayout(optionsContainer, BoxLayout.Y_AXIS));
        optionsContainer.setOpaque(false);
        middleStack.add(optionsContainer, BorderLayout.CENTER);

        centerCard.add(middleStack, BorderLayout.CENTER);
        p.add(centerCard, BorderLayout.CENTER);

        // Bottom Controls
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);

        lblAnsweredCount.setFont(UITheme.FONT_SUBTITLE);
        lblAnsweredCount.setForeground(UITheme.TEXT_MUTED);
        bottom.add(lblAnsweredCount, BorderLayout.WEST);

        JPanel navButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        navButtons.setOpaque(false);
        navButtons.add(btnPrev);
        navButtons.add(btnNext);
        navButtons.add(btnSubmit);
        bottom.add(navButtons, BorderLayout.EAST);

        p.add(bottom, BorderLayout.SOUTH);

        btnPrev.addActionListener(e -> { saveCurrentAnswer(); currentIndex--; showActiveQuestion(); });
        btnNext.addActionListener(e -> { saveCurrentAnswer(); currentIndex++; showActiveQuestion(); });
        btnSubmit.addActionListener(e -> submitAssessment());

        return p;
    }

    private void updateQuestionNavStrip() {
        questionNavStrip.removeAll();
        for (int i = 0; i < questions.size(); i++) {
            final int targetIndex = i;
            boolean isCurrent = (i == currentIndex);
            boolean isAnswered = selectedAnswers.containsKey(questions.get(i).getId());

            String text = isAnswered ? "✓ " + (i + 1) : String.valueOf(i + 1);
            JButton btn = new JButton(text);
            btn.setFont(UITheme.FONT_BADGE);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            if (isCurrent) {
                btn.setBackground(UITheme.PRIMARY);
                btn.setForeground(Color.WHITE);
                btn.setBorder(new LineBorder(UITheme.PRIMARY_DARK, 2, true));
            } else if (isAnswered) {
                btn.setBackground(UITheme.SUCCESS_LIGHT);
                btn.setForeground(UITheme.SUCCESS_DARK);
                btn.setBorder(new LineBorder(UITheme.SUCCESS, 1, true));
            } else {
                btn.setBackground(UITheme.BG_CARD);
                btn.setForeground(UITheme.TEXT_MUTED);
                btn.setBorder(new LineBorder(UITheme.BORDER, 1, true));
            }

            btn.setPreferredSize(new Dimension(50, 32));
            btn.addActionListener(e -> {
                saveCurrentAnswer();
                currentIndex = targetIndex;
                showActiveQuestion();
            });

            questionNavStrip.add(btn);
        }
        questionNavStrip.revalidate();
        questionNavStrip.repaint();
    }

    private void showActiveQuestion() {
        Question q = questions.get(currentIndex);

        lblQuizTitle.setText(selectedQuiz.getTitle());
        lblProgressText.setText("Question " + (currentIndex + 1) + " of " + questions.size());

        int pct = (int) (((currentIndex + 1) / (double) questions.size()) * 100);
        progressBar.setValue(pct);

        txtQuestion.setText((currentIndex + 1) + ". " + q.getText());

        optionsContainer.removeAll();
        optionRadios.clear();
        ButtonGroup group = new ButtonGroup();
        Integer savedChoice = selectedAnswers.get(q.getId());

        char optLetter = 'A';
        for (Option o : q.getOptions()) {
            JPanel card = new JPanel(new BorderLayout(14, 0));
            boolean isChecked = (savedChoice != null && savedChoice == o.getId());

            card.setBackground(isChecked ? UITheme.PRIMARY_LIGHT : UITheme.BG_CARD);
            card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(isChecked ? UITheme.PRIMARY : UITheme.BORDER, isChecked ? 2 : 1, true),
                new EmptyBorder(12, 16, 12, 16)
            ));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Circular letter badge
            JLabel badge = new JLabel(String.valueOf(optLetter), SwingConstants.CENTER);
            badge.setFont(UITheme.FONT_BOLD);
            badge.setPreferredSize(new Dimension(28, 28));
            badge.setOpaque(true);
            badge.setBackground(isChecked ? UITheme.PRIMARY : new Color(226, 232, 240));
            badge.setForeground(isChecked ? Color.WHITE : UITheme.TEXT_TITLE);
            badge.setBorder(new LineBorder(isChecked ? UITheme.PRIMARY : UITheme.BORDER_STRONG, 1, true));

            JRadioButton rb = new JRadioButton("  " + o.getText());
            rb.setFont(UITheme.FONT_BODY);
            rb.setForeground(UITheme.TEXT_TITLE);
            rb.setOpaque(false);
            if (isChecked) rb.setSelected(true);

            group.add(rb);
            optionRadios.add(rb);

            final Option optionRef = o;
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    rb.setSelected(true);
                    selectedAnswers.put(q.getId(), optionRef.getId());
                    refreshOptionStyles(q.getId());
                    updateQuestionNavStrip();
                }
            });
            rb.addActionListener(e -> {
                selectedAnswers.put(q.getId(), optionRef.getId());
                refreshOptionStyles(q.getId());
                updateQuestionNavStrip();
            });

            card.add(badge, BorderLayout.WEST);
            card.add(rb, BorderLayout.CENTER);
            optionsContainer.add(card);
            optionsContainer.add(Box.createVerticalStrut(10));
            optLetter++;
        }

        btnPrev.setEnabled(currentIndex > 0);
        btnNext.setEnabled(currentIndex < questions.size() - 1);

        int answered = selectedAnswers.size();
        lblAnsweredCount.setText("Answered: " + answered + " of " + questions.size() + " questions");

        updateQuestionNavStrip();
        optionsContainer.revalidate();
        optionsContainer.repaint();
    }

    private void refreshOptionStyles(int questionId) {
        Question q = questions.get(currentIndex);
        Integer chosen = selectedAnswers.get(q.getId());

        for (int i = 0; i < q.getOptions().size(); i++) {
            Option o = q.getOptions().get(i);
            JPanel card = (JPanel) optionsContainer.getComponent(i * 2);
            JLabel badge = (JLabel) card.getComponent(0);
            boolean isChosen = (chosen != null && chosen == o.getId());

            card.setBackground(isChosen ? UITheme.PRIMARY_LIGHT : UITheme.BG_CARD);
            card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(isChosen ? UITheme.PRIMARY : UITheme.BORDER, isChosen ? 2 : 1, true),
                new EmptyBorder(12, 16, 12, 16)
            ));
            badge.setBackground(isChosen ? UITheme.PRIMARY : new Color(226, 232, 240));
            badge.setForeground(isChosen ? Color.WHITE : UITheme.TEXT_TITLE);
        }

        lblAnsweredCount.setText("Answered: " + selectedAnswers.size() + " of " + questions.size() + " questions");
    }

    private void saveCurrentAnswer() {
        Question q = questions.get(currentIndex);
        for (int i = 0; i < optionRadios.size(); i++) {
            if (optionRadios.get(i).isSelected()) {
                selectedAnswers.put(q.getId(), q.getOptions().get(i).getId());
                return;
            }
        }
    }

    private void submitAssessment() {
        saveCurrentAnswer();
        if (assessmentTimer != null) assessmentTimer.stop();

        int unanswered = questions.size() - selectedAnswers.size();
        String confirmMsg = unanswered > 0
                ? "You have " + unanswered + " unanswered question" + (unanswered == 1 ? "" : "s") + ".\nDo you want to submit anyway?"
                : "Are you ready to submit your assessment?";

        int res = JOptionPane.showConfirmDialog(this, confirmMsg, "Confirm Assessment Submission",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (res != JOptionPane.YES_OPTION) {
            if (assessmentTimer != null) assessmentTimer.start();
            return;
        }

        AttemptDAO dao = new AttemptDAO();
        int attemptId = dao.submitAttempt(user.getId(), selectedQuiz.getId(), selectedAnswers);

        if (attemptId == -1) {
            JOptionPane.showMessageDialog(this, "Could not record attempt. Is MySQL running?",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (Attempt a : dao.getAttemptsByUser(user.getId())) {
            if (a.getId() == attemptId) {
                int pct = (int) Math.round(a.getPercentage());
                String gradeEmoji = pct >= 80 ? "🏆" : pct >= 60 ? "👏" : "📚";
                String verdict = pct >= 80 ? "Outstanding Mastery!"
                               : pct >= 60 ? "Proficient Performance!"
                               : "Needs Revision";

                int m = secondsElapsed / 60;
                int s = secondsElapsed % 60;
                String timeStr = String.format("%02d:%02d", m, s);

                String message = gradeEmoji + " " + verdict + "\n\n"
                        + "Assessment: " + a.getQuizTitle() + "\n"
                        + "Score: " + a.getScore() + " / " + a.getTotal() + " (" + pct + "%)\n"
                        + "Duration: " + timeStr + "\n\n"
                        + "Your submission has been recorded in your gradebook transcript.";

                JOptionPane.showMessageDialog(this, message, "Assessment Completed", JOptionPane.INFORMATION_MESSAGE);
                break;
            }
        }

        goBack();
    }

    private void goBack() {
        if (assessmentTimer != null) assessmentTimer.stop();
        new DashboardFrame(user).setVisible(true);
        dispose();
    }
}