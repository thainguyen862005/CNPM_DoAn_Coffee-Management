package test;

import Controller.LogoutServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

/*
    ============================================================
    DEVELOPMENT TESTING - UC-02: ĐĂNG XUẤT
    ============================================================

    - Các test case:
        DT-04: Đăng xuất khi Session tồn tại
        DT-05: Đăng xuất khi Session không tồn tại hoặc đã hết hạn

    Thành phần source code được kiểm thử:
    - Controller.LogoutServlet
    - HttpSession


*/
class LogoutServletTest {



    private static class TestableLogoutServlet extends LogoutServlet {
        public void callDoGet(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
            super.doGet(request, response);
        }
    }

    /*
        ============================================================
        DT-04 - UC-02 Main Flow [2.1.2 - 2.1.7]
        ============================================================

        Mô tả trong document:
        [2.1.2] Giao diện gửi yêu cầu đăng xuất đến LogoutServlet.
        [2.1.3] LogoutServlet lấy Session hiện tại.
        [2.1.4] LogoutServlet hủy Session bằng invalidate().
        [2.1.6] Hệ thống điều hướng người dùng về login.jsp.
        [2.1.7] Người dùng đăng xuất thành công.

        Input/Điều kiện:
        - Người dùng đã đăng nhập.
        - request.getSession(false) trả về Session hợp lệ.

        Expected Result:
        - Gọi session.invalidate().
        - Redirect về login.jsp.
    */
    @Test
    void DT04_logoutExistingSession_shouldInvalidateSessionAndRedirectLogin() throws Exception {
        TestableLogoutServlet servlet = new TestableLogoutServlet();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getSession(false)).thenReturn(session);

        servlet.callDoGet(request, response);

        verify(session).invalidate();
        verify(response).sendRedirect("login.jsp");
    }

    /*
        ============================================================
        DT-05 - UC-02 Alternative Flow [2.2.0 - 2.2.4]
        ============================================================

        Mô tả trong document:
        - Nếu Session không tồn tại hoặc đã hết hạn,
          LogoutServlet không cần invalidate().
        - Hệ thống vẫn điều hướng người dùng về login.jsp.
        - Người dùng ở trạng thái chưa đăng nhập.

        Input/Điều kiện:
        - request.getSession(false) trả về null.

        Expected Result:
        - Không gọi invalidate().
        - Không phát sinh lỗi.
        - Redirect về login.jsp.
    */
    @Test
    void DT05_logoutWithoutSession_shouldRedirectLoginWithoutError() throws Exception {
        TestableLogoutServlet servlet = new TestableLogoutServlet();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getSession(false)).thenReturn(null);

        servlet.callDoGet(request, response);

        verify(response).sendRedirect("login.jsp");
    }
}