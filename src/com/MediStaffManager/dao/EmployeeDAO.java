package com.MediStaffManager.dao;

import com.MediStaffManager.bean.Employee;
import com.MediStaffManager.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT nv.*, cv.TenChucVu, pb.TenPhongBan " +
                      "FROM nhan_vien nv " +
                      "LEFT JOIN chuc_vu cv ON nv.IDChucVu = cv.IDChucVu " +
                      "LEFT JOIN phong_ban pb ON nv.IDPhongBan = pb.IDPhongBan";
        
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Employee employee = new Employee();
                employee.setIdNhanVien(rs.getInt("IDNhanVien"));
                employee.setCccd(rs.getString("CCCD"));
                employee.setHoTen(rs.getString("HoTen"));
                employee.setSdt(rs.getString("SDT"));
                employee.setEmail(rs.getString("Email"));
                employee.setGioiTinh(rs.getString("GioiTinh"));
                employee.setNgaySinh(rs.getString("NgaySinh"));
                employee.setIdChucVu(rs.getInt("IDChucVu"));
                employee.setIdPhongBan(rs.getInt("IDPhongBan"));
                employee.setTenChucVu(rs.getString("TenChucVu"));
                employee.setTenPhongBan(rs.getString("TenPhongBan"));
                employees.add(employee);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving Employee data: " + e.getMessage());
            e.printStackTrace();
        }
        return employees;
    }

    public List<Employee> searchEmployees(String keyword, String criteria) {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT nv.*, cv.TenChucVu, pb.TenPhongBan " +
                      "FROM nhan_vien nv " +
                      "LEFT JOIN chuc_vu cv ON nv.IDChucVu = cv.IDChucVu " +
                      "LEFT JOIN phong_ban pb ON nv.IDPhongBan = pb.IDPhongBan " +
                      "WHERE ";

        switch (criteria) {
            case "Tên":
                query += "nv.HoTen LIKE ?";
                break;
            case "Số CCCD":
                query += "nv.CCCD LIKE ?";
                break;
            case "Số điện thoại":
                query += "nv.SDT LIKE ?";
                break;
            case "Phòng ban":
                query += "pb.TenPhongBan LIKE ?";
                break;
            default:
                query += "nv.HoTen LIKE ? OR nv.CCCD LIKE ? OR nv.SDT LIKE ? OR pb.TenPhongBan LIKE ?";
        }
        
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            String searchPattern = "%" + keyword + "%";
            if (criteria.equals("Tên") || criteria.equals("Số CCCD") || criteria.equals("Số điện thoại") || criteria.equals("Phòng ban")) {
                stmt.setString(1, searchPattern);
            } else {
                stmt.setString(1, searchPattern);
                stmt.setString(2, searchPattern);
                stmt.setString(3, searchPattern);
                stmt.setString(4, searchPattern);
            }
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Employee employee = new Employee();
                employee.setIdNhanVien(rs.getInt("IDNhanVien"));
                employee.setCccd(rs.getString("CCCD"));
                employee.setHoTen(rs.getString("HoTen"));
                employee.setSdt(rs.getString("SDT"));
                employee.setEmail(rs.getString("Email"));
                employee.setGioiTinh(rs.getString("GioiTinh"));
                employee.setNgaySinh(rs.getString("NgaySinh"));
                employee.setIdChucVu(rs.getInt("IDChucVu"));
                employee.setIdPhongBan(rs.getInt("IDPhongBan"));
                employee.setTenChucVu(rs.getString("TenChucVu"));
                employee.setTenPhongBan(rs.getString("TenPhongBan"));
                employees.add(employee);
            }
        } catch (SQLException e) {
            System.out.println("Error searching Employee data: " + e.getMessage());
            e.printStackTrace();
        }
        return employees;
    }

    public void updateEmployee(Employee employee) {
        String query = "UPDATE nhan_vien SET CCCD = ?, HoTen = ?, SDT = ?, Email = ?, GioiTinh = ?, NgaySinh = ?, IDChucVu = ?, IDPhongBan = ? WHERE IDNhanVien = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, employee.getCccd());
            stmt.setString(2, employee.getHoTen());
            stmt.setString(3, employee.getSdt());
            stmt.setString(4, employee.getEmail());
            stmt.setString(5, employee.getGioiTinh());
            stmt.setString(6, employee.getNgaySinh());
            int idChucVu = getIdChucVuByTenChucVu(employee.getTenChucVu());
            if (idChucVu <= 0) {
                throw new SQLException("Không thể tìm thấy chức vụ: " + employee.getTenChucVu());
            }
            stmt.setInt(7, idChucVu);
            int idPhongBan = getIdPhongBanByTenPhongBan(employee.getTenPhongBan());
            if (idPhongBan <= 0) {
                throw new SQLException("Không thể tìm thấy phòng ban: " + employee.getTenPhongBan());
            }
            stmt.setInt(8, idPhongBan);
            stmt.setInt(9, employee.getIdNhanVien());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating Employee: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi cập nhật nhân viên: " + e.getMessage());
        }
    }

    public void deleteEmployee(int idNhanVien) {
        String query = "DELETE FROM nhan_vien WHERE IDNhanVien = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idNhanVien);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting Employee: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi xóa nhân viên: " + e.getMessage());
        }
    }

    public int addEmployee(Employee employee) {
        // Lấy giá trị IDNhanVien lớn nhất hiện tại
        int newId = getNextIdNhanVien();
        employee.setIdNhanVien(newId);

        String query = "INSERT INTO nhan_vien (IDNhanVien, CCCD, HoTen, SDT, Email, GioiTinh, NgaySinh, IDChucVu, IDPhongBan) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, newId); // Gán giá trị cho IDNhanVien
            stmt.setString(2, employee.getCccd());
            stmt.setString(3, employee.getHoTen());
            stmt.setString(4, employee.getSdt());
            stmt.setString(5, employee.getEmail());
            stmt.setString(6, employee.getGioiTinh());
            stmt.setString(7, employee.getNgaySinh());
            stmt.setInt(8, getOrCreateChucVu(employee.getTenChucVu()));
            stmt.setInt(9, getOrCreatePhongBan(employee.getTenPhongBan()));
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Thêm nhân viên thất bại, không có hàng nào được ảnh hưởng!");
            }
            return newId;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thêm nhân viên: " + e.getMessage());
        }
    }

    private int getNextIdNhanVien() {
        String query = "SELECT MAX(IDNhanVien) FROM nhan_vien";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) + 1; // Tăng giá trị lớn nhất lên 1
            }
            return 1; // Nếu bảng rỗng, bắt đầu từ 1
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy IDNhanVien tiếp theo: " + e.getMessage());
        }
    }

    public String checkDuplicate(String cccd, String sdt, String email) {
        String query = "SELECT * FROM nhan_vien WHERE CCCD = ? OR SDT = ? OR Email = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, cccd);
            stmt.setString(2, sdt);
            stmt.setString(3, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                if (rs.getString("CCCD").equals(cccd)) {
                    return "CCCD này đã tồn tại cho nhân viên khác!";
                } else if (rs.getString("SDT").equals(sdt)) {
                    return "Số điện thoại này đã tồn tại cho nhân viên khác!";
                } else if (rs.getString("Email").equals(email)) {
                    return "Email này đã tồn tại cho nhân viên khác!";
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking duplicate Employee: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private int getIdChucVuByTenChucVu(String tenChucVu) {
        String query = "SELECT IDChucVu FROM chuc_vu WHERE TenChucVu = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, tenChucVu);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("IDChucVu");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving IDChucVu: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    private int getIdPhongBanByTenPhongBan(String tenPhongBan) {
        String query = "SELECT IDPhongBan FROM phong_ban WHERE TenPhongBan = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, tenPhongBan);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("IDPhongBan");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving IDPhongBan: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    private int getOrCreateChucVu(String tenChucVu) throws SQLException {
        if (tenChucVu == null || tenChucVu.trim().isEmpty()) {
            throw new SQLException("Tên chức vụ không được để trống!");
        }

        // Kiểm tra xem chức vụ đã tồn tại chưa
        int idChucVu = getIdChucVuByTenChucVu(tenChucVu);
        if (idChucVu != -1) {
            return idChucVu;
        }

        // Nếu chưa tồn tại, thêm mới
        String insertQuery = "INSERT INTO chuc_vu (TenChucVu, HeSoLuong) VALUES (?, 1.0)";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, tenChucVu);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Không thể thêm chức vụ mới, không có hàng nào được ảnh hưởng!");
            }
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Không thể lấy IDChucVu sau khi thêm mới!");
                }
            }
        }
    }

    private int getOrCreatePhongBan(String tenPhongBan) throws SQLException {
        if (tenPhongBan == null || tenPhongBan.trim().isEmpty()) {
            throw new SQLException("Tên phòng ban không được để trống!");
        }

        // Kiểm tra xem phòng ban đã tồn tại chưa
        int idPhongBan = getIdPhongBanByTenPhongBan(tenPhongBan);
        if (idPhongBan != -1) {
            return idPhongBan;
        }

        // Nếu chưa tồn tại, thêm mới
        String insertQuery = "INSERT INTO phong_ban (TenPhongBan) VALUES (?)";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, tenPhongBan);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Không thể thêm phòng ban mới, không có hàng nào được ảnh hưởng!");
            }
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Không thể lấy IDPhongBan sau khi thêm mới!");
                }
            }
        }
    }
}
//package com.MediStaffManager.dao;
//
//import com.MediStaffManager.bean.Employee;
//import com.MediStaffManager.utils.DBConnection;
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class EmployeeDAO {
//    public List<Employee> getAllEmployees() {
//        List<Employee> employees = new ArrayList<>();
//        String query = "SELECT nv.*, cv.TenChucVu, pb.TenPhongBan " +
//                      "FROM nhan_vien nv " +
//                      "LEFT JOIN chuc_vu cv ON nv.IDChucVu = cv.IDChucVu " +
//                      "LEFT JOIN phong_ban pb ON nv.IDPhongBan = pb.IDPhongBan";
//        
//        try (Connection conn = DBConnection.connect();
//             PreparedStatement stmt = conn.prepareStatement(query);
//             ResultSet rs = stmt.executeQuery()) {
//            while (rs.next()) {
//                Employee employee = new Employee();
//                employee.setIdNhanVien(rs.getInt("IDNhanVien"));
//                employee.setCccd(rs.getString("CCCD"));
//                employee.setHoTen(rs.getString("HoTen"));
//                employee.setSdt(rs.getString("SDT"));
//                employee.setEmail(rs.getString("Email"));
//                employee.setGioiTinh(rs.getString("GioiTinh"));
//                employee.setNgaySinh(rs.getString("NgaySinh"));
//                employee.setIdChucVu(rs.getInt("IDChucVu"));
//                employee.setIdPhongBan(rs.getInt("IDPhongBan"));
//                employee.setTenChucVu(rs.getString("TenChucVu"));
//                employee.setTenPhongBan(rs.getString("TenPhongBan"));
//                employees.add(employee);
//            }
//        } catch (SQLException e) {
//            System.out.println("Error retrieving Employee data: " + e.getMessage());
//            e.printStackTrace();
//        }
//        return employees;
//    }
//
//    public List<Employee> searchEmployees(String keyword, String criteria) {
//        List<Employee> employees = new ArrayList<>();
//        String query = "SELECT nv.*, cv.TenChucVu, pb.TenPhongBan " +
//                      "FROM nhan_vien nv " +
//                      "LEFT JOIN chuc_vu cv ON nv.IDChucVu = cv.IDChucVu " +
//                      "LEFT JOIN phong_ban pb ON nv.IDPhongBan = pb.IDPhongBan " +
//                      "WHERE ";
//
//        switch (criteria) {
//            case "Tên":
//                query += "nv.HoTen LIKE ?";
//                break;
//            case "Số CCCD":
//                query += "nv.CCCD LIKE ?";
//                break;
//            case "Số điện thoại":
//                query += "nv.SDT LIKE ?";
//                break;
//            case "Phòng ban":
//                query += "pb.TenPhongBan LIKE ?";
//                break;
//            default:
//                query += "nv.HoTen LIKE ? OR nv.CCCD LIKE ? OR nv.SDT LIKE ? OR pb.TenPhongBan LIKE ?";
//        }
//        
//        try (Connection conn = DBConnection.connect();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//            String searchPattern = "%" + keyword + "%";
//            if (criteria.equals("Tên") || criteria.equals("Số CCCD") || criteria.equals("Số điện thoại") || criteria.equals("Phòng ban")) {
//                stmt.setString(1, searchPattern);
//            } else {
//                stmt.setString(1, searchPattern);
//                stmt.setString(2, searchPattern);
//                stmt.setString(3, searchPattern);
//                stmt.setString(4, searchPattern);
//            }
//            ResultSet rs = stmt.executeQuery();
//
//            while (rs.next()) {
//                Employee employee = new Employee();
//                employee.setIdNhanVien(rs.getInt("IDNhanVien"));
//                employee.setCccd(rs.getString("CCCD"));
//                employee.setHoTen(rs.getString("HoTen"));
//                employee.setSdt(rs.getString("SDT"));
//                employee.setEmail(rs.getString("Email"));
//                employee.setGioiTinh(rs.getString("GioiTinh"));
//                employee.setNgaySinh(rs.getString("NgaySinh"));
//                employee.setIdChucVu(rs.getInt("IDChucVu"));
//                employee.setIdPhongBan(rs.getInt("IDPhongBan"));
//                employee.setTenChucVu(rs.getString("TenChucVu"));
//                employee.setTenPhongBan(rs.getString("TenPhongBan"));
//                employees.add(employee);
//            }
//        } catch (SQLException e) {
//            System.out.println("Error searching Employee data: " + e.getMessage());
//            e.printStackTrace();
//        }
//        return employees;
//    }
//
//    public void updateEmployee(Employee employee) {
//        String query = "UPDATE nhan_vien SET CCCD = ?, HoTen = ?, SDT = ?, Email = ?, GioiTinh = ?, NgaySinh = ?, IDChucVu = ?, IDPhongBan = ? WHERE IDNhanVien = ?";
//        try (Connection conn = DBConnection.connect();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setString(1, employee.getCccd());
//            stmt.setString(2, employee.getHoTen());
//            stmt.setString(3, employee.getSdt());
//            stmt.setString(4, employee.getEmail());
//            stmt.setString(5, employee.getGioiTinh());
//            stmt.setString(6, employee.getNgaySinh());
//            int idChucVu = getIdChucVuByTenChucVu(employee.getTenChucVu());
//            if (idChucVu <= 0) {
//                throw new SQLException("Không thể tìm thấy chức vụ: " + employee.getTenChucVu());
//            }
//            stmt.setInt(7, idChucVu);
//            int idPhongBan = getIdPhongBanByTenPhongBan(employee.getTenPhongBan());
//            if (idPhongBan <= 0) {
//                throw new SQLException("Không thể tìm thấy phòng ban: " + employee.getTenPhongBan());
//            }
//            stmt.setInt(8, idPhongBan);
//            stmt.setInt(9, employee.getIdNhanVien());
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            System.out.println("Error updating Employee: " + e.getMessage());
//            e.printStackTrace();
//            throw new RuntimeException("Lỗi khi cập nhật nhân viên: " + e.getMessage());
//        }
//    }
//
//    public void deleteEmployee(int idNhanVien) {
//        String query = "DELETE FROM nhan_vien WHERE IDNhanVien = ?";
//        try (Connection conn = DBConnection.connect();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setInt(1, idNhanVien);
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            System.out.println("Error deleting Employee: " + e.getMessage());
//            e.printStackTrace();
//            throw new RuntimeException("Lỗi khi xóa nhân viên: " + e.getMessage());
//        }
//    }
//
//    public String checkDuplicate(String cccd, String sdt, String email) {
//        String query = "SELECT * FROM nhan_vien WHERE CCCD = ? OR SDT = ? OR Email = ?";
//        try (Connection conn = DBConnection.connect();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setString(1, cccd);
//            stmt.setString(2, sdt);
//            stmt.setString(3, email);
//            ResultSet rs = stmt.executeQuery();
//            if (rs.next()) {
//                if (rs.getString("CCCD").equals(cccd)) {
//                    return "CCCD này đã tồn tại cho nhân viên khác!";
//                } else if (rs.getString("SDT").equals(sdt)) {
//                    return "Số điện thoại này đã tồn tại cho nhân viên khác!";
//                } else if (rs.getString("Email").equals(email)) {
//                    return "Email này đã tồn tại cho nhân viên khác!";
//                }
//            }
//        } catch (SQLException e) {
//            System.out.println("Error checking duplicate Employee: " + e.getMessage());
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    private int getIdChucVuByTenChucVu(String tenChucVu) {
//        String query = "SELECT IDChucVu FROM chuc_vu WHERE TenChucVu = ?";
//        try (Connection conn = DBConnection.connect();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setString(1, tenChucVu);
//            ResultSet rs = stmt.executeQuery();
//            if (rs.next()) {
//                return rs.getInt("IDChucVu");
//            }
//        } catch (SQLException e) {
//            System.out.println("Error retrieving IDChucVu: " + e.getMessage());
//            e.printStackTrace();
//        }
//        return -1;
//    }
//
//    private int getIdPhongBanByTenPhongBan(String tenPhongBan) {
//        String query = "SELECT IDPhongBan FROM phong_ban WHERE TenPhongBan = ?";
//        try (Connection conn = DBConnection.connect();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setString(1, tenPhongBan);
//            ResultSet rs = stmt.executeQuery();
//            if (rs.next()) {
//                return rs.getInt("IDPhongBan");
//            }
//        } catch (SQLException e) {
//            System.out.println("Error retrieving IDPhongBan: " + e.getMessage());
//            e.printStackTrace();
//        }
//        return -1;
//    }
//
//    private int getOrCreateChucVu(String tenChucVu) throws SQLException {
//        if (tenChucVu == null || tenChucVu.trim().isEmpty()) {
//            throw new SQLException("Tên chức vụ không được để trống!");
//        }
//
//        // Kiểm tra xem chức vụ đã tồn tại chưa
//        int idChucVu = getIdChucVuByTenChucVu(tenChucVu);
//        if (idChucVu != -1) {
//            return idChucVu;
//        }
//
//        // Nếu chưa tồn tại, thêm mới
//        String insertQuery = "INSERT INTO chuc_vu (TenChucVu, HeSoLuong) VALUES (?, 1.0)";
//        try (Connection conn = DBConnection.connect();
//             PreparedStatement stmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
//            stmt.setString(1, tenChucVu);
//            int affectedRows = stmt.executeUpdate();
//            if (affectedRows == 0) {
//                throw new SQLException("Không thể thêm chức vụ mới, không có hàng nào được ảnh hưởng!");
//            }
//            try (ResultSet rs = stmt.getGeneratedKeys()) {
//                if (rs.next()) {
//                    return rs.getInt(1);
//                } else {
//                    throw new SQLException("Không thể lấy IDChucVu sau khi thêm mới!");
//                }
//            }
//        }
//    }
//
//    private int getOrCreatePhongBan(String tenPhongBan) throws SQLException {
//        if (tenPhongBan == null || tenPhongBan.trim().isEmpty()) {
//            throw new SQLException("Tên phòng ban không được để trống!");
//        }
//
//        // Kiểm tra xem phòng ban đã tồn tại chưa
//        int idPhongBan = getIdPhongBanByTenPhongBan(tenPhongBan);
//        if (idPhongBan != -1) {
//            return idPhongBan;
//        }
//
//        // Nếu chưa tồn tại, thêm mới
//        String insertQuery = "INSERT INTO phong_ban (TenPhongBan) VALUES (?)";
//        try (Connection conn = DBConnection.connect();
//             PreparedStatement stmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
//            stmt.setString(1, tenPhongBan);
//            int affectedRows = stmt.executeUpdate();
//            if (affectedRows == 0) {
//                throw new SQLException("Không thể thêm phòng ban mới, không có hàng nào được ảnh hưởng!");
//            }
//            try (ResultSet rs = stmt.getGeneratedKeys()) {
//                if (rs.next()) {
//                    return rs.getInt(1);
//                } else {
//                    throw new SQLException("Không thể lấy IDPhongBan sau khi thêm mới!");
//                }
//            }
//        }
//    }
//}