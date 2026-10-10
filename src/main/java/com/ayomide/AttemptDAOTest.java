package com.ayomide;

import dao.AttemptDAO;
import dao.QuestionDAO;
import dao.QuizDAO;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Attempt;
import model.Option;
import model.Question;

public class AttemptDAOTest {
    public static void main(String[] args) {
        QuizDAO quizDAO = new QuizDAO();
        QuestionDAO questionDAO = new QuestionDAO();
        AttemptDAO attemptDAO = new AttemptDAO();

        int quizId = quizDAO.addQuiz("Scoring Test", "For testing", 1);

        questionDAO.addQuestion(quizId, "Which keyword creates an object?",
                Arrays.asList(new Option("new", true), new Option("make", false)));
        questionDAO.addQuestion(quizId, "Which symbol ends a statement?",
                Arrays.asList(new Option(";", true), new Option(":", false)));

        // Answer question 1 correctly and question 2 wrongly
        List<Question> questions = questionDAO.getQuestionsByQuiz(quizId);
        Map<Integer, Integer> picks = new HashMap<>();
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            for (Option o : q.getOptions()) {
                boolean wantCorrect = (i == 0);
                if (o.isCorrect() == wantCorrect) {
                    picks.put(q.getId(), o.getId());
                    break;
                }
            }
        }

        int attemptId = attemptDAO.submitAttempt(1, quizId, picks);
        System.out.println("Saved attempt id: " + attemptId);

        for (Attempt a : attemptDAO.getAttemptsByUser(1)) {
            System.out.println(a.getUserName() + " | " + a.getQuizTitle() + " | "
                    + a.getScore() + "/" + a.getTotal() + " | " + a.getPercentage() + "%");
        }

        attemptDAO.deleteAttempt(attemptId);
        quizDAO.deleteQuiz(quizId);
        System.out.println("Cleaned up.");
    }
}