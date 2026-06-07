package test;

import DAO.UserDAO;
import Model.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
    ============================================================
    DEVELOPMENT TESTING - UC-04: QUẢN LÝ NHÂN VIÊN
    ============================================================

    - Các test case:
        DT-09: Thêm nhân viên hợp lệ
        DT-10: Tìm kiếm/lọc nhân viên
        DT-11: Cập nhật nhân viên
        DT-12: Xóa nhân viên

    Thành phần source code được kiểm thử:
    - DAO.UserDAO
    - Model.User
    - Bảng users trong MySQL

    Lưu ý quan trọng:
    - Đây là Development Test có kết nối database thật.
    - Cần bật MySQL và có database coffee_shop_db trước khi chạy.
    - Test dùng username bắt đầu bằng "junit_" để tách biệt dữ liệu thật.
    - Mỗi test đều tự dọn dữ liệu sau khi chạy để không ảnh hưởng hệ thống.
*/
class UserDAOTest {

    /*
        Helper tìm user theo username chính xác.

        Lý do:
        - UserDAO.searchAndFilterUsers(keyword, role) dùng LIKE.
        - Nếu chỉ truyền keyword, có thể trả về nhiều username gần giống.
        - Helper này lọc lại đúng username để phục vụ kiểm tra và cleanup.
    */
    private User findExactUserByUsername(UserDAO userDAO, String username) {
        return userDAO.searchAndFilterUsers(username, null)
                .stream()
                .filter(user -> username.equals(user.getUsername()))
                .findFirst()
                .orElse(null);
    }

    /*
        Helper xóa user test nếu đã tồn tại.

        Mục đích:
        - Tránh lỗi trùng username khi chạy test nhiều lần.
        - Bảo đảm test có tính lặp lại.
        - Phù hợp với ý nghĩa Regression Testing trong Chương 8.
    */
    private void deleteUserIfExists(UserDAO userDAO, String username) {
        User oldUser = findExactUserByUsername(userDAO, username);

        if (oldUser != null) {
            userDAO.deleteUser(oldUser.getUserId());
        }
    }

    /*
        Helper tạo đối tượng User test.

        Mục đích:
        - Tránh lặp code ở nhiều test case.
        - Bảo đảm dữ liệu test thống nhất:
          username, password, role.
    */
    private User createTestUser(String username, String password, String role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        return user;
    }

    /*
        ============================================================
        DT-09 - UC-04 Alternative Flow [4.2.0 - 4.2.7]
        ============================================================

        Mô tả trong document:
        [4.2.0] Quản lý chọn chức năng thêm nhân viên.
        [4.2.1] Giao diện hiển thị form nhập username/password/role.
        [4.2.2] Quản lý nhập thông tin và nhấn Lưu.
        [4.2.3] Giao diện gửi action add.
        [4.2.4] Servlet kiểm tra dữ liệu đầu vào.
        [4.2.5] Servlet gọi UserDAO.addUser(user).
        [4.2.6] Hệ thống cập nhật và hiển thị lại danh sách.
        [4.2.7] Nhân viên mới được thêm thành công.

        Input/Điều kiện:
        - Username mới, chưa tồn tại.
        - Password hợp lệ.
        - Role = Staff.

        Expected Result:
        - UserDAO.addUser(user) trả về true.
        - User được thêm vào bảng users.
        - Tìm lại username sẽ có dữ liệu.
    */
    @Test
    void DT09_addUserValidData_shouldInsertEmployeeSuccessfully() {
        UserDAO userDAO = new UserDAO();
        String username = "junit_add_" + System.nanoTime();

        deleteUserIfExists(userDAO, username);

        User user = createTestUser(username, "123", "Staff");

        try {
            boolean added = userDAO.addUser(user);

            assertTrue(added, "Thêm nhân viên phải thành công.");

            User insertedUser = findExactUserByUsername(userDAO, username);

            assertNotNull(insertedUser, "Sau khi thêm, user phải tồn tại trong bảng users.");
            assertEquals(username, insertedUser.getUsername());
            assertEquals("Staff", insertedUser.getRole());
        } finally {
            deleteUserIfExists(userDAO, username);
        }
    }

