package model;

public class Option {
    private int id;
    private int questionId;
    private String text;
    private boolean correct;

    // For options loaded from the database
    public Option(int id, int questionId, String text, boolean correct) {
        this.id = id;
        this.questionId = questionId;
        this.text = text;
        this.correct = correct;
    }

    // For new options that are not saved yet
    public Option(String text, boolean correct) {
        this(0, 0, text, correct);
    }

    public int getId() { return id; }
    public int getQuestionId() { return questionId; }
    public String getText() { return text; }
    public boolean isCorrect() { return correct; }

    @Override
    public String toString() { return text; }
}