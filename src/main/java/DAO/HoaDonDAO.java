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
}