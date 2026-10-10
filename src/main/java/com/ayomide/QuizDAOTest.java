package com.ayomide;

import dao.QuizDAO;
import model.Quiz;

public class QuizDAOTest {
    public static void main(String[] args) {
        QuizDAO dao = new QuizDAO();

        int id = dao.addQuiz("Java Basics", "A short test on Java", 1);
        System.out.println("Added quiz with id: " + id);

        for (Quiz q : dao.getAllQuizzes()) {
            System.out.println(q.getId() + " - " + q.getTitle());
        }

        dao.deleteQuiz(id);
        System.out.println("Deleted it again. Quizzes left: " + dao.getAllQuizzes().size());
    }
}