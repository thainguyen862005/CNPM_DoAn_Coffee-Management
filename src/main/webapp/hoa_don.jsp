<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản Lý Hóa Đơn</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">

    <style>
        body { background-color: #f8f9fa; }
        /* CSS Tùy chỉnh cho khung chọn món */
        .menu-scroll-box {
            max-height: 350px;
            overflow-y: auto;
            border: 1px solid #dee2e6;
            padding: 10px;
            border-radius: 8px;
            background: #fcfcfc;
            box-shadow: inset 0 2px 4px rgba(0,0,0,0.05);
        }
        .menu-scroll-box::-webkit-scrollbar {
            width: 6px;
        }
        .menu-scroll-box::-webkit-scrollbar-track {
            background: #f1f1f1;
            border-radius: 8px;
        }
        .menu-scroll-box::-webkit-scrollbar-thumb {
            background: #c1c1c1;
            border-radius: 8px;
        }
        .menu-scroll-box::-webkit-scrollbar-thumb:hover {
            background: #a8a8a8;
        }
        .menu-item-row {
            transition: background-color 0.2s ease;
            border-radius: 6px;
            padding-top: 8px;
            padding-bottom: 8px;
        }
        .menu-item-row:hover {
            background-color: #e9ecef;
        }
        .price-tag {
            font-weight: 600;
            color: #dc3545;
        }
    </style>
</head>
<body>

<div class="container-fluid mt-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2>Quản lý hóa đơn</h2>
        <button type="button" class="btn btn-primary shadow-sm" data-bs-toggle="modal" data-bs-target="#taoHoaDonModal">
            + Tạo hóa đơn mới
        </button>
    </div>

    <div class="card shadow-sm">
        <div class="card-body">
            <table class="table table-bordered table-hover text-center align-middle">
                <thead class="table-dark">
                <tr>
                    <th>Mã HĐ</th>
                    <th>Loại hình</th>
                    <th>Bàn phục vụ</th>
                    <th>Thời gian tạo</th>
                    <th>Trạng thái</th>
                    <th>Hành động</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${not empty listHoaDon}">
                        <c:forEach var="order" items="${listHoaDon}">
                            <tr>
                                <td><strong>#${order.orderId}</strong></td>

                                <td>
                                    <span class="badge bg-secondary">
                                            ${order.orderType == 'TAKEAWAY' ? 'Mang đi' : 'Tại bàn'}
                                    </span>
                                </td>

                                <td>${order.table != null ? order.table.tableName : '-'}</td>

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
                                            class="btn btn-sm btn-info text-white shadow-sm"
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
                            <td colspan="6" class="text-muted py-4">Hiện chưa có hóa đơn nào!</td>
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
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body" id="modal-body-content">
                <div class="text-center my-4">
                    <div class="spinner-border text-info" role="status"></div>
                    <p class="mt-2">Đang tải dữ liệu...</p>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" id="btnMoCapNhat" class="btn btn-sm btn-warning" data-bs-toggle="modal" data-bs-target="#capNhatHoaDonModal" data-bs-dismiss="modal">
                    <i class="bi bi-pencil-square"></i> Gọi thêm món
                </button>
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="taoHoaDonModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <form action="HoaDonServlet" method="POST">
            <input type="hidden" name="action" value="createOrder">

            <div class="modal-content">
                <div class="modal-header bg-primary text-white">
                    <h5 class="modal-title">Tạo hóa đơn mới</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="row mb-3">
                        <div class="col-md-6 form-group">
                            <label class="fw-bold mb-2">1. Loại hình phục vụ:</label>
                            <select name="orderType" id="selectOrderType" class="form-select" onchange="toggleTableSelection()">
                                <option value="DINE_IN">Dùng tại bàn</option>
                                <option value="TAKEAWAY">Mua mang đi (Takeaway)</option>
                            </select>
                        </div>
                        <div class="col-md-6 form-group">
                            <label class="fw-bold mb-2">2. Chọn Bàn:</label>
                            <select name="tableId" id="selectTableId" class="form-select">
                                <option value="0">-- Không dùng bàn --</option>
                                <c:forEach var="table" items="${listTable}">
                                    <option value="${table.tableId}">
                                        [${table.status}] - ${table.tableName} (${table.area})
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="fw-bold mb-2">3. Chọn món ăn / thức uống:</label>
                        <div class="menu-scroll-box">
                            <c:choose>
                                <c:when test="${not empty listMenu}">
                                    <c:forEach var="item" items="${listMenu}">
                                        <div class="row align-items-center mb-2 pb-2 border-bottom mx-0 menu-item-row">
                                            <div class="col-5">
                                                <div class="form-check">
                                                    <input class="form-check-input" type="checkbox" name="itemIds" value="${item.itemId}" id="item_${item.itemId}">
                                                    <label class="form-check-label ms-2" for="item_${item.itemId}">
                                                        <strong>${item.itemName}</strong> <br>
                                                        <small class="price-tag">${item.price} VNĐ</small>
                                                    </label>
                                                </div>
                                            </div>
                                            <div class="col-3 px-1">
                                                <div class="input-group input-group-sm">
                                                    <span class="input-group-text">SL</span>
                                                    <input type="number" name="qty_${item.itemId}" class="form-control text-center" value="1" min="1">
                                                </div>
                                            </div>
                                            <div class="col-4 px-1">
                                                <input type="text" name="note_${item.itemId}" class="form-control form-control-sm" placeholder="Ghi chú (Ít đá...)">
                                            </div>
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
                    <button type="submit" class="btn btn-success"><i class="bi bi-check-circle"></i> Khởi tạo</button>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                </div>
            </div>
        </form>
    </div>
</div>

<div class="modal fade" id="capNhatHoaDonModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <form action="HoaDonServlet" method="POST">
            <input type="hidden" name="action" value="addMultipleItems">
            <input type="hidden" name="orderId" id="update_order_id" value="">

            <div class="modal-content">
                <div class="modal-header bg-warning text-dark">
                    <h5 class="modal-title">Gọi thêm món cho Hóa đơn <span id="display_order_id"></span></h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="form-group mb-3">
                        <label class="fw-bold mb-2">1. Chuyển sang bàn khác?</label>
                        <select name="newTableId" class="form-select">
                            <option value="-1" selected>-- Giữ nguyên bàn hiện tại --</option>
                            <option value="0">🛒 Chuyển sang Mua mang đi</option>
                            <c:forEach var="table" items="${listTable}">
                                <c:if test="${table.status eq 'Trống'}">
                                    <option value="${table.tableId}">
                                        [Trống] - ${table.tableName} (${table.area})
                                    </option>
                                </c:if>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group">
                        <label class="fw-bold mb-2">2. Danh sách thực đơn:</label>
                        <div class="menu-scroll-box">
                            <c:forEach var="item" items="${listMenu}">
                                <div class="row align-items-center mb-2 pb-2 border-bottom mx-0 menu-item-row">
                                    <div class="col-5">
                                        <div class="form-check">
                                            <input class="form-check-input" type="checkbox" name="itemIds" value="${item.itemId}" id="upd_item_${item.itemId}">
                                            <label class="form-check-label ms-2" for="upd_item_${item.itemId}">
                                                <strong>${item.itemName}</strong> <br>
                                                <small class="price-tag">${item.price} VNĐ</small>
                                            </label>
                                        </div>
                                    </div>
                                    <div class="col-3 px-1">
                                        <div class="input-group input-group-sm">
                                            <span class="input-group-text">SL</span>
                                            <input type="number" name="qty_${item.itemId}" class="form-control text-center" value="1" min="1">
                                        </div>
                                    </div>
                                    <div class="col-4 px-1">
                                        <input type="text" name="note_${item.itemId}" class="form-control form-control-sm" placeholder="Ghi chú riêng...">
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn btn-success"><i class="bi bi-save"></i> Lưu Thay Đổi</button>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                </div>
            </div>
        </form>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<script>
    function moPopupCapNhat(orderId) {
        document.getElementById('update_order_id').value = orderId;
        document.getElementById('display_order_id').innerText = "#" + orderId;
    }

    function toggleTableSelection() {
        var orderType = document.getElementById("selectOrderType").value;
        var tableSelect = document.getElementById("selectTableId");
        if (orderType === 'TAKEAWAY') {
            tableSelect.value = "0";
            tableSelect.disabled = true;
        } else {
            tableSelect.disabled = false;
        }
    }
</script>

<script>
    function taiDuLieuChiTiet(orderId) {
        document.getElementById('modal-body-content').innerHTML = '<div class="text-center my-4"><div class="spinner-border text-info" role="status"></div><p class="mt-2">Đang tải dữ liệu...</p></div>';

        fetch('HoaDonServlet?action=detail&orderId=' + orderId)
            .then(response => response.text())
            .then(html => {
                document.getElementById('modal-body-content').innerHTML = html;

                var btnCapNhat = document.getElementById('btnMoCapNhat');
                if (btnCapNhat) {
                    btnCapNhat.setAttribute('onclick', 'moPopupCapNhat(' + orderId + ')');
                }

                var statusInput = document.getElementById('order_status_hidden');
                if (statusInput && statusInput.value === 'Đã thanh toán') {
                    if (btnCapNhat) btnCapNhat.style.display = 'none';
                } else {
                    if (btnCapNhat) btnCapNhat.style.display = 'inline-block';
                }
            })
            .catch(error => {
                document.getElementById('modal-body-content').innerHTML = '<p class="text-danger">Lỗi mạng hoặc không tải được dữ liệu!</p>';
            });
    }
</script>
</body>
</html>