<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<h4><i class="fa fa-list"></i> Tình trạng bàn hiện tại</h4>
<hr>
<div class="row">
    <c:forEach items="${danhSachBan}" var="ban">
        <div class="col-md-3 mb-4">
            <div class="card text-center shadow-sm">
                <div class="card-body">
                    <h1 style="color: #6c757d;"><i class="fa fa-cube"></i></h1>
                    <h5>${ban.tableName}</h5>
                    <p>Khu vực: ${ban.area}</p>

                    <c:choose>
                        <%-- TRẠNG THÁI 1: TRỐNG --%>
                        <c:when test="${ban.status eq 'Trống'}">
                            <span class="badge badge-success"><i class="fa fa-check"></i> Trống</span>
                            <a href="HoaDon?action=new&tableId=${ban.tableId}" class="btn btn-outline-primary btn-sm mt-2 btn-block">Gọi Món</a>
                        </c:when>

                        <%-- TRẠNG THÁI 2: ĐANG PHỤC VỤ --%>
                        <c:when test="${ban.status eq 'Đang phục vụ'}">
                            <span class="badge badge-danger"><i class="fa fa-fire"></i> Đang phục vụ</span>
                            <form action="HoaDon" method="POST" class="mt-2">
                                <input type="hidden" name="action" value="request_payment">
                                <input type="hidden" name="tableId" value="${ban.tableId}">
                                <button type="submit" class="btn btn-outline-warning btn-sm btn-block">Chuyển Thanh Toán</button>
                            </form>
                        </c:when>

                        <%-- TRẠNG THÁI 3: CHƯA THANH TOÁN --%>
                        <c:when test="${ban.status eq 'Chưa thanh toán'}">
                            <span class="badge badge-warning text-dark"><i class="fa fa-exclamation-circle"></i> Chưa thanh toán</span>
                            <button onclick="moPopupThanhToan(${ban.tableId})" class="btn btn-outline-info btn-sm mt-2 btn-block" data-bs-toggle="modal" data-bs-target="#modalThanhToan">Thanh Toán</button>
                        </c:when>

                        <%-- PHÒNG HỜ CÁC TRẠNG THÁI KHÁC NẾU CÓ --%>
                        <c:otherwise>
                            <span class="badge badge-secondary">${ban.status}</span>
                            <button class="btn btn-outline-secondary btn-sm mt-2 btn-block">Xem</button>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </c:forEach>
</div>
<div class="modal fade" id="modalThanhToan" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Xác nhận thanh toán</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body text-center">
                <h4 id="bill-content">Đang tải dữ liệu bàn...</h4>
                <p>Xác nhận đã thu tiền khách hàng? Bàn sẽ được dọn trống.</p>
            </div>
            <div class="modal-footer d-flex justify-content-center">
                <form action="HoaDon" method="POST">
                    <input type="hidden" name="action" value="confirm_payment">
                    <input type="hidden" name="tableId" id="pay_table_id" value="">
                    <button type="submit" class="btn btn-success btn-lg">Đã Thu Tiền</button>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
    // Hàm này kích hoạt khi bấm nút "Thanh Toán" ở bàn màu Vàng
    function moPopupThanhToan(tableId) {
        // Nhét ID của bàn vào thẻ input ẩn để submit form
        document.getElementById('pay_table_id').value = tableId;
        // Đổi chữ trên Popup
        document.getElementById('bill-content').innerHTML = "Thanh toán cho Bàn số: " + tableId;
    }
</script>