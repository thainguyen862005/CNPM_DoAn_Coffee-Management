package Controller;

import Model.CoffeeTable;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/TrangChu")
public class TrangChuServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Kiểm tra xem đã đăng nhập chưa, chưa thì đuổi về login
        if (request.getSession().getAttribute("userDaDangNhap") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Lấy danh sách bàn từ code CoffeeTable cũ của ông ra
        CoffeeTable tableModel = new CoffeeTable(0, "", "", "");
        List<CoffeeTable> dsBan = tableModel.getAllTables();

        // Gửi dữ liệu bàn sang file jsp để in ra màn hình
        request.setAttribute("danhSachBan", dsBan);
        request.setAttribute("page_content", "so_do_ban.jsp");
        request.setAttribute("active_tab", "sodo");
        request.getRequestDispatcher("main_ui.jsp").forward(request, response);
    }
}