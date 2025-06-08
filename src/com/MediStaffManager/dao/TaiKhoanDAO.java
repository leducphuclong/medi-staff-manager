package com.MediStaffManager.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.MediStaffManager.bean.TaiKhoan;
import com.MediStaffManager.utils.DBConnection;

public class TaiKhoanDAO {

    /**
     * Kiểm tra thông tin đăng nhập của người dùng.
     * @param username Tên đăng nhập
     * @param password Mật khẩu (chưa hash)
     * @return Đối tượng TaiKhoan nếu đăng nhập thành công, null nếu thất bại.
     */
    public TaiKhoan login(String username, String password) {
        String sql = "SELECT IDNhanVien, TenDangNhap, MatKhau, VaiTro "
                   + "FROM tai_khoan "
                   + "WHERE TenDangNhap = ? AND MatKhau = ?";
        // LƯU Ý KHÔNG AN TOÀN: Trong thực tế, nên hash password trước khi so sánh.
        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new TaiKhoan(
                            rs.getInt("IDNhanVien"),
                            rs.getString("TenDangNhap"),
                            rs.getString("MatKhau"),
                            rs.getString("VaiTro")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi đăng nhập TaiKhoan: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Đăng ký một tài khoản mới.
     * @param taiKhoan Đối tượng TaiKhoan chứa thông tin đăng ký.
     * @return true nếu đăng ký thành công, false nếu thất bại (ví dụ: TenDangNhap đã tồn tại).
     */
    public boolean register(TaiKhoan taiKhoan) {
        String sql = "INSERT INTO tai_khoan (TenDangNhap, MatKhau, VaiTro) VALUES (?, ?, ?)";
        // LƯU Ý: Trước khi gọi method này, nên hash mật khẩu và lưu hash vào CSDL.
        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, taiKhoan.getTenDangNhap());
            pstmt.setString(2, taiKhoan.getMatKhau());
            pstmt.setString(3, taiKhoan.getVaiTro());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                // Lấy ID tự tăng được tạo ra
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        taiKhoan.setIdNhanVien(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            // Kiểm tra mã lỗi vi phạm ràng buộc UNIQUE (Trùng TenDangNhap)
            if ("23000".equals(e.getSQLState())) {
                System.err.println("Lỗi đăng ký: Tên đăng nhập '" 
                                   + taiKhoan.getTenDangNhap() 
                                   + "' đã tồn tại.");
            } else {
                System.err.println("Lỗi khi đăng ký TaiKhoan: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Lấy thông tin tài khoản bằng ID (IDNhanVien).
     * @param idNhanVien ID của tài khoản.
     * @return Đối tượng TaiKhoan nếu tìm thấy, null nếu không.
     */
    public TaiKhoan getTaiKhoanById(int idNhanVien) {
        String sql = "SELECT IDNhanVien, TenDangNhap, VaiTro "
                   + "FROM tai_khoan WHERE IDNhanVien = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idNhanVien);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new TaiKhoan(
                            rs.getInt("IDNhanVien"),
                            rs.getString("TenDangNhap"),
                            null, // không trả về mật khẩu
                            rs.getString("VaiTro")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy TaiKhoan bằng ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy danh sách tất cả tài khoản.
     * @return Danh sách các TaiKhoan (chỉ lấy TenDangNhap + VaiTro, không trả về MatKhau).
     */
    public List<TaiKhoan> getAllTaiKhoan() {
        List<TaiKhoan> list = new ArrayList<>();
        String sql = "SELECT IDNhanVien, TenDangNhap, VaiTro FROM tai_khoan";
        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                TaiKhoan tk = new TaiKhoan(
                        rs.getInt("IDNhanVien"),
                        rs.getString("TenDangNhap"),
                        null, // không trả về mật khẩu
                        rs.getString("VaiTro")
                );
                list.add(tk);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách TaiKhoan: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Cập nhật thông tin VaiTro của tài khoản (ví dụ thay đổi vai trò).
     * @param taiKhoan Đối tượng TaiKhoan chứa IDNhanVien và VaiTro mới.
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean updateVaiTro(TaiKhoan taiKhoan) {
        String sql = "UPDATE tai_khoan SET VaiTro = ? WHERE IDNhanVien = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, taiKhoan.getVaiTro());
            pstmt.setInt(2, taiKhoan.getIdNhanVien());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật VaiTro: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Xóa tài khoản theo IDNhanVien.
     * @param idNhanVien ID của TaiKhoan cần xóa.
     * @return true nếu xóa thành công, false nếu thất bại.
     */
    public boolean deleteTaiKhoan(int idNhanVien) {
        String sql = "DELETE FROM tai_khoan WHERE IDNhanVien = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idNhanVien);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa TaiKhoan: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Nếu cần thêm các phương thức khác như đổi mật khẩu, tìm kiếm theo vai trò, v.v., bạn có thể implement tương tự.
}
