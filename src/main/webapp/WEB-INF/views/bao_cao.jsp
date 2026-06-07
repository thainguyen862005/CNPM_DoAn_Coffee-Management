<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<div class="container-fluid mt-4">
    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h2 class="h2 text-dark fw-bold">
            <i class="bi bi-graph-up-arrow text-primary"></i> Báo Cáo & Thống Kê Doanh Thu
        </h2>

        <form action="${pageContext.request.contextPath}/BaoCao" method="get" class="d-flex align-items-center bg-white p-2 rounded shadow-sm border">
            <label class="me-2 fw-bold text-muted small mb-0"><i class="bi bi-funnel-fill text-secondary"></i> Thời gian:</label>
            <select name="timeFilter" class="form-select form-select-sm fw-bold text-dark border-secondary" onchange="this.form.submit()" style="min-width: 180px; cursor: pointer;">
                <option value="all" ${selectedFilter == 'all' ? 'selected' : ''}>Toàn thời gian</option>
                <option value="today" ${selectedFilter == 'today' ? 'selected' : ''}>1 Ngày (Hôm nay)</option>
                <option value="week" ${selectedFilter == 'week' ? 'selected' : ''}>1 Tuần qua</option>
                <option value="month" ${selectedFilter == 'month' ? 'selected' : ''}>1 Tháng qua</option>
                <option value="year" ${selectedFilter == 'year' ? 'selected' : ''}>1 Năm qua</option>
            </select>
        </form>
    </div>

    <div class="row">

        <div class="col-md-6 mb-4">
            <div class="card border-0 border-start border-primary border-5 shadow-sm h-100">
                <div class="card-body py-4">
                    <div class="row align-items-center">
                        <div class="col mr-2">
                            <div class="text-xs font-weight-bold text-primary text-uppercase mb-1" style="letter-spacing: 0.5px;">
                                Tổng Số Hóa Đơn Thành Công
                            </div>
                            <div class="h3 mb-0 font-weight-bold text-dark">
                                <c:out value="${not empty totalInvoices ? totalInvoices : 0}"/> <span class="fs-5 text-muted fw-normal">đơn hàng</span>
                            </div>
                            <p class="text-muted small mb-0 mt-2">
                                Trạng thái: <span class="badge bg-success">Đã thanh toán</span>
                            </p>
                        </div>
                        <div class="col-auto">
                            <div class="p-3 bg-light rounded-circle text-center">
                                <i class="bi bi-receipt text-primary" style="font-size: 2.5rem;"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-md-6 mb-4">
            <div class="card border-0 border-start border-success border-5 shadow-sm h-100">
                <div class="card-body py-4">
                    <div class="row align-items-center">
                        <div class="col mr-2">
                            <div class="text-xs font-weight-bold text-success text-uppercase mb-1" style="letter-spacing: 0.5px;">
                                Tổng Doanh Thu Thực Tế
                            </div>
                            <div class="h3 mb-0 font-weight-bold text-success">
                                <fmt:formatNumber value="${not empty totalRevenue ? totalRevenue : 0}" type="number" maxFractionDigits="0"/> <span class="fs-5 text-muted fw-normal">đ</span>
                            </div>
                            <p class="text-muted small mb-0 mt-2">
                                Kỳ báo cáo:
                                <span class="text-primary fw-bold">
                                    <c:choose>
                                        <c:when test="${selectedFilter == 'today'}">Hôm nay</c:when>
                                        <c:when test="${selectedFilter == 'week'}">7 ngày gần nhất</c:when>
                                        <c:when test="${selectedFilter == 'month'}">30 ngày gần nhất</c:when>
                                        <c:when test="${selectedFilter == 'year'}">365 ngày gần nhất</c:when>
                                        <c:otherwise>Toàn bộ lịch sử quán</c:otherwise>
                                    </c:choose>
                                </span>
                            </p>
                        </div>
                        <div class="col-auto">
                            <div class="p-3 bg-light rounded-circle text-center">
                                <i class="bi bi-cash-coin text-success" style="font-size: 2.5rem;"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </div>

    <div class="row mt-2">
        <div class="col-12">
            <div class="alert alert-info shadow-sm mb-0" role="alert">
                <i class="bi bi-info-circle-fill"></i> Dữ liệu thống kê được tính toán tự động dựa trên thời gian thực hoàn thành giao dịch tại quầy bàn.
            </div>
        </div>
    </div>
</div>