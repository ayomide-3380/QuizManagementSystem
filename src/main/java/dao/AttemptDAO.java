package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.Attempt;
import model.Option;
import model.Question;

public class AttemptDAO {

    // selected = for each question id, the option id the student picked.
    // A question that is missing from the map counts as unanswered.
    // Returns the new attempt's id, or -1 if it failed.
    public int submitAttempt(int userId, int quizId, Map<Integer, Integer> selected) {
        List<Question> questions = new QuestionDAO().getQuestionsByQuiz(quizId);

        int score = 0;
        for (Question q : questions) {
            Integer chosen = selected.get(q.getId());
            if (chosen == null) continue;
            for (Option o : q.getOptions()) {
                if (o.getId() == chosen && o.isCorrect()) {
                    score++;
                    break;
                }
            }
        }
        int total = questions.size();

        String sqlAttempt = "INSERT INTO quiz_attempts (user_id, quiz_id, score, total) VALUES (?, ?, ?, ?)";
        String sqlAnswer = "INSERT INTO answers (attempt_id, question_id, option_id) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int attemptId;
                try (PreparedStatement ps = con.prepareStatement(sqlAttempt, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, userId);
                    ps.setInt(2, quizId);
                    ps.setInt(3, score);
                    ps.setInt(4, total);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("No attempt id returned");
                        attemptId = keys.getInt(1);
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(sqlAnswer)) {
                    for (Question q : questions) {
                        Integer chosen = selected.get(q.getId());
                        ps.setInt(1, attemptId);
                        ps.setInt(2, q.getId());
                        if (chosen == null) ps.setNull(3, Types.INTEGER);
                        else ps.setInt(3, chosen);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                con.commit();
                return attemptId;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // A student's own results, newest first
    public List<Attempt> getAttemptsByUser(int userId) {
        return query(BASE_SQL + " WHERE a.user_id = ? ORDER BY a.attempted_at DESC, a.id DESC", userId);
    }

    // Everyone's results (for the admin), newest first
    public List<Attempt> getAllAttempts() {
        return query(BASE_SQL + " ORDER BY a.attempted_at DESC, a.id DESC", null);
    }

    // Removes an attempt and its answers (answers go too, because of ON DELETE CASCADE)
    public boolean deleteAttempt(int attemptId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM quiz_attempts WHERE id = ?")) {
            ps.setInt(1, attemptId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ---- helpers ----

    private static final String BASE_SQL =
        "SELECT a.id, a.user_id, u.full_name, a.quiz_id, q.title, a.score, a.total, a.attempted_at "
      + "FROM quiz_attempts a "
      + "JOIN users u ON a.user_id = u.id "
      + "JOIN quizzes q ON a.quiz_id = q.id";

    private List<Attempt> query(String sql, Integer userId) {
        List<Attempt> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (userId != null) ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Attempt(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("full_name"),
                        rs.getInt("quiz_id"),
                        rs.getString("title"),
                        rs.getInt("score"),
                        rs.getInt("total"),
                        rs.getTimestamp("attempted_at").toLocalDateTime()
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}