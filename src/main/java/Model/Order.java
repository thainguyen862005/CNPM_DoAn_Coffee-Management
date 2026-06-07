package Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int orderId;
    private CoffeeTable table;
    private LocalDateTime createdAt;
    private String status;
    private List<OrderDetail> orderDetails = new ArrayList<>(); // Danh sách món đã gọi
    private String orderType = "DINE_IN";

    private static List<Order> danhSachHoaDon = new ArrayList<>();

    public Order() {}

    public Order(int orderId, LocalDateTime createdAt, String status) {
        this.orderId = orderId;
        this.createdAt = createdAt;
        this.status = status;
    }

    public Order(CoffeeTable table) {
        this.table = table;
        this.createdAt = LocalDateTime.now();
        this.status = "Đang phục vụ";
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public CoffeeTable getTable() {
        return table;
    }

    public void setTable(CoffeeTable table) {
        this.table = table;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }
    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public static List<Order> getDanhSachHoaDon() {
        return danhSachHoaDon;
    }

    public static void setDanhSachHoaDon(List<Order> danhSachHoaDon) {
        Order.danhSachHoaDon = danhSachHoaDon;
    }

    // Logic từ OrderService đưa vào đây
    public static Order createOrder(User staff, CoffeeTable table, List<OrderDetail> cart) {
        if (staff == null || staff.getRole() == null) {
            System.out.println("Lỗi: Người dùng chưa đăng nhập!");
            return null;
        }

        if (!staff.isStaff() && !staff.isManager()) {
            System.out.println("Lỗi: Bạn không có quyền tạo Model.Order!");
            return null;
        }

        if (table == null) {
            System.out.println("Lỗi: Chưa chọn bàn!");
            return null;
        }

        if (!table.isAvailableForOrder()) {
            System.out.println("Lỗi: Bàn " + table.getTableName() + " đang " + table.getStatus() + ", không thể tạo Model.Order");
            return null;
        }

        if (cart == null || cart.isEmpty()) {
            System.out.println("Lỗi: Vui lòng chọn ít nhất 1 món.");
            return null;
        }

        Order newOrder = new Order(table);

        for (OrderDetail item : cart) {
            newOrder.addItem(item.getMenuItem(), item.getQuantity());
        }

        table.updateStatus("Đang phục vụ");
        System.out.println("Thông báo: Tạo Model.Order thành công.");
        return newOrder;
    }

    // Cài đặt hàm addItem
    public void addItem(MenuItem item, int quantity) {
        if (item == null || quantity <= 0) {
            return;
        }

        for (OrderDetail detail : orderDetails) {
            if (detail.getMenuItem() != null
                    && detail.getMenuItem().getItemId() == item.getItemId()) {
                detail.setQuantity(detail.getQuantity() + quantity);
                detail.calculateSubtotal();
                return;
            }
        }

        this.orderDetails.add(new OrderDetail(item, quantity));
    }

    // Cài đặt hàm tính tiền
    public double calculateTotal() {
        double total = 0.0;
        for (OrderDetail detail : orderDetails) {
            total += detail.calculateSubtotal();
        }
        return total;
    }

    public void updateStatus(String status) { this.status = status; }

    public Order getOrderById(int orderId) {
        for (Order o : danhSachHoaDon) {
            if (o.orderId == orderId) return o;
        }
        return null;
    }

    public double calculateRevenue(LocalDateTime start, LocalDateTime end) {
        double total = 0;
        for (Order o : danhSachHoaDon) {
            if (o.status.equals("Đã thanh toán") &&
                    !o.createdAt.isBefore(start) && !o.createdAt.isAfter(end)) {
                total += 150000;
            }
        }
        return total;
    }
}