package com.MediStaffManager.dao;
<<<<<<< HEAD
 
import com.MediStaffManager.bean.TaiKhoan;
import com.MediStaffManager.utils.DBConnection;
 
=======

>>>>>>> ThaoDuyen-QuanLyLuong-ThongKe
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

<<<<<<< HEAD
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
=======
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
>>>>>>> ThaoDuyen-QuanLyLuong-ThongKe
