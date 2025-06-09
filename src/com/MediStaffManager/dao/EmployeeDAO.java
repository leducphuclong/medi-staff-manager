package com.MediStaffManager.dao;

import com.MediStaffManager.bean.Employee;
import com.MediStaffManager.utils.DBConnection;
import java.sql.*;
import java.math.BigDecimal; // Đảm bảo đã import
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    private Employee mapRowToEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setIdNhanVien(rs.getInt("IDNhanVien"));
        employee.setCccd(rs.getString("CCCD"));
        employee.setHoTen(rs.getString("HoTen"));
        employee.setSdt(rs.getString("SDT"));
        employee.setEmail(rs.getString("Email"));
        employee.setGioiTinh(rs.getString("GioiTinh"));

        Date ngaySinhSQL = rs.getDate("NgaySinh");
        if (ngaySinhSQL != null) {
            employee.setNgaySinh(ngaySinhSQL.toLocalDate().toString());
        } else {
            employee.setNgaySinh(null);
        }

        employee.setIdChucVu(rs.getInt("IDChucVu"));
        employee.setIdPhongBan(rs.getInt("IDPhongBan"));
        employee.setTenChucVu(rs.getString("TenChucVu"));
        employee.setTenPhongBan(rs.getString("TenPhongBan"));
        // Lấy HeSoLuong từ bảng chuc_vu
        employee.setHeSoLuong(rs.getBigDecimal("HeSoLuong"));
        return employee;
    }

    public List<Employee> getAllEmployees() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        // Thêm cv.HeSoLuong vào câu SELECT
        String query = "SELECT nv.*, cv.TenChucVu, pb.TenPhongBan, cv.HeSoLuong " +
                      "FROM nhan_vien nv " +
                      "LEFT JOIN chuc_vu cv ON nv.IDChucVu = cv.IDChucVu " +
                      "LEFT JOIN phong_ban pb ON nv.IDPhongBan = pb.IDPhongBan ORDER BY nv.IDNhanVien";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                employees.add(mapRowToEmployee(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all Employee data: " + e.getMessage());
            throw e;
        }
        return employees;
    }

    public List<Employee> searchEmployees(String keyword, String criteria) throws SQLException {
        List<Employee> employees = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder(
            // Thêm cv.HeSoLuong vào câu SELECT
            "SELECT nv.*, cv.TenChucVu, pb.TenPhongBan, cv.HeSoLuong " +
            "FROM nhan_vien nv " +
            "LEFT JOIN chuc_vu cv ON nv.IDChucVu = cv.IDChucVu " +
            "LEFT JOIN phong_ban pb ON nv.IDPhongBan = pb.IDPhongBan " +
            "WHERE "
        );

        switch (criteria.toLowerCase()) {
            case "hoten":
                queryBuilder.append("LOWER(nv.HoTen) LIKE LOWER(?)");
                break;
            case "cccd":
                queryBuilder.append("nv.CCCD LIKE ?");
                break;
            case "sdt":
                queryBuilder.append("nv.SDT LIKE ?");
                break;
            case "email":
                queryBuilder.append("LOWER(nv.Email) LIKE LOWER(?)");
                break;
            default:
                 System.err.println("Search criteria not recognized: " + criteria + ". Defaulting to HoTen search.");
                 queryBuilder.append("LOWER(nv.HoTen) LIKE LOWER(?)");
                 break;
        }
        queryBuilder.append(" ORDER BY nv.IDNhanVien");

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {

            String searchPattern = "%" + (keyword == null ? "" : keyword) + "%";
            stmt.setString(1, searchPattern);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                employees.add(mapRowToEmployee(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching Employee data: " + e.getMessage());
            throw e;
        }
        return employees;
    }

    // Khi thêm hoặc cập nhật nhân viên, IDChucVu sẽ được lấy dựa trên TenChucVu.
    // HeSoLuong của nhân viên sẽ được xác định bởi HeSoLuong của ChucVu đó.
    // Nếu bạn muốn HeSoLuong có thể tùy chỉnh độc lập, bạn cần:
    // 1. Thêm cột HeSoLuong vào bảng `nhan_vien`.
    // 2. Sửa đổi Employee bean để có trường HeSoLuong riêng.
    // 3. Sửa câu lệnh INSERT/UPDATE để ghi HeSoLuong này.
    // Hiện tại, HeSoLuong của Employee bean sẽ được dùng để hiển thị,
    // và khi lưu, IDChucVu sẽ quyết định HeSoLuong thực tế theo bảng chuc_vu.

    public void updateEmployee(Employee employee) throws SQLException {
        // Giữ nguyên logic updateEmployee, vì HeSoLuong liên kết với IDChucVu
        // Giá trị HeSoLuong trong employee object chủ yếu để truyền qua JSON nếu cần
        String query = "UPDATE nhan_vien SET CCCD = ?, HoTen = ?, SDT = ?, Email = ?, GioiTinh = ?, NgaySinh = ?, IDChucVu = ?, IDPhongBan = ? WHERE IDNhanVien = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, employee.getCccd());
            stmt.setString(2, employee.getHoTen());
            stmt.setString(3, employee.getSdt());
            stmt.setString(4, employee.getEmail());
            stmt.setString(5, employee.getGioiTinh());
            if (employee.getNgaySinh() != null && !employee.getNgaySinh().isEmpty()) {
                stmt.setDate(6, java.sql.Date.valueOf(employee.getNgaySinh()));
            } else {
                stmt.setNull(6, java.sql.Types.DATE);
            }
            stmt.setInt(7, getOrCreateChucVu(employee.getTenChucVu(), employee.getHeSoLuong())); // Truyền HeSoLuong vào đây
            stmt.setInt(8, getOrCreatePhongBan(employee.getTenPhongBan()));
            stmt.setInt(9, employee.getIdNhanVien());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Cập nhật nhân viên thất bại, không tìm thấy nhân viên với ID: " + employee.getIdNhanVien());
            }
        } catch (SQLException e) {
            System.err.println("Error updating Employee: " + e.getMessage());
            throw e;
        }
    }

    public int addEmployee(Employee employee) throws SQLException {
        // Giữ nguyên logic addEmployee
        String query = "INSERT INTO nhan_vien (CCCD, HoTen, SDT, Email, GioiTinh, NgaySinh, IDChucVu, IDPhongBan) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, employee.getCccd());
            stmt.setString(2, employee.getHoTen());
            stmt.setString(3, employee.getSdt());
            stmt.setString(4, employee.getEmail());
            stmt.setString(5, employee.getGioiTinh());
            if (employee.getNgaySinh() != null && !employee.getNgaySinh().isEmpty()) {
                stmt.setDate(6, java.sql.Date.valueOf(employee.getNgaySinh()));
            } else {
                stmt.setNull(6, java.sql.Types.DATE);
            }
            stmt.setInt(7, getOrCreateChucVu(employee.getTenChucVu(), employee.getHeSoLuong())); // Truyền HeSoLuong vào đây
            stmt.setInt(8, getOrCreatePhongBan(employee.getTenPhongBan()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Thêm nhân viên thất bại, không có hàng nào được ảnh hưởng.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Trả về ID tự tăng
                } else {
                    throw new SQLException("Thêm nhân viên thất bại, không lấy được ID tự tăng.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding Employee: " + e.getMessage());
            throw e;
        }
    }

    public String checkDuplicate(String cccd, String sdt, String email, Integer currentEmployeeId) throws SQLException {
        // ... (giữ nguyên)
        StringBuilder queryBuilder = new StringBuilder("SELECT CCCD, SDT, Email FROM nhan_vien WHERE (CCCD = ? OR SDT = ? OR Email = ?)");
        if (currentEmployeeId != null && currentEmployeeId > 0) {
            queryBuilder.append(" AND IDNhanVien != ?");
        }

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {
            stmt.setString(1, cccd);
            stmt.setString(2, sdt);
            stmt.setString(3, email);
            if (currentEmployeeId != null && currentEmployeeId > 0) {
                stmt.setInt(4, currentEmployeeId);
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                if (cccd != null && cccd.equals(rs.getString("CCCD"))) {
                    return "CCCD này đã tồn tại cho nhân viên khác!";
                }
                if (sdt != null && sdt.equals(rs.getString("SDT"))) {
                    return "Số điện thoại này đã tồn tại cho nhân viên khác!";
                }
                if (email != null && email.equals(rs.getString("Email"))) {
                    return "Email này đã tồn tại cho nhân viên khác!";
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking duplicate Employee: " + e.getMessage());
            throw e;
        }
        return null;
    }

    // Thay đổi getOrCreateChucVu để chấp nhận HeSoLuong
    // Nếu tạo mới ChucVu, sẽ dùng HeSoLuong này. Nếu ChucVu đã tồn tại, HeSoLuong hiện tại của ChucVu đó sẽ được giữ nguyên (trừ khi có logic cập nhật HeSoLuong ChucVu riêng)
    public int getOrCreateChucVu(String tenChucVu, BigDecimal heSoLuongNeuTaoMoi) throws SQLException {
        if (tenChucVu == null || tenChucVu.trim().isEmpty()) {
            throw new SQLException("Tên chức vụ không được để trống khi tạo hoặc lấy ID.");
        }
        int idChucVu = getIdChucVuByTenChucVu(tenChucVu);
        if (idChucVu != -1) {
            return idChucVu;
        }
        // Thêm mới nếu không tồn tại
        String insertQuery = "INSERT INTO chuc_vu (TenChucVu, HeSoLuong) VALUES (?, ?)";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, tenChucVu);
            if (heSoLuongNeuTaoMoi != null) {
                stmt.setBigDecimal(2, heSoLuongNeuTaoMoi);
            } else {
                stmt.setBigDecimal(2, BigDecimal.valueOf(1.0)); // Giá trị mặc định nếu không cung cấp
            }
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Không thể lấy IDChucVu sau khi thêm mới.");
                }
            }
        }
    }


    private int getIdChucVuByTenChucVu(String tenChucVu) throws SQLException {
        // ... (giữ nguyên)
        if (tenChucVu == null || tenChucVu.trim().isEmpty()) return -1;
        String query = "SELECT IDChucVu FROM chuc_vu WHERE TenChucVu = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, tenChucVu);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("IDChucVu");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving IDChucVu by TenChucVu: " + e.getMessage());
            throw e;
        }
        return -1;
    }

    private int getIdPhongBanByTenPhongBan(String tenPhongBan) throws SQLException {
        // ... (giữ nguyên)
        if (tenPhongBan == null || tenPhongBan.trim().isEmpty()) return -1;
        String query = "SELECT IDPhongBan FROM phong_ban WHERE TenPhongBan = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, tenPhongBan);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("IDPhongBan");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving IDPhongBan by TenPhongBan: " + e.getMessage());
            throw e;
        }
        return -1;
    }
    public int getOrCreatePhongBan(String tenPhongBan) throws SQLException {
        // ... (giữ nguyên)
        if (tenPhongBan == null || tenPhongBan.trim().isEmpty()) {
            throw new SQLException("Tên phòng ban không được để trống khi tạo hoặc lấy ID.");
        }
        int idPhongBan = getIdPhongBanByTenPhongBan(tenPhongBan);
        if (idPhongBan != -1) {
            return idPhongBan;
        }
        String insertQuery = "INSERT INTO phong_ban (TenPhongBan) VALUES (?)";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, tenPhongBan);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Không thể lấy IDPhongBan sau khi thêm mới.");
                }
            }
        }
    }
    public void deleteEmployee(int idNhanVien) throws SQLException {
      String query = "DELETE FROM nhan_vien WHERE IDNhanVien = ?";
      try (Connection conn = DBConnection.connect();
           PreparedStatement stmt = conn.prepareStatement(query)) {
          stmt.setInt(1, idNhanVien);
          int affectedRows = stmt.executeUpdate();
          if (affectedRows == 0) {
              throw new SQLException("Xóa nhân viên thất bại, không tìm thấy nhân viên với ID: " + idNhanVien);
          }
      } catch (SQLException e) {
          System.err.println("Error deleting Employee: " + e.getMessage());
          throw e;
      }
      }
    // Method to get HeSoLuong by TenChucVu for JavaScript to update the form
    public BigDecimal getHeSoLuongByTenChucVu(String tenChucVu) throws SQLException {
        if (tenChucVu == null || tenChucVu.trim().isEmpty()) {
            return null;
        }
        String query = "SELECT HeSoLuong FROM chuc_vu WHERE TenChucVu = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, tenChucVu);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("HeSoLuong");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving HeSoLuong by TenChucVu: " + e.getMessage());
            throw e;
        }
        return null; // Hoặc BigDecimal.ONE nếu muốn có giá trị mặc định
    }


    public List<String> getAllDistinctTenChucVu() throws SQLException {
        // ... (giữ nguyên)
        List<String> tenChucVuList = new ArrayList<>();
        String query = "SELECT DISTINCT TenChucVu FROM chuc_vu ORDER BY TenChucVu";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                tenChucVuList.add(rs.getString("TenChucVu"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving distinct TenChucVu: " + e.getMessage());
            throw e;
        }
        return tenChucVuList;
    }

    public List<String> getAllDistinctTenPhongBan() throws SQLException {
        // ... (giữ nguyên)
        List<String> tenPhongBanList = new ArrayList<>();
        String query = "SELECT DISTINCT TenPhongBan FROM phong_ban ORDER BY TenPhongBan";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                tenPhongBanList.add(rs.getString("TenPhongBan"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving distinct TenPhongBan: " + e.getMessage());
            throw e;
        }
        return tenPhongBanList;
    }
}