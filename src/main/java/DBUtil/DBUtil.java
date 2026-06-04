package DBUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/coffee_shop_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Ho_Chi_Minh";
    private static final String USER = "root";

    private static final String PASSWORD = "";

    private DBUtil() {}

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("-> KẾT NỐI DATABASE THÀNH CÔNG!");
        } catch (ClassNotFoundException e) {
            System.out.println("-> LỖI: Chưa có thư viện MySQL Driver (mysql-connector-j.jar) trong thư mục lib!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("-> LỖI: Sai tên Database, sai mật khẩu MySQL hoặc chưa bật MySQL!");
            e.printStackTrace();
        }
        return conn;
    }
}