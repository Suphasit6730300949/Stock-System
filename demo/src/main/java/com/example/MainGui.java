package com.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MainGui extends JFrame {
    private StockManager manager = new StockManager();
    private JTable table;
    private DefaultTableModel tableModel;

    public MainGui() {
        setTitle("Stock System - Database Ready");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // ทำให้หน้าต่างเด้งขึ้นมาตรงกลางจอ
        
        JTabbedPane tabbedPane = new JTabbedPane();

        // ==========================================
        // --- หน้าที่ 1: Dashboard (แสดงรายการสินค้า) ---
        // ==========================================
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // กำหนดหัวตาราง
        String[] columnNames = {"Product ID", "Name", "Price (฿)", "Qty in Stock", "Category"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // ป้องกันไม่ให้คลิกแก้ข้อมูลในตารางโดยตรง
            }
        };
        
        table = new JTable(tableModel);
        table.setRowHeight(25); // เพิ่มความสูงของแถวให้อ่านง่าย
        refreshTable(); // โหลดข้อมูลจาก DB มาแสดง
        
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        // ปุ่ม Refresh
        JButton btnRefresh = new JButton("↻ Refresh Data");
        btnRefresh.setFont(new Font("Arial", Font.BOLD, 14));
        // แก้ไขตรงนี้: ลบ manager.saveData() ออกไปแล้ว
        btnRefresh.addActionListener(e -> refreshTable()); 
        mainPanel.add(btnRefresh, BorderLayout.SOUTH);

        // ==========================================
        // --- หน้าที่ 2: Manage Stock (เพิ่มสินค้าใหม่) ---
        // ==========================================
        JPanel managePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtId = new JTextField(15);
        JTextField txtName = new JTextField(15);
        JTextField txtPrice = new JTextField(15);
        JTextField txtQty = new JTextField(15);
        JComboBox<String> cbCategory = new JComboBox<>(new String[]{"Electronics", "Office Supplies", "Furniture", "Beverage", "General"});
        
        JButton btnAdd = new JButton("Add Product");
        btnAdd.setBackground(new Color(46, 204, 113));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);

        // จัดวาง Layout
        gbc.gridx = 0; gbc.gridy = 0; managePanel.add(new JLabel("Product ID:"), gbc);
        gbc.gridx = 1; managePanel.add(txtId, gbc);

        gbc.gridx = 0; gbc.gridy = 1; managePanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; managePanel.add(txtName, gbc);

        gbc.gridx = 0; gbc.gridy = 2; managePanel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1; managePanel.add(txtPrice, gbc);

        gbc.gridx = 0; gbc.gridy = 3; managePanel.add(new JLabel("Initial Qty:"), gbc);
        gbc.gridx = 1; managePanel.add(txtQty, gbc);

        gbc.gridx = 0; gbc.gridy = 4; managePanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1; managePanel.add(cbCategory, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; managePanel.add(btnAdd, gbc);

        // Action เมื่อกดปุ่ม Add Product
        btnAdd.addActionListener(e -> {
            try {
                // ดึงค่าจากหน้าจอ
                String id = txtId.getText();
                String name = txtName.getText();
                double price = Double.parseDouble(txtPrice.getText());
                int qty = Integer.parseInt(txtQty.getText());
                String category = (String) cbCategory.getSelectedItem();

                // ตรวจสอบค่าว่าง
                if (id.isEmpty() || name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter ID and Name!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // สร้าง Object และบันทึกลง Database
                Product p = new Product(id, name, price, qty, category);
                manager.addProduct(p);
                
                JOptionPane.showMessageDialog(this, "Product Added Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // เคลียร์ช่องข้อความ
                txtId.setText("");
                txtName.setText("");
                txtPrice.setText("");
                txtQty.setText("");
                
                // อัปเดตตารางหน้าแรก
                refreshTable();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Price and Quantity must be numbers!", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // เพิ่มแท็บลงในโปรแกรม
        tabbedPane.addTab("Dashboard", mainPanel);
        tabbedPane.addTab("Add Product", managePanel);

        add(tabbedPane);
        setVisible(true);
    }

    // ฟังก์ชันสำหรับโหลดข้อมูลมาใส่ตาราง
    private void refreshTable() {
        tableModel.setRowCount(0); // ล้างข้อมูลเก่าออกก่อน
        try {
            for (Product p : manager.getAllProducts()) {
                tableModel.addRow(new Object[]{p.getId(), p.getName(), p.getPrice(), p.getQuantity(), p.getCategory()});
            }
        } catch (Exception e) {
            System.out.println("Cannot fetch data from DB. Is MySQL running?");
        }
    }

    public static void main(String[] args) {
        // กำหนดให้หน้าตาโปรแกรมดูสวยขึ้น (คล้ายระบบปฏิบัติการที่กำลังใช้งานอยู่)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // รันโปรแกรม
        SwingUtilities.invokeLater(() -> new MainGui());
    }
}