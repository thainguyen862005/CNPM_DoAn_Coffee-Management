package test;

import Model.CoffeeTable;
import Model.MenuItem;
import Model.Order;
import Model.OrderDetail;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderTest {
    private Order order;
    private MenuItem cafeDa;
    private MenuItem traSua;

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
    @Before
    public void setUp() {
        // Thiết lập dữ liệu giả lập (Mock data) trước mỗi ca test
        CoffeeTable table = new CoffeeTable(1, "Bàn 1", "Tầng 1", "Trống");
        order = new Order(table);

        cafeDa = new MenuItem();
        cafeDa.setItemId(1);
        cafeDa.setItemName("Cà phê đá");
        cafeDa.setPrice(20000.0);

        traSua = new MenuItem();
        traSua.setItemId(2);
        traSua.setItemName("Trà sữa trân châu");
        traSua.setPrice(35000.0);
    }

    @Test
    public void testTaoOrderVaTinhThanhTienMotMon() {
        // Test logic của UC-05: Thêm món và tính Subtotal
        OrderDetail detail = new OrderDetail(cafeDa, 2); // Mua 2 ly cafe đá
        double expectedSubtotal = 40000.0;

        Assert.assertEquals("Thành tiền của 1 món phải bằng Đơn giá x Số lượng",
                expectedSubtotal, detail.calculateSubtotal(), 0.0);
    }

    @Test
    public void testCapNhatOrderVaTinhTongTien() {
        // Test logic của UC-06: Thêm nhiều món và tính Total
        order.addItem(cafeDa, 1); // 20.000
        order.addItem(traSua, 2); // 70.000

        double expectedTotal = 90000.0;
        Assert.assertEquals("Tổng tiền hóa đơn phải bằng tổng các thành tiền (subtotal)",
                expectedTotal, order.calculateTotal(), 0.0);
    }

    @Test
    public void testSoLuongMonKhongHopLe() {
        // Test luồng ngoại lệ: Cố tình thêm món với số lượng âm
        OrderDetail detail = new OrderDetail(cafeDa, -5);

        double subtotal = detail.calculateSubtotal();
        Assert.assertTrue("Thành tiền không được là số âm", subtotal <= 0);
    }
}