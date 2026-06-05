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
                            <button type="button" onclick="moPopupThanhToan(${ban.tableId})" class="btn btn-outline-info btn-sm mt-2 btn-block">Thanh Toán</button>
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
            <div class="modal-header bg-warning">
                <h5 class="modal-title"><i class="fa fa-calculator"></i> Xác nhận thanh toán</h5>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body" id="bill-content">
                <h4 class="text-center">Đang tải dữ liệu hóa đơn...</h4>
            </div>
        </div>
    </div>
</div>

<script>
    function moPopupThanhToan(tableId) {
        // Dùng jQuery ép mở cái bảng Popup lên liền
        $('#modalThanhToan').modal('show');

        document.getElementById('bill-content').innerHTML = '<div class="text-center my-3"><div class="spinner-border text-warning"></div><p>Đang lôi hóa đơn ra...</p></div>';

        fetch('HoaDon?action=detail_by_table&tableId=' + tableId)
            .then(response => response.text())
            .then(html => {
                document.getElementById('bill-content').innerHTML = html;
            })
            .catch(error => {
                document.getElementById('bill-content').innerHTML = '<div class="alert alert-danger">Lỗi mạng! Không lấy được hóa đơn.</div>';
            });
    }
</script>