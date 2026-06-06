package DAO;

import DBUtil.DBUtil;
import Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class UserDAO {

    /*
        UC-01 - Đăng nhập
        Main Flow [1.1.5]: UserDAO truy vấn bảng users trong MySQL để kiểm tra username và password.
        Main Flow [1.1.6]: Cơ sở dữ liệu trả về thông tin người dùng nếu tài khoản hợp lệ.
        Alternative Flow [1.3.1]: UserDAO không tìm thấy tài khoản phù hợp trong cơ sở dữ liệu.
    */
    public User findByUsernameAndPassword(String username, String password) {
        String sql = "SELECT user_id, username, password, role FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }

        } catch (Exception e) {
            System.out.println("Lỗi UC-01 khi truy vấn đăng nhập: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /*
        UC-04 - Quản lý nhân viên
        Main Flow [4.1.4]: UserDAO truy vấn bảng users trong MySQL để lấy danh sách nhân viên.
        Main Flow [4.1.5]: Cơ sở dữ liệu trả về danh sách nhân viên.
    */
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT user_id, username, password, role FROM users ORDER BY user_id ASC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }

        } catch (Exception e) {
            System.out.println("Lỗi UC-04 khi lấy danh sách nhân viên: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /*
    UC-04 - Quản lý nhân viên

    Alternative Flow [4.7.2 - 4.7.3]: UserDAO tìm kiếm nhân viên theo từ khóa username trong bảng users.
    Alternative Flow [4.8.2 - 4.8.3]: UserDAO lọc danh sách nhân viên theo role trong bảng users.
    Alternative Flow [4.9.1]: Nếu không có nhân viên thỏa điều kiện, danh sách trả về sẽ rỗng.

    note:
    - keyword rỗng + role rỗng: lấy tất cả nhân viên.
    - keyword có + role rỗng: tìm theo username.
    - keyword rỗng + role có: lọc theo role.
    - keyword có + role có: tìm theo username và lọc theo role.
*/
    public List<User> searchAndFilterUsers(String keyword, String role) {
        List<User> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT user_id, username, password, role FROM users WHERE 1=1"
        );

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasRole = role != null && !role.trim().isEmpty();

        if (hasKeyword) {
            sql.append(" AND username LIKE ?");
        }

        if (hasRole) {
            sql.append(" AND role = ?");
        }

        sql.append(" ORDER BY user_id ASC");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int index = 1;

            if (hasKeyword) {
                ps.setString(index++, "%" + keyword.trim() + "%");
            }

            if (hasRole) {
                ps.setString(index++, role.trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToUser(rs));
                }
            }

        } catch (Exception e) {
            System.out.println("Lỗi UC-04 khi tìm kiếm/lọc nhân viên: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /*
        UC-04 - Quản lý nhân viên
        Alternative Flow [4.5.1]: Servlet phát hiện username bị trùng.
    */
    public boolean isUsernameExists(String username) {
        String sql = "SELECT user_id FROM users WHERE username = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            System.out.println("Lỗi UC-04 khi kiểm tra username trùng: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /*
        UC-04 - Quản lý nhân viên
        Alternative Flow [4.2.5]: Servlet gọi UserDAO.addUser(user) để lưu nhân viên mới vào MySQL.
    */
    public boolean addUser(User user) {
        String sql = "INSERT INTO users(username, password, role) VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Lỗi UC-04 khi thêm nhân viên: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /*
        UC-04 - Quản lý nhân viên
        Alternative Flow [4.3.5]: Servlet gọi UserDAO.updateUser(user) để cập nhật thông tin nhân viên trong MySQL.

        Ghi chú:
        - Nếu password rỗng, chỉ cập nhật username và role.
        - Nếu password có dữ liệu, cập nhật cả password.
    */
    public boolean updateUser(User user) {
        String sqlWithPassword = "UPDATE users SET username = ?, password = ?, role = ? WHERE user_id = ?";
        String sqlWithoutPassword = "UPDATE users SET username = ?, role = ? WHERE user_id = ?";

        boolean hasPassword = user.getPassword() != null && !user.getPassword().trim().isEmpty();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(hasPassword ? sqlWithPassword : sqlWithoutPassword)) {

            if (hasPassword) {
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getRole());
                ps.setInt(4, user.getUserId());
            } else {
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getRole());
                ps.setInt(3, user.getUserId());
            }

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Lỗi UC-04 khi cập nhật nhân viên: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /*
        UC-04 - Quản lý nhân viên
        Alternative Flow [4.4.4]: Servlet gọi UserDAO.deleteUser(userId) để xóa nhân viên trong MySQL.
    */
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Lỗi UC-04 khi xóa nhân viên: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    private User mapResultSetToUser(ResultSet rs) throws Exception {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        return user;
    }
}