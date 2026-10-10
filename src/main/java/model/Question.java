package model;

import java.util.ArrayList;
import java.util.List;

public class Question {
    private int id;
    private int quizId;
    private String text;
    private List<Option> options = new ArrayList<>();

    public Question(int id, int quizId, String text) {
        this.id = id;
        this.quizId = quizId;
        this.text = text;
    }

    public int getId() { return id; }
    public int getQuizId() { return quizId; }
    public String getText() { return text; }
    public List<Option> getOptions() { return options; }

    public void setOptions(List<Option> options) { this.options = options; }

    @Override
    public String toString() { return text; }
}