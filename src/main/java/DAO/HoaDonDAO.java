package DAO;

import DBUtil.DBUtil;
import Model.CoffeeTable;
import Model.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {

    // Hàm lấy danh sách tất cả hóa đơn từ Database
    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        // Câu SQL JOIN 2 bảng để lấy cả tên bàn
        String sql = "SELECT o.order_id, o.created_at, o.status, o.table_id, t.table_name " +
                "FROM orders o LEFT JOIN coffee_tables t ON o.table_id = t.table_id " +
                "ORDER BY o.created_at DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));

                // Ép kiểu thời gian từ MySQL (Timestamp) sang Java (LocalDateTime)
                if (rs.getTimestamp("created_at") != null) {
                    order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                }

                order.setStatus(rs.getString("status"));

                // Lấy thông tin bàn
                int tableId = rs.getInt("table_id");
                if (tableId > 0) {
                    order.setTable(new CoffeeTable(tableId, rs.getString("table_name"), "", ""));
                }

                list.add(order);
            }
        } catch (Exception e) {
            System.out.println("Lỗi truy vấn danh sách hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // Hàm lấy 1 hóa đơn chi tiết dựa theo ID
    public Order getOrderById(int orderId) {
        Order order = null;

        String sqlOrder = "SELECT o.order_id, o.created_at, o.status, o.table_id, t.table_name " +
                "FROM orders o LEFT JOIN coffee_tables t ON o.table_id = t.table_id " +
                "WHERE o.order_id = ?";

        // ĐÃ SỬA: Lấy thêm m.item_id
        String sqlDetails = "SELECT d.quantity, d.unit_price, d.subtotal, m.item_name, m.item_id " +
                "FROM order_details d JOIN menu_items m ON d.item_id = m.item_id " +
                "WHERE d.order_id = ?";

        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement psOrder = conn.prepareStatement(sqlOrder);
            psOrder.setInt(1, orderId);
            ResultSet rsOrder = psOrder.executeQuery();

            if (rsOrder.next()) {
                order = new Order();
                order.setOrderId(rsOrder.getInt("order_id"));
                if (rsOrder.getTimestamp("created_at") != null) {
                    order.setCreatedAt(rsOrder.getTimestamp("created_at").toLocalDateTime());
                }
                order.setStatus(rsOrder.getString("status"));

                int tableId = rsOrder.getInt("table_id");
                if (tableId > 0) {
                    order.setTable(new Model.CoffeeTable(tableId, rsOrder.getString("table_name"), "", ""));
                }

                PreparedStatement psDetail = conn.prepareStatement(sqlDetails);
                psDetail.setInt(1, orderId);
                ResultSet rsDetail = psDetail.executeQuery();

                while (rsDetail.next()) {
                    Model.MenuItem item = new Model.MenuItem();
                    // ĐÃ SỬA: Gắn mã ID và Giá tiền cho món ăn
                    item.setItemId(rsDetail.getInt("item_id"));
                    item.setItemName(rsDetail.getString("item_name"));
                    item.setPrice(rsDetail.getDouble("unit_price"));

                    // Ném vào danh sách của hóa đơn
                    order.addItem(item, rsDetail.getInt("quantity"));
                }
            }
        } catch (Exception e) {
            System.out.println("Lỗi truy vấn chi tiết hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
        return order;
    }

    public List<DoanhThuNamDTO> layBaoCaoDoanhThuTheoNam() {
        List<DoanhThuNamDTO> list = new ArrayList<>();

        // Câu lệnh SQL tối ưu đã chuẩn bị trước đó
        String sql = "WITH DoanhThuTheoNam AS ( " +
                "    SELECT YEAR(o.order_date) AS nam, SUM(od.subtotal) AS tong_doanh_thu " +
                "    FROM order_details od " +
                "    JOIN orders o ON od.order_id = o.order_id " +
                "    WHERE o.payment_status = N'Đã thanh toán' " +
                "    GROUP BY YEAR(o.order_date) " +
                "), " +
                "XepHangMonAn AS ( " +
                "    SELECT YEAR(o.order_date) AS nam, mi.item_name, " +
                "    ROW_NUMBER() OVER (PARTITION BY YEAR(o.order_date) ORDER BY SUM(od.quantity) DESC) AS xep_hang " +
                "    FROM order_details od " +
                "    JOIN orders o ON od.order_id = o.order_id " +
                "    JOIN menu_items mi ON od.item_id = mi.item_id " +
                "    WHERE o.payment_status = N'Đã thanh toán' " +
                "    GROUP BY YEAR(o.order_date), mi.item_name " +
                ") " +
                "SELECT dt.nam, dt.tong_doanh_thu, xh.item_name AS mon_ban_chay " +
                "FROM DoanhThuTheoNam dt " +
                "LEFT JOIN XepHangMonAn xh ON dt.nam = xh.nam AND xh.xep_hang = 1";

        try (Connection conn = DBUtil.getConnection(); // Sử dụng lớp DBUtil có sẵn của bạn
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int nam = rs.getInt("nam");
                double tongDoanhThu = rs.getDouble("tong_doanh_thu");
                String monBanChay = rs.getString("mon_ban_chay");

                list.add(new DoanhThuNamDTO(nam, tongDoanhThu, monBanChay));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Hàm tạo hóa đơn mới
    public boolean createOrder(int tableId) {
        // Lệnh SQL: Thêm hóa đơn với thời gian hiện tại và trạng thái 'Đang phục vụ'
        String sql = "INSERT INTO orders (table_id, created_at, status) VALUES (?, NOW(), 'Đang phục vụ')";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Nếu tableId > 0 tức là khách ngồi tại bàn. Nếu bằng 0 là khách Mua mang đi
            if (tableId > 0) {
                ps.setInt(1, tableId);
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }

            int rowAffected = ps.executeUpdate();
            return rowAffected > 0; // Trả về true nếu thêm thành công

        } catch (Exception e) {
            System.out.println("Lỗi tạo hóa đơn mới: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    // 1. Hàm lấy danh sách Menu đồ uống ném lên Form
    public List<Model.MenuItem> getAllMenuItems() {
        List<Model.MenuItem> list = new ArrayList<>();
        String sql = "SELECT item_id, item_name, price FROM menu_items WHERE status = 'Còn bán'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while(rs.next()) {
                Model.MenuItem item = new Model.MenuItem();
                item.setItemId(rs.getInt("item_id"));
                item.setItemName(rs.getString("item_name"));
                item.setPrice(rs.getDouble("price"));
                list.add(item);
            }
        } catch(Exception e) { e.printStackTrace(); }
        return list;
    }

    // 2. Hàm tạo hóa đơn và trả về Mã Hóa Đơn (ID) vừa tạo
    public int createOrderAndReturnId(int tableId) {
        String sql = "INSERT INTO orders (table_id, created_at, status) VALUES (?, NOW(), 'Đang phục vụ')";
        try (Connection conn = DBUtil.getConnection();
             // Chú ý: RETURN_GENERATED_KEYS giúp lấy được ID ngay sau khi INSERT
             PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            if (tableId > 0) ps.setInt(1, tableId);
            else ps.setNull(1, java.sql.Types.INTEGER);

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1); // Trả về mã ID

        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }

    // 3. Hàm thêm từng món ăn khách chọn vào hóa đơn đó
    public void addOrderDetail(int orderId, int itemId, int quantity) {
        String sql = "INSERT INTO order_details (order_id, item_id, quantity, unit_price, subtotal) " +
                "SELECT ?, ?, ?, price, price * ? FROM menu_items WHERE item_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, itemId);
            ps.setInt(3, quantity);
            ps.setInt(4, quantity);
            ps.setInt(5, itemId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
    // Hàm lấy danh sách tất cả các bàn từ Database để đổ lên Form
    public List<Model.CoffeeTable> getAllTables() {
        List<Model.CoffeeTable> list = new ArrayList<>();
        String sql = "SELECT table_id, table_name, area, status FROM coffee_tables";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while(rs.next()) {
                Model.CoffeeTable table = new Model.CoffeeTable(
                        rs.getInt("table_id"),
                        rs.getString("table_name"),
                        rs.getString("area"),
                        rs.getString("status")
                );
                list.add(table);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Hàm cập nhật trạng thái hóa đơn dựa trên ID bàn
    public void updateStatusByTable(int tableId, String oldStatus, String newStatus) {
        String sql = "UPDATE orders SET status = ? WHERE table_id = ? AND status = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, tableId);
            ps.setString(3, oldStatus);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Lỗi cập nhật trạng thái: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Hàm cập nhật trạng thái vật lý thẳng vào bảng coffee_tables
    public void updateCoffeeTableStatus(int tableId, String status) {
        String sql = "UPDATE coffee_tables SET status = ? WHERE table_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, tableId);
            ps.executeUpdate();

            System.out.println("Đã cập nhật Bảng coffee_tables bàn số " + tableId + " thành: " + status);

        } catch (Exception e) {
            System.out.println("Lỗi cập nhật bảng coffee_tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // Thêm class này vào cuối file HoaDonDAO.java hoặc tạo file mới trong thư mục Model
    public class DoanhThuNamDTO {
        private int nam;
        private double tongDoanhThu;
        private String monBanChay;

        public DoanhThuNamDTO(int nam, double tongDoanhThu, String monBanChay) {
            this.nam = nam;
            this.tongDoanhThu = tongDoanhThu;
            this.monBanChay = monBanChay;
        }
        // Getter / Setter tương ứng nếu cần
        public int getNam() { return nam; }
        public double getTongDoanhThu() { return tongDoanhThu; }
        public String getMonBanChay() { return monBanChay; }
    }
}

    // 1. Hàm lấy ID bàn hiện tại của hóa đơn
    public int getTableIdByOrderId(int orderId) {
        String sql = "SELECT table_id FROM orders WHERE order_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("table_id");
        } catch (Exception e) { e.printStackTrace(); }
        return 0; // Trả về 0 nghĩa là đang ở trạng thái Mang đi
    }

    // 2. Hàm đổi bàn mới cho hóa đơn
    public void updateOrderTable(int orderId, int newTableId) {
        String sql = "UPDATE orders SET table_id = ? WHERE order_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (newTableId > 0) ps.setInt(1, newTableId);
            else ps.setNull(1, java.sql.Types.INTEGER); // Chuyển sang Mua mang đi
            ps.setInt(2, orderId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // 3. Hàm gọi thêm món (Nếu món đã có thì cộng dồn số lượng, chưa có thì thêm mới)
    public void addOrUpdateOrderDetail(int orderId, int itemId, int quantityAdded) {
        String checkSql = "SELECT quantity FROM order_details WHERE order_id = ? AND item_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement psCheck = conn.prepareStatement(checkSql)) {

            psCheck.setInt(1, orderId);
            psCheck.setInt(2, itemId);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                // Đã gọi món này rồi -> Cộng dồn số lượng và tính lại tiền (Subtotal)
                int newQty = rs.getInt("quantity") + quantityAdded;
                String updateSql = "UPDATE order_details SET quantity = ?, subtotal = unit_price * ? WHERE order_id = ? AND item_id = ?";
                try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                    psUpdate.setInt(1, newQty);
                    psUpdate.setInt(2, newQty);
                    psUpdate.setInt(3, orderId);
                    psUpdate.setInt(4, itemId);
                    psUpdate.executeUpdate();
                }
            } else {
                // Món mới hoàn toàn -> Gọi lại hàm thêm mới của bạn
                addOrderDetail(orderId, itemId, quantityAdded);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    //Thanh toán hoá đơn, chuyển trạng thái, lưu hoá đơn
    public void thanhToanHoaDon(int orderId, int tableId) {
        String sqlUpdateOrder = "UPDATE orders SET status = 'Đã thanh toán' WHERE order_id = ?";
        String sqlUpdateTable = "UPDATE coffee_tables SET status = 'Trống' WHERE table_id = ?";

        try (Connection conn = DBUtil.getConnection()) {
            // UC-20: Lưu trạng thái hóa đơn thành Đã thanh toán
            try (PreparedStatement ps1 = conn.prepareStatement(sqlUpdateOrder)) {
                ps1.setInt(1, orderId);
                ps1.executeUpdate();
            }

            // UC-19: Cập nhật trạng thái bàn về Trống (chỉ cập nhật nếu hóa đơn có gắn với bàn)
            if (tableId > 0) {
                try (PreparedStatement ps2 = conn.prepareStatement(sqlUpdateTable)) {
                    ps2.setInt(1, tableId);
                    ps2.executeUpdate();
                }
            }
        } catch (Exception e) {
            System.out.println("Lỗi khi thanh toán: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Hàm tìm hóa đơn đang mở dựa vào ID Bàn (Dùng cho bên Sơ đồ bàn)
    public Order getOrderByTableId(int tableId) {
        String sql = "SELECT order_id FROM orders WHERE table_id = ? AND status != 'Đã thanh toán' ORDER BY order_id DESC LIMIT 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tableId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Tận dụng lại hàm getOrderById đã viết từ trước cho khỏe
                return getOrderById(rs.getInt("order_id"));
            }
        } catch (Exception e) {
            System.out.println("Lỗi tìm order theo bàn: " + e.getMessage());
        }
        return null;
    }
    public boolean removeOrderDetail(int orderId, int itemId) {
        String sql = "DELETE FROM order_details WHERE order_id = ? AND item_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, itemId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println("Lỗi khi xóa món khỏi order: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    // Hàm xóa hoàn toàn hóa đơn (Sử dụng khi hóa đơn bị xóa hết món)
    public void deleteOrder(int orderId) {
        String sql = "DELETE FROM orders WHERE order_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Lỗi xóa hóa đơn trống: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Hàm cập nhật nhanh trạng thái hóa đơn (Dùng cho Chuyển thanh toán)
    public void updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Lỗi cập nhật trạng thái: " + e.getMessage());
        }
    }
}
