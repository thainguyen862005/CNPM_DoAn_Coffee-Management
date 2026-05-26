import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class OrderDetail {
    private int orderDetailId;
    private int quantity;
    private double unitPrice;
    private double subtotal;
    private MenuItem menuItem;

    public OrderDetail() {}

    public OrderDetail(int orderDetailId, int quantity, double unitPrice, double subtotal) {
        this.orderDetailId = orderDetailId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

    public OrderDetail(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;

        if (menuItem != null) {
            this.unitPrice = menuItem.getPrice();
        } else {
            this.unitPrice = 0;
        }

        this.subtotal = calculateSubtotal();
    }

    public int getOrderDetailId() { return orderDetailId; }
    public void setOrderDetailId(int orderDetailId) { this.orderDetailId = orderDetailId; }
    public MenuItem getMenuItem() { return menuItem; }
    public void setMenuItem(MenuItem menuItem) { this.menuItem = menuItem; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        calculateSubtotal();
    }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        calculateSubtotal();
    }
    public double getSubtotal() { return subtotal; }

    public double calculateSubtotal() {
        this.subtotal = this.quantity * this.unitPrice;
        return this.subtotal;
    }

    public boolean addOrderDetail(OrderDetail detail) {
        // TODO: Lệnh SQL INSERT chi tiết món vào database
        return false;
    }

    public List<OrderDetail> getBestSellingItems(LocalDateTime start, LocalDateTime end) {
        List<OrderDetail> bestSellers = new ArrayList<>();
        bestSellers.add(new OrderDetail(new MenuItem(2, "Trà đào", 30000, "", "Còn bán"), 100));
        bestSellers.add(new OrderDetail(new MenuItem(1, "Ca phe den", 20000, "", "Còn bán"), 80));
        return bestSellers;
    }
}