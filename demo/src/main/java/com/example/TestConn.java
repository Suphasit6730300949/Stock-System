package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
 
public class TestConn {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/stock_db?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String password = "123456"; // Leave empty if using XAMPP
 
        try {
            Connection con = DriverManager.getConnection(url, user, password);
            if (con != null) {
                System.out.println("CONNECTION SUCCESSFUL!");
            }
        } catch (Exception e) {
            System.out.println("CONNECTION FAILED!");
            e.printStackTrace(); // This prints the EXACT reason it failed
        }
    }
}