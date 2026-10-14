package ui;

import dao.AttemptDAO;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import model.Attempt;
import model.User;

/**
 * Ultra-refined Gradebook and Results Frame with badge cell renderers
 * and summary KPIs.
 */
public class ResultsFrame extends JFrame {

    private final User user;
    private final AttemptDAO dao = new AttemptDAO();

    private final DefaultTableModel model;
    private final JTable table;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JTextField txtSearch = new JTextField();
    private final JButton btnBack = UITheme.createSecondaryButton("← Back to Dashboard");

    public ResultsFrame(User user) {
        this.user = user;

        setTitle("Quiz Management System - " + (user.isAdmin() ? "All Student Submissions" : "My Assessment Transcript"));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(960, 680);
        setMinimumSize(new Dimension(880, 600));
        setLocationRelativeTo(null);
        UITheme.applyWindowIcon(this);

        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(UITheme.BG_APP);
        root.setBorder(new EmptyBorder(22, 30, 22, 30));

        // 1. Header with theme toggle & back button
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(UITheme.createHeader(
                user.isAdmin() ? "Student Results & Gradebook" : "My Results Transcript",
                user.isAdmin() ? "Inspect performance records across all registered learners"
                               : "Your historical quiz submissions, scores, and dates"
        ), BorderLayout.WEST);

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topRight.setOpaque(false);
        topRight.add(UITheme.createThemeToggle(this));
        topRight.add(btnBack);
        top.add(topRight, BorderLayout.EAST);
        root.add(top, BorderLayout.NORTH);

        // Fetch records
        List<Attempt> attempts = user.isAdmin()
                ? dao.getAllAttempts()
                : dao.getAttemptsByUser(user.getId());

        // 2. Metrics row
        JPanel metricsPanel = new JPanel(new GridLayout(1, 3, 16, 0));
        metricsPanel.setOpaque(false);

        int totalAttempts = attempts.size();
        double sumPct = 0;
        int highestPct = 0;
        for (Attempt a : attempts) {
            sumPct += a.getPercentage();
            if (a.getPercentage() > highestPct) highestPct = (int) Math.round(a.getPercentage());
        }
        int avgPct = totalAttempts > 0 ? (int) Math.round(sumPct / totalAttempts) : 0;

        metricsPanel.add(UITheme.createStatCard("RECORDED SUBMISSIONS", String.valueOf(totalAttempts), "Completed attempts", UITheme.PRIMARY, "📝"));
        metricsPanel.add(UITheme.createStatCard("AVERAGE PERFORMANCE", avgPct + "%", "Cumulative grade", UITheme.SUCCESS, "📊"));
        metricsPanel.add(UITheme.createStatCard("HIGHEST SCORE", highestPct + "%", "Best single record", UITheme.WARNING, "🏆"));

        // 3. Center Table Card
        JPanel tableCard = UITheme.createCardPanel();
        tableCard.setLayout(new BorderLayout(0, 12));

        // Search bar
        JPanel searchBar = new JPanel(new BorderLayout(10, 0));
        searchBar.setOpaque(false);
        JLabel lblSearch = new JLabel("🔍 Filter Records:");
        lblSearch.setFont(UITheme.FONT_BOLD);
        lblSearch.setForeground(UITheme.TEXT_MUTED);

        UITheme.styleInput(txtSearch);
        txtSearch.putClientProperty("JTextField.placeholderText", "Search by student name, quiz topic, or score...");
        searchBar.add(lblSearch, BorderLayout.WEST);
        searchBar.add(txtSearch, BorderLayout.CENTER);
        tableCard.add(searchBar, BorderLayout.NORTH);

        // Table Model
        String[] columns = user.isAdmin()
                ? new String[]{"Student Name", "Assessment Topic", "Score", "Percentage", "Date Completed"}
                : new String[]{"Assessment Topic", "Score", "Percentage", "Date Completed"};

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        for (Attempt a : attempts) {
            if (user.isAdmin()) {
                model.addRow(new Object[]{
                    a.getUserName(),
                    a.getQuizTitle(),
                    a.getScore() + " / " + a.getTotal(),
                    Math.round(a.getPercentage()) + "%",
                    a.getAttemptedAt().format(fmt)
                });
            } else {
                model.addRow(new Object[]{
                    a.getQuizTitle(),
                    a.getScore() + " / " + a.getTotal(),
                    Math.round(a.getPercentage()) + "%",
                    a.getAttemptedAt().format(fmt)
                });
            }
        }

        UITheme.styleTable(table);

        // Percentage column badge renderer
        int pctColIndex = user.isAdmin() ? 3 : 2;
        table.getColumnModel().getColumn(pctColIndex).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSel, boolean hasFoc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(tbl, val, isSel, hasFoc, row, col);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(UITheme.FONT_BADGE);

                if (!isSel && val != null) {
                    try {
                        String s = val.toString().replace("%", "").trim();
                        int p = Integer.parseInt(s);
                        if (p >= 80) {
                            lbl.setForeground(UITheme.SUCCESS_DARK);
                            lbl.setBackground(UITheme.SUCCESS_LIGHT);
                        } else if (p >= 60) {
                            lbl.setForeground(UITheme.PRIMARY);
                            lbl.setBackground(UITheme.PRIMARY_LIGHT);
                        } else {
                            lbl.setForeground(UITheme.WARNING_DARK);
                            lbl.setBackground(UITheme.WARNING_LIGHT);
                        }
                    } catch (Exception ignored) {}
                }
                return lbl;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        JPanel centerWrapper = new JPanel(new BorderLayout(0, 16));
        centerWrapper.setOpaque(false);
        centerWrapper.add(metricsPanel, BorderLayout.NORTH);
        centerWrapper.add(tableCard, BorderLayout.CENTER);
        root.add(centerWrapper, BorderLayout.CENTER);

        btnBack.addActionListener(e -> {
            new DashboardFrame(user).setVisible(true);
            dispose();
        });

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
    }
}