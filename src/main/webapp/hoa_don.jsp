<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<div class="container-fluid mt-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2>Quản lý hóa đơn</h2>
        <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#taoHoaDonModal">
            + Tạo hóa đơn mới
        </button>
    </div>

    <div class="card shadow">
        <div class="card-body">
            <table class="table table-bordered table-hover text-center align-middle">
                <thead class="table-dark">
                <tr>
                    <th>Mã HĐ</th>
                    <th>Bàn phục vụ</th>
                    <th>Thời gian tạo</th>
                    <th>Trạng thái</th>
                    <th>Hành động</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <%-- Nếu có dữ liệu thì in ra từng dòng --%>
                    <c:when test="${not empty listHoaDon}">
                        <c:forEach var="order" items="${listHoaDon}">
                            <tr>
                                <td><strong>#${order.orderId}</strong></td>

                                <td>${order.table != null ? order.table.tableName : 'Mang đi'}</td>

                                <td>${order.createdAt}</td>

                                <td>
                                    <c:choose>
                                        <c:when test="${order.status == 'Đã thanh toán'}">
                                            <span class="badge bg-success">${order.status}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-warning text-dark">${order.status}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>

                                <td>
                                    <button type="button"
                                            class="btn btn-sm btn-info text-white"
                                            data-toggle="modal" data-target="#chiTietModal"
                                            data-bs-toggle="modal" data-bs-target="#chiTietModal"
                                            onclick="taiDuLieuChiTiet(${order.orderId})">
                                        <i class="bi bi-eye"></i> Chi tiết
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>

                    <c:otherwise>
                        <tr>
                            <td colspan="5" class="text-muted py-4">Hiện chưa có hóa đơn nào!</td>
                        </tr>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</div>

<div class="modal fade" id="chiTietModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header bg-info text-white">
                <h5 class="modal-title">Chi tiết hóa đơn</h5>
                <button type="button" class="close text-white" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body" id="modal-body-content">
                <div class="text-center my-4">
                    <div class="spinner-border text-info" role="status"></div>
                    <p class="mt-2">Đang tải dữ liệu...</p>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Đóng</button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="taoHoaDonModal" tabindex="-1">
    <div class="modal-dialog">
        <form action="HoaDon" method="POST">
            <input type="hidden" name="action" value="create">

            <div class="modal-content">
                <div class="modal-header bg-primary text-white">
                    <h5 class="modal-title">Tạo hóa đơn mới</h5>
                    <button type="button" class="close text-white" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="form-group mb-3">
                        <label class="fw-bold mb-2">1. Chọn khu vực / Bàn phục vụ:</label>
                        <select name="tableId" class="form-select form-control">
                            <option value="0">🛒 Mua mang đi (Takeaway)</option>
                            <c:forEach var="table" items="${listTable}">
                                <option value="${table.tableId}">
                                    [${table.status}] - ${table.tableName} (${table.area})
                                </option>
                            </c:forEach>

                        </select>
                    </div>

                    <div class="form-group">
                        <label class="fw-bold mb-2">2. Chọn món ăn / thức uống:</label>
                        <div style="max-height: 250px; overflow-y: auto; border: 1px solid #dee2e6; padding: 10px; border-radius: 5px; background: #f8f9fa;">
                            <c:choose>
                                <c:when test="${not empty listMenu}">
                                    <c:forEach var="item" items="${listMenu}">
                                        <div class="d-flex justify-content-between align-items-center mb-2 pb-2 border-bottom">
                                            <div class="form-check">
                                                <input class="form-check-input" type="checkbox" name="itemIds" value="${item.itemId}" id="item_${item.itemId}">
                                                <label class="form-check-label ms-2" for="item_${item.itemId}">
                                                    <strong>${item.itemName}</strong> <br>
                                                    <small class="text-danger">${item.price} VNĐ</small>
                                                </label>
                                            </div>
                                            <input type="number" name="qty_${item.itemId}" class="form-control text-center" style="width: 70px;" value="1" min="1">
                                        </div>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <p class="text-muted">Chưa có món ăn nào trong Menu DB.</p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn btn-success">Khởi tạo</button>
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Hủy</button>
                </div>
            </div>
        </form>
    </div>
</div>

<script>
    function taiDuLieuChiTiet(orderId) {
        // Hiện chữ Đang tải... trong lúc chờ Server trả dữ liệu
        document.getElementById('modal-body-content').innerHTML = '<div class="text-center my-4"><div class="spinner-border text-info" role="status"></div><p class="mt-2">Đang tải dữ liệu...</p></div>';

        // Gọi ngầm xuống Servlet để xin HTML ruột
        fetch('HoaDon?action=detail&id=' + orderId)
            .then(response => response.text())
            .then(html => {
                // Đổ dữ liệu HTML lấy được vào trong thân của Popup
                document.getElementById('modal-body-content').innerHTML = html;
            })
            .catch(error => {
                document.getElementById('modal-body-content').innerHTML = '<p class="text-danger">Lỗi mạng hoặc không tải được dữ liệu!</p>';
            });
    }
</script>