package Controller;

import DAO.HoaDonDAO;
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        HoaDonDAO dao = new HoaDonDAO();

        // LUỒNG 1: XEM CHI TIẾT 1 HÓA ĐƠN
        if ("detail".equals(action)) {
            String idParam = request.getParameter("id");
            if (idParam != null) {
                int orderId = Integer.parseInt(idParam);

                // Gọi DAO để tìm hóa đơn và các món ăn trong hóa đơn đó từ Database
                Order order = dao.getOrderById(orderId);
                request.setAttribute("order", order);
            }
            request.getRequestDispatcher("chi_tiet_hoa_don.jsp").forward(request, response);
        }

        // LUỒNG 2: XEM TẤT CẢ DANH SÁCH (Mặc định)
        else {
            List<Order> listHoaDon = dao.getAllOrders();
            List<Model.MenuItem> listMenu = dao.getAllMenuItems();

            // 1. THÊM DÒNG NÀY: Gọi DAO lấy danh sách Bàn từ Database
            List<Model.CoffeeTable> listTable = dao.getAllTables();

            request.setAttribute("listHoaDon", listHoaDon);
            request.setAttribute("listMenu", listMenu);

            // 2. THÊM DÒNG NÀY: Gửi danh sách Bàn qua JSP
            request.setAttribute("listTable", listTable);

            request.setAttribute("page_content", "hoa_don.jsp");
            request.setAttribute("active_tab", "hoadon");
            request.getRequestDispatcher("main_ui.jsp").forward(request, response);
        }
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("create".equals(action)) {
            String tableIdParam = request.getParameter("tableId");
            int tableId = (tableIdParam != null && !tableIdParam.isEmpty()) ? Integer.parseInt(tableIdParam) : 0;

            HoaDonDAO dao = new HoaDonDAO();
            // 1. Tạo hóa đơn rỗng trước để lấy mã ID
            int newOrderId = dao.createOrderAndReturnId(tableId);

            // 2. Quét xem nhân viên đã Tick chọn những món nào trên màn hình để lưu vào DB
            if (newOrderId > 0) {
                String[] selectedItems = request.getParameterValues("itemIds"); // Lấy các ô checkbox
                if (selectedItems != null) {
                    for (String itemIdStr : selectedItems) {
                        int itemId = Integer.parseInt(itemIdStr);
                        // Lấy số lượng tương ứng với món ăn đó
                        String qtyStr = request.getParameter("qty_" + itemId);
                        int quantity = (qtyStr != null && !qtyStr.isEmpty()) ? Integer.parseInt(qtyStr) : 1;

                        // Lưu món ăn vào Database
                        dao.addOrderDetail(newOrderId, itemId, quantity);
                    }
                }
            }
            response.sendRedirect("HoaDon");
        }
    }
}