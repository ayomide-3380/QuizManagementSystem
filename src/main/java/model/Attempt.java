package model;

import java.time.LocalDateTime;

public class Attempt {
    private int id;
    private int userId;
    private String userName;
    private int quizId;
    private String quizTitle;
    private int score;
    private int total;
    private LocalDateTime attemptedAt;

    public Attempt(int id, int userId, String userName, int quizId, String quizTitle,
                   int score, int total, LocalDateTime attemptedAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.quizId = quizId;
        this.quizTitle = quizTitle;
        this.score = score;
        this.total = total;
        this.attemptedAt = attemptedAt;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getUserName() { return userName; }
    public int getQuizId() { return quizId; }
    public String getQuizTitle() { return quizTitle; }
    public int getScore() { return score; }
    public int getTotal() { return total; }
    public LocalDateTime getAttemptedAt() { return attemptedAt; }

    // e.g. 70.0 for 7 out of 10
    public double getPercentage() {
        return total == 0 ? 0 : (score * 100.0) / total;
    }
}