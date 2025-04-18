package com.MediStaffManager.dao;

import com.MediStaffManager.utils.DBConnection;
import com.MediStaffManager.bean.NhanVien;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class phongBanDAO {
    private Connection connection;

    public phongBanDAO() {
        this.connection = DBConnection.connect();
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
        String queryThemPhongBanMoi = "INSERT INTO phong_ban (IDPhongBan, TenPhongBan) VALUES (?, ?) ON DUPLICATE KEY UPDATE TenPhongBan = ?";
        String queryCapNhatNhanVien = "UPDATE nhan_vien SET IDPhongBan = ? WHERE IDPhongBan = ?";
        String queryXoaPhongBanCu = "DELETE FROM phong_ban WHERE IDPhongBan = ?";
        try {
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

            try (PreparedStatement stmtXoaPhongBanCu = connection.prepareStatement(queryXoaPhongBanCu)) {
                stmtXoaPhongBanCu.setInt(1, idPhongBanCu);
                stmtXoaPhongBanCu.executeUpdate();
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
    
}