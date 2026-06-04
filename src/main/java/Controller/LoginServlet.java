package Controller;

import Model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/Login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String u = request.getParameter("txtUsername");
        String p = request.getParameter("txtPassword");

        // Khởi tạo model User để gọi code cũ của ông
        User user = new User();
        user.setUsername(u);
        user.setPassword(p);

        // Đoạn này giả lập check cơ bản theo mảng tĩnh của ông
        boolean dangNhapThanhCong = false;
        String chucVu = "";

        for (int i = 0; i < user.getAllUsers().size(); i++) {
            User tk = user.getAllUsers().get(i);
            if (tk.getUsername().equals(u) && tk.getPassword().equals(p)) {
                dangNhapThanhCong = true;
                chucVu = tk.getRole();
                break;
            }
        }

        if (dangNhapThanhCong) {
            // Lưu session và chuyển sang trang MainUI (Trang chủ)
            HttpSession session = request.getSession();
            session.setAttribute("userDaDangNhap", u);
            session.setAttribute("role", chucVu);

            response.sendRedirect("TrangChu");
        } else {
            // Sai thì báo lỗi đẩy về trang login
            request.setAttribute("thongBaoLoi", "Sai tên đăng nhập hoặc mật khẩu!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}