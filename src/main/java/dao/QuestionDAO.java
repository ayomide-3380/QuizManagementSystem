package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Option;
import model.Question;

public class QuestionDAO {

    // Saves a question together with its options. Returns the new question id, or -1 if it failed.
    // If any part fails, nothing is saved (this is called a transaction).
    public int addQuestion(int quizId, String text, List<Option> options) {
        String sqlQ = "INSERT INTO questions (quiz_id, question_text) VALUES (?, ?)";
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int questionId;
                try (PreparedStatement ps = con.prepareStatement(sqlQ, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, quizId);
                    ps.setString(2, text);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("No question id returned");
                        questionId = keys.getInt(1);
                    }
                }
                insertOptions(con, questionId, options);
                con.commit();
                return questionId;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Read: every question of a quiz, each with its options
    public List<Question> getQuestionsByQuiz(int quizId) {
        List<Question> list = new ArrayList<>();
        String sqlQ = "SELECT id, quiz_id, question_text FROM questions WHERE quiz_id = ? ORDER BY id";
        String sqlO = "SELECT id, question_id, option_text, is_correct FROM options WHERE question_id = ? ORDER BY id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement psQ = con.prepareStatement(sqlQ);
             PreparedStatement psO = con.prepareStatement(sqlO)) {

            psQ.setInt(1, quizId);
            try (ResultSet rsQ = psQ.executeQuery()) {
                while (rsQ.next()) {
                    Question q = new Question(rsQ.getInt("id"), rsQ.getInt("quiz_id"),
                            rsQ.getString("question_text"));

                    List<Option> options = new ArrayList<>();
                    psO.setInt(1, q.getId());
                    try (ResultSet rsO = psO.executeQuery()) {
                        while (rsO.next()) {
                            options.add(new Option(rsO.getInt("id"), rsO.getInt("question_id"),
                                    rsO.getString("option_text"), rsO.getBoolean("is_correct")));
                        }
                    }
                    q.setOptions(options);
                    list.add(q);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Update: changes the text and replaces all the options
    public boolean updateQuestion(int questionId, String text, List<Option> options) {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE questions SET question_text = ? WHERE id = ?")) {
                    ps.setString(1, text);
                    ps.setInt(2, questionId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM options WHERE question_id = ?")) {
                    ps.setInt(1, questionId);
                    ps.executeUpdate();
                }
                insertOptions(con, questionId, options);
                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete (its options go too, because of ON DELETE CASCADE)
    public boolean deleteQuestion(int questionId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM questions WHERE id = ?")) {
            ps.setInt(1, questionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Shared helper used by add and update
    private void insertOptions(Connection con, int questionId, List<Option> options) throws SQLException {
        String sql = "INSERT INTO options (question_id, option_text, is_correct) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (Option o : options) {
                ps.setInt(1, questionId);
                ps.setString(2, o.getText());
                ps.setBoolean(3, o.isCorrect());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public int getTotalQuestionCount() {
        String sql = "SELECT COUNT(*) FROM questions";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getQuestionCountByQuiz(int quizId) {
        String sql = "SELECT COUNT(*) FROM questions WHERE quiz_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}