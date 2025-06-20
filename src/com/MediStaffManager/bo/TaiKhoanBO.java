package com.MediStaffManager.bo;
 
import com.MediStaffManager.dao.TaiKhoanDAO;
import com.MediStaffManager.bean.TaiKhoan;
 
import java.sql.SQLException;
import java.util.List;
import java.util.Arrays;
 
public class TaiKhoanBO {
    private TaiKhoanDAO taiKhoanDAO;
    private final List<String> VALID_ROLES = Arrays.asList("Kế toán", "Quản lý Nhân sự");
 
    public TaiKhoanBO() {
        this.taiKhoanDAO = new TaiKhoanDAO();
    }
 
    private void validateTaiKhoanData(TaiKhoan taiKhoan, boolean isUpdate, String matKhauNhapLai) {
        if (taiKhoan == null) {
            throw new IllegalArgumentException("Dữ liệu tài khoản không được để trống.");
        }
        if (taiKhoan.getTenDangNhap() == null || taiKhoan.getTenDangNhap().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống.");
        }
        if (taiKhoan.getTenDangNhap().trim().length() < 3 || taiKhoan.getTenDangNhap().trim().length() > 50) {
            throw new IllegalArgumentException("Tên đăng nhập phải từ 3 đến 50 ký tự.");
        }
        if (!taiKhoan.getTenDangNhap().matches("^[a-zA-Z0-9_]+$")) {
             throw new IllegalArgumentException("Tên đăng nhập chỉ được chứa chữ cái, số và dấu gạch dưới (_).");
        }
 
        if (taiKhoan.getMatKhau() == null || taiKhoan.getMatKhau().isEmpty()) {
            if (!isUpdate) {
                throw new IllegalArgumentException("Mật khẩu không được để trống.");
            }
        } else {
             if (taiKhoan.getMatKhau().length() < 6) {
                throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự.");
            }
             if (matKhauNhapLai != null && !taiKhoan.getMatKhau().equals(matKhauNhapLai)) {
                 throw new IllegalArgumentException("Mật khẩu và Nhập lại mật khẩu không khớp.");
             }
        }
 
        if (taiKhoan.getVaiTro() == null || taiKhoan.getVaiTro().trim().isEmpty()) {
            throw new IllegalArgumentException("Vai trò không được để trống.");
        }
        if (!VALID_ROLES.contains(taiKhoan.getVaiTro())) {
            throw new IllegalArgumentException("Vai trò không hợp lệ. Chỉ chấp nhận: " + String.join(", ", VALID_ROLES));
        }
 
        if (!isUpdate) { // Only check for duplicates on add
            try {
                if (taiKhoanDAO.checkTenDangNhapExists(taiKhoan.getTenDangNhap())) {
                    throw new IllegalArgumentException("Tên đăng nhập '" + taiKhoan.getTenDangNhap() + "' đã tồn tại.");
                }
            } catch (SQLException e) {
                throw new RuntimeException("Lỗi khi kiểm tra tên đăng nhập: " + e.getMessage(), e);
            }
        }
    }
 
    public List<TaiKhoan> getAllTaiKhoan() {
        try {
            return taiKhoanDAO.getAllTaiKhoan();
        } catch (SQLException e) {
            System.err.println("Error in TaiKhoanBO (getAllTaiKhoan): " + e.getMessage());
            throw new RuntimeException("Không thể tải danh sách tài khoản: " + e.getMessage(), e);
        }
    }
    
    public TaiKhoan getTaiKhoanByTenDangNhap(String tenDangNhap) {
        try {
            return taiKhoanDAO.getTaiKhoanByTenDangNhap(tenDangNhap);
        } catch (SQLException e) {
            System.err.println("Error in TaiKhoanBO (getTaiKhoanByTenDangNhap): " + e.getMessage());
            throw new RuntimeException("Không thể tải tài khoản: " + e.getMessage(), e);
        }
    }
 
 
    public void addTaiKhoan(TaiKhoan taiKhoan, String matKhauNhapLai) {
        try {
            validateTaiKhoanData(taiKhoan, false, matKhauNhapLai);
            // In a real app, hash taiKhoan.getMatKhau() here before calling DAO
            taiKhoanDAO.addTaiKhoan(taiKhoan);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (SQLException e) {
            System.err.println("Error in TaiKhoanBO (addTaiKhoan): " + e.getMessage());
             if (e.getMessage() != null && e.getMessage().contains("đã tồn tại")) {
                throw new IllegalArgumentException(e.getMessage());
            }
            throw new RuntimeException("Lỗi khi thêm tài khoản: " + e.getMessage(), e);
        }
    }
 
    public void updateTaiKhoan(TaiKhoan taiKhoan, String matKhauNhapLai, boolean passwordChanged) {
        try {
            if (taiKhoan.getTenDangNhap() == null || taiKhoan.getTenDangNhap().trim().isEmpty()) {
                 throw new IllegalArgumentException("Tên đăng nhập không hợp lệ để cập nhật.");
            }
 
            TaiKhoan existingTaiKhoan = taiKhoanDAO.getTaiKhoanByTenDangNhap(taiKhoan.getTenDangNhap());
            if (existingTaiKhoan == null) {
                throw new IllegalArgumentException("Không tìm thấy tài khoản để cập nhật.");
            }
 
            if (!passwordChanged) {
                taiKhoan.setMatKhau(existingTaiKhoan.getMatKhau()); // Keep old password
                matKhauNhapLai = existingTaiKhoan.getMatKhau(); // To pass validation if not changed
            }
 
            validateTaiKhoanData(taiKhoan, true, matKhauNhapLai);
            
            // In a real app, if passwordChanged, hash taiKhoan.getMatKhau() here
            taiKhoanDAO.updateTaiKhoan(taiKhoan);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (SQLException e) {
            System.err.println("Error in TaiKhoanBO (updateTaiKhoan): " + e.getMessage());
            throw new RuntimeException("Lỗi khi cập nhật tài khoản: " + e.getMessage(), e);
        }
    }
 
    public void deleteTaiKhoan(String tenDangNhap) {
        System.out.println("[BO] Nhận được yêu cầu xóa từ Controller cho tài khoản: " + tenDangNhap);
        try {
            if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
                throw new IllegalArgumentException("Tên đăng nhập không hợp lệ để xóa.");
            }
            // Add any business rules before deletion, e.g., cannot delete the last admin account
            System.out.println("[BO] Đang gọi DAO để xóa tài khoản: " + tenDangNhap);
            taiKhoanDAO.deleteTaiKhoan(tenDangNhap);
            System.out.println("[BO] DAO đã thực thi xong.");
        } catch (IllegalArgumentException e) {
            System.err.println("[BO] Lỗi IllegalArgumentException: " + e.getMessage());
            throw e;
        } catch (SQLException e) {
            System.err.println("[BO] Lỗi SQLException từ DAO: " + e.getMessage());
            throw new RuntimeException("Lỗi khi xóa tài khoản: " + e.getMessage(), e);
        }
    }
    
    public List<String> getValidRoles() {
        return VALID_ROLES;
    }
}