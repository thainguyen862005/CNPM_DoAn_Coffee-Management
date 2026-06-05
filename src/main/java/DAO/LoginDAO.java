package DAO;

public class LoginDAO {
        // Phương thức kiểm tra đăng nhập
        public boolean checkLogin(String username, String password) {
            // Ở đây bạn có thể kết nối đến cơ sở dữ liệu và kiểm tra thông tin đăng nhập
            // Ví dụ giả lập với một tài khoản cố định
            if ("admin".equals(username) && "admin123".equals(password)) {
                return true; // Đăng nhập thành công
            }
            return false; // Đăng nhập thất bại
        }
}
