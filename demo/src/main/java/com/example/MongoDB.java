package com.example;

import com.mongodb.client.*;
import org.bson.Document;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data Access Object: จัดการการติดต่อกับ MongoDB
 * ทุก method เป็น CRUD ตรง ๆ — ไม่มี business logic
 */
public class MongoDB {

    private static final String URI            = "mongodb+srv://StockDB:Tt0897938633@cluster0.nkctc3k.mongodb.net/?appName=Cluster0";
    private static final String DB_NAME        = "mystock";
    private static final String COLLECTION     = "items"; //table
    private static final String CAT_COLLECTION = "categories"; //table

    private final MongoCollection<Document> collection; //ตารางItem
    private final MongoCollection<Document> catCollection; //ตารางCategory
    private final MongoClient               mongoClient;

    //Constructor
    public MongoDB() {
        mongoClient   = MongoClients.create(URI);
        MongoDatabase db = mongoClient.getDatabase(DB_NAME);
        collection    = db.getCollection(COLLECTION);
        catCollection = db.getCollection(CAT_COLLECTION);
    }

    // ── Helper: แปลง Date ↔ LocalDateTime ───────────────────────────────────

    private LocalDateTime toLocalDateTime(Object val) {
        if (val instanceof Date d) {
            return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        return null;
    }

    //แปลงกลับเป็น Date เพื่อเก็บใน Mongo DB
    private Date toDate(LocalDateTime ldt) {
        if (ldt == null) return null;
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }

    // ── Items ────────────────────────────────────────────────────────────────

    /** โหลด items ทั้งหมดจาก MongoDB */
    public List<Item> loadAllItems() { //ดึงสินค้าทั้งหมด
        List<Item> items = new ArrayList<>(); //เตรียมList
        for (Document doc : collection.find()) { //loop ทุก document ใน MongoDB
            double        price       = doc.containsKey("price")       ? doc.getDouble("price")       : 0.0;
            String        description = doc.containsKey("description") ? doc.getString("description") : "";
            LocalDateTime createdAt   = toLocalDateTime(doc.get("createdAt"));
            LocalDateTime updatedAt   = toLocalDateTime(doc.get("updatedAt"));
            items.add(new Item(
                    doc.getString("name"),
                    doc.getInteger("quantity"),
                    doc.getString("category"),
                    price,
                    description,
                    createdAt,
                    updatedAt));
        }
        return items; //เอาข้อมูลจาก Mongo → สร้างเป็น object List ของ Item
    }

    /** เพิ่ม item ใหม่ — บันทึก createdAt = now, updatedAt = null */
    public void insertItem(Item item) {
        LocalDateTime now = LocalDateTime.now();
        item.setCreatedAt(now);
        item.setUpdatedAt(null);

        Document doc = new Document()
                .append("name",        item.getName())
                .append("quantity",    item.getQuantity())
                .append("category",    item.getCategory())
                .append("price",       item.getPrice())
                .append("description", item.getDescription())
                .append("createdAt",   toDate(now))
                .append("updatedAt",   null); 
        collection.insertOne(doc); //insert ลง MongoDB
    }

    /** อัปเดต item — บันทึก updatedAt = now */
    public void updateItem(String oldName, Item item) {
        LocalDateTime now = LocalDateTime.now();
        item.setUpdatedAt(now);

        Document filter = new Document("name", oldName); //หาitemที่ชื่อเดิม
        Document update = new Document("$set", new Document() //แก้ค่าบาง field
                .append("name",        item.getName())
                .append("quantity",    item.getQuantity())
                .append("category",    item.getCategory())
                .append("price",       item.getPrice())
                .append("description", item.getDescription())
                .append("updatedAt",   toDate(now))); //เวลาที่update
        collection.updateOne(filter, update);
    }

    /** ลบ item ตามชื่อ */
    public void deleteItem(String name) {
        collection.deleteOne(new Document("name", name)); //ลบตามชื่อ
    }

    // ── Categories ──────────────────────────────────────────────────────────

    /** โหลด categories ทั้งหมด */
    public List<String> loadAllCategories() {
        List<String> cats = new ArrayList<>();
        for (Document doc : catCollection.find()) {
            cats.add(doc.getString("name"));
        }
        return cats;
    }

    /** เพิ่ม category (ถ้ายังไม่มี) */
    public void insertCategory(String categoryName) {
        Document existing = catCollection.find(new Document("name", categoryName)).first();
        if (existing == null) {
            catCollection.insertOne(new Document("name", categoryName));
        }
    }

    /** ลบ category */
    public void deleteCategory(String categoryName) {
        catCollection.deleteOne(new Document("name", categoryName));
    }

    // ── Lifecycle ────────────────────────────────────────────────────────────

    public void close() {
        mongoClient.close();
    }
}