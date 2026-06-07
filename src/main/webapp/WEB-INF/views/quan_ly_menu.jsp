<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý món ăn</title>

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
</head>
<body>

    <div class="container-fluid mt-4">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h2>Quản lý món ăn</h2>
            <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#themMonMoi">
                + Thêm món ăn
            </button>
        </div>

        <div class="card shadow">
            <div class="card-body">
                <table class="table table-bordered table-hover text-center align-middle">
                    <thead class="table-dark">
                    <tr>
                        <th>Mã món</th>
                        <th>Tên món</th>
                        <th>Giá tiền</th>
                        <th>Mô tả</th>
                        <th>Trạng thái</th>
                    </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="mon" items="${MenuItem}">
                                                <tr>
                                                    <td>${mon.itemId}</td>

                                                    <td class="font-weight-bold">${mon.itemName}</td>

                                                    <td class="text-danger">${mon.price} đ</td>

                                                    <td>${mon.description}</td>

                                                    <td>
                                                        <span class="badge ${mon.status == 'Có sẵn' ? 'badge-success' : 'badge-danger'}">
                                                            ${mon.status}
                                                        </span>
                                                    </td>
                                                </tr>
                                            </c:forEach>

                        <c:if test="${empty MenuItem}">
                            <tr>
                                <td colspan="5" class="text-center text-muted">Chưa có món ăn nào trong thực đơn.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <div class="modal fade" id="themMonMoi" tabindex="-1" role="dialog" aria-labelledby="modalTitle" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered" role="document">
            <div class="modal-content">
                <div class="modal-header bg-primary text-white">
                    <h5 class="modal-title" id="modalTitle">Thêm Món Ăn Mới</h5>
                    <button type="button" class="close text-white" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>

                <form action="QuanLyMenu" method="POST">
                    <div class="modal-body">
                        <input type="hidden" name="action" value="them_mon">

                        <div class="form-group">
                            <label for="maMon">Mã món ăn <span class="text-danger">*</span></label>
                            <input type="number" class="form-control" id="maMon" name="maMon" placeholder="" required>
                        </div>

                        <div class="form-group">
                            <label for="tenMon">Tên món ăn <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="tenMon" name="tenMon" placeholder="VD: Cà phê sữa đá" required>
                        </div>

                        <div class="form-group">
                            <label for="giaTien">Giá tiền (VNĐ) <span class="text-danger">*</span></label>
                            <input type="number" class="form-control" id="giaTien" name="giaTien" min="0" step="1000" placeholder="VD: 25000" required>
                        </div>

                        <div class="form-group">
                            <label for="moTa">Mô tả</label>
                            <textarea class="form-control" id="moTa" name="moTa" rows="3" placeholder="Ghi chú thành phần..."></textarea>
                        </div>

                        <div class="form-group">
                            <label for="trangThai">Trạng thái</label>
                            <select class="form-control" id="trangThai" name="trangThai">
                                <option value="Có sẵn">Có sẵn</option>
                                <option value="Hết hàng">Hết hàng</option>
                            </select>
                        </div>
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Hủy</button>
                        <button type="submit" class="btn btn-success"><i class="fa fa-save"></i> Lưu món ăn</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/jquery@3.5.1/dist/jquery.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>