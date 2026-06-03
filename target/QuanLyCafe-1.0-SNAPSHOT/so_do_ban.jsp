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

                    <c:if test="${ban.status == 'Trống'}">
                        <span class="badge badge-success"><i class="fa fa-check"></i> Trống</span>
                        <button class="btn btn-outline-primary btn-sm mt-2 btn-block">Gọi Món</button>
                    </c:if>

                    <c:if test="${ban.status != 'Trống'}">
                        <span class="badge badge-danger"><i class="fa fa-times"></i> Đang phục vụ</span>
                        <button class="btn btn-outline-warning btn-sm mt-2 btn-block">Xem Chi Tiết</button>
                    </c:if>
                </div>
            </div>
        </div>
    </c:forEach>
</div>