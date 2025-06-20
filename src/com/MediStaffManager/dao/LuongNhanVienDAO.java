package com.MediStaffManager.dao;

import com.MediStaffManager.bean.LuongNhanVienBEAN;
import com.MediStaffManager.bean.ThongKeResult;
import com.MediStaffManager.utils.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LuongNhanVienDAO {

    /**
     * Retrieves a list of all employees and their salary details for a specific month.
     * Uses a LEFT JOIN to include employees who do not have a salary record for that month yet.
     * @param thangNam The month and year in "YYYY-MM" format.
     * @return A list of LuongNhanVienBEAN objects.
     */
    public List<LuongNhanVienBEAN> layLuongTheoThang(String thangNam) {
        List<LuongNhanVienBEAN> resultList = new ArrayList<>();
        String sql = "SELECT n.IDNhanVien, n.HoTen, c.IDChucVu, c.TenChucVu, c.HeSoLuong, p.TenPhongBan, " +
                     "l.IDLuong, l.ThangNam, l.LuongCoBanChuan, l.Thuong, l.PhuCap, l.TangCa, l.TongLuong " +
                     "FROM nhan_vien n " +
                     "JOIN chuc_vu c ON n.IDChucVu = c.IDChucVu " +
                     "JOIN phong_ban p ON n.IDPhongBan = p.IDPhongBan " +
                     "LEFT JOIN luong_nhan_vien l ON n.IDNhanVien = l.IDNhanVien AND l.ThangNam = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, thangNam);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultList.add(mapResultSetToLuongBean(rs, thangNam));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    /**
     * NEW METHOD: Retrieves salary details for employees in a specific department for a given month.
     * @param thangNam The month and year in "YYYY-MM" format.
     * @param idPhongBan The ID of the department to filter by.
     * @return A list of LuongNhanVienBEAN objects.
     */
    public List<LuongNhanVienBEAN> layLuongTheoThangVaPhongBan(String thangNam, int idPhongBan) {
        List<LuongNhanVienBEAN> resultList = new ArrayList<>();
        String sql = "SELECT n.IDNhanVien, n.HoTen, c.IDChucVu, c.TenChucVu, c.HeSoLuong, p.TenPhongBan, " +
                     "l.IDLuong, l.ThangNam, l.LuongCoBanChuan, l.Thuong, l.PhuCap, l.TangCa, l.TongLuong " +
                     "FROM nhan_vien n " +
                     "JOIN chuc_vu c ON n.IDChucVu = c.IDChucVu " +
                     "JOIN phong_ban p ON n.IDPhongBan = p.IDPhongBan " +
                     "LEFT JOIN luong_nhan_vien l ON n.IDNhanVien = l.IDNhanVien AND l.ThangNam = ? " +
                     "WHERE n.IDPhongBan = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, thangNam);
            ps.setInt(2, idPhongBan);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultList.add(mapResultSetToLuongBean(rs, thangNam));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    /**
     * Helper method to map a ResultSet row to a LuongNhanVienBEAN object.
     */
    private LuongNhanVienBEAN mapResultSetToLuongBean(ResultSet rs, String thangNamQuery) throws SQLException {
        LuongNhanVienBEAN bean = new LuongNhanVienBEAN();
        // --- From NhanVien, ChucVu & PhongBan (always present) ---
        bean.setIdNhanVien(rs.getInt("IDNhanVien"));
        bean.setHoTen(rs.getString("HoTen"));
        bean.setIdChucVu(rs.getInt("IDChucVu"));
        bean.setTenChucVu(rs.getString("TenChucVu"));
        bean.setHeSoLuong(rs.getBigDecimal("HeSoLuong"));
        // Assuming LuongNhanVienBEAN has tenPhongBan field. If not, add it.
        // bean.setTenPhongBan(rs.getString("TenPhongBan"));

        // --- From LuongNhanVien (check for null) ---
        Integer idLuong = (Integer) rs.getObject("IDLuong");
        if (idLuong != null) {
            bean.setIdLuong(idLuong);
            bean.setThangNam(rs.getString("ThangNam"));
            bean.setLuongCoBanChuan(rs.getBigDecimal("LuongCoBanChuan"));
            bean.setThuong(rs.getBigDecimal("Thuong"));
            bean.setPhuCap(rs.getBigDecimal("PhuCap"));
            bean.setTangCa(rs.getBigDecimal("TangCa"));
            bean.setTongLuong(rs.getBigDecimal("TongLuong"));
            bean.setTrangThai("Đã có");
        } else {
            // Employee exists, but no salary record for this month
            bean.setThangNam(thangNamQuery); // Set the month we queried for
            bean.setTrangThai("Chưa có");
            // Other salary fields remain null/zero by default
        }
        return bean;
    }
    
    /**
     * Inserts a new salary record into the database.
     * @param lnv The bean containing salary information to add.
     * @return true if the insertion was successful, false otherwise.
     */
    public boolean themLuong(LuongNhanVienBEAN lnv) {
        String sql = "INSERT INTO luong_nhan_vien (IDNhanVien, IDChucVu, ThangNam, LuongCoBanChuan, Thuong, PhuCap, TangCa, TongLuong) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lnv.getIdNhanVien());
            ps.setInt(2, lnv.getIdChucVu());
            ps.setString(3, lnv.getThangNam());
            ps.setBigDecimal(4, lnv.getLuongCoBanChuan());
            ps.setBigDecimal(5, lnv.getThuong());
            ps.setBigDecimal(6, lnv.getPhuCap());
            ps.setBigDecimal(7, lnv.getTangCa());
            ps.setBigDecimal(8, lnv.getTongLuong());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // Could be a unique constraint violation (IDNhanVien, ThangNam)
            return false;
        }
    }

    /**
     * Updates an existing salary record.
     * @param luong The bean with updated salary information.
     * @return true if the update was successful, false otherwise.
     */
    public boolean capNhatLuong(LuongNhanVienBEAN luong) {
        String sql = "UPDATE luong_nhan_vien SET IDChucVu = ?, LuongCoBanChuan = ?, Thuong = ?, PhuCap = ?, " +
                     "TangCa = ?, TongLuong = ? WHERE IDLuong = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, luong.getIdChucVu());
            ps.setBigDecimal(2, luong.getLuongCoBanChuan());
            ps.setBigDecimal(3, luong.getThuong());
            ps.setBigDecimal(4, luong.getPhuCap());
            ps.setBigDecimal(5, luong.getTangCa());
            ps.setBigDecimal(6, luong.getTongLuong());
            ps.setInt(7, luong.getIdLuong());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a salary record from the database.
     * @param idLuong The ID of the salary record to delete.
     * @return true if deletion was successful, false otherwise.
     */
    public boolean xoaLuong(int idLuong) {
        String sql = "DELETE FROM luong_nhan_vien WHERE IDLuong = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idLuong);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Finds all salary records for a specific employee across all time.
     * @param idNhanVien The employee's ID.
     * @return A list of LuongNhanVienBEAN objects.
     */
    public List<LuongNhanVienBEAN> timKiemLuongTheoIDNhanVien(int idNhanVien) {
        List<LuongNhanVienBEAN> resultList = new ArrayList<>();
        String sql = "SELECT l.*, n.HoTen, c.TenChucVu, c.HeSoLuong " +
                     "FROM luong_nhan_vien l " +
                     "JOIN nhan_vien n ON l.IDNhanVien = n.IDNhanVien " +
                     "JOIN chuc_vu c ON l.IDChucVu = c.IDChucVu " +
                     "WHERE l.IDNhanVien = ? ORDER BY l.ThangNam DESC";
        
        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idNhanVien);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LuongNhanVienBEAN bean = new LuongNhanVienBEAN();
                    bean.setIdLuong(rs.getInt("IDLuong"));
                    bean.setIdNhanVien(rs.getInt("IDNhanVien"));
                    bean.setHoTen(rs.getString("HoTen"));
                    bean.setIdChucVu(rs.getInt("IDChucVu"));
                    bean.setTenChucVu(rs.getString("TenChucVu"));
                    bean.setHeSoLuong(rs.getBigDecimal("HeSoLuong"));
                    bean.setThangNam(rs.getString("ThangNam"));
                    bean.setLuongCoBanChuan(rs.getBigDecimal("LuongCoBanChuan"));
                    bean.setThuong(rs.getBigDecimal("Thuong"));
                    bean.setPhuCap(rs.getBigDecimal("PhuCap"));
                    bean.setTangCa(rs.getBigDecimal("TangCa"));
                    bean.setTongLuong(rs.getBigDecimal("TongLuong"));
                    bean.setTrangThai("Đã có");
                    resultList.add(bean);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    /**
     * Performs an aggregation query over a list of months.
     * @param thangNamList A list of months in "YYYY-MM" format.
     * @return A ThongKeResult object with the aggregated data.
     */
    public ThongKeResult thongKeTrongKhoangThoiGian(List<String> thangNamList) {
        ThongKeResult result = new ThongKeResult();
        if (thangNamList == null || thangNamList.isEmpty()) {
            return result;
        }
        
        String placeholders = thangNamList.stream().map(m -> "?").collect(Collectors.joining(","));
        String sql = "SELECT COUNT(IDLuong) as SoLuong, " +
                     "SUM(TongLuong) as TongChi, " +
                     "AVG(TongLuong) as LuongTB, " +
                     "MAX(TongLuong) as LuongMax, " +
                     "MIN(TongLuong) as LuongMin " +
                     "FROM luong_nhan_vien WHERE ThangNam IN (" + placeholders + ")";

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < thangNamList.size(); i++) {
                ps.setString(i + 1, thangNamList.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getLong("SoLuong") > 0) {
                    result.setSoLuongNhanVien(rs.getLong("SoLuong"));
                    result.setTongChiTra(rs.getBigDecimal("TongChi"));
                    result.setLuongTrungBinh(rs.getBigDecimal("LuongTB"));
                    result.setLuongCaoNhat(rs.getBigDecimal("LuongMax"));
                    result.setLuongThapNhat(rs.getBigDecimal("LuongMin"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // --- Simple Lookup Methods ---

    public BigDecimal getLuongCoBanChuan() {
        String sql = "SELECT LuongCoBanChuan FROM luong_nhan_vien WHERE LuongCoBanChuan IS NOT NULL ORDER BY IDLuong DESC LIMIT 1";
        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal("LuongCoBanChuan");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new BigDecimal("7000000"); // Default fallback value
    }

    public BigDecimal layHeSoLuongTheoChucVu(int idChucVu) {
        String sql = "SELECT HeSoLuong FROM chuc_vu WHERE IDChucVu = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idChucVu);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("HeSoLuong");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ONE; // Default fallback
    }

    public String layHoTenTheoIDNhanVien(int idNhanVien) {
        String sql = "SELECT HoTen FROM nhan_vien WHERE IDNhanVien = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idNhanVien);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("HoTen");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String layTenChucVuTheoIdCV(int idChucVu) {
        String sql = "SELECT TenChucVu FROM chuc_vu WHERE IDChucVu = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idChucVu);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("TenChucVu");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}