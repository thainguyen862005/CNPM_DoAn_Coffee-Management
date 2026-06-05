package test;

import Model.MenuItem;
import Model.Order;
import Model.OrderDetail;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderTest {

    // Kịch bản 1: Test tính tiền hóa đơn bình thường
    @Test
    public void testCalculateTotal_NormalCase() {
        Order order = new Order();
        List<OrderDetail> listChiTiet = new ArrayList<>();

        MenuItem cafe = new MenuItem(); cafe.setPrice(20000.0);
        OrderDetail detail1 = new OrderDetail(); detail1.setMenuItem(cafe); detail1.setQuantity(2); detail1.setUnitPrice(20000.0);

        MenuItem traDao = new MenuItem(); traDao.setPrice(30000.0);
        OrderDetail detail2 = new OrderDetail(); detail2.setMenuItem(traDao); detail2.setQuantity(1); detail2.setUnitPrice(30000.0);

        listChiTiet.add(detail1); listChiTiet.add(detail2);
        order.setOrderDetails(listChiTiet);

        // Mong đợi: 2*20k + 1*30k = 70.000đ
        assertEquals(70000.0, order.calculateTotal(), "Lỗi: Tổng tiền tính toán bị sai!");
    }

    // Kịch bản 2: Test ngoại lệ - Hóa đơn trống (Khách vô ngồi nhưng chưa gọi món)
    @Test
    public void testCalculateTotal_EmptyOrder() {
        Order order = new Order();
        // Cố tình đưa vào một danh sách rỗng
        order.setOrderDetails(new ArrayList<>());

        // Mong đợi: Hóa đơn 0đ, hệ thống không bị crash
        assertEquals(0.0, order.calculateTotal(), "Lỗi: Hóa đơn trống phải trả về 0đ!");
    }

    // Kịch bản 3: Test số lượng lớn - Khách gọi 100 ly cafe
    @Test
    public void testCalculateTotal_LargeQuantity() {
        Order order = new Order();
        List<OrderDetail> listChiTiet = new ArrayList<>();

        MenuItem cafe = new MenuItem(); cafe.setPrice(25000.0);
        OrderDetail detail1 = new OrderDetail(); detail1.setMenuItem(cafe);
        detail1.setQuantity(100); // 100 ly
        detail1.setUnitPrice(25000.0);

        listChiTiet.add(detail1);
        order.setOrderDetails(listChiTiet);

        // Mong đợi: 100 * 25.000 = 2.500.000đ
        assertEquals(2500000.0, order.calculateTotal(), "Lỗi: Tính toán số tiền lớn bị sai!");
    }
}