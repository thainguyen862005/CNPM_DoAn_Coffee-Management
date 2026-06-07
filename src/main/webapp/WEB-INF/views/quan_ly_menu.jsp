<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<div class="container-fluid mt-4">
    <div class="row">

        <div class="col-md-4 mb-4">
            <div class="card shadow-sm">
                <div class="card-header ${not empty itemEdit ? 'bg-warning text-dark' : 'bg-success text-white'}">
                    <h5 class="mb-0">
                        <c:choose>
                            <c:when test="${not empty itemEdit}">
                                <i class="bi bi-pencil-square"></i> Cập Nhật Món  (#${itemEdit.itemId})
                            </c:when>
                            <c:otherwise>
                                <i class="bi bi-plus-circle"></i> Thêm Món Mới
                            </c:otherwise>
                        </c:choose>
                    </h5>
                </div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/QuanLyMenu" method="post">
                        <input type="hidden" name="action" value="${not empty itemEdit ? 'update' : 'add'}">
                        <input type="hidden" name="itemId" value="${itemEdit.itemId}">

                        <div class="mb-3">
                            <label class="form-label fw-bold">Tên món <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" name="itemName" value="${itemEdit.itemName}" required placeholder="Ví dụ: Cơm rang dưa bò">
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Giá bán (VNĐ) <span class="text-danger">*</span></label>
                            <input type="number" class="form-control" name="price" value="${itemEdit.price}" min="0" required placeholder="Ví dụ: 45000">
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Mô tả chi tiết</label>
                            <textarea class="form-control" name="description" rows="3" placeholder="Nguyên liệu, cách chế biến...">${itemEdit.description}</textarea>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Trạng thái kinh doanh</label>
                            <select class="form-select" name="status">
                                <option value="Còn bán" ${itemEdit.status == 'Còn bán' ? 'selected' : ''}>Còn bán</option>
                                <option value="Ngừng bán" ${itemEdit.status == 'Ngừng bán' ? 'selected' : ''}>Ngừng bán</option>
                            </select>
                        </div>

                        <div class="d-grid gap-2">
                            <button type="submit" class="btn ${not empty itemEdit ? 'btn-warning' : 'btn-success'} fw-bold">
                                ${not empty itemEdit ? 'Lưu thay đổi' : 'Thêm món '}
                            </button>
                            <c:if test="${not empty itemEdit}">
                                <a href="${pageContext.request.contextPath}/QuanLyMenu" class="btn btn-outline-secondary">Hủy bỏ</a>
                            </c:if>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <div class="col-md-8">
            <div class="card shadow-sm">
                <div class="card-header bg-primary text-white d-flex justify-content-between align-middle">
                    <h5 class="mb-0 align-self-center"><i class="bi bi-list-ul"></i> Danh Sách Thực Đơn Hệ Thống</h5>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover table-striped mb-0 align-middle">
                            <thead class="table-dark">
                            <tr>
                                <th style="width: 10%">Mã món</th>
                                <th style="width: 25%">Tên món</th>
                                <th style="width: 15%">Giá tiền</th>
                                <th style="width: 25%">Mô tả</th>
                                <th style="width: 13%">Trạng thái</th>
                                <th style="width: 12%" class="text-center">Thao tác</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${not empty menuList}">
                                    <c:forEach var="item" items="${menuList}">
                                        <tr>
                                            <td><strong>#${item.itemId}</strong></td>
                                            <td class="text-primary fw-bold">${item.itemName}</td>
                                            <td class="text-danger fw-bold">
                                                <fmt:formatNumber value="${item.price}" type="number" maxFractionDigits="0"/> đ
                                            </td>
                                            <td>
                                                <small class="text-muted">
                                                    <c:out value="${empty item.description ? 'Không có mô tả' : item.description}" />
                                                </small>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${item.status == 'Còn bán'}">
                                                        <span class="badge bg-light-success text-success border border-success px-2 py-1">Còn bán</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-light-danger text-danger border border-danger px-2 py-1">Ngừng bán</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="text-center">
                                                <div class="btn-group" role="group">
                                                    <a href="${pageContext.request.contextPath}/QuanLyMenu?action=edit&id=${item.itemId}" class="btn btn-sm btn-outline-warning" title="Sửa thông tin món">
                                                        Sửa
                                                    </a>
                                                    <a href="${pageContext.request.contextPath}/QuanLyMenu?action=delete&id=${item.itemId}" class="btn btn-sm btn-outline-danger" title="Ngừng bán món này"
                                                       onclick="return confirm('Bạn có chắc muốn ngừng bán món [${item.itemName}] không?');">
                                                        Xóa
                                                    </a>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="6" class="text-center text-muted py-4">Chưa có dữ liệu món trong cơ sở dữ liệu.</td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

    </div>
</div>