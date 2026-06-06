<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<c:choose>
    <c:when test="${not empty order}">
        <input type="hidden" id="order_status_hidden" value="${order.status}">
        <p><strong>Mã HĐ:</strong> #${order.orderId}</p>
        <p><strong>Thời gian:</strong> ${order.createdAt}</p>

        <table class="table table-bordered text-center mt-3">
            <thead class="table-light">
            <tr>
                <th>Tên món</th>
                <th>Số lượng</th>
                <th>Đơn giá</th>
                <c:if test="${order.status != 'Đã thanh toán'}">
                    <th>Thao tác</th>
                </c:if>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="detail" items="${order.orderDetails}">
                <tr>
                    <td class="align-middle">${detail.menuItem.itemName}</td>
                    <td class="align-middle">${detail.quantity}</td>
                    <td class="align-middle">${detail.unitPrice}</td>

                    <c:if test="${order.status != 'Đã thanh toán'}">
                        <td class="align-middle">
                            <form action="HoaDon" method="post" onsubmit="return confirm('Bạn có chắc chắn muốn hủy món này không?');" class="m-0">
                                <input type="hidden" name="action" value="removeMenuItem">
                                <input type="hidden" name="itemId" value="${detail.menuItem.itemId}">
                                <input type="hidden" name="orderId" value="${order.orderId}">
                                <button type="submit" class="btn btn-sm btn-outline-danger"><i class="fa fa-trash"></i> Hủy</button>
                            </form>
                        </td>
                    </c:if>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <h5 class="text-danger text-end fw-bold">Tổng tiền: ${order.calculateTotal()} VNĐ</h5>

        <hr>

        <c:if test="${order.status != 'Đã thanh toán'}">
            <div class="bg-light p-3 rounded border border-warning">
                <div class="form-group row align-items-center">
                    <label class="col-sm-5 col-form-label fw-bold">Khách đưa (VNĐ):</label>
                    <div class="col-sm-7">
                        <input type="number" id="tienKhachDua" class="form-control" oninput="tinhTienThoi(${order.calculateTotal()})" placeholder="Nhập tiền khách đưa...">
                    </div>
                </div>
                <div class="form-group row mt-2 align-items-center">
                    <label class="col-sm-5 col-form-label fw-bold">Tiền thối lại:</label>
                    <div class="col-sm-7">
                        <input type="text" id="tienThoiLai" class="form-control text-danger font-weight-bold" readonly value="0 đ">
                    </div>
                </div>
            </div>

            <form id="formThanhToan" class="text-center mt-3" onsubmit="return thucHienThanhToan(event, ${order.calculateTotal()});">
                <input type="hidden" name="action" value="thanh_toan">
                <input type="hidden" name="tableId" value="${order.table != null ? order.table.tableId : 0}">
                <input type="hidden" name="orderId" value="${order.orderId}">

                <button type="submit" class="btn btn-success btn-lg btn-block mt-2">
                    <i class="fa fa-print"></i> Thanh Toán & In Hóa Đơn
                </button>
            </form>
        </c:if>

        <c:if test="${order.status == 'Đã thanh toán'}">
            <div class="alert alert-success text-center font-weight-bold mt-3">
                <i class="fa fa-check-circle"></i> Hóa đơn này đã được thanh toán
            </div>
        </c:if>

    </c:when>
    <c:otherwise>
        <div class="alert alert-danger">Không tìm thấy dữ liệu hóa đơn này!</div>
    </c:otherwise>
</c:choose>