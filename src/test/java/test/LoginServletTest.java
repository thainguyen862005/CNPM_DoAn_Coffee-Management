package test;

import Controller.LoginServlet;
import DAO.UserDAO;
import Model.User;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/*
    ============================================================
    DEVELOPMENT TESTING - UC-01: ĐĂNG NHẬP
    ============================================================

    - Các test case:
        DT-01: Đăng nhập thiếu username/password
        DT-02: Đăng nhập sai tài khoản hoặc mật khẩu
        DT-03: Đăng nhập thành công

    Thành phần source code được kiểm thử:
    - Controller.LoginServlet
    - DAO.UserDAO
    - Model.User
    - HttpSession

*/
class LoginServletTest {


    private static class TestableLoginServlet extends LoginServlet {
        public void callDoPost(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
            super.doPost(request, response);
        }
    }

    /*
        Helper tìm user theo username chính xác.

        Lý do cần helper:
        - UserDAO hiện tại chưa có hàm findByUsername(username).
        - searchAndFilterUsers(keyword, role) dùng LIKE nên có thể trả về
          nhiều user gần giống.
        - Helper này lọc lại username chính xác để phục vụ cleanup dữ liệu test.
    */
    private User findExactUserByUsername(UserDAO userDAO, String username) {
        return userDAO.searchAndFilterUsers(username, null)
                .stream()
                .filter(user -> username.equals(user.getUsername()))
                .findFirst()
                .orElse(null);
    }

    /*
        Helper xóa user test nếu đã tồn tại.

        Mục đích:
        - Tránh lỗi trùng username khi chạy test nhiều lần.
        - Đảm bảo test có thể chạy lặp lại như yêu cầu Regression Testing.
    */
    private void deleteUserIfExists(UserDAO userDAO, String username) {
        User oldUser = findExactUserByUsername(userDAO, username);

        if (oldUser != null) {
            userDAO.deleteUser(oldUser.getUserId());
        }
    }

    /*
        ============================================================
        DT-01 - UC-01 Alternative Flow [1.2.0 - 1.2.3]
        ============================================================

        Mô tả trong document:
        - Nếu người dùng nhập thiếu username hoặc password,
          LoginServlet phát hiện dữ liệu đầu vào không hợp lệ.
        - Hệ thống trả về login.jsp và hiển thị thông báo:
          "Vui lòng nhập đầy đủ username và password."

        Input/Điều kiện:
        - txtUsername rỗng
        - txtPassword rỗng

        Expected Result:
        - Không tạo Session đăng nhập.
        - Set request attribute "thongBaoLoi".
        - Forward về login.jsp.
    */
    @Test
    void DT01_loginMissingUsernameOrPassword_shouldForwardLoginWithErrorMessage() throws Exception {
        TestableLoginServlet servlet = new TestableLoginServlet();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getParameter("txtUsername")).thenReturn("");
        when(request.getParameter("txtPassword")).thenReturn("");
        when(request.getRequestDispatcher("login.jsp")).thenReturn(dispatcher);

        servlet.callDoPost(request, response);

        verify(request).setCharacterEncoding("UTF-8");
        verify(response).setCharacterEncoding("UTF-8");

        verify(request).setAttribute(
                "thongBaoLoi",
                "Vui lòng nhập đầy đủ username và password."
        );

        verify(dispatcher).forward(request, response);

        verify(response, never()).sendRedirect("TrangChu");
        verify(request, never()).getSession();
    }

    /*
        ============================================================
        DT-02 - UC-01 Alternative Flow [1.3.0 - 1.3.4]
        ============================================================

        Mô tả trong document:
        - Nếu tài khoản không tồn tại hoặc mật khẩu không chính xác,
          UserDAO không tìm thấy tài khoản phù hợp.
        - LoginServlet không tạo Session.
        - Giao diện hiển thị thông báo:
          "Tài khoản hoặc mật khẩu không chính xác."

        Input/Điều kiện:
        - Username chắc chắn không tồn tại.
        - Password sai.

        Expected Result:
        - Không tạo Session đăng nhập.
        - Forward về login.jsp.
        - Có thông báo lỗi đăng nhập sai.


    */
    @Test
    void DT02_loginWrongAccount_shouldForwardLoginWithInvalidAccountMessage() throws Exception {
        TestableLoginServlet servlet = new TestableLoginServlet();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        String wrongUsername = "not_exist_junit_" + System.nanoTime();

        when(request.getParameter("txtUsername")).thenReturn(wrongUsername);
        when(request.getParameter("txtPassword")).thenReturn("wrong_password");
        when(request.getRequestDispatcher("login.jsp")).thenReturn(dispatcher);

        servlet.callDoPost(request, response);

        verify(request).setAttribute(
                "thongBaoLoi",
                "Tài khoản hoặc mật khẩu không chính xác."
        );

        verify(dispatcher).forward(request, response);

        verify(response, never()).sendRedirect("TrangChu");
        verify(request, never()).getSession();
    }

    /*
        ============================================================
        DT-03 - UC-01 Main Flow [1.1.3 - 1.1.9]
        ============================================================

        Mô tả trong document:
        [1.1.3] login.jsp gửi username/password đến LoginServlet bằng POST.
        [1.1.4] LoginServlet tiếp nhận dữ liệu và gọi UserDAO.
        [1.1.5] UserDAO truy vấn bảng users trong MySQL.
        [1.1.6] Database trả về thông tin user nếu hợp lệ.
        [1.1.7] LoginServlet tạo Session và lưu username, role, userId.
        [1.1.8] Hệ thống điều hướng đến TrangChuServlet.
        [1.1.9] Người dùng truy cập hệ thống thành công.

        Input/Điều kiện:
        - Tạo user test trong bảng users.
        - Đăng nhập bằng username/password vừa tạo.

        Expected Result:
        - Session có userDaDangNhap.
        - Session có role.
        - Session có userId.
        - Redirect đến TrangChu.


    */
    @Test
    void DT03_loginValidAccount_shouldCreateSessionAndRedirectTrangChu() throws Exception {
        UserDAO userDAO = new UserDAO();
        String username = "junit_login_" + System.nanoTime();
        String password = "123";

        deleteUserIfExists(userDAO, username);

        User testUser = new User();
        testUser.setUsername(username);
        testUser.setPassword(password);
        testUser.setRole("Manager");

        boolean inserted = userDAO.addUser(testUser);
        assertTrue(inserted, "Không thêm được user test. Hãy kiểm tra MySQL và database coffee_shop_db.");

        User insertedUser = findExactUserByUsername(userDAO, username);
        assertNotNull(insertedUser, "User test phải tồn tại trước khi test đăng nhập.");

        try {
            TestableLoginServlet servlet = new TestableLoginServlet();

            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            HttpSession session = mock(HttpSession.class);

            when(request.getParameter("txtUsername")).thenReturn(username);
            when(request.getParameter("txtPassword")).thenReturn(password);
            when(request.getSession()).thenReturn(session);

            servlet.callDoPost(request, response);

            verify(session).setAttribute("userDaDangNhap", username);
            verify(session).setAttribute("role", "Manager");
            verify(session).setAttribute("userId", insertedUser.getUserId());

            verify(response).sendRedirect("TrangChu");
        } finally {
            deleteUserIfExists(userDAO, username);
        }
    }
}