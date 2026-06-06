package Controller;

import DAO.UserDAO;
import Model.User;

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

        if (!checkLogin(request, response)) {
            return;
        }

        if (!checkManagerPermission(request, response)) {
            return;
        }

        loadEmployeeList(request, response);
    }

    /*
        UC-04 - Quản lý nhân viên
        Alternative Flow [4.2.3]: Giao diện gửi dữ liệu đến QuanLyNhanVienServlet với action add.
        Alternative Flow [4.3.3]: Giao diện gửi dữ liệu đến QuanLyNhanVienServlet với action update.
        Alternative Flow [4.4.3]: Giao diện gửi yêu cầu đến QuanLyNhanVienServlet với action delete.
    */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        if (!checkLogin(request, response)) {
            return;
        }

        if (!checkManagerPermission(request, response)) {
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
        UC-03 - Kiểm tra quyền truy cập
        Alternative Flow [3.2.0 - 3.2.3]:
        Nếu Session không tồn tại, hệ thống điều hướng người dùng về login.jsp.
    */
    private boolean checkLogin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userDaDangNhap") == null) {
            response.sendRedirect("login.jsp");
            return false;
        }

        return true;
    }

    /*
        UC-03 - Kiểm tra quyền truy cập
        Main Flow [3.1.3]: Hệ thống lấy thông tin role của người dùng từ Session.
        Main Flow [3.1.4]: Servlet so sánh role với quyền được phép truy cập chức năng.
        Alternative Flow [3.3.0 - 3.3.3]:
        Nếu role không đủ quyền, hệ thống hiển thị access_denied.jsp.

        UC-04 - Alternative Flow [4.6.0 - 4.6.3]:
        Nếu người dùng không có quyền Manager, hệ thống từ chối truy cập chức năng quản lý nhân viên.
    */
    private boolean checkManagerPermission(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String role = (String) session.getAttribute("role");

        if (!"Manager".equals(role)) {
            request.getRequestDispatcher("access_denied.jsp").forward(request, response);
            return false;
        }

        return true;
    }

    /*
        UC-04 - Quản lý nhân viên
        Main Flow [4.1.3]: Servlet gọi UserDAO.getAllUsers().
        Main Flow [4.1.6]: Servlet gửi danh sách nhân viên sang trang quan_ly_nhan_vien.jsp.
        Main Flow [4.1.7]: Giao diện hiển thị danh sách nhân viên cho Quản lý.
    */
    private void loadEmployeeList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<User> users = userDAO.getAllUsers();

        request.setAttribute("danhSachNhanVien", users);
        request.setAttribute("page_content", "quan_ly_nhan_vien.jsp");
        request.setAttribute("active_tab", "nhanvien");

        request.getRequestDispatcher("main_ui.jsp").forward(request, response);
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