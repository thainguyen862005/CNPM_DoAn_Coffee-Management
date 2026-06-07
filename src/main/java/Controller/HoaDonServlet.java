package Controller;

import DAO.HoaDonDAO;
import DAO.so_do_banDAO;
import Model.Order;
import Util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/HoaDon")
public class HoaDonServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        /*
            UC-03 - KIỂM TRA QUYỀN TRUY CẬP

            [3.1.2] Kiểm tra Session hiện tại.
            [3.1.5] Nếu người dùng đã đăng nhập thì cho phép tiếp tục xem danh sách hoặc chi tiết hóa đơn.

            Alternative Flow [3.2.0 - 3.2.3]:
                - Nếu chưa đăng nhập thì điều hướng về login.jsp.
                - Dừng toàn bộ request.
           */
        if (!AuthUtil.checkLogin(request, response)) {
            return;
        }

        String action = request.getParameter("action");
        HoaDonDAO dao = new HoaDonDAO();

        if ("detail".equals(action)) {
            // [UC-11] Bước 1: Servlet tiếp nhận request xem chi tiết hóa đơn từ giao diện
            String idParam = request.getParameter("id");
            if (idParam != null) {
                int orderId = Integer.parseInt(idParam);
                // [UC-11] Bước 2: Gọi DAO truy vấn Database để lấy thông tin chi tiết Order
                Order order = dao.getOrderById(orderId);
                request.setAttribute("order", order);
            }
            // [UC-11] Bước 3: Đẩy dữ liệu (order) sang chi_tiet_hoa_don.jsp để render HTML
            request.getRequestDispatcher("/WEB-INF/views/chi_tiet_hoa_don.jsp").forward(request, response);
        }
        else if ("detail_by_table".equals(action)) {
            String tableIdParam = request.getParameter("tableId");
            if (tableIdParam != null) {
                int tableId = Integer.parseInt(tableIdParam);
                Order order = dao.getOrderByTableId(tableId);
                request.setAttribute("order", order);
            }
            request.getRequestDispatcher("/WEB-INF/views/chi_tiet_hoa_don.jsp").forward(request, response);
        }
        else {
            List<Order> listHoaDon = dao.getAllOrders();
            List<Model.MenuItem> listMenu = dao.getAllMenuItems();
            List<Model.CoffeeTable> listTable = dao.getAllTables();

            request.setAttribute("listHoaDon", listHoaDon);
            request.setAttribute("listMenu", listMenu);
            request.setAttribute("listTable", listTable);

            request.setAttribute("page_content", "hoa_don.jsp");
            request.setAttribute("active_tab", "hoadon");
            request.getRequestDispatcher("/WEB-INF/views/main_ui.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        if (!AuthUtil.checkLogin(request, response)) {
            return;
        }

        String action = request.getParameter("action");
        HoaDonDAO dao = new HoaDonDAO();
        so_do_banDAO banDao = new so_do_banDAO();

        if ("thanh_toan".equals(action)) {

            /*
                UC-03 - Kiểm tra quyền thanh toán hóa đơn.
                Cashier là tác nhân thực hiện nghiệp vụ thanh toán. Manager và Staff không được gửi trực tiếp action thanh_toan.
            */
            // [UC-12] Bước 1: Tiếp nhận request thanh toán từ AJAX truyền lên
            if (!AuthUtil.checkRole(request, response, "Cashier")) {
                return;
            }

            int orderId = Integer.parseInt(request.getParameter("orderId"));
            int tableId = Integer.parseInt(request.getParameter("tableId"));
            // [UC-12] Bước 2: Gọi HoaDonDAO để xử lý nghiệp vụ lưu hóa đơn và dọn bàn
            dao.thanhToanHoaDon(orderId, tableId);
            // [UC-12] Bước 3: Trả tín hiệu "success" về cho hàm Fetch API ở Front-end
            response.getWriter().write("success");
            return;
        }
        // [UC-05] MAIN FLOW: Tạo Order
        else if ("create".equals(action)) {
            /*
                UC-03 - KIỂM TRA QUYỀN TẠO HÓA ĐƠN

                Nghiệp vụ:
                - Staff là nhân viên phục vụ, được phép tạo order.
                - Manager và Cashier không được thực hiện action create.

                [3.1.3] Lấy role từ Session.
                [3.1.4] So sánh role với quyền Staff.
                [3.1.5] Nếu đúng role thì tiếp tục tạo hóa đơn.

                Alternative Flow [3.3.0 - 3.3.3]:
                - Nếu không phải Staff thì hiển thị access_denied.jsp.
                - Dừng xử lý request.
            */
            if (!AuthUtil.checkRole(request, response, "Staff")) {
                return;
            }
            // ĐIỀU KIỆN 2: BẢO VỆ BÀN PHỤC VỤ (BÀN CHƯA THANH TOÁN THÌ KHÔNG ĐƯỢC ĐẶT)
            String tableIdParam = request.getParameter("tableId");
            int tableId = (tableIdParam != null && !tableIdParam.isEmpty()) ? Integer.parseInt(tableIdParam) : 0;

            if (tableId > 0) {
                Model.Order activeOrder = dao.getOrderByTableId(tableId); //
                if (activeOrder != null) {
                    response.sendRedirect("HoaDon?error=table_not_paid");
                    return; // Chặn không cho tạo đè hóa đơn mới lên bàn cũ chưa tính tiền
                }
            }
            // LUỒNG XỬ LÝ CHÍNH CHO STAFF: TẠO HÓA ĐƠN
            int newOrderId = dao.createOrderAndReturnId(tableId);

            if (newOrderId > 0) {
                // Chuyển trạng thái bàn trong MySQL sang "Đang phục vụ"
                dao.updateCoffeeTableStatus(tableId, "Đang phục vụ"); //

                String[] selectedItems = request.getParameterValues("itemIds"); //
                if (selectedItems != null) {
                    for (String itemIdStr : selectedItems) {
                        int itemId = Integer.parseInt(itemIdStr);
                        String qtyStr = request.getParameter("qty_" + itemId);
                        int quantity = (qtyStr != null && !qtyStr.isEmpty()) ? Integer.parseInt(qtyStr) : 1;

                        // Lưu món vào chi tiết đơn hàng
                        dao.addOrderDetail(newOrderId, itemId, quantity); //
                    }
                }
            }

            response.sendRedirect("HoaDon");
        }
        // [UC-06] MAIN FLOW: Cập nhật Order (Đổi bàn, Gọi thêm món)
        else if ("addMultipleItems".equals(action) || "update".equals(action)) {

            /*
                UC-03 - Kiểm tra quyền cập nhật order.
                Chỉ Staff được thêm món hoặc cập nhật hóa đơn.
            */
            if (!AuthUtil.checkRole(request, response, "Staff")) {
                return;
            }

            int orderId = Integer.parseInt(request.getParameter("orderId"));

            Model.Order currentOrder = dao.getOrderById(orderId);
            if (currentOrder != null && "Đã thanh toán".equals(currentOrder.getStatus())) {
                response.sendRedirect("HoaDon?error=paid");
                return;
            }

            String newTableIdParam = request.getParameter("newTableId");
            int newTableId = (newTableIdParam != null && !newTableIdParam.isEmpty()) ? Integer.parseInt(newTableIdParam) : -1;

            if (newTableId != -1) {
                int oldTableId = dao.getTableIdByOrderId(orderId);
                if (oldTableId != newTableId) {
                    dao.updateOrderTable(orderId, newTableId);
                    if (oldTableId > 0) banDao.updateCoffeeTableStatus(oldTableId, "Trống");
                    if (newTableId > 0) banDao.updateCoffeeTableStatus(newTableId, "Đang phục vụ");
                }
            }

            String[] selectedItems = request.getParameterValues("itemIds");
            if (selectedItems != null) {
                for (String itemIdStr : selectedItems) {
                    int itemId = Integer.parseInt(itemIdStr);
                    String qtyStr = request.getParameter("qty_" + itemId);
                    int quantity = (qtyStr != null && !qtyStr.isEmpty()) ? Integer.parseInt(qtyStr) : 1;

                    dao.addOrUpdateOrderDetail(orderId, itemId, quantity);
                }
            }
            response.sendRedirect("HoaDon");
        }
        // [UC-06] MAIN FLOW: Xóa món trong Order
        else if ("removeMenuItem".equals(action)) {

            /*
                UC-03 - Kiểm tra quyền xóa món khỏi order.
                Chỉ Staff được thay đổi chi tiết order.
            */
            if (!AuthUtil.checkRole(request, response, "Staff")) {
                return;
            }

            int orderId = Integer.parseInt(request.getParameter("orderId"));
            int itemId = Integer.parseInt(request.getParameter("itemId"));

            // Xóa món khỏi chi tiết hóa đơn
            dao.removeOrderDetail(orderId, itemId);

            // [UC-06] ALTERNATIVE FLOW 3C: Tự động xóa Order nếu order rỗng
            Model.Order orderAfterRemove = dao.getOrderById(orderId);
            if (orderAfterRemove != null && orderAfterRemove.getOrderDetails().isEmpty()) {
                int tableId = dao.getTableIdByOrderId(orderId);

                // Xóa tận gốc hóa đơn
                dao.deleteOrder(orderId);

                // Trả bàn về trạng thái Trống để đón khách khác
                if (tableId > 0) {
                    banDao.updateCoffeeTableStatus(tableId, "Trống");
                }
            }

            // Fix lỗi Redirect: Điều hướng chuẩn về trang Quản Lý Hóa Đơn hiện hành
            response.sendRedirect("HoaDon");
        }

        else if ("chuyen_thanh_toan".equals(action)) {

            /*
                UC-03 - Kiểm tra quyền chuyển order sang trạng thái chờ thanh toán.
                Đây là thao tác của nhân viên phục vụ nên chỉ Staff được thực hiện.
            */
            if (!AuthUtil.checkRole(request, response, "Staff")) {
                return;
            }

            int tableId = Integer.parseInt(request.getParameter("tableId"));

            // Tìm cái hóa đơn của bàn này
            Model.Order order = dao.getOrderByTableId(tableId);
            if (order != null) {
                // Đổi trạng thái nó sang "Chưa thanh toán"
                dao.updateOrderStatus(order.getOrderId(), "Chưa thanh toán");
            }

            // Đá về lại Trang Chủ (Sơ đồ bàn) để nó tự tải lại màu Vàng
            response.sendRedirect("TrangChu");
        }
    }
}