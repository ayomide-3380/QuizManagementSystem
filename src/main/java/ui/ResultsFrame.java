package ui;

import dao.AttemptDAO;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Attempt;
import model.User;

public class ResultsFrame extends JFrame {

    private final User user;

    public ResultsFrame(User user) {
        this.user = user;

        setTitle("Quiz Management System - Results");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(680, 460);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 15));
        root.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel(user.isAdmin() ? "All quiz results" : "My quiz results",
                SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        root.add(title, BorderLayout.NORTH);

        // Admins see everyone's attempts, students see only their own
        AttemptDAO dao = new AttemptDAO();
        List<Attempt> attempts = user.isAdmin()
                ? dao.getAllAttempts()
                : dao.getAttemptsByUser(user.getId());

        String[] columns = {"Student", "Quiz", "Score", "Percentage", "Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        for (Attempt a : attempts) {
            model.addRow(new Object[]{
                a.getUserName(),
                a.getQuizTitle(),
                a.getScore() + " / " + a.getTotal(),
                Math.round(a.getPercentage()) + "%",
                a.getAttemptedAt().format(fmt)
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(26);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        // A student already knows their own name, so hide that column for them
        if (!user.isAdmin()) {
            table.removeColumn(table.getColumnModel().getColumn(0));
        }

        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JLabel lblCount = new JLabel(attempts.isEmpty()
                ? "No results yet. Take a quiz first!"
                : attempts.size() + " result(s)");

        JButton btnBack = new JButton("Back to dashboard");
        btnBack.addActionListener(e -> {
            new DashboardFrame(user).setVisible(true);
            dispose();
        });

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(lblCount, BorderLayout.WEST);
        bottom.add(btnBack, BorderLayout.EAST);
        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);
    }
}