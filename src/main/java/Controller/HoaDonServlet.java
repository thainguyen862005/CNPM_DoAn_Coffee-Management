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
        HoaDonDAO dao = new HoaDonDAO();
        // LUỒNG 1: TẠO HÓA ĐƠN MỚI
        if ("create".equals(action)) {
            String tableIdParam = request.getParameter("tableId");
            int tableId = (tableIdParam != null && !tableIdParam.isEmpty()) ? Integer.parseInt(tableIdParam) : 0;

            int newOrderId = dao.createOrderAndReturnId(tableId);

            if (newOrderId > 0) {
                // THÊM DÒNG NÀY: Chuyển trạng thái bàn trong MySQL thành Đang phục vụ
                dao.updateCoffeeTableStatus(tableId, "Đang phục vụ");
                String[] selectedItems = request.getParameterValues("itemIds");
                if (selectedItems != null) {
                    for (String itemIdStr : selectedItems) {
                        int itemId = Integer.parseInt(itemIdStr);
                        String qtyStr = request.getParameter("qty_" + itemId);
                        int quantity = (qtyStr != null && !qtyStr.isEmpty()) ? Integer.parseInt(qtyStr) : 1;
                        dao.addOrderDetail(newOrderId, itemId, quantity);
                    }
                }
            }
            response.sendRedirect("HoaDon");
        }

        // LUỒNG 2: NÚT "CHUYỂN THANH TOÁN"
        else if ("request_payment".equals(action)) {
            int tableId = Integer.parseInt(request.getParameter("tableId"));
            // 1. Cập nhật hóa đơn
            HoaDonDAO hoaDonDao = new HoaDonDAO();
            hoaDonDao.updateStatusByTable(tableId, "Đang phục vụ", "Chưa thanh toán");
            // 2. Cập nhật bàn
            so_do_banDAO banDao = new so_do_banDAO();
            banDao.updateCoffeeTableStatus(tableId, "Chưa thanh toán");

            response.sendRedirect("TrangChu");
        }

        // LUỒNG 3: NÚT "ĐÃ THU TIỀN" (POPUP XÁC NHẬN)
        else if ("confirm_payment".equals(action)) {
            int tableId = Integer.parseInt(request.getParameter("tableId"));
            // 1. Cập nhật hóa đơn
            HoaDonDAO hoaDonDao = new HoaDonDAO();
            hoaDonDao.updateStatusByTable(tableId, "Chưa thanh toán", "Đã thanh toán");
            // 2. Cập nhật bàn
            so_do_banDAO banDao = new so_do_banDAO();
            banDao.updateCoffeeTableStatus(tableId, "Trống");

            response.sendRedirect("TrangChu");
        }
    }
}