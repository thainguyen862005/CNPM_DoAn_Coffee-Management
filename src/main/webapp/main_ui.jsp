<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Hệ Thống Quản Lý - MainUI</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <style>
        .sidebar { background-color: #343a40; height: 100vh; padding: 0; }
        .sidebar a { color: #cfd8dc; display: block; padding: 15px; border-bottom: 1px solid #454d55; text-decoration: none; }
        .sidebar a:hover { background-color: #495057; color: white; }
        .sidebar i { width: 25px; }
        .top-navbar { background-color: #007bff; color: white; padding: 10px 20px; }
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
            <a href="TrangChuServlet" style="background-color: #007bff; color: white;">
                <i class="fa fa-th-large"></i> Sơ Đồ Bàn
            </a>
            <a href="#"><i class="fa fa-coffee"></i> Quản Lý Thực Đơn</a>
            <a href="#"><i class="fa fa-file-text-o"></i> Hóa Đơn & Đặt Món</a>

            <c:if test="${sessionScope.role == 'Manager'}">
                <a href="#"><i class="fa fa-users"></i> Quản Lý Nhân Viên</a>
                <a href="#"><i class="fa fa-bar-chart"></i> Báo Cáo Doanh Thu</a>
            </c:if>
        </div>

        <div class="col-md-10 mt-4">
            <h4><i class="fa fa-list"></i> Tình trạng bàn hiện tại</h4>
            <hr>
            <div class="row">
                <c:forEach items="${danhSachBan}" var="ban">
                    <div class="col-md-3 mb-4">
                        <div class="card text-center shadow-sm">
                            <div class="card-body">
                                <h1 style="color: #6c757d;"><i class="fa fa-cube"></i></h1>
                                <h5>${ban.tableName}</h5>
                                <p>Khu vực: ${ban.area}</p>

                                <c:if test="${ban.status == 'Trống'}">
                                    <span class="badge badge-success"><i class="fa fa-check"></i> Trống</span>
                                    <button class="btn btn-outline-primary btn-sm mt-2 btn-block">Gọi Món</button>
                                </c:if>

                                <c:if test="${ban.status != 'Trống'}">
                                    <span class="badge badge-danger"><i class="fa fa-times"></i> Đang phục vụ</span>
                                    <button class="btn btn-outline-warning btn-sm mt-2 btn-block">Xem Chi Tiết</button>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>
</div>
</body>
</html>