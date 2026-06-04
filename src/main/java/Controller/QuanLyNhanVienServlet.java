package Controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/QuanLyNhanVien")
public class QuanLyNhanVienServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Gửi tên file JSP ruột sang cho main_ui
        request.setAttribute("page_content", "quan_ly_nhan_vien.jsp");
        // Gửi cờ active để làm sáng nút trên Sidebar
        request.setAttribute("active_tab", "nhanvien");
        request.getRequestDispatcher("main_ui.jsp").forward(request, response);
    }
}
