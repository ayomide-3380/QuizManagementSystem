package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
        "jdbc:mysql://localhost:3306/quiz_db?useSSL=false&allowPublicKeyRetrieval=true&connectTimeout=3000";
    private static final String USER = "root";
    private static final String PASSWORD = "";   // XAMPP default is empty

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // True if the database can be reached right now
    public static boolean isAvailable() {
        try (Connection con = getConnection()) {
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}