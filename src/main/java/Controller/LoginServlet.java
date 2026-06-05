package Controller;

import Model.User;
import DAO.UserDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/Login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String u = request.getParameter("txtUsername");
        String p = request.getParameter("txtPassword");

        if (u == null || u.trim().isEmpty() || p == null || p.trim().isEmpty()) {
            request.setAttribute("thongBaoLoi", "Vui lòng nhập tên đăng nhập và mật khẩu!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        // Đăng nhập bằng database
        UserDAO dao = new UserDAO();
        User tk = dao.findByUsernameAndPassword(u.trim(), p);

        if (tk != null) {
            HttpSession session = request.getSession();
            session.setAttribute("userDaDangNhap", tk.getUsername());
            session.setAttribute("role", tk.getRole());

            response.sendRedirect("TrangChu");
        } else {
            // Sai tài khoản/mật khẩu (hoặc DB lỗi -> dao trả null)
            request.setAttribute("thongBaoLoi", "Sai tên đăng nhập hoặc mật khẩu (hoặc chưa kết nối DB đúng)! ");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}