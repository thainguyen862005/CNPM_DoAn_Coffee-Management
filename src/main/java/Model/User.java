package Model;

import java.util.List;
import java.util.ArrayList;

public class User {

    private int userId;
    private String username;
    private String password;
    private String role;

    private static List<User> userList = new ArrayList<>();

    static {
        userList.add(new User(1, "admin", "123", "Manager"));
        userList.add(new User(2, "staff1", "123", "Staff"));
        userList.add(new User(3, "staff2", "123", "Staff"));
    }

    public User() {}

    public User(int userId, String username, String password, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // UC-01 : Đăng nhập
    public boolean login() {
        for (int i = 0; i < userList.size(); i++) {
            User u = userList.get(i);
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                this.userId = u.getUserId();
                this.role = u.getRole();
                return true;
            }
        }
        return false;
    }

    // UC-02 : Đăng xuất
    public void logout() {
        System.out.println("Đăng xuất thành công!");
        this.userId = 0;
        this.username = null;
        this.password = null;
        this.role = null;
    }

    // UC-03 : Phân quyền
    public boolean isManager() {
        return role != null && role.equalsIgnoreCase("Manager");
    }

    public boolean isStaff() {
        return role != null && role.equalsIgnoreCase("Staff");
    }

    // UC-04 : Xem danh sách nhân viên
    public List<User> getAllUsers() {
        return userList;
    }

    // UC-04 : Thêm nhân viên
    public boolean addUser(User user) {
        return userList.add(user);
    }

    // UC-04 : Cập nhật nhân viên
    public boolean updateUser(User user) {
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUserId() == user.getUserId()) {
                userList.set(i, user);
                return true;
            }
        }
        return false;
    }

    // UC-04 : Xóa nhân viên (ĐÃ SỬA LỖI VÒNG LẶP)
    public boolean deleteUser(int userId) {
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUserId() == userId) {
                userList.remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "ID: " + userId + " | Username: " + username + " | Role: " + role;
    }
}