<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<c:choose>
    <c:when test="${not empty order}">
        <input type="hidden" id="order_status_hidden" value="${order.status}">
        <p><strong>Mã HĐ:</strong> #${order.orderId}</p>
        <p><strong>Thời gian:</strong> ${order.createdAt}</p>
        <table class="table table-bordered text-center mt-3">
            <thead class="table-light">
            <tr><th>Tên món</th><th>Số lượng</th><th>Đơn giá</th></tr>
            </thead>
            <tbody>
            <c:forEach var="detail" items="${order.orderDetails}">
                <tr>
                    <td>${detail.menuItem.itemName}</td>
                    <td>${detail.quantity}</td>
                    <td>${detail.unitPrice}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <h5 class="text-danger text-end fw-bold">Tổng tiền: ${order.calculateTotal()} VNĐ</h5>
    </c:when>
    <c:otherwise>
        <div class="alert alert-danger">Không tìm thấy dữ liệu hóa đơn này!</div>
    </c:otherwise>
</c:choose>