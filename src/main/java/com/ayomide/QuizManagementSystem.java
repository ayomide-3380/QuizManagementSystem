package com.ayomide;

import dao.DBConnection;
import java.sql.Connection;

public class QuizManagementSystem {

    public static void main(String[] args) {
        try (Connection con = DBConnection.getConnection()) {
            System.out.println("Connected to quiz_db successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}