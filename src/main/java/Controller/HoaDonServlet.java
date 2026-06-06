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
            response.sendRedirect("HoaDon");
        }
        else if ("request_payment".equals(action)) {
            int tableId = Integer.parseInt(request.getParameter("tableId"));
            dao.updateStatusByTable(tableId, "Đang phục vụ", "Chưa thanh toán");
            banDao.updateCoffeeTableStatus(tableId, "Chưa thanh toán");
            response.sendRedirect("TrangChu");
        }
        else if ("confirm_payment".equals(action)) {
            int tableId = Integer.parseInt(request.getParameter("tableId"));
            dao.updateStatusByTable(tableId, "Chưa thanh toán", "Đã thanh toán");
            banDao.updateCoffeeTableStatus(tableId, "Trống");
            response.sendRedirect("TrangChu");
        }
        else if ("createOrder".equals(action) || "create".equals(action)) {
            String tableIdParam = request.getParameter("tableId");
            int tableId = (tableIdParam != null && !tableIdParam.isEmpty()) ? Integer.parseInt(tableIdParam) : 0;

            int newOrderId = dao.createOrderAndReturnId(tableId);

            if (newOrderId > 0) {
                if (tableId > 0) {
                    banDao.updateCoffeeTableStatus(tableId, "Đang phục vụ");
                }

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
            response.sendRedirect("HoaDon?action=detail&id=" + orderId);
        }
        else if ("removeMenuItem".equals(action)) {
            int orderId = Integer.parseInt(request.getParameter("orderId"));
            int itemId = Integer.parseInt(request.getParameter("itemId"));

            dao.removeOrderDetail(orderId, itemId);
            response.sendRedirect("HoaDon?action=detail&id=" + orderId);
        }
    }
}