package Util;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class AuthUtil {

    /*
        UC-03 - Alternative Flow [3.2.0 - 3.2.3]: Nếu Session không tồn tại hoặc người dùng chưa đăng nhập:
        -> Điều hướng về login.jsp.
        -> Không cho xử lý tiếp request.
    */
    public static boolean checkLogin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userDaDangNhap") == null || session.getAttribute("role") == null)  {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return false;
        }

        return true;
    }

    /*
        UC-03 - Main Flow [3.1.3 - 3.1.5]:
        - Lấy role từ Session.
        - So sánh role với quyền yêu cầu.

        UC-03 - Alternative Flow [3.3.0 - 3.3.3] -> Nếu role không đủ quyền:
        - Forward sang access_denied.jsp.
        - Không cho xử lý tiếp request.
    */
    public static boolean checkRole(HttpServletRequest request, HttpServletResponse response, String requiredRole)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userDaDangNhap") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return false;
        }

        String currentRole = (String) session.getAttribute("role");

        if (!requiredRole.equals(currentRole)) {
            request.getRequestDispatcher("/WEB-INF/views/access_denied.jsp").forward(request, response);
            return false;
        }

        return true;
    }

    /*
        UC-03 + UC-04: Dùng riêng cho các chức năng chỉ Quản lý được truy cập,
        vd: Quản lý nhân viên, Quản lý menu, Báo cáo doanh thu.
    */
    public static boolean checkManager(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        return checkRole(request, response, "Manager");
    }
}