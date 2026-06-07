package test;

import Util.AuthUtil;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/*
    ============================================================
    DEVELOPMENT TESTING - UC-03: KIỂM TRA QUYỀN TRUY CẬP
    ============================================================

    - Các test case:
        DT-06: Chưa đăng nhập
        DT-07: Role Manager đủ quyền
        DT-08: Role Staff không đủ quyền truy cập chức năng Manager

    Thành phần source code được kiểm thử:
    - Util.AuthUtil
    - HttpSession
    - access_denied.jsp


*/
class AuthUtilTest {

    /*
        ============================================================
        DT-06 - UC-03 Alternative Flow [3.2.0 - 3.2.3]
        ============================================================

        Mô tả trong document:
        [3.2.0] Bắt đầu nhánh thay thế nếu Session không tồn tại.
        [3.2.1] Hệ thống xác định người dùng chưa đăng nhập.
        [3.2.2] Hệ thống điều hướng người dùng về login.jsp.
        [3.2.3] Người dùng cần đăng nhập lại.

        Input/Điều kiện:
        - request.getSession(false) trả về null.
        - contextPath = /QuanLyCafe.

        Expected Result:
        - AuthUtil.checkLogin(...) trả về false.
        - Redirect về /QuanLyCafe/login.jsp.
    */
    @Test
    void DT06_checkLoginNoSession_shouldRedirectToLogin() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getSession(false)).thenReturn(null);
        when(request.getContextPath()).thenReturn("/QuanLyCafe");

        boolean result = AuthUtil.checkLogin(request, response);

        assertFalse(result);
        verify(response).sendRedirect("/QuanLyCafe/login.jsp");
    }

    /*
        ============================================================
        DT-07 - UC-03 Main Flow [3.1.2 - 3.1.7]
        ============================================================

        Mô tả trong document:
        [3.1.2] Servlet kiểm tra Session hiện tại.
        [3.1.3] Hệ thống lấy role từ Session.
        [3.1.4] Servlet so sánh role với quyền được phép.
        [3.1.5] Nếu role hợp lệ, cho phép request tiếp tục xử lý.
        [3.1.7] Người dùng truy cập chức năng thành công.

        Input/Điều kiện:
        - Session tồn tại.
        - userDaDangNhap = admin.
        - role hiện tại = Manager.
        - requiredRole = Manager.

        Expected Result:
        - AuthUtil.checkRole(...) trả về true.
        - Không redirect về login.jsp.
        - Không forward đến access_denied.jsp.
    */
    @Test
    void DT07_checkRoleManagerAccessManagerFunction_shouldAllowRequest() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userDaDangNhap")).thenReturn("admin");
        when(session.getAttribute("role")).thenReturn("Manager");

        boolean result = AuthUtil.checkRole(request, response, "Manager");

        assertTrue(result);

        verify(response, never()).sendRedirect(anyString());
        verify(request, never()).getRequestDispatcher("/WEB-INF/views/access_denied.jsp");
    }

    /*
        ============================================================
        DT-08 - UC-03 Alternative Flow [3.3.0 - 3.3.3]
        ============================================================

        Mô tả trong document:
        [3.3.0] Bắt đầu nhánh role không đủ quyền.
        [3.3.1] Servlet từ chối xử lý request.
        [3.3.2] Hệ thống hiển thị access_denied.jsp.
        [3.3.3] Người dùng không được truy cập chức năng yêu cầu quyền.

        Input/Điều kiện:
        - Session tồn tại.
        - userDaDangNhap = staff1.
        - role hiện tại = Staff.
        - requiredRole = Manager.

        Expected Result:
        - AuthUtil.checkRole(...) trả về false.
        - Set request attribute requiredRole = Manager.
        - Set request attribute currentRole = Staff.
        - Forward đến /WEB-INF/views/access_denied.jsp.
    */
    @Test
    void DT08_checkRoleStaffAccessManagerFunction_shouldForwardAccessDenied() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userDaDangNhap")).thenReturn("staff1");
        when(session.getAttribute("role")).thenReturn("Staff");

        when(request.getRequestDispatcher("/WEB-INF/views/access_denied.jsp"))
                .thenReturn(dispatcher);

        boolean result = AuthUtil.checkRole(request, response, "Manager");

        assertFalse(result);

        verify(request).setAttribute("requiredRole", "Manager");
        verify(request).setAttribute("currentRole", "Staff");
        verify(dispatcher).forward(request, response);
    }
}