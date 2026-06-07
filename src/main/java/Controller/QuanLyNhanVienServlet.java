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

    [4.2.4]: Kiểm tra username, password và role.
    [4.2.5]: Gọi UserDAO.addUser(user).
    [4.2.6]: Hiển thị lại danh sách sau khi thêm thành công.

    Alternative Flow [4.5.0 - 4.5.4]: Không lưu dữ liệu nếu đầu vào không hợp lệ, username trùng hoặc thao tác cơ sở dữ liệu thất bại.
*/
    private void handleAdd(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        User user = getUserFromRequest(request, false);

        // UC-04 [4.2.4] và [4.5.1]: Kiểm tra dữ liệu đầu vào.
        if (!validateUserInput(user, true)) {
            request.setAttribute(
                    "errorMessage",
                    "Vui lòng nhập đầy đủ username, password và chọn đúng vai trò."
            );
            loadEmployeeList(request, response);
            return;
        }

        // UC-04 [4.5.1]: Không cho phép username bị trùng.
        if (userDAO.isUsernameExists(user.getUsername())) {
            request.setAttribute(
                    "errorMessage",
                    "Username đã tồn tại. Vui lòng chọn username khác."
            );
            loadEmployeeList(request, response);
            return;
        }

        // UC-04 [4.2.5]: Thực hiện thêm nhân viên.
        boolean added = userDAO.addUser(user);

        if (!added) {
            request.setAttribute(
                    "errorMessage",
                    "Không thể thêm nhân viên do lỗi cơ sở dữ liệu."
            );
            loadEmployeeList(request, response);
            return;
        }

        // UC-04 [4.2.6 - 4.2.7]: Tải lại danh sách bằng PRG.
        response.sendRedirect(
                request.getContextPath()
                        + "/QuanLyNhanVien?success=add"
        );
    }

    /*
    UC-04 - Quản lý nhân viên
    Alternative Flow [4.3.0 - 4.3.7]: Cập nhật nhân viên.

    [4.3.4]: Kiểm tra dữ liệu cập nhật.
    [4.3.5]: Gọi UserDAO.updateUser(user).
    [4.3.6]: Hiển thị lại danh sách sau khi cập nhật.

    Alternative Flow [4.5.0 - 4.5.4]: Không cập nhật nếu ID không hợp lệ, dữ liệu sai, username trùng hoặc cơ sở dữ liệu xử lý thất bại.
*/
    private void handleUpdate(HttpServletRequest request,
                              HttpServletResponse response)
            throws ServletException, IOException {

        User user;

        try {
            user = getUserFromRequest(request, true);
        } catch (NumberFormatException e) {
            request.setAttribute(
                    "errorMessage",
                    "Mã nhân viên cần cập nhật không hợp lệ."
            );
            loadEmployeeList(request, response);
            return;
        }

        // ID phải lớn hơn 0.
        if (user.getUserId() <= 0) {
            request.setAttribute(
                    "errorMessage",
                    "Không xác định được nhân viên cần cập nhật."
            );
            loadEmployeeList(request, response);
            return;
        }

        // UC-04 [4.3.4] và [4.5.1]: Kiểm tra dữ liệu.
        if (!validateUserInput(user, false)) {
            request.setAttribute(
                    "errorMessage",
                    "Dữ liệu cập nhật không hợp lệ."
            );
            loadEmployeeList(request, response);
            return;
        }

/*
    UC-04 [4.5.1]
    Business Rule BR-04-02: Username phải duy nhất trong hệ thống.
    Khi cập nhật, username được phép giữ nguyên cho chính tài khoản đó, nhưng không được trùng với username của tài khoản khác.
*/
        if (userDAO.isUsernameExistsForOtherUser(
                user.getUsername(),
                user.getUserId()
        )) {
            request.setAttribute(
                    "errorMessage",
                    "Username đã được sử dụng bởi tài khoản khác."
            );
            loadEmployeeList(request, response);
            return;
        }

        // UC-04 [4.3.5]: Cập nhật dữ liệu.
        boolean updated = userDAO.updateUser(user);

        if (!updated) {
            request.setAttribute(
                    "errorMessage",
                    "Không thể cập nhật nhân viên. Tài khoản có thể không tồn tại."
            );
            loadEmployeeList(request, response);
            return;
        }

        // UC-04 [4.3.6 - 4.3.7]: Tải lại danh sách.
        response.sendRedirect(
                request.getContextPath()
                        + "/QuanLyNhanVien?success=update"
        );
    }

    /*
    UC-04 - Quản lý nhân viên
    Alternative Flow [4.4.0 - 4.4.6]: Xóa nhân viên.

    [4.4.3]: Giao diện gửi action delete và userId.
    [4.4.4]: Servlet gọi UserDAO.deleteUser(userId).
    [4.4.5]: Hiển thị lại danh sách nhân viên.

    Alternative Flow [4.5.0 - 4.5.4]: Không thực hiện xóa nếu userId không hợp lệ hoặc thao tác cơ sở dữ liệu thất bại.
*/
    private void handleDelete(HttpServletRequest request,
                              HttpServletResponse response)
            throws ServletException, IOException {

        String userIdRaw = trim(request.getParameter("userId"));

        if (userIdRaw.isEmpty()) {
            request.setAttribute(
                    "errorMessage",
                    "Không xác định được nhân viên cần xóa."
            );
            loadEmployeeList(request, response);
            return;
        }

        final int userId;

        try {
            userId = Integer.parseInt(userIdRaw);
        } catch (NumberFormatException e) {
            request.setAttribute(
                    "errorMessage",
                    "Mã nhân viên cần xóa không hợp lệ."
            );
            loadEmployeeList(request, response);
            return;
        }

        if (userId <= 0) {
            request.setAttribute(
                    "errorMessage",
                    "Mã nhân viên cần xóa không hợp lệ."
            );
            loadEmployeeList(request, response);
            return;
        }

    /*
    UC-04 - Alternative Flow [4.10.0 - 4.10.4]
    Business Rule BR-04-01: Quản lý không được xóa chính tài khoản đang đăng nhập.

    Nếu userId cần xóa trùng với userId lưu trong Session:
    - Không gọi UserDAO.deleteUser(userId).
    - Không thay đổi dữ liệu trong MySQL.
    - Hiển thị thông báo lỗi trên giao diện quản lý nhân viên.
*/
        Object currentUserIdObject =
                request.getSession(false).getAttribute("userId");

        if (currentUserIdObject instanceof Integer
                && ((Integer) currentUserIdObject) == userId) {

            request.setAttribute(
                    "errorMessage",
                    "Bạn không thể xóa tài khoản đang đăng nhập."
            );
            loadEmployeeList(request, response);
            return;
        }

        /*
    UC-04 [4.4.4]: Sau khi kiểm tra BR-04-01 thành công, Servlet gọi UserDAO.deleteUser(userId) để xóa nhân viên khỏi cơ sở dữ liệu.
        */
        boolean deleted = userDAO.deleteUser(userId);

        if (!deleted) {
            request.setAttribute(
                    "errorMessage",
                    "Không thể xóa nhân viên. Tài khoản có thể không tồn tại."
            );
            loadEmployeeList(request, response);
            return;
        }

        // UC-04 [4.4.5 - 4.4.6]: Tải lại danh sách.
        response.sendRedirect(
                request.getContextPath()
                        + "/QuanLyNhanVien?success=delete"
        );
    }

    private User getUserFromRequest(HttpServletRequest request,
                                    boolean includeId) {

        User user = new User();

        if (includeId) {
            String userIdRaw = trim(request.getParameter("userId"));

            if (userIdRaw.isEmpty()) {
                throw new NumberFormatException("User ID is empty");
            }

            user.setUserId(Integer.parseInt(userIdRaw));
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

    /*
        UC-03 và UC-04:
        Các vai trò hợp lệ thống nhất giữa document, code và database:
        Manager, Staff, Cashier.
    */
    private boolean isValidRole(String role) {
        return role != null && Arrays.asList( "Manager", "Staff", "Cashier").contains(role);
    }
    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}