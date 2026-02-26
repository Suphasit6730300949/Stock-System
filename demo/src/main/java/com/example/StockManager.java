package com.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockManager {

    // ADD products in DB
    public void addProduct(Product p) {
        String sql = "INSERT INTO products (id, name, price, quantity, category) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, p.getId());
            pstmt.setString(2, p.getName());
            pstmt.setDouble(3, p.getPrice());
            pstmt.setInt(4, p.getQuantity());
            pstmt.setString(5, p.getCategory());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Dispalay all products
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Product(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        rs.getString("category")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Update stock with SQL calculation to prevent negative values
    public void updateStock(String id, int changeAmount) {
        String sql = "UPDATE products SET quantity = quantity + ? WHERE id = ? AND (quantity + ?) >= 0";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, changeAmount);
            pstmt.setString(2, id);
            pstmt.setInt(3, changeAmount);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                System.out.println("Update failed: Stock cannot be negative or ID not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveData() {
        throw new UnsupportedOperationException("Unimplemented method 'saveData'");
    }
}
