package Model;

/*
    Model Layer - User
    - UC-01: Đăng nhập
    - UC-03: Kiểm tra quyền truy cập
    - UC-04: Quản lý nhân viên

    Vai trò:
    - User biểu diễn dữ liệu người dùng.
    - fix lại code cũ bỏ xử lý login/logout/add/update/delete trong Model.
    - chuyển các thao tác truy vấn và CRUD sang UserDAO.
*/

public class User {

    private int userId;
    private String username;
    private String password;
    private String role;

    public User() {
    }

    public User(int userId, String username, String password, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    /*
    UC-03 - Kiểm tra quyền truy cập
    Main Flow [3.1.3]: Hệ thống lấy thông tin role của người dùng từ Session.
    Main Flow [3.1.4]: Servlet hoặc lớp xử lý so sánh role với quyền được phép truy cập chức năng.

    note:
    -  chỉ kiểm tra vai trò của User.
*/
    public boolean isManager() {
        return role != null && role.equalsIgnoreCase("Manager");
    }

    public boolean isStaff() {
        return role != null && role.equalsIgnoreCase("Staff");
    }

    public boolean isCashier() {
        return role != null && role.equalsIgnoreCase("Cashier");
    }
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}