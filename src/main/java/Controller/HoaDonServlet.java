package Controller;

import DAO.HoaDonDAO;
import DAO.so_do_banDAO;
import Model.Order;
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
        String action = request.getParameter("action");
        HoaDonDAO dao = new HoaDonDAO();

        if ("detail".equals(action)) {
            String idParam = request.getParameter("id");
            if (idParam != null) {
                int orderId = Integer.parseInt(idParam);
                Order order = dao.getOrderById(orderId);
                request.setAttribute("order", order);
            }
            request.getRequestDispatcher("chi_tiet_hoa_don.jsp").forward(request, response);
        }
        else if ("detail_by_table".equals(action)) {
            String tableIdParam = request.getParameter("tableId");
            if (tableIdParam != null) {
                int tableId = Integer.parseInt(tableIdParam);
                Order order = dao.getOrderByTableId(tableId);
                request.setAttribute("order", order);
            }
            request.getRequestDispatcher("chi_tiet_hoa_don.jsp").forward(request, response);
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
            request.getRequestDispatcher("main_ui.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        HoaDonDAO dao = new HoaDonDAO();
        so_do_banDAO banDao = new so_do_banDAO();

        if ("thanh_toan".equals(action)) {
            int orderId = Integer.parseInt(request.getParameter("orderId"));
            int tableId = Integer.parseInt(request.getParameter("tableId"));
            dao.thanhToanHoaDon(orderId, tableId);
            response.getWriter().write("success");
            return;
        }
        // [UC-05] MAIN FLOW: Tạo Order
        else if ("create".equals(action)) {
            // ĐIỀU KIỆN 1: PHÂN QUYỀN (ADMIN BỊ CHẶN - STAFF THÌ ĐƯỢC CHẠY TIẾP)
            jakarta.servlet.http.HttpSession session = request.getSession();
            String role = (String) session.getAttribute("role");

            // Nếu người dùng là Admin hoặc Manager -> Hệ thống chặn lại lập tức
            if (role != null && (role.equalsIgnoreCase("Admin"))) {
                response.sendRedirect("HoaDon?error=unauthorized");
                return; // Gặp lệnh này Admin sẽ bị dừng luồng và đẩy ra ngoài ngay lập tức
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