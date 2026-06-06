package Controller;

import DAO.UserDAO;
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

    /*
        UC-01 - Đăng nhập
        Main Flow [1.1.1]: Giao diện login.jsp hiển thị form đăng nhập.
        Nếu người dùng truy cập trực tiếp /Login bằng GET, hệ thống trả về login.jsp.
    */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    /*
        UC-01 - Đăng nhập
        Main Flow [1.1.3]: Giao diện gửi dữ liệu đăng nhập đến LoginServlet bằng phương thức POST.
        Main Flow [1.1.4]: LoginServlet tiếp nhận dữ liệu, kiểm tra thông tin đầu vào và gọi UserDAO.
    */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String username = request.getParameter("txtUsername");
        String password = request.getParameter("txtPassword");

        /*
            UC-01 - Alternative Flow [1.2.0 - 1.2.3]
            Nếu người dùng nhập thiếu username hoặc password,
            hệ thống trả về login.jsp và hiển thị thông báo lỗi.
        */
        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            request.setAttribute("thongBaoLoi", "Vui lòng nhập đầy đủ username và password.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        UserDAO userDAO = new UserDAO();

        /*
            UC-01 - Main Flow [1.1.5 - 1.1.6]
            Gọi UserDAO để truy vấn bảng users trong MySQL.
        */
        User user = userDAO.findByUsernameAndPassword(username.trim(), password);

        if (user != null) {
            /*
                UC-01 - Main Flow [1.1.7]
                LoginServlet khởi tạo Session và lưu thông tin username, role của người dùng.
            */
            HttpSession session = request.getSession();
            session.setAttribute("userDaDangNhap", user.getUsername());
            session.setAttribute("role", user.getRole());
            session.setAttribute("userId", user.getUserId());

            /*
                UC-01 - Main Flow [1.1.8]
                Hệ thống điều hướng người dùng đến giao diện chính.

                Ghi chú khớp source:
                - LoginServlet redirect đến TrangChu.
                - TrangChuServlet sau đó forward sang main_ui.jsp.
                - Trong document có thể ghi: điều hướng đến giao diện chính main_ui.jsp thông qua TrangChuServlet.
            */
            response.sendRedirect("TrangChu");
        } else {
            /*
                UC-01 - Alternative Flow [1.3.0 - 1.3.4]
                Tài khoản không tồn tại hoặc mật khẩu không chính xác.
            */
            request.setAttribute("thongBaoLoi", "Tài khoản hoặc mật khẩu không chính xác.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}