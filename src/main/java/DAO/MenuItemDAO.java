package DAO;

<<<<<<< HEAD
// Lệnh import các thư viện và Model cần thiết
import DBUtil.DBUtil;
import Model.MenuItem;
import DBUtil.DBUtil;
import Model.MenuItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
// Khai báo Class bọc ngoài các phương thức
public class MenuItemDAO {

    public void AddMenuItem(MenuItem mon) {
        String sql = "INSERT INTO menu_items (item_ID,item_name, price, description, status) VALUES (?, ?, ?, ?, ?)";

        // Đã thêm lại chữ "try ("
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mon.getItemId());
            ps.setString(2, mon.getItemName());
            ps.setDouble(3, mon.getPrice());
            // Đã đổi lại đúng thứ tự số 3 là Description, số 4 là Status
            ps.setString(4, mon.getDescription());
            ps.setString(5, mon.getStatus());

            int rowAffected = ps.executeUpdate();

            if (rowAffected > 0) {
                System.out.println("Thêm món ăn thành công!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Hàm lấy toàn bộ danh sách món ăn từ Database
    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> danhSachMon = new ArrayList<>();

        // Lưu ý: Đổi tên bảng 'menu_items' cho đúng với CSDL của bạn
        String sql = "SELECT * FROM menu_items";

public class MenuItemDAO {

    // Hàm lấy thông tin chi tiết 1 món ăn dựa vào Mã món (ID)
    public MenuItem getItemById(int itemId) {
        MenuItem item = null;
        String sql = "SELECT item_id, item_name, price FROM menu_items WHERE item_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Truyền ID khách chọn vào câu truy vấn
            ps.setInt(1, itemId);
            ResultSet rs = ps.executeQuery();

            // Nếu tìm thấy món ăn, đóng gói vào Object MenuItem
            if (rs.next()) {
                item = new MenuItem();
                item.setItemId(rs.getInt("item_id"));
                item.setItemName(rs.getString("item_name"));
                item.setPrice(rs.getDouble("price"));
            }
        } catch (Exception e) {
            System.out.println("Lỗi lấy thông tin món ăn: " + e.getMessage());
            e.printStackTrace();
        }
        return item; // Trả về đối tượng để Servlet dùng
    }
    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> list = new ArrayList<>();
        String sql = "SELECT item_id, item_name, price FROM menu_items WHERE status = 'Còn bán'";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Đọc từng dòng dữ liệu từ DB
                // LƯU Ý: Thay thế "item_id", "item_name"... bằng đúng tên cột trong Database của bạn
                int id = rs.getInt("item_id");
                String ten = rs.getString("item_name");
                double gia = rs.getDouble("price");
                String moTa = rs.getString("description");
                String trangThai = rs.getString("status");

                // Đưa vào Constructor 5 tham số của class MenuItem
                MenuItem mon = new MenuItem(id, ten, gia, moTa, trangThai);

                // Thêm vào danh sách
                danhSachMon.add(mon);
            }

        } catch (Exception e) {
            System.out.println("Lỗi khi lấy danh sách món ăn: " + e.getMessage());
            e.printStackTrace();
        }

        return danhSachMon; // Trả danh sách về cho Servlet

            while(rs.next()) {
                MenuItem item = new MenuItem();
                item.setItemId(rs.getInt("item_id"));
                item.setItemName(rs.getString("item_name"));
                item.setPrice(rs.getDouble("price"));
                list.add(item);
            }
        } catch(Exception e) {
            System.out.println("Lỗi lấy danh sách món ăn: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
}