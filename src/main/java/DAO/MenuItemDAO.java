package DAO;

import DBUtil.DBUtil;
import Model.MenuItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MenuItemDAO {

    // Cập nhật hàm getItemById để lấy đầy đủ cả description và status phục vụ chỉnh sửa
    public MenuItem getItemById(int itemId) {
        MenuItem item = null;
        String sql = "SELECT item_id, item_name, price, description, status FROM menu_items WHERE item_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    item = new MenuItem();
                    item.setItemId(rs.getInt("item_id"));
                    item.setItemName(rs.getString("item_name"));
                    item.setPrice(rs.getDouble("price"));
                    item.setDescription(rs.getString("description"));
                    item.setStatus(rs.getString("status"));
                }
            }
        } catch (Exception e) {
            System.out.println("Lỗi lấy thông tin món ăn: " + e.getMessage());
            e.printStackTrace();
        }
        return item;
    }

    // Hàm lấy danh sách món ăn đang bán (phục vụ cho Khách hàng/Order)
    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> list = new ArrayList<>();
        String sql = "SELECT item_id, item_name, price, description, status FROM menu_items WHERE status = 'Còn bán'";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while(rs.next()) {
                MenuItem item = new MenuItem();
                item.setItemId(rs.getInt("item_id"));
                item.setItemName(rs.getString("item_name"));
                item.setPrice(rs.getDouble("price"));
                item.setDescription(rs.getString("description"));
                item.setStatus(rs.getString("status"));
                list.add(item);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // THÊM MỚI: Hàm lấy TẤT CẢ món ăn (kể cả Ngừng bán) dành riêng cho Quản lý
    public List<MenuItem> getAllMenuItemsForManagement() {
        List<MenuItem> list = new ArrayList<>();
        String sql = "SELECT item_id, item_name, price, description, status FROM menu_items";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while(rs.next()) {
                MenuItem item = new MenuItem();
                item.setItemId(rs.getInt("item_id"));
                item.setItemName(rs.getString("item_name"));
                item.setPrice(rs.getDouble("price"));
                item.setDescription(rs.getString("description"));
                item.setStatus(rs.getString("status"));
                list.add(item);
            }
        } catch(Exception e) {
            System.out.println("Lỗi lấy danh sách quản lý: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // THÊM MỚI: Thêm món ăn vào Database
    public boolean addMenuItem(MenuItem item) {
        String sql = "INSERT INTO menu_items (item_name, price, description, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getItemName());
            ps.setDouble(2, item.getPrice());
            ps.setString(3, item.getDescription());
            ps.setString(4, item.getStatus());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // THÊM MỚI: Cập nhật thông tin món ăn trong Database
    public boolean updateMenuItem(MenuItem item) {
        String sql = "UPDATE menu_items SET item_name = ?, price = ?, description = ?, status = ? WHERE item_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getItemName());
            ps.setDouble(2, item.getPrice());
            ps.setString(3, item.getDescription());
            ps.setString(4, item.getStatus());
            ps.setInt(5, item.getItemId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // THÊM MỚI: Đổi trạng thái sang "Ngừng bán" (Xóa mềm)
    public boolean deleteMenuItem(int itemId) {
        String sql = "UPDATE menu_items SET status = 'Ngừng bán' WHERE item_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}