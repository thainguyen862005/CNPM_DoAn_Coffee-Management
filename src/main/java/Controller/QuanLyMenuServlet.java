package Controller;

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

        MenuItemDAO dao = new MenuItemDAO();
        String action = request.getParameter("action");

        // 1. Xử lý xóa món ăn (Chuyển trạng thái về Ngừng bán)
        if ("delete".equals(action)) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                dao.deleteMenuItem(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            response.sendRedirect(request.getContextPath() + "/QuanLyMenu");
            return;
        }

        // 2. Xử lý yêu cầu Sửa (Lấy dữ liệu cũ đưa lên form)
        if ("edit".equals(action)) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                MenuItem item = dao.getItemById(id);
                request.setAttribute("itemEdit", item); // Đẩy đối tượng cần sửa sang JSP
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 3. Luôn lấy danh sách mới nhất để hiển thị lên bảng
        List<MenuItem> list = dao.getAllMenuItemsForManagement();
        request.setAttribute("menuList", list);

        // Gửi tên file JSP ruột sang cho main_ui
        request.setAttribute("page_content", "quan_ly_menu.jsp");
        // Gửi cờ active để làm sáng nút trên Sidebar
        request.setAttribute("active_tab", "menu");
        request.getRequestDispatcher("/WEB-INF/views/main_ui.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Kiểm tra quyền truy cập khi submit dữ liệu
        if (!AuthUtil.checkManager(request, response)) {
            return;
        }

        request.setCharacterEncoding("UTF-8");
        MenuItemDAO dao = new MenuItemDAO();

        String action = request.getParameter("action");
        String itemName = request.getParameter("itemName");
        String priceStr = request.getParameter("price");
        String description = request.getParameter("description");
        String status = request.getParameter("status");

        try {
            double price = Double.parseDouble(priceStr);

            MenuItem item = new MenuItem();
            item.setItemName(itemName);
            item.setPrice(price);
            item.setDescription(description);
            item.setStatus(status);

            if ("add".equals(action)) {
                dao.addMenuItem(item);
            } else if ("update".equals(action)) {
                int itemId = Integer.parseInt(request.getParameter("itemId"));
                item.setItemId(itemId);
                dao.updateMenuItem(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Dùng phương pháp PRG (Post-Redirect-Get) để tránh lặp dữ liệu khi F5 (refresh) trang
        response.sendRedirect(request.getContextPath() + "/QuanLyMenu");
    }
}
