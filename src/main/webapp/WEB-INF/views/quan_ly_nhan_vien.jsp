<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<link rel="stylesheet"
      href="${pageContext.request.contextPath}/assets/css/employee-management.css">

<div class="content-wrapper">

    <div class="page-title">
        <i class="fa fa-users"></i> Quản lý nhân viên
    </div>

    <%-- ========== FORM ADD NV ============== --%>
    <div class="card-custom ">
        <div class="card-header-custom">
            <i class="fa fa-user-plus"></i> Thêm nhân viên mới
        </div>

        <div class="card-body-custom">

            <%--
                UC-04 - Quản lý nhân viên
                Alternative Flow [4.2.1]: Giao diện hiển thị form nhập thông tin nhân viên mới.
                Alternative Flow [4.2.2]: Quản lý nhập thông tin nhân viên và nhấn nút Lưu.
                Alternative Flow [4.2.3]: Giao diện gửi dữ liệu đến QuanLyNhanVienServlet với action add.
            --%>

            <form action="QuanLyNhanVien" method="post">
                <input type="hidden" name="action" value="add">

                <div class="form-row">
                    <div class="form-group col-md-4">
                        <label>Tên đăng nhập</label>
                        <input type="text"
                               class="form-control"
                               name="username"
                               placeholder="Nhập username"
                               required>
                    </div>

                    <div class="form-group col-md-4">
                        <label>Mật khẩu</label>
                        <input type="password"
                               class="form-control"
                               name="password"
                               placeholder="Nhập password"
                               required>
                    </div>

                    <div class="form-group col-md-3">
                        <label>Vai trò</label>
                        <select class="form-control" name="role" required>
                            <option value="">-- Chọn vai trò --</option>
                            <option value="Manager">Quản lý</option>
                            <option value="Staff">Nhân viên phục vụ</option>
                            <option value="Cashier">Thu ngân</option>
                        </select>
                    </div>

                    <div class="form-group col-md-1 d-flex align-items-end">
                        <button type="submit" class="btn btn-blue btn-block">
                            <i class="fa fa-plus"></i>
                        </button>
                    </div>
                </div>
            </form>

        </div>
    </div>

    <%-- ======== SEARCH & FILTER NV ============== --%>
    <div class="card-custom employee-filter-card">
        <div class="card-header-custom">
            <i class="fa fa-search"></i> Tìm kiếm và lọc nhân viên
        </div>

        <div class="card-body-custom">

            <%--
                UC-04 - Quản lý nhân viên

                Alternative Flow [4.7.0]: Quản lý nhập từ khóa tìm kiếm theo tên đăng nhập.
                Alternative Flow [4.7.1]: Giao diện quan_ly_nhan_vien.jsp gửi keyword đến QuanLyNhanVienServlet bằng phương thức GET.
                Alternative Flow [4.8.0]: Quản lý chọn vai trò cần lọc.
                Alternative Flow [4.8.1]: Giao diện quan_ly_nhan_vien.jsp gửi role đến QuanLyNhanVienServlet bằng phương thức GET.

            --%>

            <form action="QuanLyNhanVien" method="get">
                <div class="form-row">

                    <div class="form-group col-md-6">
                        <label>Tìm theo tên đăng nhập</label>
                        <input type="text"
                               class="form-control"
                               name="keyword"
                               value="${keyword}"
                               placeholder="Nhập username cần tìm">
                    </div>

                    <div class="form-group col-md-4">
                        <label>Lọc theo vai trò</label>
                        <select class="form-control" name="role">
                            <option value="" ${empty selectedRole ? 'selected' : ''}>
                                -- Tất cả vai trò --
                            </option>

                            <option value="Manager" ${selectedRole == 'Manager' ? 'selected' : ''}>
                                Quản lý
                            </option>

                            <option value="Staff" ${selectedRole == 'Staff' ? 'selected' : ''}>
                                Nhân viên phục vụ
                            </option>

                            <option value="Cashier" ${selectedRole == 'Cashier' ? 'selected' : ''}>
                                Thu ngân
                            </option>
                        </select>
                    </div>

                    <div class="form-group col-md-2 d-flex align-items-end">
                        <button type="submit" class="btn btn-blue btn-block mr-1" title="Tìm kiếm">
                            <i class="fa fa-search"></i>
                        </button>

                        <a href="QuanLyNhanVien" class="btn btn-secondary btn-block ml-1" title="Làm mới">
                            <i class="fa fa-refresh"></i>
                        </a>
                    </div>

                </div>
            </form>

        </div>
    </div>

    <%-- =========== LIST NV ==================== --%>
    <div class="card-custom employee-list-card">
        <div class="card-header-custom">
            <i class="fa fa-list"></i> Danh sách nhân viên
        </div>

        <div class="card-body-custom">

            <%--
                UC-04 - Quản lý nhân viên
                    Main Flow [4.1.6]: Servlet gửi danh sách nhân viên sang trang quan_ly_nhan_vien.jsp.
                    Main Flow [4.1.7]: Giao diện hiển thị danh sách nhân viên cho Quản lý.

                Servlet cần truyền:
                request.setAttribute("danhSachNhanVien", list);
            --%>

            <div class="employee-table-scroll">
                <table class="table table-bordered table-hover">
                    <thead>
                    <tr>
                        <th style="width: 80px;">ID</th>
                        <th>Tên đăng nhập</th>
                        <th style="width: 170px;">Vai trò</th>
                        <th style="width: 260px;">Hành động</th>
                    </tr>
                    </thead>

                    <tbody>
                    <c:choose>

                        <%--
                            UC-04 - Quản lý nhân viên
                            Alternative Flow [4.9.2]: Servlet gửi danh sách rỗng sang quan_ly_nhan_vien.jsp.
                            Alternative Flow [4.9.3]: Giao diện hiển thị thông báo không tìm thấy nhân viên phù hợp.
                        --%>
                        <c:when test="${empty danhSachNhanVien}">
                            <tr>
                                <td colspan="4" class="text-center text-muted">
                                    Không tìm thấy nhân viên phù hợp.
                                </td>
                            </tr>
                        </c:when>

                        <c:otherwise>
                            <c:forEach var="nv" items="${danhSachNhanVien}">
                                <tr>
                                    <td class="text-center">${nv.userId}</td>

                                    <td>
                                        <i class="fa fa-user-circle"></i>
                                            ${nv.username}
                                    </td>

                                    <td class="text-center">
                                        <c:choose>
                                            <c:when test="${nv.role == 'Manager'}">
                                                <span class="badge-role badge-manager">Quản lý</span>
                                            </c:when>

                                            <c:when test="${nv.role == 'Staff'}">
                                                <span class="badge-role badge-staff">Nhân viên phục vụ</span>
                                            </c:when>

                                            <c:when test="${nv.role == 'Cashier'}">
                                                <span class="badge-role badge-cashier">Thu ngân</span>
                                            </c:when>

                                            <c:otherwise>
                                                <span class="badge badge-secondary">${nv.role}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td class="text-center">

                                            <%--
                                                UC-04 - Quản lý nhân viên
                                                Alternative Flow [4.3.0]: Quản lý chọn nhân viên cần sửa.
                                                Alternative Flow [4.3.1]: Giao diện hiển thị thông tin hiện tại của nhân viên.
                                            --%>
                                        <button class="btn btn-yellow btn-sm"
                                                data-toggle="modal"
                                                data-target="#editModal${nv.userId}">
                                            <i class="fa fa-pencil"></i> Sửa
                                        </button>

                                            <%--
                                                UC-04 - Quản lý nhân viên
                                                Alternative Flow [4.4.0]: Quản lý chọn nhân viên cần xóa.
                                                Alternative Flow [4.4.1]: Hệ thống yêu cầu Quản lý xác nhận thao tác xóa.
                                                Alternative Flow [4.4.2]: Quản lý xác nhận xóa nhân viên.
                                                Alternative Flow [4.4.3]: Giao diện gửi yêu cầu đến QuanLyNhanVienServlet với action delete.
                                            --%>
                                        <form action="QuanLyNhanVien"
                                              method="post"
                                              style="display:inline-block;"
                                              onsubmit="return confirm('Bạn có chắc muốn xóa nhân viên này không?');">

                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="userId" value="${nv.userId}">

                                            <button type="submit" class="btn btn-red btn-sm">
                                                <i class="fa fa-trash"></i> Xóa
                                            </button>
                                        </form>
                                    </td>
                                </tr>

                                <%-- =============== MODAL SỬA NV ==================== --%>
                                <div class="modal fade" id="editModal${nv.userId}" tabindex="-1" role="dialog">
                                    <div class="modal-dialog" role="document">
                                        <div class="modal-content">

                                            <div class="modal-header bg-primary text-white">
                                                <h5 class="modal-title">
                                                    <i class="fa fa-pencil"></i> Cập nhật nhân viên
                                                </h5>

                                                <button type="button" class="close text-white" data-dismiss="modal">
                                                    <span>&times;</span>
                                                </button>
                                            </div>

                                            <form action="QuanLyNhanVien" method="post">
                                                <div class="modal-body">

                                                        <%--
                                                            UC-04 - Quản lý nhân viên
                                                            Alternative Flow [4.3.2]: Quản lý chỉnh sửa thông tin và nhấn nút Cập nhật.
                                                            Alternative Flow [4.3.3]: Giao diện gửi dữ liệu đến QuanLyNhanVienServlet với action update.
                                                        --%>
                                                    <input type="hidden" name="action" value="update">
                                                    <input type="hidden" name="userId" value="${nv.userId}">

                                                    <div class="form-group">
                                                        <label>Tên đăng nhập</label>
                                                        <input type="text"
                                                               class="form-control"
                                                               name="username"
                                                               value="${nv.username}"
                                                               required>
                                                    </div>

                                                    <div class="form-group">
                                                        <label>Mật khẩu mới</label>
                                                        <input type="password"
                                                               class="form-control"
                                                               name="password"
                                                               placeholder="Để trống nếu không đổi mật khẩu">
                                                    </div>

                                                    <div class="form-group">
                                                        <label>Vai trò</label>
                                                        <select class="form-control" name="role" required>
                                                            <option value="Manager" ${nv.role == 'Manager' ? 'selected' : ''}>
                                                                Quản lý
                                                            </option>

                                                            <option value="Staff" ${nv.role == 'Staff' ? 'selected' : ''}>
                                                                Nhân viên phục vụ
                                                            </option>

                                                            <option value="Cashier" ${nv.role == 'Cashier' ? 'selected' : ''}>
                                                                Thu ngân
                                                            </option>
                                                        </select>
                                                    </div>

                                                </div>

                                                <div class="modal-footer">
                                                    <button type="button" class="btn btn-secondary" data-dismiss="modal">
                                                        Hủy
                                                    </button>

                                                    <button type="submit" class="btn btn-blue">
                                                        <i class="fa fa-save"></i> Cập nhật
                                                    </button>
                                                </div>
                                            </form>

                                        </div>
                                    </div>
                                </div>

                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>

        </div>
    </div>

</div>