package ui;

import dao.AttemptDAO;
import dao.QuestionDAO;
import dao.QuizDAO;
import dao.UserDAO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import model.User;

/**
 * Ultra-refined Dashboard featuring gradient hero banner, KPI metric cards,
 * and categorized action cards.
 */
public class DashboardFrame extends JFrame {

    private final User currentUser;

    public DashboardFrame(User user) {
        this.currentUser = user;

        setTitle("Quiz Management System - " + (user.isAdmin() ? "Admin Console" : "Student Portal"));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(960, 720);
        setMinimumSize(new Dimension(880, 640));
        setLocationRelativeTo(null);
        UITheme.applyWindowIcon(this);

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(UITheme.BG_APP);
        root.setBorder(new EmptyBorder(22, 28, 22, 28));

        // 1. Top Section: Hero Gradient Banner with User Details & Controls
        root.add(buildHeroBanner(), BorderLayout.NORTH);

        // 2. Center: KPI Metrics + Categorized Action Cards
        JPanel center = new JPanel(new BorderLayout(0, 18));
        center.setOpaque(false);

        center.add(buildStatsPanel(), BorderLayout.NORTH);
        center.add(buildActionsPanel(), BorderLayout.CENTER);

        root.add(center, BorderLayout.CENTER);

        // 3. Footer
        root.add(buildFooter(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JPanel buildHeroBanner() {
        JPanel hero = new JPanel(new BorderLayout(16, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint paint = new GradientPaint(
                    0, 0, new Color(30, 58, 138),
                    getWidth(), getHeight(), new Color(37, 99, 235)
                );
                g2.setPaint(paint);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        hero.setOpaque(false);
        hero.setBorder(new EmptyBorder(18, 24, 18, 24));

        // Left Avatar & Title
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        left.setOpaque(false);

        // Circular Monogram Initial
        String initials = currentUser.getFullName().length() >= 2
                ? currentUser.getFullName().substring(0, 1).toUpperCase()
                : "U";
        JLabel avatar = new JLabel(initials, SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        avatar.setForeground(UITheme.PRIMARY_DARK);
        avatar.setBackground(Color.WHITE);
        avatar.setOpaque(true);
        avatar.setPreferredSize(new Dimension(46, 46));
        avatar.setBorder(new LineBorder(new Color(255, 255, 255, 120), 2, true));

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);

        JPanel roleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        roleRow.setOpaque(false);

        JLabel lblGreeting = new JLabel("Welcome back,");
        lblGreeting.setFont(UITheme.FONT_SUBTITLE);
        lblGreeting.setForeground(new Color(191, 219, 254));

        JLabel roleBadge = new JLabel("  " + (currentUser.isAdmin() ? "ADMINISTRATOR" : "STUDENT") + "  ");
        roleBadge.setFont(UITheme.FONT_BADGE);
        roleBadge.setForeground(Color.WHITE);
        roleBadge.setBackground(currentUser.isAdmin() ? new Color(99, 102, 241, 180) : new Color(16, 185, 129, 180));
        roleBadge.setOpaque(true);
        roleBadge.setBorder(new LineBorder(new Color(255, 255, 255, 80), 1, true));

        roleRow.add(lblGreeting);
        roleRow.add(roleBadge);

        JLabel lblName = new JLabel(currentUser.getFullName());
        lblName.setFont(UITheme.FONT_HERO);
        lblName.setForeground(Color.WHITE);

        text.add(roleRow);
        text.add(Box.createVerticalStrut(2));
        text.add(lblName);

        left.add(avatar);
        left.add(text);
        hero.add(left, BorderLayout.WEST);

        // Right Navigation Action Buttons
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        right.setOpaque(false);

        JButton btnTheme = UITheme.createThemeToggle(this);
        btnTheme.setForeground(Color.WHITE);
        btnTheme.setBackground(new Color(255, 255, 255, 45));
        btnTheme.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(255, 255, 255, 90), 1, true),
            new EmptyBorder(7, 14, 7, 14)
        ));

        JButton btnSettings = new JButton("⚙ Account");
        btnSettings.setFont(UITheme.FONT_BOLD);
        btnSettings.setForeground(Color.WHITE);
        btnSettings.setBackground(new Color(255, 255, 255, 45));
        btnSettings.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(255, 255, 255, 90), 1, true),
            new EmptyBorder(7, 14, 7, 14)
        ));
        btnSettings.setFocusPainted(false);
        btnSettings.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSettings.addActionListener(e -> {
            new SettingsFrame(currentUser).setVisible(true);
            dispose();
        });

