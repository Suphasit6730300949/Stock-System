package com.example;

import com.mongodb.client.*;
import org.bson.Document;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MongoDB {
    private static final String URI = "mongodb+srv://StockDB:Tt0897938633@cluster0.nkctc3k.mongodb.net/?appName=Cluster0";
    private static final String DB_NAME = "mystock";
    private static final String COLLECTION = "items";
    private static final String CAT_COLLECTION = "categories";

    private MongoClient mongoClient;
    private MongoCollection<Document> collection;
    private MongoCollection<Document> catCollection;

    public MongoDB() {
        mongoClient = MongoClients.create(URI);
        MongoDatabase db = mongoClient.getDatabase(DB_NAME);
        collection = db.getCollection(COLLECTION);
        catCollection = db.getCollection(CAT_COLLECTION);
    }

    // ── Helper: Date ↔ LocalDateTime ──
    private LocalDateTime toLocalDateTime(Object val) {
        if (val instanceof Date d) {
            return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        return null;
    }

    private Date toDate(LocalDateTime ldt) {
        if (ldt == null) return null;
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }

    // ── โหลดทุก item จาก MongoDB ──
    public List<Item> loadAllItems() {
        List<Item> items = new ArrayList<>();
        for (Document doc : collection.find()) {
            double price = doc.containsKey("price") ? doc.getDouble("price") : 0.0;
            LocalDateTime createdAt = toLocalDateTime(doc.get("createdAt"));
            LocalDateTime updatedAt = toLocalDateTime(doc.get("updatedAt"));
            items.add(new Item(
                    doc.getString("name"),
                    doc.getInteger("quantity"),
                    doc.getString("category"),
                    price,
                    createdAt,
                    updatedAt));
        }
        return items;
    }

    // ── เพิ่ม item (บันทึก createdAt = now, updatedAt = null) ──
    public void insertItem(Item item) {
        LocalDateTime now = LocalDateTime.now();
        item.setCreatedAt(now);
        item.setUpdatedAt(null);

        Document doc = new Document()
                .append("name", item.getName())
                .append("quantity", item.getQuantity())
                .append("category", item.getCategory())
                .append("price", item.getPrice())
                .append("createdAt", toDate(now))
                .append("updatedAt", null);
        collection.insertOne(doc);
    }

    // ── แก้ไข item (อัปเดต updatedAt = now) ──
    public void updateItem(String oldName, Item item) {
        LocalDateTime now = LocalDateTime.now();
        item.setUpdatedAt(now);

        Document filter = new Document("name", oldName);
        Document update = new Document("$set", new Document()
                .append("name", item.getName())
                .append("quantity", item.getQuantity())
                .append("category", item.getCategory())
                .append("price", item.getPrice())
                .append("updatedAt", toDate(now)));
        collection.updateOne(filter, update);
    }

    // ── ลบ item ──
    public void deleteItem(String name) {
        collection.deleteOne(new Document("name", name));
    }

    // โหลด categories ทั้งหมดจาก MongoDB
    public List<String> loadAllCategories() {
        List<String> cats = new ArrayList<>();
        for (Document doc : catCollection.find()) {
            cats.add(doc.getString("name"));
        }
        return cats;
    }

    // บันทึก category ใหม่ลง MongoDB
    public void insertCategory(String categoryName) {
        Document existing = catCollection.find(new Document("name", categoryName)).first();
        if (existing == null) {
            catCollection.insertOne(new Document("name", categoryName));
        }
    }

    // ลบ category ออกจาก MongoDB
    public void deleteCategory(String categoryName) {
        catCollection.deleteOne(new Document("name", categoryName));
    }

    public void close() {
        mongoClient.close();
    }
}