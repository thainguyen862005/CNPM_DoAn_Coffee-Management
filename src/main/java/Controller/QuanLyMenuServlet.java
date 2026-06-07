package Controller;

import Util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/QuanLyMenu")
public class QuanLyMenuServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*
            UC-03 - KIỂM TRA QUYỀN TRUY CẬP

            Main Flow:
            [3.1.2] Kiểm tra Session hiện tại.
            [3.1.3] Lấy role của người dùng từ Session.
            [3.1.4] So sánh role hiện tại với role Manager.
            [3.1.5] Nếu role hợp lệ thì tiếp tục xử lý request.

            Alternative Flow:
            [3.2.0 - 3.2.3] Nếu chưa đăng nhập ==> AuthUtil điều hướng về login.jsp.

            [3.3.0 - 3.3.3] Nếu không có role Manager:
            -> AuthUtil forward sang access_denied.jsp.
            -> Dừng xử lý request.
        */
        if (!AuthUtil.checkManager(request, response)) {
            return;
        }

        // Gửi tên file JSP ruột sang cho main_ui
        request.setAttribute("page_content", "quan_ly_menu.jsp");
        // Gửi cờ active để làm sáng nút trên Sidebar
        request.setAttribute("active_tab", "menu");
        request.getRequestDispatcher("/WEB-INF/views/main_ui.jsp").forward(request, response);
    }
}