        JButton btnLogout = new JButton("🚪 Sign Out");
        btnLogout.setFont(UITheme.FONT_BOLD);
        btnLogout.setForeground(new Color(254, 202, 202));
        btnLogout.setBackground(new Color(220, 38, 38, 160));
        btnLogout.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(254, 202, 202, 120), 1, true),
            new EmptyBorder(7, 14, 7, 14)
        ));
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> {
            int res = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to sign out?",
                    "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });

        right.add(btnSettings);
        right.add(btnLogout);
        hero.add(right, BorderLayout.EAST);

        return hero;
    }

    private JPanel buildStatsPanel() {
        JPanel stats = new JPanel(new GridLayout(1, 0, 16, 0));
        stats.setOpaque(false);

        QuizDAO quizDAO = new QuizDAO();
        AttemptDAO attemptDAO = new AttemptDAO();

        if (currentUser.isAdmin()) {
            UserDAO userDAO = new UserDAO();
            QuestionDAO questionDAO = new QuestionDAO();

            int quizzes = quizDAO.getQuizCount();
            int questions = questionDAO.getTotalQuestionCount();
            int students = userDAO.getStudentCount();
            int attempts = attemptDAO.getTotalAttemptCount();

            stats.add(UITheme.createStatCard("ACTIVE QUIZZES", String.valueOf(quizzes), "Available topics", UITheme.PRIMARY, "📚"));
            stats.add(UITheme.createStatCard("QUESTION BANK", String.valueOf(questions), "Questions in system", UITheme.INFO, "❓"));
            stats.add(UITheme.createStatCard("STUDENT BODY", String.valueOf(students), "Active learners", UITheme.SUCCESS, "👥"));
            stats.add(UITheme.createStatCard("TOTAL SUBMISSIONS", String.valueOf(attempts), "Evaluated tests", UITheme.WARNING, "📈"));
        } else {
            int quizzes = quizDAO.getQuizCount();
            int attempts = attemptDAO.getAttemptCountByUser(currentUser.getId());
            double avgScore = attemptDAO.getAverageScoreForUser(currentUser.getId());

            stats.add(UITheme.createStatCard("AVAILABLE QUIZZES", String.valueOf(quizzes), "Ready to challenge", UITheme.PRIMARY, "📚"));
            stats.add(UITheme.createStatCard("COMPLETED TESTS", String.valueOf(attempts), "Your total submissions", UITheme.SUCCESS, "✅"));
            stats.add(UITheme.createStatCard("AVERAGE PERFORMANCE", Math.round(avgScore) + "%", "Overall average grade", UITheme.WARNING, "🏆"));
        }

        return stats;
    }

    private JPanel buildActionsPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 12));
        wrapper.setOpaque(false);

        JLabel sectionTitle = new JLabel("System Modules & Operations");
        sectionTitle.setFont(UITheme.FONT_SECTION);
        sectionTitle.setForeground(UITheme.TEXT_TITLE);
        wrapper.add(sectionTitle, BorderLayout.NORTH);

        int cols = currentUser.isAdmin() ? 3 : 2;
        JPanel grid = new JPanel(new GridLayout(0, cols, 16, 16));
        grid.setOpaque(false);

        if (currentUser.isAdmin()) {
            grid.add(createActionCard("CURRICULUM", "📚 Manage Quizzes", "Create, edit, search, and organize quizzes",
                    () -> new ManageQuizzesFrame(currentUser)));
            grid.add(createActionCard("QUESTION BANK", "❓ Manage Questions", "Author multiple-choice questions & set correct answers",
                    () -> new ManageQuestionsFrame(currentUser)));
            grid.add(createActionCard("ACCOUNTS", "👥 Manage Users", "Administer student credentials, passwords, and permissions",
                    () -> new ManageUsersFrame(currentUser)));
            grid.add(createActionCard("SIMULATION", "🚀 Preview Quizzes", "Test and experience quizzes as a student participant",
                    () -> new QuizTakeFrame(currentUser)));
            grid.add(createActionCard("ANALYTICS", "📊 Student Results", "Inspect submissions, scores, and dates across all students",
                    () -> new ResultsFrame(currentUser)));
            grid.add(createActionCard("PREFERENCES", "⚙ Admin Settings", "Configure your administrator profile and credentials",
                    () -> new SettingsFrame(currentUser)));
        } else {
            grid.add(createActionCard("ASSESSMENT", "🚀 Take an Assessment", "Browse all active quizzes and test your knowledge",
                    () -> new QuizTakeFrame(currentUser)));
            grid.add(createActionCard("TRANSCRIPT", "📊 My Results & History", "Review your performance records and score percentages",
                    () -> new ResultsFrame(currentUser)));
            grid.add(createActionCard("SECURITY", "⚙ Profile & Password", "Update your full name or change your login password",
                    () -> new SettingsFrame(currentUser)));
            grid.add(createActionCard("AUTHENTICATION", "🚪 Sign Out", "Safely terminate your student portal session",
                    () -> new LoginFrame()));
        }

        wrapper.add(grid, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createActionCard(String category, String title, String desc, java.util.function.Supplier<JFrame> supplier) {
        JPanel card = new JPanel(new BorderLayout(8, 6));
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UITheme.BORDER, 1, true),
            new EmptyBorder(16, 18, 16, 18)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);

        JLabel catLbl = new JLabel(category);
        catLbl.setFont(UITheme.FONT_BADGE);
        catLbl.setForeground(UITheme.PRIMARY);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_SECTION);
        titleLbl.setForeground(UITheme.TEXT_TITLE);

        JLabel descLbl = new JLabel("<html><p style='width:180px; color:#64748b; font-size:12px; margin-top:2px;'>" + desc + "</p></html>");
        descLbl.setFont(UITheme.FONT_SMALL);

        text.add(catLbl);
        text.add(Box.createVerticalStrut(3));
        text.add(titleLbl);
        text.add(Box.createVerticalStrut(4));
        text.add(descLbl);

        JLabel arrow = new JLabel("→");
        arrow.setFont(new Font("Segoe UI", Font.BOLD, 20));
        arrow.setForeground(UITheme.PRIMARY);

        card.add(text, BorderLayout.CENTER);
        card.add(arrow, BorderLayout.EAST);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(241, 245, 249));
                card.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(UITheme.PRIMARY, 1, true),
                    new EmptyBorder(16, 18, 16, 18)
                ));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(UITheme.BG_CARD);
                card.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(UITheme.BORDER, 1, true),
                    new EmptyBorder(16, 18, 16, 18)
                ));
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                supplier.get().setVisible(true);
                dispose();
            }
        });

        return card;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel status = new JLabel("● Database: Connected (MySQL quiz_db on port 3306)");
        status.setFont(UITheme.FONT_SMALL);
        status.setForeground(UITheme.SUCCESS);

        JLabel appInfo = new JLabel("Quiz Management System • Enterprise Desktop Edition");
        appInfo.setFont(UITheme.FONT_SMALL);
        appInfo.setForeground(UITheme.TEXT_MUTED);

        footer.add(status, BorderLayout.WEST);
        footer.add(appInfo, BorderLayout.EAST);
        return footer;
    }
}