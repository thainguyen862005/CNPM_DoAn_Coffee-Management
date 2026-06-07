package Controller;

import DAO.HoaDonDAO;
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

        // 1. Lấy giá trị bộ lọc từ combobox gửi lên (nếu không có thì mặc định là 'all')
        String timeFilter = request.getParameter("timeFilter");
        if (timeFilter == null || timeFilter.trim().isEmpty()) {
            timeFilter = "all";
        }

        // 2. Gọi DAO truyền tham số thời gian vào để tính toán dữ liệu thu gọn
        HoaDonDAO hoaDonDao = new HoaDonDAO();
        int totalInvoices = hoaDonDao.getTotalInvoices(timeFilter);
        double totalRevenue = hoaDonDao.getTotalRevenue(timeFilter);

        // 3. Gửi số liệu báo cáo và giữ lại trạng thái combobox vừa chọn sang JSP
        request.setAttribute("totalInvoices", totalInvoices);
        request.setAttribute("totalRevenue", totalRevenue);
        request.setAttribute("selectedFilter", timeFilter);

        // Gửi thông tin cấu trúc giao diện chính main_ui
        request.setAttribute("page_content", "bao_cao.jsp");
        request.setAttribute("active_tab", "baocao");

        request.getRequestDispatcher("/WEB-INF/views/main_ui.jsp").forward(request, response);
    }
}
