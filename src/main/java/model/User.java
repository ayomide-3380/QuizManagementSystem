package model;

public class User {
    private int id;
    private String fullName;
    private String username;
    private String role;      // "ADMIN" or "STUDENT"

    public User(int id, String fullName, String username, String role) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.role = role;
    }

    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getUsername() { return username; }
    public String getRole() { return role; }

    public boolean isAdmin() { return "ADMIN".equals(role); }
}