    /*
        ============================================================
        DT-10 - UC-04 Alternative Flow [4.7.0 - 4.9.4]
        ============================================================

        Mô tả trong document:
        [4.7.0] Quản lý nhập từ khóa tìm kiếm username.
        [4.7.2] Servlet gọi UserDAO.searchAndFilterUsers(keyword, role).
        [4.7.3] UserDAO truy vấn bảng users với username LIKE keyword.
        [4.7.5] Hệ thống hiển thị danh sách nhân viên phù hợp.

        Đồng thời kiểm tra:
        [4.8.0 - 4.8.6] Lọc nhân viên theo vai trò.
        [4.9.0 - 4.9.4] Không tìm thấy nhân viên phù hợp thì danh sách rỗng.

        Input/Điều kiện:
        - Tạo user test role Staff.
        - Tìm kiếm bằng keyword trong username.
        - Lọc theo role Staff.
        - Tìm kiếm keyword không tồn tại.

        Expected Result:
        - Danh sách tìm kiếm chứa user test.
        - Danh sách lọc Staff chỉ chứa role Staff.
        - Keyword không tồn tại trả về danh sách rỗng.
    */
    @Test
    void DT10_searchAndFilterUsers_shouldReturnCorrectEmployeeList() {
        UserDAO userDAO = new UserDAO();
        String username = "junit_search_" + System.nanoTime();

        deleteUserIfExists(userDAO, username);

        User user = createTestUser(username, "123", "Staff");

        try {
            assertTrue(userDAO.addUser(user), "Cần thêm user test trước khi tìm kiếm/lọc.");

            List<User> searchResult = userDAO.searchAndFilterUsers(username, null);

            boolean containsInsertedUser = searchResult.stream()
                    .anyMatch(u -> username.equals(u.getUsername()));

            assertTrue(
                    containsInsertedUser,
                    "Kết quả tìm kiếm phải chứa username vừa thêm."
            );

            List<User> filterResult = userDAO.searchAndFilterUsers(null, "Staff");

            assertNotNull(filterResult, "Danh sách lọc theo role không được null.");

            for (User staff : filterResult) {
                assertEquals(
                        "Staff",
                        staff.getRole(),
                        "Khi lọc role = Staff, mọi user trả về đều phải có role Staff."
                );
            }

            String notExistKeyword = "not_exist_keyword_" + System.nanoTime();
            List<User> emptyResult = userDAO.searchAndFilterUsers(notExistKeyword, null);

            assertTrue(
                    emptyResult.isEmpty(),
                    "Keyword không tồn tại phải trả về danh sách rỗng."
            );
        } finally {
            deleteUserIfExists(userDAO, username);
        }
    }

