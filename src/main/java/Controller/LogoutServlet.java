package Controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;


@WebServlet("/Logout")
public class LogoutServlet extends HttpServlet {

    /*
        UC-02 - Đăng xuất
        Main Flow [2.1.2]: Giao diện gửi yêu cầu đăng xuất đến LogoutServlet.
        Main Flow [2.1.3]: LogoutServlet lấy phiên làm việc hiện tại của người dùng.
        Main Flow [2.1.4]: LogoutServlet hủy Session bằng invalidate().
        Main Flow [2.1.6]: Hệ thống điều hướng người dùng về trang login.jsp.

        Alternative Flow [2.2.0 - 2.2.4]:
        Nếu Session không tồn tại hoặc đã hết hạn, hệ thống vẫn điều hướng về login.jsp.
    */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        response.sendRedirect("login.jsp");
    }
}