package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Quiz;

public class QuizDAO {

    // Create: returns the new quiz's id, or -1 if it failed
    public int addQuiz(String title, String description, int createdBy) {
        String sql = "INSERT INTO quizzes (title, description, created_by) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, title);
            ps.setString(2, description);
            ps.setInt(3, createdBy);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Read: all quizzes
    public List<Quiz> getAllQuizzes() {
        List<Quiz> list = new ArrayList<>();
        String sql = "SELECT id, title, description, created_by FROM quizzes ORDER BY id";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Quiz(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getInt("created_by")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Update
    public boolean updateQuiz(Quiz quiz) {
        String sql = "UPDATE quizzes SET title = ?, description = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, quiz.getTitle());
            ps.setString(2, quiz.getDescription());
            ps.setInt(3, quiz.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete (its questions and options go too, because of ON DELETE CASCADE)
    public boolean deleteQuiz(int id) {
        String sql = "DELETE FROM quizzes WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Returns total number of quizzes
    public int getQuizCount() {
        String sql = "SELECT COUNT(*) FROM quizzes";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}