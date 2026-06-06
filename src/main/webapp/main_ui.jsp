<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Hệ Thống Quản Lý Quán Cafe</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <style>
        .sidebar { background-color: #343a40; height: 100vh; padding: 0; }
        .sidebar a { color: #cfd8dc; display: block; padding: 15px; border-bottom: 1px solid #454d55; text-decoration: none; }
        .sidebar a:hover { background-color: #495057; color: white; }
        .sidebar i { width: 25px; }
        .top-navbar { background-color: #007bff; color: white; padding: 10px 20px; }
        .sidebar a.active-tab {
            background-color: cornflowerblue;
            color: white;
            font-weight: bold;
            border-left: 5px solid #ffc107; /* Vạch vàng nổi bật */
        }
    </style>
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

<div class="container-fluid">
    <div class="row">
        <div class="col-md-2 sidebar">

            <%--
                UC-03 - Kiểm tra quyền truy cập
                Main Flow [3.1.3]: Hệ thống lấy thông tin role của người dùng từ Session.
                Main Flow [3.1.4]: Hệ thống sử dụng role để hiển thị chức năng phù hợp.

                *note: phân quyền ở tần giao diện
            --%>
            <a href="TrangChu" class="${active_tab == 'sodo' ? 'active-tab' : ''}">
                <i class="fa fa-th-large"></i> Sơ Đồ Bàn
            </a>

            <c:if test="${sessionScope.role == 'Manager'}">
                <a href="QuanLyMenu" class="${active_tab == 'menu' ? 'active-tab' : ''}">
                    <i class="fa fa-coffee"></i> Quản Lý Thực Đơn
                </a>

                <a href="HoaDon" class="${active_tab == 'hoadon' ? 'active-tab' : ''}">
                    <i class="fa fa-file-text-o"></i> Hóa Đơn & Đặt Món
                </a>

                <a href="QuanLyNhanVien" class="${active_tab == 'nhanvien' ? 'active-tab' : ''}">
                    <i class="fa fa-users"></i> Quản Lý Nhân Viên
                </a>

                <a href="BaoCao" class="${active_tab == 'baocao' ? 'active-tab' : ''}">
                    <i class="fa fa-bar-chart"></i> Báo Cáo Doanh Thu
                </a>
            </c:if>

            <c:if test="${sessionScope.role == 'Staff'}">
                <a href="QuanLyMenu" class="${active_tab == 'menu' ? 'active-tab' : ''}">
                    <i class="fa fa-coffee"></i> Quản Lý Thực Đơn
                </a>

                <a href="HoaDon" class="${active_tab == 'hoadon' ? 'active-tab' : ''}">
                    <i class="fa fa-file-text-o"></i> Hóa Đơn & Đặt Món
                </a>
            </c:if>

            <c:if test="${sessionScope.role == 'Cashier'}">
                <a href="HoaDon" class="${active_tab == 'hoadon' ? 'active-tab' : ''}">
                    <i class="fa fa-file-text-o"></i> Hóa Đơn & Đặt Món
                </a>
            </c:if>
        </div>

        <div class="col-md-10 mt-4">
            <jsp:include page="${page_content}" />
        </div>
    </div>
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