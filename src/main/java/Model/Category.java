package Model;

import java.util.List;
import java.util.ArrayList;

public class Category {
    private int categoryId;
    private String categoryName;
    private String description;

    private static List<Category> danhSachDanhMuc = new ArrayList<>();
    public Category() {}

    public Category(int categoryId, String categoryName, String description) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
    }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<MenuItem> getMenuItems() {
        // TODO: SQL lấy tất cả món ăn thuộc Danh mục này
        return new ArrayList<>();
    }

    public List<Category> getAllCategories() {
        return danhSachDanhMuc;
    }

    public boolean addCategory(Category cat) {
        danhSachDanhMuc.add(cat);
        return true;
    }

    public boolean updateCategory(Category cat) {
        for (int i = 0; i < danhSachDanhMuc.size(); i++) {
            if (danhSachDanhMuc.get(i).getCategoryId() == cat.getCategoryId()) {
                danhSachDanhMuc.set(i, cat);
                return true;
            }
        }
        return false;
    }

    public boolean deleteCategory(int categoryId) {
        for (int i = 0; i < danhSachDanhMuc.size(); i++) {
            if (danhSachDanhMuc.get(i).getCategoryId() == categoryId) {
                danhSachDanhMuc.remove(i);
                return true;
            }
        }
        return false;
    }
}