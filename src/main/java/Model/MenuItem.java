package Model;

import java.util.List;
import java.util.ArrayList;

public class MenuItem {
    private int itemId;
    private String itemName;
    private double price;
    private String description;
    private String status;

    private static List<MenuItem> danhSachMon = new ArrayList<>();

    public MenuItem() {}

    public MenuItem(int itemId, String itemName, double price, String description, String status) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.price = price;
        this.description = description;
        this.status = status;
    }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isAvailable() {
        return "Còn bán".equalsIgnoreCase(this.status);
    }

    public void updateStatus(String status) {
        this.status = status;
    }

    public List<MenuItem> getAllMenuItems() {
        return danhSachMon;
    }

    public boolean addMenuItem(MenuItem item) {
        // Kiem tra du lieu hop le
        if (item.getItemName().equals("") || item.getPrice() <= 0) {
            return false;
        }
        danhSachMon.add(item);
        return true;
    }

    public boolean updateMenuItem(MenuItem item) {
        for (int i = 0; i < danhSachMon.size(); i++) {
            if (danhSachMon.get(i).getItemId() == item.getItemId()) {
                danhSachMon.set(i, item);
                return true;
            }
        }
        return false;
    }

    public boolean deleteMenuItem(int itemId) {
        for (int i = 0; i < danhSachMon.size(); i++) {
            if (danhSachMon.get(i).getItemId() == itemId) {
                danhSachMon.get(i).setStatus("Ngừng bán");
                return true;
            }
        }
        return false;
    }
}