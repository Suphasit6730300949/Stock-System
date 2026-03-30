package com.example;

import java.time.LocalDateTime;

public class Item {
    private String name;
    private int quantity;
    private String category;
    private double price;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Item(String name, int quantity, String category) {
        this.name = name;
        this.quantity = quantity;
        this.category = category;
        this.price = 0.0;
    }

    public Item(String name, int quantity, String category, double price) {
        this.name = name;
        this.quantity = quantity;
        this.category = category;
        this.price = price;
        this.description = "";
    }

    public Item(String name, int quantity, String category, double price, String description) {
        this.name = name;
        this.quantity = quantity;
        this.category = category;
        this.price = price;
        this.description = description != null ? description : "";
    }

    public Item(String name, int quantity, String category, double price,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.name = name;
        this.quantity = quantity;
        this.category = category;
        this.price = price;
        this.description = "";
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Item(String name, int quantity, String category, double price, String description,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.name = name;
        this.quantity = quantity;
        this.category = category;
        this.price = price;
        this.description = description != null ? description : "";
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    public void setDescription(String description) {
        this.description = description != null ? description : "";
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return name + " (à¸¿" + String.format("%.2f", price) + ")";
    }
}