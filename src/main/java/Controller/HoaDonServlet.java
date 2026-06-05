package Controller;

import DAO.HoaDonDAO;
import DAO.MenuItemDAO;
import DAO.so_do_banDAO;
import Model.MenuItem;
import Model.Order;
import Model.OrderDetail;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/HoaDon")
public class HoaDonServlet extends HttpServlet {
    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private MenuItemDAO menuItemDAO = new MenuItemDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Cấu hình encoding để nhận tiếng Việt có dấu
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) action = "list";

        try {
            switch (action) {
                case "createOrder":
                    int tableId = Integer.parseInt(request.getParameter("tableId"));
                    String orderType = request.getParameter("orderType"); // DINE_IN hoặc TAKEAWAY

                    int newOrderId = hoaDonDAO.createOrderAndReturnId(tableId, orderType);
                    response.sendRedirect("chi_tiet_hoa_don.jsp?orderId=" + newOrderId);
                    break;

                case "addMenuItem":
                    int orderId = Integer.parseInt(request.getParameter("orderId"));
                    int itemId = Integer.parseInt(request.getParameter("itemId"));
                    int quantity = Integer.parseInt(request.getParameter("quantity"));
                    String note = request.getParameter("note"); // Ghi chú thêm

                    MenuItem menuItem = menuItemDAO.getItemById(itemId);
                    OrderDetail detail = new OrderDetail(menuItem, quantity);
                    detail.setNote(note);
                    detail.setStatus("PENDING");

                    hoaDonDAO.addOrUpdateOrderDetail(orderId, detail);
                    response.sendRedirect("chi_tiet_hoa_don.jsp?orderId=" + orderId);
                    break;

                case "removeMenuItem":
                    int detailId = Integer.parseInt(request.getParameter("detailId"));
                    int currentOrderId = Integer.parseInt(request.getParameter("orderId"));

                    boolean isRemoved = hoaDonDAO.removeOrderDetailSafe(detailId);

                    if (!isRemoved) {
                        request.setAttribute("errorMessage", "Không thể xóa món đã bắt đầu pha chế!");
                        // Dùng RequestDispatcher để đẩy thông báo lỗi về lại trang JSP hiện tại
                        request.getRequestDispatcher("chi_tiet_hoa_don.jsp?orderId=" + currentOrderId).forward(request, response);
                    } else {
                        response.sendRedirect("chi_tiet_hoa_don.jsp?orderId=" + currentOrderId);
                    }
                    break;

                default:
                    response.sendRedirect("hoa_don.jsp");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}