<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đăng ký tài khoản - Hệ thống quản lý quán cafe</title>

    <link rel="stylesheet"
          href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/auth-extra.css">
</head>

<body>

<div class="register-container">
    <div class="card">
        <div class="card-header">
            <i class="fa fa-user-plus"></i> Đăng ký tài khoản nhân viên
        </div>

        <div class="card-body">

            <%--
                UC-04 - Quản lý nhân viên
                Main Flow [4.2.1]: Quản lý nhập thông tin nhân viên mới
                Main Flow [4.2.2]: Giao diện gửi thông tin nhân viên đến QuanLyNhanVienServlet

                note:
                Trang register.jsp không phải đăng ký công khai.
                Trang này dùng để Quản lý tạo tài khoản nhân viên mới.
            --%>

            <div class="note-box">
                <strong>Lưu ý:</strong>
                Trang này dùng để tạo tài khoản nhân viên mới,
                thuộc chức năng <b>UC-04 - Quản lý nhân viên</b>.
            </div>

            <form action="QuanLyNhanVien" method="post">
                <input type="hidden" name="action" value="add">

                <div class="form-group">
                    <label for="username">
                        <i class="fa fa-user"></i> Tên đăng nhập
                    </label>
                    <input type="text"
                           class="form-control"
                           id="username"
                           name="username"
                           placeholder="Nhập tên đăng nhập"
                           required>
                </div>

                <div class="form-group">
                    <label for="password">
                        <i class="fa fa-lock"></i> Mật khẩu
                    </label>
                    <input type="password"
                           class="form-control"
                           id="password"
                           name="password"
                           placeholder="Nhập mật khẩu"
                           required>
                </div>

                <div class="form-group">
                    <label for="role">
                        <i class="fa fa-id-badge"></i> Vai trò
                    </label>
                    <select class="form-control" id="role" name="role" required>
                        <option value="">-- Chọn vai trò --</option>
                        <option value="Manager">Quản lý</option>
                        <option value="Staff">Nhân viên phục vụ</option>
                        <option value="Cashier">Thu ngân</option>
                    </select>
                </div>

                <div class="text-right">
                    <a href="QuanLyNhanVien" class="btn btn-secondary">
                        <i class="fa fa-arrow-left"></i> Quay lại
                    </a>

                    <button type="submit" class="btn btn-primary">
                        <i class="fa fa-save"></i> Lưu tài khoản
                    </button>
                </div>
            </form>

        </div>
    </div>
</div>

</body>
</html>