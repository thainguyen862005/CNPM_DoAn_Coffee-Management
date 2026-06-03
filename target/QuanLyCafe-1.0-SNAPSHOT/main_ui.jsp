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
        <a href="login.jsp" class="btn btn-danger btn-sm ml-3"><i class="fa fa-sign-out"></i> Đăng xuất</a>
    </div>
</div>

<div class="container-fluid">
    <div class="row">
        <div class="col-md-2 sidebar">

            <a href="TrangChuServlet" class="${active_tab == 'sodo' ? 'active-tab' : ''}">
                <i class="fa fa-th-large"></i> Sơ Đồ Bàn
            </a>

            <a href="QuanLyMenuServlet" class="${active_tab == 'menu' ? 'active-tab' : ''}">
                <i class="fa fa-coffee"></i> Quản Lý Thực Đơn
            </a>

            <a href="HoaDonServlet" class="${active_tab == 'hoadon' ? 'active-tab' : ''}">
                <i class="fa fa-file-text-o"></i> Hóa Đơn & Đặt Món
            </a>

            <c:if test="${sessionScope.role == 'Manager'}">
                <a href="QuanLyNhanVienServlet" class="${active_tab == 'nhanvien' ? 'active-tab' : ''}">
                    <i class="fa fa-users"></i> Quản Lý Nhân Viên
                </a>
                <a href="BaoCaoServlet" class="${active_tab == 'baocao' ? 'active-tab' : ''}">
                    <i class="fa fa-bar-chart"></i> Báo Cáo Doanh Thu
                </a>
            </c:if>
        </div>

        <div class="col-md-10 mt-4">
            <jsp:include page="${page_content}" />
        </div>
    </div>
</div>
</body>
</html>