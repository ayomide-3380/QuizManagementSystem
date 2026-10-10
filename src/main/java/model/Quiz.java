package model;

public class Quiz {
    private int id;
    private String title;
    private String description;
    private int createdBy;

    public Quiz(int id, String title, String description, int createdBy) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getCreatedBy() { return createdBy; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }

    // Lets a quiz show its title when placed in a list or dropdown
    @Override
    public String toString() { return title; }
}