package Controller;

<<<<<<< HEAD
import DAO.MenuItemDAO;
import Model.MenuItem;

import Util.AuthUtil;
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
