<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Đăng nhập hệ thống - Đồ án NLU</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <style>
        body { background-color: #f4f6f9; margin-top: 100px; }
        .login-card { max-width: 400px; margin: 0 auto; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }
    </style>
</head>
<body>
<div class="container">
    <div class="card login-card">
        <div class="card-header text-center bg-primary text-white">
            <h4><i class="fa fa-coffee"></i> Quản Lý Quán Cafe</h4>
        </div>
        <div class="card-body">
            <p style="color:red; text-align:center;">${thongBaoLoi}</p>

            <form action="LoginServlet" method="POST">
                <div class="form-group">
                    <label><i class="fa fa-user"></i> Tên đăng nhập</label>
                    <input type="text" name="txtUsername" class="form-control" required>
                </div>
                <div class="form-group">
                    <label><i class="fa fa-lock"></i> Mật khẩu</label>
                    <input type="password" name="txtPassword" class="form-control" required>
                </div>
                <button type="submit" class="btn btn-primary btn-block">
                    <i class="fa fa-sign-in"></i> Đăng Nhập
                </button>
            </form>
        </div>
    </div>
</div>
</body>
</html>