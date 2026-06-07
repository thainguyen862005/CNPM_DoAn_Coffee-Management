<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Báo Cáo Doanh Thu</title>
    <style>
            body {
                font-family: Arial, sans-serif;
                padding: 20px;
            }

            table {
                border-collapse: collapse;
                width: 100%;
                max-width: 600px;
            }

            th, td {
                border: 1px solid #e0e4e8; /* Viền xám nhạt của bảng */
            }

            th {
                background-color: #353b43;
                color: #ffffff;
                font-weight: bold;
                text-align: center;
                padding: 18px 10px;
            }

            td {
                padding: 0; /* Xóa khoảng cách của td để container lấp đầy */
                height: 50px;
            }

            /* Container bên trong các ô */
            .cell-container {
                width: 100%;
                height: 100%;
                min-height: 50px;
                box-sizing: border-box;
                border: none; /* Không tạo viền cho container */
                padding: 10px; /* Giữ khoảng cách an toàn cho nội dung bên trong */
                display: flex;
                align-items: center; /* Căn giữa nội dung theo chiều dọc */
            }
        </style>
</head>
<body>
    <h2>Báo Cáo Doanh Thu Theo Năm</h2>

    <table class="table table-bordered mt-3"> <thead class="thead-dark">
            <tr>
                <th>Năm</th>
                <th>Doanh thu</th>
                <th>Món bán chạy nhất</th>
            </tr>
        </thead>
        <tbody>
            <tr data-year="2026">
                <td>2026</td>
                <td style="color: #2e7d32; font-weight: bold;" id="revenue-2026">0 đ</td>
                <td id="best-2026">--</td>
            </tr>
            <tr data-year="2025">
                <td>2025</td>
                <td style="color: #2e7d32; font-weight: bold;" id="revenue-2025">0 đ</td>
                <td id="best-2025">--</td>
            </tr>
            <tr data-year="2024">
                <td>2024</td>
                <td style="color: #2e7d32; font-weight: bold;" id="revenue-2024">0 đ</td>
                <td id="best-2024">--</td>
            </tr>
        </tbody>
    </table>

    <script>
        // Hàm gọi API lấy dữ liệu JSON như bạn đã viết
        async function taiDuLieuDoanhThu() {
            try {
                const response = await fetch('BaoCao?action=lay_du_lieu'); // Trỏ đến Servlet trả JSON của bạn
                if (!response.ok) throw new Error("Lỗi mạng");
                const data = await response.json();

                data.forEach(item => {
                    const oDoanhThu = document.getElementById(`revenue-${item.nam}`);
                    const oMonBanChay = document.getElementById(`best-${item.nam}`);
                    if (oDoanhThu && oMonBanChay) {
                        oDoanhThu.innerText = Number(item.tong_doanh_thu).toLocaleString('vi-VN') + ' đ';
                        oMonBanChay.innerText = item.mon_ban_chay;
                    }
                });
            } catch (error) {
                console.error('Lỗi lấy dữ liệu:', error);
            }
        }

        // Load dữ liệu
        taiDuLieuDoanhThu();
    </script>