<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Không có quyền truy cập</title>

    <link rel="stylesheet"
          href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/auth-extra.css">
</head>

<body>

<div class="denied-container">
    <div class="card">
        <div class="card-header">
            <i class="fa fa-ban"></i> Truy cập bị từ chối
        </div>

        <div class="card-body">

            <%--
                UC-03 - Kiểm tra quyền truy cập
                Alternative Flow [3.3.2]: Hệ thống hiển thị trang access_denied.jsp
                hoặc thông báo “Bạn không có quyền truy cập chức năng này”.
            --%>
            <div class="denied-icon">
                <i class="fa fa-lock"></i>
            </div>

            <h4>Bạn không có quyền truy cập</h4>

            <p class="text-muted">
                Tài khoản hiện tại không được phép sử dụng chức năng này.
            </p>

            <a href="TrangChu" class="btn btn-primary">
                <i class="fa fa-home"></i> Về trang chủ
            </a>
        </div>
    </div>
</div>

</body>
</html>