package com.ayomide;

import dao.QuestionDAO;
import dao.QuizDAO;
import java.util.Arrays;
import model.Option;
import model.Question;

public class QuestionDAOTest {
    public static void main(String[] args) {
        QuizDAO quizDAO = new QuizDAO();
        QuestionDAO questionDAO = new QuestionDAO();

        int quizId = quizDAO.addQuiz("Test Quiz", "For testing", 1);

        int qId = questionDAO.addQuestion(quizId, "Which keyword creates an object in Java?",
                Arrays.asList(
                        new Option("new", true),
                        new Option("make", false),
                        new Option("create", false),
                        new Option("build", false)));
        System.out.println("Added question id: " + qId);

        for (Question q : questionDAO.getQuestionsByQuiz(quizId)) {
            System.out.println(q.getText());
            for (Option o : q.getOptions()) {
                System.out.println("  " + (o.isCorrect() ? "[x] " : "[ ] ") + o.getText());
            }
        }

        quizDAO.deleteQuiz(quizId);   // also removes its questions and options
        System.out.println("Cleaned up.");
    }
}