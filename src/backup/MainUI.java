package Model;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MainUI extends JFrame {

    private User currentUser = new User();

    // Dữ liệu giả lập chạy Runtime
    private List<CoffeeTable> danhSachBan = new ArrayList<>();
    private List<Order> danhSachOrder = new ArrayList<>();
    private List<OrderDetail> currentCart = new ArrayList<>(); // Giỏ hàng tạm

    private JTabbedPane tabbedPane;

    public MainUI() {
        // UC-05: Khởi tạo dữ liệu bàn
        danhSachBan.add(new CoffeeTable(1, "Bàn 1", "Tầng 1", "Trống"));
        danhSachBan.add(new CoffeeTable(2, "Bàn 2", "Tầng 1", "Trống"));
        danhSachBan.add(new CoffeeTable(3, "Bàn 3", "Tầng 2", "Trống"));

        danhSachBan.add(new CoffeeTable(4, "Bàn 4", "Tầng 2", "Trống"));
        danhSachBan.add(new CoffeeTable(5, "Bàn 5", "Sân vườn", "Trống"));
        danhSachBan.add(new CoffeeTable(6, "Bàn 6", "Sân vườn", "Đã đặt"));
        danhSachBan.add(new CoffeeTable(7, "Bàn 7", "Phòng lạnh", "Trống"));
        danhSachBan.add(new CoffeeTable(8, "Bàn 8", "Phòng lạnh", "Trống"));

        // BƠM DỮ LIỆU GIẢ CHO TAB BÁO CÁO: Tạo 1 hóa đơn đã thanh toán từ trước
        Order fakeOrder = new Order(new CoffeeTable(99, "Bàn Khách Lẻ", "Mang đi", "Trống"));
        fakeOrder.addItem(new MenuItem(1, "Cà phê sữa", 25000, "", "Còn bán"), 2); // Bán 2 ly = 50k
        fakeOrder.updateStatus("Đã thanh toán");
        danhSachOrder.add(fakeOrder);

        setTitle("Hệ Thống Quản Lý Quán Cà Phê - Nhóm 7");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        hienThiDangNhap(); // UC-01
    }

    // =====================================================
    // UC-01: Đăng nhập
    // =====================================================
    private void hienThiDangNhap() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        JTextField txtUser = new JTextField();
        JPasswordField txtPass = new JPasswordField();

        panel.add(new JLabel("Tài khoản (admin/staff1):"));
        panel.add(txtUser);
        panel.add(new JLabel("Mật khẩu (123):"));
        panel.add(txtPass);

        int result = JOptionPane.showConfirmDialog(null, panel, "Đăng nhập hệ thống", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            currentUser.setUsername(txtUser.getText());
            currentUser.setPassword(new String(txtPass.getPassword()));

            if (currentUser.login()) {
                JOptionPane.showMessageDialog(null, "Đăng nhập thành công! Chào " + currentUser.getRole());
                khoiTaoGiaoDienChinh();
            } else {
                JOptionPane.showMessageDialog(null, "Sai tài khoản hoặc mật khẩu!");
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
    }

    // =====================================================
    // UC-03: Phân quyền & Khởi tạo giao diện
    // =====================================================
    private void khoiTaoGiaoDienChinh() {
        tabbedPane = new JTabbedPane();

        // Manager thấy hết, Staff chỉ thấy Phục Vụ và Thanh Toán
        if (currentUser.isManager()) {
            tabbedPane.addTab("Hệ Thống", taoTabHeThong());
            tabbedPane.addTab("Quản Lý Bàn", taoTabQuanLyBan());
            tabbedPane.addTab("Thực Đơn", taoTabThucDon());
            tabbedPane.addTab("Báo Cáo", taoTabBaoCao());
        }

        tabbedPane.addTab("Phục Vụ (Model.Order)", taoTabPhucVu());
        tabbedPane.addTab("Thanh Toán", taoTabThanhToan());

        // Refresh dữ liệu mỗi khi chuyển Tab
        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            String title = tabbedPane.getTitleAt(index);
            if(title.equals("Phục Vụ (Model.Order)")) {
                tabbedPane.setComponentAt(index, taoTabPhucVu());
            } else if (title.equals("Thanh Toán")) {
                tabbedPane.setComponentAt(index, taoTabThanhToan());
            } else if (title.equals("Quản Lý Bàn")) {
                tabbedPane.setComponentAt(index, taoTabQuanLyBan());
            } else if (title.equals("Thực Đơn")) {
                tabbedPane.setComponentAt(index, taoTabThucDon());
            }
        });

        add(tabbedPane, BorderLayout.CENTER);

        // UC-02: Đăng xuất
        JButton btnLogout = new JButton("Đăng Xuất");
        btnLogout.setBackground(Color.RED);
        btnLogout.setForeground(Color.WHITE);
        btnLogout.addActionListener(e -> {
            currentUser.logout();
            dispose();
            new MainUI().setVisible(true);
        });
        add(btnLogout, BorderLayout.SOUTH);

        setVisible(true);
    }

    // =====================================================
    // UC-04: TAB HỆ THỐNG (Quản lý nhân viên)
    // =====================================================
    private JPanel taoTabHeThong() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Tên đăng nhập", "Quyền"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        Runnable loadData = () -> {
            model.setRowCount(0);
            for (User u : new User().getAllUsers()) {
                model.addRow(new Object[]{u.getUserId(), u.getUsername(), u.getRole()});
            }
        };
        loadData.run();

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel pnlBot = new JPanel();
        JButton btnAdd = new JButton("Thêm NV");
        JButton btnEdit = new JButton("Sửa NV");
        JButton btnDel = new JButton("Xóa NV");

        btnAdd.addActionListener(e -> {
            JTextField txtId = new JTextField();
            JTextField txtUser = new JTextField();
            JPasswordField txtPass = new JPasswordField(); // Yêu cầu nhập pass
            JComboBox<String> cbRole = new JComboBox<>(new String[]{"Staff", "Manager"});

            Object[] fields = { "ID:", txtId, "Username:", txtUser, "Password:", txtPass, "Role:", cbRole };
            if (JOptionPane.showConfirmDialog(null, fields, "Thêm NV", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    currentUser.addUser(new User(Integer.parseInt(txtId.getText()), txtUser.getText(), new String(txtPass.getPassword()), cbRole.getSelectedItem().toString()));
                    loadData.run();
                    JOptionPane.showMessageDialog(null, "Thêm nhân viên thành công!");
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(null, "Lỗi: ID phải là số!");
                }
            }
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = (int) table.getValueAt(row, 0);
                JTextField txtUser = new JTextField((String) table.getValueAt(row, 1));
                JPasswordField txtPass = new JPasswordField("123");
                JComboBox<String> cbRole = new JComboBox<>(new String[]{"Staff", "Manager"});
                cbRole.setSelectedItem(table.getValueAt(row, 2));

                Object[] fields = { "Username:", txtUser, "Password:", txtPass, "Role:", cbRole };
                if (JOptionPane.showConfirmDialog(null, fields, "Sửa NV", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                    currentUser.updateUser(new User(id, txtUser.getText(), new String(txtPass.getPassword()), cbRole.getSelectedItem().toString()));
                    loadData.run();
                    JOptionPane.showMessageDialog(null, "Cập nhật thành công!");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn 1 nhân viên trong bảng để sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnDel.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa nhân viên này không?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    currentUser.deleteUser((int) table.getValueAt(row, 0));
                    loadData.run();
                    JOptionPane.showMessageDialog(null, "Đã xóa nhân viên!");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn 1 nhân viên trong bảng để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        });

        pnlBot.add(btnAdd); pnlBot.add(btnEdit); pnlBot.add(btnDel);
        panel.add(pnlBot, BorderLayout.SOUTH);
        return panel;
    }

    // =====================================================
    // UC-05 & 06: TAB QUẢN LÝ BÀN
    // =====================================================
    private JPanel taoTabQuanLyBan() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Tên Bàn", "Khu Vực", "Trạng Thái (UC-06)"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        Runnable loadData = () -> {
            model.setRowCount(0);
            for (CoffeeTable t : danhSachBan) {
                model.addRow(new Object[]{t.getTableId(), t.getTableName(), t.getArea(), t.getStatus()});
            }
        };
        loadData.run();

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel pnlBot = new JPanel();
        JButton btnAdd = new JButton("Thêm Bàn Mới");
        JButton btnEditStatus = new JButton("Sửa Trạng Thái");

        btnAdd.addActionListener(e -> {
            danhSachBan.add(new CoffeeTable(danhSachBan.size() + 1, "Bàn " + (danhSachBan.size() + 1), "Khu A", "Trống"));
            loadData.run();
            JOptionPane.showMessageDialog(null, "Thêm bàn thành công!");
        });

        btnEditStatus.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row != -1) {
                int id = (int) table.getValueAt(row, 0);
                CoffeeTable currentTable = danhSachBan.stream().filter(t -> t.getTableId() == id).findFirst().orElse(null);
                if(currentTable != null) {
                    JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Trống", "Đang phục vụ", "Chờ thanh toán", "Đã đặt"});
                    cbStatus.setSelectedItem(currentTable.getStatus());
                    if(JOptionPane.showConfirmDialog(null, cbStatus, "Chọn trạng thái", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                        currentTable.updateStatus(cbStatus.getSelectedItem().toString());
                        loadData.run();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn 1 bàn để sửa trạng thái!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        });

        pnlBot.add(btnAdd);
        pnlBot.add(btnEditStatus);
        panel.add(pnlBot, BorderLayout.SOUTH);
        return panel;
    }

    // =====================================================
    // UC-07: TAB THỰC ĐƠN
    // =====================================================
    private JPanel taoTabThucDon() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Tên Món", "Giá", "Trạng thái"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        MenuItem mTemp = new MenuItem();
        Runnable loadData = () -> {
            model.setRowCount(0);
            for (MenuItem m : mTemp.getAllMenuItems()) {
                model.addRow(new Object[]{m.getItemId(), m.getItemName(), m.getPrice(), m.getStatus()});
            }
        };
        loadData.run();

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel pnlBot = new JPanel();
        JButton btnAdd = new JButton("Thêm Món");
        JButton btnEdit = new JButton("Đổi Giá");
        JButton btnDel = new JButton("Ngừng Bán");

        btnAdd.addActionListener(e -> {
            JTextField txtId = new JTextField(); JTextField txtName = new JTextField(); JTextField txtPrice = new JTextField();
            Object[] fields = { "ID:", txtId, "Tên:", txtName, "Giá:", txtPrice };
            if (JOptionPane.showConfirmDialog(null, fields, "Thêm Món", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                mTemp.addMenuItem(new MenuItem(Integer.parseInt(txtId.getText()), txtName.getText(), Double.parseDouble(txtPrice.getText()), "", "Còn bán"));
                loadData.run();
            }
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row != -1) {
                int id = (int) table.getValueAt(row, 0);
                String oldPrice = String.valueOf(table.getValueAt(row, 2));
                String newPriceStr = JOptionPane.showInputDialog(null, "Nhập giá bán mới:", oldPrice);
                if(newPriceStr != null && !newPriceStr.trim().isEmpty()) {
                    try {
                        double newPrice = Double.parseDouble(newPriceStr);
                        for(MenuItem m : mTemp.getAllMenuItems()) {
                            if(m.getItemId() == id) {
                                m.setPrice(newPrice);
                                mTemp.updateMenuItem(m);
                                break;
                            }
                        }
                        loadData.run();
                        JOptionPane.showMessageDialog(null, "Đổi giá thành công!");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Giá tiền không hợp lệ!");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn 1 món để đổi giá!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnDel.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                mTemp.deleteMenuItem((int) table.getValueAt(row, 0));
                loadData.run();
            } else {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn 1 món để ngừng bán!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        });

        pnlBot.add(btnAdd); pnlBot.add(btnEdit); pnlBot.add(btnDel);
        panel.add(pnlBot, BorderLayout.SOUTH);
        return panel;
    }

    // =====================================================
    // UC-08, 09, 10, 16, 17: TAB PHỤC VỤ
    // =====================================================
    private JPanel taoTabPhucVu() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel pnlTop = new JPanel(new GridLayout(2, 2, 5, 5));
        JComboBox<String> cbTables = new JComboBox<>(); // UC-16
        for (CoffeeTable t : danhSachBan) cbTables.addItem(t.getTableName() + " (" + t.getStatus() + ")");

        JComboBox<String> cbMenu = new JComboBox<>(); // UC-17
        MenuItem mTemp = new MenuItem();
        List<MenuItem> monDangBan = new ArrayList<>();

        for (MenuItem m : mTemp.getAllMenuItems()) {
            if (m.isAvailable()) {
                monDangBan.add(m);
                cbMenu.addItem(m.getItemName() + " - " + m.getPrice());
            }
        }

        pnlTop.add(new JLabel("Chọn Bàn (UC-16):")); pnlTop.add(cbTables);
        pnlTop.add(new JLabel("Chọn Món (UC-17):")); pnlTop.add(cbMenu);
        panel.add(pnlTop, BorderLayout.NORTH);

        JTextArea txtCart = new JTextArea("Giỏ hàng chờ chốt...\n");
        panel.add(new JScrollPane(txtCart), BorderLayout.CENTER);

        JPanel pnlBot = new JPanel();
        JButton btnAddCart = new JButton("Thêm vào giỏ");
        JButton btnViewOrder = new JButton("Xem giỏ hiện tại (UC-10)");
        JButton btnChot = new JButton("Chốt Model.Order (UC-08 & 09)");

        cbTables.addActionListener(e -> {
            currentCart.clear();
            txtCart.setText("Đã đổi bàn, giỏ hàng tạm đã được làm mới.\n");
        });

        btnAddCart.addActionListener(e -> {
            if (monDangBan.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Không có món nào đang được bán!");
                return;
            }

            if (cbMenu.getSelectedIndex() < 0) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn món!");
                return;
            }

            MenuItem selectedMenu = monDangBan.get(cbMenu.getSelectedIndex());
            themVaoGio(selectedMenu, 1);

            txtCart.setText("=== GIỎ HÀNG TẠM ===\n");
            for (OrderDetail d : currentCart) {
                txtCart.append(d.getMenuItem().getItemName()
                        + " x" + d.getQuantity()
                        + " = " + d.calculateSubtotal() + " VNĐ\n");
            }
        });

        btnViewOrder.addActionListener(e -> {
            CoffeeTable selectedTable = danhSachBan.get(cbTables.getSelectedIndex());
            Order currentO = timOrderTheoBan(selectedTable);
            if(currentO != null) {
                txtCart.setText("=== ĐÃ GỌI (UC-10) ===\n");
                for(OrderDetail d : currentO.getOrderDetails()) txtCart.append(d.getMenuItem().getItemName() + " x" + d.getQuantity() + "\n");
            } else {
                txtCart.setText("Bàn này chưa gọi món nào.\n");
            }
        });

        btnChot.addActionListener(e -> {
            if (cbTables.getSelectedIndex() < 0) {
                JOptionPane.showMessageDialog(null, "Chưa có bàn nào để chọn!");
                return;
            }

            if (currentCart.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Chưa chọn món!");
                return;
            }

            CoffeeTable selectedTable = danhSachBan.get(cbTables.getSelectedIndex());
            boolean success = false;

            if (selectedTable.getStatus().equals("Trống")) {
                Order newOrder = Order.createOrder(currentUser, selectedTable, currentCart);

                if (newOrder != null) {
                    danhSachOrder.add(newOrder);
                    JOptionPane.showMessageDialog(null, "Tạo Model.Order mới thành công!");
                    success = true;
                } else {
                    JOptionPane.showMessageDialog(null, "Tạo Model.Order thất bại!");
                }

            } else if (selectedTable.getStatus().equals("Đang phục vụ")) {
                Order currentO = timOrderTheoBan(selectedTable);

                if (currentO != null) {
                    for (OrderDetail d : currentCart) {
                        currentO.addItem(d.getMenuItem(), d.getQuantity());
                    }

                    JOptionPane.showMessageDialog(null, "Cập nhật thêm món thành công!");
                    success = true;
                } else {
                    JOptionPane.showMessageDialog(null, "Không tìm thấy order hiện tại của bàn này!");
                }

            } else {
                JOptionPane.showMessageDialog(null, "Bàn này đã đặt hoặc chờ thanh toán, không thể chốt order!");
            }

            if (success) {
                currentCart.clear();
                txtCart.setText("Giỏ hàng trống.\n");
                tabbedPane.setComponentAt(tabbedPane.getSelectedIndex(), taoTabPhucVu());
            }
        });

        pnlBot.add(btnAddCart); pnlBot.add(btnViewOrder); pnlBot.add(btnChot);
        panel.add(pnlBot, BorderLayout.SOUTH);
        return panel;
    }

    // =====================================================
    // UC-11, 12, 18, 19, 20: TAB THANH TOÁN
    // =====================================================
    private JPanel taoTabThanhToan() {
        JPanel panel = new JPanel(new BorderLayout());

        JComboBox<String> cbTables = new JComboBox<>();
        for (CoffeeTable t : danhSachBan) {
            if(t.getStatus().equals("Đang phục vụ") || t.getStatus().equals("Chờ thanh toán")) {
                cbTables.addItem(t.getTableName());
            }
        }
        panel.add(cbTables, BorderLayout.NORTH);

        JTextArea txtBill = new JTextArea("Chọn bàn và nhấn Xem Chi Tiết...\n");
        panel.add(new JScrollPane(txtBill), BorderLayout.CENTER);

        JPanel pnlBot = new JPanel();
        JButton btnView = new JButton("Xem Chi Tiết (UC-11 & 18)");
        JButton btnPay = new JButton("Thanh Toán (UC-12)");

        btnView.addActionListener(e -> {
            if (cbTables.getItemCount() == 0) return;
            String tableName = cbTables.getSelectedItem().toString();
            CoffeeTable table = danhSachBan.stream().filter(t -> t.getTableName().equals(tableName)).findFirst().orElse(null);
            Order currentO = timOrderTheoBan(table);

            if (currentO != null) {
                txtBill.setText("=== HÓA ĐƠN " + tableName + " ===\n");
                for(OrderDetail d : currentO.getOrderDetails()) {
                    txtBill.append(d.getMenuItem().getItemName() + " | SL: " + d.getQuantity() + " | Tiền: " + d.calculateSubtotal() + "\n");
                }
                txtBill.append("\n--> TỔNG CỘNG (UC-18): " + currentO.calculateTotal() + " VNĐ\n");
            }
        });

        btnPay.addActionListener(e -> {
            if (cbTables.getItemCount() == 0) return;
            String tableName = cbTables.getSelectedItem().toString();
            CoffeeTable table = danhSachBan.stream().filter(t -> t.getTableName().equals(tableName)).findFirst().orElse(null);
            Order currentO = timOrderTheoBan(table);

            if (currentO != null) {
                currentO.updateStatus("Đã thanh toán"); // UC-20
                table.updateStatus("Trống"); // UC-19
                JOptionPane.showMessageDialog(null, "Thanh toán thành công! Bàn đã trống.");

                // 1. Làm mới lại tab Thanh Toán hiện tại
                tabbedPane.setComponentAt(tabbedPane.getSelectedIndex(), taoTabThanhToan());

                // 2. Tìm xem tab Quản Lý Bàn đang nằm ở đâu (nếu user có quyền) thì mới làm mới
                for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                    if (tabbedPane.getTitleAt(i).equals("Quản Lý Bàn")) {
                        tabbedPane.setComponentAt(i, taoTabQuanLyBan());
                        break;
                    }
                }
            }
        });

        pnlBot.add(btnView); pnlBot.add(btnPay);
        panel.add(pnlBot, BorderLayout.SOUTH);
        return panel;
    }

    // =====================================================
    // UC-13, 14, 15: TAB BÁO CÁO
    // =====================================================
    private JPanel taoTabBaoCao() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea txtReport = new JTextArea("Khu vực hiển thị thống kê...\n");
        panel.add(new JScrollPane(txtReport), BorderLayout.CENTER);

        JPanel pnlBot = new JPanel();
        JButton btnHistory = new JButton("Lịch sử (UC-13)");
        JButton btnRev = new JButton("Doanh Thu (UC-14)");
        JButton btnTop = new JButton("Món Bán Chạy (UC-15)");

        btnHistory.addActionListener(e -> {
            txtReport.setText("=== LỊCH SỬ GIAO DỊCH ===\n");
            for(Order o : danhSachOrder) {
                if(o.getStatus().equals("Đã thanh toán")) {
                    txtReport.append("Thời gian: " + (o.getCreatedAt() != null ? o.getCreatedAt() : "N/A") + " | Tổng thu: " + o.calculateTotal() + " VNĐ\n");
                }
            }
        });

        btnRev.addActionListener(e -> {
            double total = 0;
            for(Order o : danhSachOrder) if(o.getStatus().equals("Đã thanh toán")) total += o.calculateTotal();
            txtReport.setText("=== BÁO CÁO DOANH THU ===\nTổng doanh thu thực tế: " + total + " VNĐ");
        });

        btnTop.addActionListener(e -> {
            OrderDetail mockDetail = new OrderDetail();
            List<OrderDetail> topList = mockDetail.getBestSellingItems(LocalDateTime.now(), LocalDateTime.now());
            txtReport.setText("=== MÓN BÁN CHẠY NHẤT ===\n");
            for(OrderDetail d : topList) txtReport.append(d.getMenuItem().getItemName() + " | Đã bán: " + d.getQuantity() + " ly\n");
        });

        pnlBot.add(btnHistory); pnlBot.add(btnRev); pnlBot.add(btnTop);
        panel.add(pnlBot, BorderLayout.SOUTH);
        return panel;
    }

    private Order timOrderTheoBan(CoffeeTable table) {
        if (table == null) {
            return null;
        }

        for (Order o : danhSachOrder) {
            if (o.getTable() != null
                    && o.getTable().getTableId() == table.getTableId()
                    && (o.getStatus().equals("Đang phục vụ")
                    || o.getStatus().equals("Chờ thanh toán"))) {
                return o;
            }
        }

        return null;
    }

    private void themVaoGio(MenuItem item, int quantity) {
        if (item == null || quantity <= 0) {
            return;
        }

        for (OrderDetail detail : currentCart) {
            if (detail.getMenuItem() != null
                    && detail.getMenuItem().getItemId() == item.getItemId()) {
                detail.setQuantity(detail.getQuantity() + quantity);
                detail.calculateSubtotal();
                return;
            }
        }

        currentCart.add(new OrderDetail(item, quantity));
    }

    public static void main(String[] args) {
        new MainUI();
    }
}