    /*
        ============================================================
        DT-11 - UC-04 Alternative Flow [4.3.0 - 4.3.7]
        ============================================================

        Mô tả trong document:
        [4.3.0] Quản lý chọn nhân viên cần sửa.
        [4.3.1] Giao diện hiển thị thông tin hiện tại.
        [4.3.2] Quản lý chỉnh sửa và nhấn Cập nhật.
        [4.3.3] Giao diện gửi action update.
        [4.3.4] Servlet kiểm tra dữ liệu cập nhật.
        [4.3.5] Servlet gọi UserDAO.updateUser(user).
        [4.3.6] Hệ thống cập nhật và hiển thị lại danh sách.
        [4.3.7] Thông tin nhân viên được cập nhật thành công.

        Input/Điều kiện:
        - Tạo user test role Staff.
        - Cập nhật username.
        - Cập nhật role sang Cashier.
        - Để password rỗng.

        Expected Result:
        - updateUser(user) trả về true.
        - Username mới tồn tại.
        - Role được đổi sang Cashier.
        - Password cũ vẫn còn dùng được do update password rỗng.

        Lý do kiểm tra password rỗng:
        - Trong source UserDAO.updateUser(user), nếu password rỗng
          thì chỉ update username và role.
        - Điều này khớp với form cập nhật nhân viên trong UC-04.
    */
    @Test
    void DT11_updateUserValidData_shouldUpdateEmployeeSuccessfully() {
        UserDAO userDAO = new UserDAO();

        String oldUsername = "junit_update_old_" + System.nanoTime();
        String newUsername = "junit_update_new_" + System.nanoTime();

        deleteUserIfExists(userDAO, oldUsername);
        deleteUserIfExists(userDAO, newUsername);

        User user = createTestUser(oldUsername, "123", "Staff");

        try {
            assertTrue(userDAO.addUser(user), "Cần thêm user test trước khi cập nhật.");

            User insertedUser = findExactUserByUsername(userDAO, oldUsername);
            assertNotNull(insertedUser, "User test phải tồn tại trước khi cập nhật.");

            insertedUser.setUsername(newUsername);
            insertedUser.setPassword("");
            insertedUser.setRole("Cashier");

            boolean updated = userDAO.updateUser(insertedUser);

            assertTrue(updated, "Cập nhật nhân viên phải thành công.");

            User updatedUser = findExactUserByUsername(userDAO, newUsername);

            assertNotNull(updatedUser, "Sau khi cập nhật, username mới phải tồn tại.");
            assertEquals(newUsername, updatedUser.getUsername());
            assertEquals("Cashier", updatedUser.getRole());

            User loginWithOldPassword = userDAO.findByUsernameAndPassword(newUsername, "123");

            assertNotNull(
                    loginWithOldPassword,
                    "Khi cập nhật với password rỗng, password cũ phải được giữ nguyên."
            );
        } finally {
            deleteUserIfExists(userDAO, oldUsername);
            deleteUserIfExists(userDAO, newUsername);
        }
    }

    /*
        ============================================================
        DT-12 - UC-04 Alternative Flow [4.4.0 - 4.4.6]
        ============================================================

        Mô tả trong document:
        [4.4.0] Quản lý chọn nhân viên cần xóa.
        [4.4.1] Hệ thống yêu cầu xác nhận.
        [4.4.2] Quản lý xác nhận xóa.
        [4.4.3] Giao diện gửi action delete.
        [4.4.4] Servlet gọi UserDAO.deleteUser(userId).
        [4.4.5] Hệ thống cập nhật lại danh sách nhân viên.
        [4.4.6] Nhân viên được xóa thành công.

        Input/Điều kiện:
        - Tạo user test.
        - Lấy userId của user test.
        - Gọi UserDAO.deleteUser(userId).

        Expected Result:
        - deleteUser(userId) trả về true.
        - Tìm lại username sau khi xóa trả về null.
    */
    @Test
    void DT12_deleteUserValidUserId_shouldDeleteEmployeeSuccessfully() {
        UserDAO userDAO = new UserDAO();
        String username = "junit_delete_" + System.nanoTime();

        deleteUserIfExists(userDAO, username);

        User user = createTestUser(username, "123", "Staff");

        assertTrue(userDAO.addUser(user), "Cần thêm user test trước khi xóa.");

        User insertedUser = findExactUserByUsername(userDAO, username);
        assertNotNull(insertedUser, "User test phải tồn tại trước khi xóa.");

        boolean deleted = userDAO.deleteUser(insertedUser.getUserId());

        assertTrue(deleted, "Xóa nhân viên phải thành công.");

        User deletedUser = findExactUserByUsername(userDAO, username);

        assertNull(deletedUser, "Sau khi xóa, không được tìm thấy username trong bảng users.");
    }
}