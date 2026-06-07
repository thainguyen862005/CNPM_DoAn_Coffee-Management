<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Hệ Thống Quản Lý Quán Cafe</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main-layout.css">
</head>
<body>
<div class="top-navbar d-flex justify-content-between align-items-center">
    <h4><i class="fa fa-desktop"></i> HỆ THỐNG QUẢN LÝ QUÁN CAFE</h4>
    <div>
        <span><i class="fa fa-user-circle"></i> Xin chào: ${sessionScope.userDaDangNhap} </span>

        <%--
            UC-02 - Đăng xuất
            Main Flow [2.1.1]: Người dùng chọn chức năng Đăng xuất trên giao diện main_ui.jsp.
            Main Flow [2.1.2]: Giao diện gửi yêu cầu đăng xuất đến LogoutServlet.
        --%>
        <a href="Logout" class="btn btn-danger btn-sm ml-3">
            <i class="fa fa-sign-out"></i> Đăng xuất
        </a>
    </div>
</div>

<div class="app-body">

    <aside class="sidebar">
        <a href="TrangChu" class="${active_tab == 'sodo' ? 'active-tab' : ''}">
            <i class="fa fa-th-large"></i> Sơ Đồ Bàn
        </a>

            <%--
                UC-03 - KIỂM TRA QUYỀN TRUY CẬP Ở TẦNG GIAO DIỆN

                Main Flow [3.1.3]: Hệ thống lấy role của người dùng từ Session.
                Main Flow [3.1.4]:Hệ thống dựa vào role để hiển thị các chức năng phù hợp.

                note:
                - chỉ phân quyền hiển thị trên giao diện.
            --%>

            <%--
                Chức năng Quản lý thực đơn: Chỉ tài khoản có role Manager được hiển thị.
            --%>
            <c:if test="${sessionScope.role == 'Manager'}">
                <a href="QuanLyMenu"
                   class="${active_tab == 'menu' ? 'active-tab' : ''}">
                    <i class="fa fa-coffee"></i> Quản Lý Thực Đơn
                </a>
            </c:if>

            <%--
                Chức năng Hóa đơn và Đặt món:
                - Manager: được xem và theo dõi hoạt động hóa đơn.
                - Staff: được tạo và cập nhật order.
                - Cashier: được xem và thanh toán hóa đơn.
                Quyền thực hiện từng action cụ thể vẫn được kiểm tra trong HoaDonServlet.
            --%>
            <c:if test="${sessionScope.role == 'Manager'
              || sessionScope.role == 'Staff'
              || sessionScope.role == 'Cashier'}">

                <a href="HoaDon"
                   class="${active_tab == 'hoadon' ? 'active-tab' : ''}">
                    <i class="fa fa-file-text-o"></i> Hóa Đơn & Đặt Món
                </a>
            </c:if>

            <%--
                UC-04 - Quản lý nhân viên:
                Preconditions: Người dùng đã đăng nhập và có role Manager -> Chỉ Manager được hiển thị chức năng này.
            --%>
            <c:if test="${sessionScope.role == 'Manager'}">
                <a href="QuanLyNhanVien"
                   class="${active_tab == 'nhanvien' ? 'active-tab' : ''}">
                    <i class="fa fa-users"></i> Quản Lý Nhân Viên
                </a>
            </c:if>

            <%--
                Chức năng Báo cáo doanh thu: Chỉ tài khoản có role Manager được hiển thị.
            --%>
            <c:if test="${sessionScope.role == 'Manager'}">
                <a href="BaoCao"
                   class="${active_tab == 'baocao' ? 'active-tab' : ''}">
                    <i class="fa fa-bar-chart"></i> Báo Cáo Doanh Thu
                </a>
            </c:if>
    </aside>
    <main class="main-content">
        <div class="main-content-inner">
            <jsp:include page="/WEB-INF/views/${page_content}" />
        </div>
    </main>

</div>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
<script>
    // 1. Tự động tính tiền thối khi gõ số
    function tinhTienThoi(tongTien) {
        let khachDua = document.getElementById('tienKhachDua').value;
        let tienThoi = khachDua - tongTien;
        if(tienThoi < 0) tienThoi = 0;
        document.getElementById('tienThoiLai').value = tienThoi.toLocaleString('vi-VN') + " đ";
    }

    // 2. Dùng AJAX để thanh toán không chuyển trang
    function thucHienThanhToan(event, tongTien) {
        // Chặn không cho form load sang trang khác
        event.preventDefault();

        let khachDua = document.getElementById('tienKhachDua').value;
        if(khachDua === "" || parseInt(khachDua) < tongTien) {
            alert("Khách đưa chưa đủ tiền kìa!");
            return false;
        }

        if(confirm('Xác nhận đã thu đủ ' + tongTien.toLocaleString('vi-VN') + ' đ?')) {
            let form = event.target;
            let formData = new URLSearchParams(new FormData(form));

            // Gửi ngầm dữ liệu xuống HoaDonServlet
            fetch('HoaDon', {
                method: 'POST',
                body: formData
            }).then(() => {
                let container = document.getElementById('bill-content');
                if (!container) container = document.getElementById('modal-body-content');

                if(container) {
                    container.innerHTML =
                        '<div class="alert alert-success text-center mt-3">' +
                        '<i class="fa fa-check-circle" style="font-size: 50px;"></i>' +
                        '<h4 class="mt-3">Thanh toán thành công!</h4>' +
                        '<p>Bàn đã được dọn sạch sẽ.</p>' +
                        '<button type="button" class="btn btn-secondary mt-3" onclick="location.reload()">Đóng & Làm mới</button>' +
                        '</div>';
                }
            }).catch(error => alert("Lỗi kết nối tới máy chủ!"));
        }
        return false;
    }
</script>
</body>
</html>