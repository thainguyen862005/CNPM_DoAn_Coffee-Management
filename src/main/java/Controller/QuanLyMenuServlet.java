package Controller;

import DAO.MenuItemDAO;
import Model.MenuItem;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@WebServlet("/QuanLyMenu")
public class QuanLyMenuServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if ("them_mon".equals(action)) {
            // 1. Lấy dữ liệu người dùng nhập từ Modal
            int maMon = Integer.parseInt(request.getParameter("maMon"));
            String tenMon = request.getParameter("tenMon");
            double giaTien = Double.parseDouble(request.getParameter("giaTien"));
            String moTa = request.getParameter("moTa");
            String trangThai = request.getParameter("trangThai");

            // 2. Tạo đối tượng và gọi DAO để lưu vào CSDL
            MenuItem monMoi = new MenuItem(maMon,tenMon, giaTien, moTa, trangThai);
            MenuItemDAO dao = new MenuItemDAO();
            dao.AddMenuItem(monMoi); // Bạn cần đảm bảo có hàm INSERT INTO trong DAO

            // 3. Xong việc thì load lại trang để bảng tự động hiện món mới
            response.sendRedirect("QuanLyMenu");
        }
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Lấy dữ liệu cần thiết (ví dụ danh sách món ăn để hiển thị)
        MenuItemDAO dao = new MenuItemDAO();
        List<MenuItem> listMenu = dao.getAllMenuItems(); // Giả sử bạn có hàm này

        // 2. Gửi dữ liệu sang JSP
        request.setAttribute("MenuItem", listMenu);

        // 3. QUAN TRỌNG: Thiết lập biến 'page_content' để main_ui.jsp biết cần nạp file nào
        request.setAttribute("page_content", "quan_ly_menu.jsp");

        // 4. Thiết lập tab đang hoạt động (để Sidebar đổi màu đúng)
        request.setAttribute("active_tab", "quanlymenu");

        // 5. Gọi bộ khung main_ui.jsp lên thay vì gọi trực tiếp file jsp của bạn
        request.getRequestDispatcher("main_ui.jsp").forward(request, response);
    }
}
