package Controller;

import DAO.HoaDonDAO;
import Util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/BaoCao")
public class BaoCaoServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy tham số 'action' từ URL (nếu có)
        String action = request.getParameter("action");

        if ("lay_du_lieu".equals(action)) {
            // 
            // TRƯỜNG HỢP 1: JS gọi xuống để lấy JSON
            // 
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            HoaDonDAO hoaDonDAO = new HoaDonDAO();
            List<HoaDonDAO.DoanhThuNamDTO> danhSach = hoaDonDAO.layBaoCaoDoanhThuTheoNam();

            StringBuilder json = new StringBuilder();
            json.append("[");
            for (int i = 0; i < danhSach.size(); i++) {
                HoaDonDAO.DoanhThuNamDTO dto = danhSach.get(i);
                json.append(String.format("{\"nam\": %d, \"tong_doanh_thu\": %.2f, \"mon_ban_chay\": \"%s\"}",
                        dto.getNam(), dto.getTongDoanhThu(), dto.getMonBanChay() != null ? dto.getMonBanChay() : "--"));
                if (i < danhSach.size() - 1) json.append(",");
            }
            json.append("]");

            PrintWriter out = response.getWriter();
            out.print(json.toString());
            out.flush();

        } else {
            // 
            // TRƯỜNG HỢP 2: Người dùng bấm vào Menu để xem trang HTML
            // 
            // Set tab đang active để Menu sáng lên
            request.setAttribute("active_tab", "baocaongay");

            // Nhét file bao_cao.jsp (cái ruột) vào ${page_content}
            request.setAttribute("page_content", "bao_cao.jsp");

            // Đẩy toàn bộ ra khung main_ui.jsp
            request.getRequestDispatcher("main_ui.jsp").forward(request, response);
        }
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
