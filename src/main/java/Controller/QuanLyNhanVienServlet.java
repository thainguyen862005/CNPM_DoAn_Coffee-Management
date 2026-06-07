package Controller;

import DAO.UserDAO;
import Model.User;

import Util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/*

    note:
    - Kiểm tra Session và role Manager trước khi cho phép truy cập.
    - Lấy danh sách nhân viên.
    - Xử lý thêm, cập nhật, xóa nhân viên.
*/

@WebServlet("/QuanLyNhanVien")
public class QuanLyNhanVienServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    /*
        UC-04 - Quản lý nhân viên
        Main Flow [4.1.1]: Giao diện gửi request đến QuanLyNhanVienServlet.
        Main Flow [4.1.2]: QuanLyNhanVienServlet kiểm tra Session và quyền truy cập.
        Main Flow [4.1.3]: Nếu người dùng có role Manager, Servlet gọi UserDAO.getAllUsers().
        Main Flow [4.1.6]: Servlet gửi danh sách nhân viên sang trang quan_ly_nhan_vien.jsp.
    */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

    /*
        UC-03 - Kiểm tra quyền truy cập
        [3.1.2]: Servlet kiểm tra Session hiện tại.
        [3.1.3]: Lấy role từ Session.
        [3.1.4]: So sánh role với quyền Manager.
        [3.1.5]: Nếu hợp lệ, cho phép request tiếp tục xử lý.

        UC-04 - Quản lý nhân viên
        [4.1.2]: QuanLyNhanVienServlet kiểm tra Session và quyền truy cập.
        [4.6.0 - 4.6.3]: Nếu không có quyền Manager, hiển thị access_denied.jsp.
    */
        if (!AuthUtil.checkManager(request, response)) {
            return;
        }

        loadEmployeeList(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

    /*
        UC-03 - Kiểm tra quyền truy cập
        Áp dụng cho các thao tác thêm, sửa, xóa nhân viên.
        Chỉ tài khoản có role Manager mới được xử lý tiếp.
    */
        if (!AuthUtil.checkManager(request, response)) {
            return;
        }

        String action = request.getParameter("action");

        if ("add".equals(action)) {
            handleAdd(request, response);
        } else if ("update".equals(action)) {
            handleUpdate(request, response);
        } else if ("delete".equals(action)) {
            handleDelete(request, response);
        } else {
            response.sendRedirect("QuanLyNhanVien");
        }
    }

    /*
    UC-04 - Quản lý nhân viên

    Main Flow [4.1.3]: Servlet gọi UserDAO để lấy danh sách nhân viên.
    Main Flow [4.1.6]: Servlet gửi danh sách nhân viên sang trang quan_ly_nhan_vien.jsp.
    Main Flow [4.1.7]: Giao diện hiển thị danh sách nhân viên cho Quản lý.

    Alternative Flow [4.7.0 - 4.7.5]: Xử lý tìm kiếm nhân viên theo username.
    Alternative Flow [4.8.0 - 4.8.5]: Xử lý lọc nhân viên theo role.
    Alternative Flow [4.9.0 - 4.9.3]: Nếu không có kết quả, Servlet gửi danh sách rỗng để JSP hiển thị thông báo.
*/
    private void loadEmployeeList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    /*
        UC-04 - Alternative Flow [4.7.1] và [4.8.1]: Nhận keyword và role từ form tìm kiếm/lọc trên quan_ly_nhan_vien.jsp.
    */
        String keyword = request.getParameter("keyword");
        String role = request.getParameter("role");

    /*
        UC-04 - Main Flow [4.1.3] kết hợp Alternative Flow [4.7.2] và [4.8.2]:
        - Gọi UserDAO.searchAndFilterUsers(keyword, role).
        - Nếu keyword/role rỗng, DAO sẽ trả về toàn bộ danh sách nhân viên.
    */
        List<User> users = userDAO.searchAndFilterUsers(keyword, role);

    /*
        UC-04 - Main Flow [4.1.6]: Gửi danh sách nhân viên sang quan_ly_nhan_vien.jsp.
    */
        request.setAttribute("danhSachNhanVien", users);

    /*
        Giữ lại điều kiện tìm kiếm/lọc để JSP hiển thị lại trên form sau khi submit.
    */
        request.setAttribute("keyword", keyword == null ? "" : keyword.trim());
        request.setAttribute("selectedRole", role == null ? "" : role.trim());

        request.setAttribute("page_content", "quan_ly_nhan_vien.jsp");
        request.setAttribute("active_tab", "nhanvien");

        request.getRequestDispatcher("/WEB-INF/views/main_ui.jsp").forward(request, response);
    }

    /*
        UC-04 - Quản lý nhân viên
        Alternative Flow [4.2.0 - 4.2.7]: Thêm nhân viên.

        [4.2.4]: Servlet kiểm tra dữ liệu đầu vào như username, password và role.
        [4.2.5]: Servlet gọi UserDAO.addUser(user) để lưu nhân viên mới vào MySQL.
        [4.2.6]: Hệ thống cập nhật và hiển thị lại danh sách nhân viên.

        Alternative Flow [4.5.0 - 4.5.4]:
        Nếu dữ liệu không hợp lệ hoặc username bị trùng, hệ thống hiển thị lỗi.
    */
    private void handleAdd(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = getUserFromRequest(request, false);

        if (!validateUserInput(user, true)) {
            request.setAttribute("errorMessage", "Dữ liệu không hợp lệ. Vui lòng kiểm tra username, password và role.");
            loadEmployeeList(request, response);
            return;
        }

        if (userDAO.isUsernameExists(user.getUsername())) {
            request.setAttribute("errorMessage", "Username đã tồn tại. Vui lòng chọn username khác.");
            loadEmployeeList(request, response);
            return;
        }

        userDAO.addUser(user);
        response.sendRedirect("QuanLyNhanVien");
    }

    /*
        UC-04 - Quản lý nhân viên
        Alternative Flow [4.3.0 - 4.3.7]: Cập nhật nhân viên.

        [4.3.4]: Servlet kiểm tra dữ liệu cập nhật.
        [4.3.5]: Servlet gọi UserDAO.updateUser(user) để cập nhật thông tin nhân viên trong MySQL.
        [4.3.6]: Hệ thống cập nhật và hiển thị lại danh sách nhân viên.
    */
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = getUserFromRequest(request, true);

        if (!validateUserInput(user, false)) {
            request.setAttribute("errorMessage", "Dữ liệu cập nhật không hợp lệ.");
            loadEmployeeList(request, response);
            return;
        }

        userDAO.updateUser(user);
        response.sendRedirect("QuanLyNhanVien");
    }

    /*
        UC-04 - Quản lý nhân viên
        Alternative Flow [4.4.0 - 4.4.6]: Xóa nhân viên.

        [4.4.4]: Servlet gọi UserDAO.deleteUser(userId) để xóa nhân viên trong MySQL.
        [4.4.5]: Hệ thống cập nhật và hiển thị lại danh sách nhân viên.
    */
    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String userIdRaw = request.getParameter("userId");

        if (userIdRaw != null && !userIdRaw.trim().isEmpty()) {
            int userId = Integer.parseInt(userIdRaw);
            userDAO.deleteUser(userId);
        }

        response.sendRedirect("QuanLyNhanVien");
    }

    private User getUserFromRequest(HttpServletRequest request, boolean includeId) {
        User user = new User();

        if (includeId) {
            String userIdRaw = request.getParameter("userId");
            if (userIdRaw != null && !userIdRaw.trim().isEmpty()) {
                user.setUserId(Integer.parseInt(userIdRaw));
            }
        }

        user.setUsername(trim(request.getParameter("username")));
        user.setPassword(trim(request.getParameter("password")));
        user.setRole(trim(request.getParameter("role")));

        return user;
    }

    /*
        UC-04 - Alternative Flow [4.5.1]
        Servlet phát hiện username rỗng, password rỗng, role không hợp lệ hoặc username bị trùng.

        Ghi chú:
        - Khi thêm mới: password bắt buộc nhập.
        - Khi cập nhật: password được phép rỗng nếu không muốn đổi mật khẩu.
    */
    private boolean validateUserInput(User user, boolean passwordRequired) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return false;
        }

        if (passwordRequired && (user.getPassword() == null || user.getPassword().trim().isEmpty())) {
            return false;
        }

        return isValidRole(user.getRole());
    }

    private boolean isValidRole(String role) {
        return Arrays.asList("Manager", "Staff", "Cashier").contains(role);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}