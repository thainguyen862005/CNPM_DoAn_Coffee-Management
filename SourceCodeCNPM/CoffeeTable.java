import java.util.List;
import java.util.ArrayList;

public class CoffeeTable {
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
        return new ArrayList<>();
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