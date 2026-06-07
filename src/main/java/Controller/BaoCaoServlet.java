package Controller;

import DAO.HoaDonDAO;
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
    }
}
