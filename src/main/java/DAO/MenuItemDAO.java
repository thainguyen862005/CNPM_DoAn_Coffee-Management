package DAO;

import DBUtil.DBUtil;
import Model.MenuItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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