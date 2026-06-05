<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="DAO.HoaDonDAO" %>
<%@ page import="DAO.MenuItemDAO" %>
<%@ page import="Model.Order" %>
<%@ page import="Model.MenuItem" %>
<%@ page import="java.util.List" %>
<%
    int orderId = Integer.parseInt(request.getParameter("orderId"));
    HoaDonDAO hoaDonDAO = new HoaDonDAO();
    MenuItemDAO menuItemDAO = new MenuItemDAO();

    Order order = hoaDonDAO.getOrderById(orderId);
    List<MenuItem> menuItems = menuItemDAO.getAllMenuItems();

    request.setAttribute("order", order);
    request.setAttribute("menuItems", menuItems);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chi Tiết Hóa Đơn - Quản Lý Quán Cafe</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background-color: #f8f9fa; font-family: Arial, sans-serif; }
        .card { border: none; border-radius: 10px; }
        .table th { background-color: #343a40; color: white; }
    </style>
</head>
<body>
<div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Chi Tiết Hóa Đơn <span class="text-primary">#<%= orderId %></span></h2>
        <a href="hoa_don.jsp" class="btn btn-outline-secondary">&larr; Quay lại danh sách</a>
    </div>

    <div class="card shadow-sm mb-4">
        <div class="card-body d-flex gap-4">
            <p class="mb-0 fs-5"><strong>Bàn:</strong> <span class="text-success">${order.table != null ? order.table.tableName : 'Khách mang đi'}</span></p>
            <p class="mb-0 fs-5"><strong>Loại hình:</strong> <span class="badge bg-secondary">${order.orderType == 'TAKEAWAY' ? 'Mang đi' : 'Dùng tại bàn'}</span></p>
        </div>
    </div>

    <c:if test="${not empty requestScope.errorMessage}">
        <div class="alert alert-danger shadow-sm">${requestScope.errorMessage}</div>
    </c:if>

    <div class="card shadow-sm mb-4">
        <div class="card-header bg-primary text-white fw-bold">
            + Thêm món vào hóa đơn
        </div>
        <div class="card-body bg-light">
            <form action="HoaDonServlet" method="post" class="row g-3 align-items-end">
                <input type="hidden" name="action" value="addMenuItem">
                <input type="hidden" name="orderId" value="<%= orderId %>">

                <div class="col-md-4">
                    <label class="form-label fw-bold">Chọn món:</label>
                    <select name="itemId" class="form-select" required>
                        <option value="" disabled selected>-- Chọn đồ uống --</option>
                        <c:forEach items="${menuItems}" var="menu">
                            <option value="${menu.itemId}">${menu.itemName} - ${menu.price} VNĐ</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="col-md-2">
                    <label class="form-label fw-bold">Số lượng:</label>
                    <input type="number" name="quantity" class="form-control text-center" value="1" min="1" required>
                </div>

                <div class="col-md-4">
                    <label class="form-label fw-bold">Ghi chú (Tùy chọn):</label>
                    <input type="text" name="note" class="form-control" placeholder="Ví dụ: Ít đá, nhiều sữa...">
                </div>

                <div class="col-md-2">
                    <button type="submit" class="btn btn-success w-100">Thêm món</button>
                </div>
            </form>
        </div>
    </div>

    <div class="card shadow-sm">
        <div class="card-header bg-dark text-white fw-bold">
            Danh sách món đã gọi
        </div>
        <div class="card-body p-0">
            <table class="table table-hover table-bordered text-center align-middle mb-0">
                <thead>
                <tr>
                    <th>Tên món</th>
                    <th>Số lượng</th>
                    <th>Đơn giá</th>
                    <th>Thành tiền</th>
                    <th>Ghi chú</th>
                    <th>Trạng thái</th>
                    <th>Thao tác</th>
                </tr>
                </thead>
                <tbody>
                <c:set var="totalAmount" value="0"/>
                <c:choose>
                    <c:when test="${not empty order.orderDetails}">
                        <c:forEach items="${order.orderDetails}" var="item">
                            <tr>
                                <td class="fw-bold">${item.menuItem.itemName}</td>
                                <td>${item.quantity}</td>
                                <td>${item.unitPrice}</td>
                                <td class="text-danger fw-bold">${item.subtotal}</td>
                                <td>${empty item.note ? '<span class="text-muted">-</span>' : item.note}</td>

                                <td>
                                    <c:choose>
                                        <c:when test="${item.status == 'PENDING'}"><span class="badge bg-warning text-dark">Chờ pha chế</span></c:when>
                                        <c:when test="${item.status == 'PREPARING'}"><span class="badge bg-primary">Đang làm</span></c:when>
                                        <c:when test="${item.status == 'SERVED'}"><span class="badge bg-success">Đã phục vụ</span></c:when>
                                        <c:otherwise><span class="badge bg-secondary">${item.status}</span></c:otherwise>
                                    </c:choose>
                                </td>

                                <td>
                                    <form action="HoaDonServlet" method="post" onsubmit="return confirm('Bạn có chắc chắn muốn hủy món này không?');" class="m-0">
                                        <input type="hidden" name="action" value="removeMenuItem">
                                        <input type="hidden" name="detailId" value="${item.orderDetailId}">
                                        <input type="hidden" name="orderId" value="${order.orderId}">
                                        <button type="submit" class="btn btn-sm btn-danger"
                                                <c:if test="${item.status != 'PENDING'}">disabled title="Không thể xóa món đã bắt đầu pha chế"</c:if>>
                                            Hủy món
                                        </button>
                                    </form>
                                </td>
                            </tr>
                            <c:set var="totalAmount" value="${totalAmount + item.subtotal}"/>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="7" class="text-muted py-4">Chưa có món nào trong hóa đơn này.</td>
                        </tr>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>
        <div class="card-footer bg-white text-end py-3">
            <h4 class="mb-0">Tổng hóa đơn: <span class="text-danger fw-bold">${totalAmount} VNĐ</span></h4>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>