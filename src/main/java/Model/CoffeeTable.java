package Model;

import java.util.List;
import java.util.ArrayList;

public class CoffeeTable {

    private static List<CoffeeTable> danhSachBan = new ArrayList<>();

    // Bơm dữ liệu giả lập vào ngay khi chương trình chạy
    static {
        danhSachBan.add(new CoffeeTable(1, "Bàn 1", "Tầng 1", "Trống"));
        danhSachBan.add(new CoffeeTable(2, "Bàn 2", "Tầng 1", "Trống"));
        danhSachBan.add(new CoffeeTable(3, "Bàn 3", "Tầng 2", "Trống"));
        danhSachBan.add(new CoffeeTable(4, "Bàn 4", "Tầng 2", "Trống"));
        danhSachBan.add(new CoffeeTable(5, "Bàn 5", "Sân vườn", "Trống"));
        danhSachBan.add(new CoffeeTable(6, "Bàn 6", "Sân vườn", "Đang phục vụ"));
        danhSachBan.add(new CoffeeTable(7, "Bàn 7", "Phòng lạnh", "Trống"));
        danhSachBan.add(new CoffeeTable(8, "Bàn 8", "Phòng lạnh", "Trống"));
    }

    private int tableId;
    private String tableName;
    private String area;
    private String status;

    public CoffeeTable(int tableId, String tableName, String area, String status) {
        this.tableId = tableId;
        this.tableName = tableName;
        this.area = area;
        this.status = status;
    }

    public int getTableId() { return tableId; }
    public void setTableId(int tableId) { this.tableId = tableId; }
    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public void updateStatus(String status) {
        this.status = status;
    }

    public boolean isAvailableForOrder() {
        return "Trống".equalsIgnoreCase(this.status);
    }

    public List<CoffeeTable> getAllTables() {
        return danhSachBan;
    }

    public boolean addTable(CoffeeTable table) {
        return false;
    }

    public boolean updateTable(CoffeeTable table) {
        return false;
    }

    public boolean deleteTable(int tableId) {
        return false;
    }
}