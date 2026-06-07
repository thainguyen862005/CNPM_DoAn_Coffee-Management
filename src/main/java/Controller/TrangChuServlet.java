package Controller;

import DAO.so_do_banDAO;
import Model.CoffeeTable;
import Util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/TrangChu")
public class TrangChuServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*
        UC-03 - Alternative Flow [3.2.0 - 3.2.3]:

            - Kiểm tra Session hiện tại của người dùng.
            - Nếu Session không tồn tại hoặc chưa có thông tin đăng nhập, AuthUtil sẽ điều hướng người dùng về login.jsp.
            -> return dùng để dừng toàn bộ xử lý phía dưới.
*/
        if (!AuthUtil.checkLogin(request, response)) {
            return;
        }

        // Lấy danh sách bàn từ code CoffeeTable cũ của ông ra
        so_do_banDAO dao = new so_do_banDAO();
        List<CoffeeTable> dsBan = dao.getAllTablesFromDB(); // Gọi dữ liệu thật từ MySQL

        // Gửi dữ liệu bàn sang file jsp để in ra màn hình
        request.setAttribute("danhSachBan", dsBan);
        request.setAttribute("page_content", "so_do_ban.jsp");
        request.setAttribute("active_tab", "sodo");
        request.getRequestDispatcher("/WEB-INF/views/main_ui.jsp").forward(request, response);
    }
}