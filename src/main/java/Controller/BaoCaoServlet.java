package Controller;

import Util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/BaoCao")
public class BaoCaoServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*
        UC-03 - KIỂM TRA QUYỀN TRUY CẬP

        [3.1.2] Kiểm tra Session hiện tại.
        [3.1.3] Lấy role từ Session.
        [3.1.4] So sánh role với quyền Manager.
        [3.1.5] Nếu hợp lệ thì tiếp tục xử lý request.

        Alternative Flow [3.2.0 - 3.2.3] => Nếu chưa đăng nhập thì chuyển về login.jsp.

        Alternative Flow [3.3.0 - 3.3.3]:
        - Nếu không phải Manager thì hiển thị access_denied.jsp.
        - Dừng xử lý request.
    */
        if (!AuthUtil.checkManager(request, response)) {
            return;
        }

        // Gửi tên file JSP ruột sang cho main_ui
        request.setAttribute("page_content", "bao_cao.jsp");
        // Gửi cờ active để làm sáng nút trên Sidebar
        request.setAttribute("active_tab", "baocao");
        request.getRequestDispatcher("/WEB-INF/views/main_ui.jsp").forward(request, response);
    }
}
