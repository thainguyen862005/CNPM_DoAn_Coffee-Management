package DAO;

import Model.User;
import DBUtil.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {
    public User findByUsernameAndPassword(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));

                return user; // Trả về thông tin tài khoản
            }
        } catch (Exception e) {
            System.out.println("Lỗi truy vấn cơ sở dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }

        return null; // Trả về null nếu sai tài khoản/mật khẩu
    }
}