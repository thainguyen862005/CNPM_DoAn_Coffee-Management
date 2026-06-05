package DAO;

import DBUtil.DBUtil;
import Model.CoffeeTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class so_do_banDAO {

    public List<CoffeeTable> getAllTablesFromDB() {
        List<CoffeeTable> listBan = new ArrayList<>();

        // SQL: Lấy trực tiếp trạng thái của Order, nếu không có Order thì gán là 'Trống'
        String sql = "SELECT t.table_id, t.table_name, t.area, " +
                "COALESCE(MAX(o.status), 'Trống') AS computed_status " +
                "FROM coffee_tables t " +
                "LEFT JOIN orders o ON t.table_id = o.table_id AND o.status != 'Đã thanh toán' " +
                "GROUP BY t.table_id, t.table_name, t.area " +
                "ORDER BY t.table_id ASC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CoffeeTable ban = new CoffeeTable(
                        rs.getInt("table_id"),
                        rs.getString("table_name"),
                        rs.getString("area"),
                        rs.getString("computed_status")
                );
                listBan.add(ban);
            }
        } catch (Exception e) {
            System.out.println("Lỗi khi lấy danh sách bàn: " + e.getMessage());
            e.printStackTrace();
        }
        return listBan;
    }
    // Thêm hàm này vào so_do_banDAO.java
    public void updateCoffeeTableStatus(int tableId, String status) {
        String sql = "UPDATE coffee_tables SET status = ? WHERE table_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, tableId);
            ps.executeUpdate();
            System.out.println("so_do_banDAO: Đã cập nhật bàn " + tableId + " thành " + status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}