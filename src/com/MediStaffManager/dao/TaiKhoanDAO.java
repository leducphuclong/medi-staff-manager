package com.MediStaffManager.dao;
 
import com.MediStaffManager.bean.TaiKhoan;
import com.MediStaffManager.utils.DBConnection;
 
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
 
public class TaiKhoanDAO {
 
    private TaiKhoan mapRowToTaiKhoan(ResultSet rs) throws SQLException {
        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setTenDangNhap(rs.getString("TenDangNhap"));
        taiKhoan.setMatKhau(rs.getString("MatKhau"));
        taiKhoan.setVaiTro(rs.getString("VaiTro"));
        taiKhoan.setIdNhanVien((Integer) rs.getObject("IDNhanVien"));
        return taiKhoan;
    }
 
    public List<TaiKhoan> getAllTaiKhoan() throws SQLException {
        List<TaiKhoan> danhSachTaiKhoan = new ArrayList<>();
        String query = "SELECT TenDangNhap, MatKhau, VaiTro, IDNhanVien FROM tai_khoan ORDER BY TenDangNhap";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                danhSachTaiKhoan.add(mapRowToTaiKhoan(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all TaiKhoan: " + e.getMessage());
            throw e;
        }
        return danhSachTaiKhoan;
    }
 
    public TaiKhoan getTaiKhoanByTenDangNhap(String tenDangNhap) throws SQLException {
        String query = "SELECT TenDangNhap, MatKhau, VaiTro, IDNhanVien FROM tai_khoan WHERE TenDangNhap = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, tenDangNhap);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToTaiKhoan(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving TaiKhoan by TenDangNhap: " + e.getMessage());
            throw e;
        }
        return null;
    }
    
    public String maHoa(String pw) {
    	BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    	String daMaHoa = passwordEncoder.encode(pw);
    	return daMaHoa;
    }
    
    public void addTaiKhoan(TaiKhoan taiKhoan) throws SQLException {
        String query = "INSERT INTO tai_khoan (TenDangNhap, MatKhau, VaiTro, IDNhanVien) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, taiKhoan.getTenDangNhap());
            stmt.setString(2, maHoa(taiKhoan.getMatKhau()));
            stmt.setString(3, taiKhoan.getVaiTro());
            
            if (taiKhoan.getIdNhanVien() == null) {
                throw new SQLException("ID Nhân viên không được để trống khi thêm tài khoản.");
            }
            stmt.setInt(4, taiKhoan.getIdNhanVien());
 
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Thêm tài khoản thất bại, không có hàng nào được ảnh hưởng.");
            }
        } catch (SQLException e) {
            System.err.println("Error adding TaiKhoan: " + e.getMessage());
            if (e.getSQLState().equals("23000")) { // Integrity constraint violation
                 if (e.getMessage().contains("PRIMARY")) {
                    throw new SQLException("Tên đăng nhập '" + taiKhoan.getTenDangNhap() + "' đã tồn tại.", e.getSQLState(), e);
                 } else if (e.getMessage().contains("uq_idnhanvien") || e.getMessage().contains("tai_khoan_ibfk_1")) { // Tên khóa ngoại hoặc unique
                    throw new SQLException("Nhân viên với ID " + taiKhoan.getIdNhanVien() + " đã có tài khoản.", e.getSQLState(), e);
                 }
            }
            throw e;
        }
    }
 
    public void updateTaiKhoan(TaiKhoan taiKhoan) throws SQLException {
        String query = "UPDATE tai_khoan SET MatKhau = ?, VaiTro = ?, IDNhanVien = ? WHERE TenDangNhap = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, maHoa(taiKhoan.getMatKhau()));
            stmt.setString(2, taiKhoan.getVaiTro());
 
            if (taiKhoan.getIdNhanVien() == null) {
                throw new SQLException("ID Nhân viên không được để trống khi cập nhật tài khoản.");
            }
            stmt.setInt(3, taiKhoan.getIdNhanVien());
            stmt.setString(4, taiKhoan.getTenDangNhap());
 
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Cập nhật tài khoản thất bại, không tìm thấy tài khoản với tên đăng nhập: " + taiKhoan.getTenDangNhap());
            }
        } catch (SQLException e) {
            System.err.println("Error updating TaiKhoan: " + e.getMessage());
             if (e.getSQLState().equals("23000") && (e.getMessage().contains("uq_idnhanvien") || e.getMessage().contains("tai_khoan_ibfk_1"))) {
                throw new SQLException("Nhân viên với ID " + taiKhoan.getIdNhanVien() + " đã có tài khoản.", e.getSQLState(), e);
             }
            throw e;
        }
    }
 
    public void deleteTaiKhoan(String tenDangNhap) throws SQLException {
        System.out.println("[DAO] Chuẩn bị xóa tài khoản: " + tenDangNhap);
        String query = "DELETE FROM tai_khoan WHERE TenDangNhap = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, tenDangNhap);
            System.out.println("[DAO] Đang thực thi câu lệnh: " + stmt.toString());
            int affectedRows = stmt.executeUpdate();
            System.out.println("[DAO] Số hàng bị ảnh hưởng: " + affectedRows);

            if (affectedRows == 0) {
                System.err.println("[DAO] Không tìm thấy tài khoản để xóa: " + tenDangNhap);
                // Không ném lỗi ở đây để tránh lỗi khi người dùng bấm nút xóa 2 lần.
                // Việc xóa một thứ không tồn tại có thể coi là thành công.
                // Nếu muốn nó phải báo lỗi, hãy bỏ comment dòng dưới.
                // throw new SQLException("Xóa tài khoản thất bại, không tìm thấy tài khoản với tên đăng nhập: " + tenDangNhap);
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Lỗi SQLException khi xóa: " + e.getMessage());
            e.printStackTrace(); // In ra stack trace đầy đủ để debug
            throw e;
        }
    }
 
    public boolean checkTenDangNhapExists(String tenDangNhap) throws SQLException {
        String query = "SELECT TenDangNhap FROM tai_khoan WHERE TenDangNhap = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, tenDangNhap);
            try(ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking if TenDangNhap exists: " + e.getMessage());
            throw e;
        }
    }
 
    public boolean checkIdNhanVienExists(Integer idNhanVien, String tenDangNhapToExclude) throws SQLException {
        String query = "SELECT IDNhanVien FROM tai_khoan WHERE IDNhanVien = ? AND (TenDangNhap != ? OR ? IS NULL)";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idNhanVien);
            stmt.setString(2, tenDangNhapToExclude);
            stmt.setString(3, tenDangNhapToExclude);
 
            try (ResultSet rs = stmt.executeQuery()){
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking if IDNhanVien exists: " + e.getMessage());
            throw e;
        }
    }
}