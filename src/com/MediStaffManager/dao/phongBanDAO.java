package com.MediStaffManager.dao;

import com.MediStaffManager.utils.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet; // Specifically import ResultSet
import java.util.ArrayList;
import java.util.List;

public class phongBanDAO {
    private Connection connection;

    public phongBanDAO() {
        this.connection = DBConnection.connect();
    }

    // Phương thức xóa một phòng ban theo ID
    public boolean xoaPhongBanById(int idPhongBan) {
        String queryKiemTraNhanVien = "SELECT COUNT(*) FROM nhan_vien WHERE IDPhongBan = ?";
        String queryXoaPhongBan = "DELETE FROM phong_ban WHERE IDPhongBan = ?";
        try {
            // Kiểm tra xem phòng ban còn nhân viên không
            try (PreparedStatement stmtKiemTra = connection.prepareStatement(queryKiemTraNhanVien)) {
                stmtKiemTra.setInt(1, idPhongBan);
                try (ResultSet rs = stmtKiemTra.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("DAO: Phòng ban (ID: " + idPhongBan + ") vẫn còn nhân viên. Không thể xóa.");
                        return false; // Không thể xóa nếu còn nhân viên
                    }
                }
            }

            // Nếu không còn nhân viên, tiến hành xóa phòng ban
            try (PreparedStatement stmtXoaPhongBan = connection.prepareStatement(queryXoaPhongBan)) {
                stmtXoaPhongBan.setInt(1, idPhongBan);
                int affectedRows = stmtXoaPhongBan.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("DAO: Đã xóa phòng ban (ID: " + idPhongBan + ")");
                    return true;
                } else {
                    System.out.println("DAO: Không tìm thấy phòng ban (ID: " + idPhongBan + ") để xóa.");
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO: Lỗi SQL khi xóa phòng ban (ID: " + idPhongBan + "): " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Phương thức xóa một phòng ban theo tên
    public boolean xoaPhongBan(String tenPhongBan) {
        String queryKiemTraNhanVien = "SELECT COUNT(*) FROM nhan_vien nv " +
                                      "JOIN phong_ban pb ON nv.IDPhongBan = pb.IDPhongBan " +
                                      "WHERE pb.TenPhongBan = ?";
        String queryXoaPhongBan = "DELETE FROM phong_ban WHERE TenPhongBan = ?";
        try {
            try (PreparedStatement stmtKiemTra = connection.prepareStatement(queryKiemTraNhanVien)) {
                stmtKiemTra.setString(1, tenPhongBan);
                try (ResultSet rs = stmtKiemTra.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("Phòng ban vẫn còn nhân viên. Không thể xóa.");
                        return false;
                    }
                }
            }

            // Xóa phòng ban
            try (PreparedStatement stmtPhongBan = connection.prepareStatement(queryXoaPhongBan)) {
                stmtPhongBan.setString(1, tenPhongBan);
                return stmtPhongBan.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<Object[]> layDanhSachPhongBan() {
        List<Object[]> phongBanList = new ArrayList<>();
        String query = "SELECT IDPhongBan, TenPhongBan FROM phong_ban";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int idPhongBan = rs.getInt("IDPhongBan");
                String tenPhongBan = rs.getString("TenPhongBan");
                phongBanList.add(new Object[]{idPhongBan, tenPhongBan});
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return phongBanList;
    }
    
    public boolean kiemTraPhongBanTonTai(int idPhongBan) {
        String query = "SELECT COUNT(*) FROM phong_ban WHERE IDPhongBan = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idPhongBan);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Phương thức thêm một phòng ban mới
    public boolean themPhongBan(int idPhongBan, String tenPhongBan) {    	
        String query = "INSERT INTO phong_ban (IDPhongBan, TenPhongBan) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idPhongBan);
            stmt.setString(2, tenPhongBan);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean suaPhongBan(int idPhongBanCu, int idPhongBanMoi, String tenPhongBanMoi) {
        // Kiểm tra trùng ID (ngoại trừ chính nó)
        String checkIdSql = "SELECT COUNT(*) FROM phong_ban WHERE IDPhongBan = ? AND IDPhongBan <> ?";
        // Kiểm tra trùng tên (ngoại trừ chính nó)
        String checkNameSql = "SELECT COUNT(*) FROM phong_ban WHERE TenPhongBan = ? AND IDPhongBan <> ?";
        String queryThemPhongBanMoi = "INSERT INTO phong_ban (IDPhongBan, TenPhongBan) VALUES (?, ?) ON DUPLICATE KEY UPDATE TenPhongBan = ?";
        String queryCapNhatNhanVien = "UPDATE nhan_vien SET IDPhongBan = ? WHERE IDPhongBan = ?";
        String queryXoaPhongBanCu = "DELETE FROM phong_ban WHERE IDPhongBan = ?";
        try {
            // Kiểm tra trùng ID
            try (PreparedStatement stmtCheckId = connection.prepareStatement(checkIdSql)) {
                stmtCheckId.setInt(1, idPhongBanMoi);
                stmtCheckId.setInt(2, idPhongBanCu);
                try (ResultSet rs = stmtCheckId.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        // Đã tồn tại ID khác
                        return false;
                    }
                }
            }
            // Kiểm tra trùng tên
            try (PreparedStatement stmtCheckName = connection.prepareStatement(checkNameSql)) {
                stmtCheckName.setString(1, tenPhongBanMoi);
                stmtCheckName.setInt(2, idPhongBanCu);
                try (ResultSet rs = stmtCheckName.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        // Đã tồn tại tên khác
                        return false;
                    }
                }
            }

            connection.setAutoCommit(false);

            try (PreparedStatement stmtThemPhongBanMoi = connection.prepareStatement(queryThemPhongBanMoi)) {
                stmtThemPhongBanMoi.setInt(1, idPhongBanMoi);
                stmtThemPhongBanMoi.setString(2, tenPhongBanMoi);
                stmtThemPhongBanMoi.setString(3, tenPhongBanMoi);
                stmtThemPhongBanMoi.executeUpdate();
            }

            try (PreparedStatement stmtNhanVien = connection.prepareStatement(queryCapNhatNhanVien)) {
                stmtNhanVien.setInt(1, idPhongBanMoi);
                stmtNhanVien.setInt(2, idPhongBanCu);
                stmtNhanVien.executeUpdate();
            }

            // Chỉ xóa phòng ban cũ nếu ID thực sự thay đổi
            if (idPhongBanCu != idPhongBanMoi) {
                try (PreparedStatement stmtXoaPhongBanCu = connection.prepareStatement(queryXoaPhongBanCu)) {
                    stmtXoaPhongBanCu.setInt(1, idPhongBanCu);
                    int deletedRows = stmtXoaPhongBanCu.executeUpdate();
                    if (deletedRows > 0) {
                        System.out.println("DAO: Đã xóa phòng ban cũ (ID: " + idPhongBanCu + ")");
                    } else {
                        System.out.println("DAO: Không tìm thấy phòng ban cũ (ID: " + idPhongBanCu + ") để xóa.");
                        // Điều này không nên xảy ra nếu idPhongBanCu tồn tại và khác idPhongBanMoi
                    }
                }
            } else {
                // Trường hợp ID không đổi, chỉ cập nhật tên (đã được xử lý bởi queryThemPhongBanMoi với ON DUPLICATE KEY UPDATE)
                // Không cần hành động xóa ở đây.
                System.out.println("DAO: ID phòng ban không thay đổi (ID: " + idPhongBanCu + "). Chỉ cập nhật thông tin.");
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }


    public int layIdPhongBanTheoTen(String tenPhongBan) {
        String query = "SELECT IDPhongBan FROM phong_ban WHERE TenPhongBan = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, tenPhongBan);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("IDPhongBan");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}