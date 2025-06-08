package com.MediStaffManager.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.MediStaffManager.bean.CaLamViecBEAN;
import com.MediStaffManager.utils.DBConnection;

public class CaLamViecDAO {
    private Connection connection;

    public CaLamViecDAO() {
        this.connection = DBConnection.connect();
    }

    public List<CaLamViecBEAN> layCaLamViecTheoNgayLamViec(String ngayLamViec) {
        List<CaLamViecBEAN> caLamViecList = new ArrayList<>();
        String sql = "SELECT cl.IDCaLam, nv.IDNhanVien, nv.HoTen, cl.NgayLamViec, cl.tenCa, cl.moTaCa, " +
                     "cl.GioBatDauThucTe, cl.GioKetThucThucTe, cl.GioNghiBatDau, cl.GioNghiKetThuc, cl.DonVi, " +
                     "cl.GhiChu, cl.laTrucOnCall " +
                     "FROM ca_lam_viec cl " +
                     "JOIN nhan_vien nv ON cl.IDNhanVien = nv.IDNhanVien " +
                     "WHERE cl.NgayLamViec = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, ngayLamViec);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int idCaLam = rs.getInt("IDCaLam");
                int idNhanVien = rs.getInt("IDNhanVien");
                String tenNhanVien = rs.getString("HoTen");
                String tenCa = rs.getString("tenCa");
                String moTaCa = rs.getString("moTaCa");
                String gioBatDauThucTe = rs.getString("GioBatDauThucTe");
                String gioKetThucThucTe = rs.getString("GioKetThucThucTe");
                String gioNghiBatDau = rs.getString("GioNghiBatDau");
                String gioNghiKetThuc = rs.getString("GioNghiKetThuc");
                String donVi = rs.getString("DonVi");
                String ghiChu = rs.getString("GhiChu");
                boolean laTrucOnCall = rs.getBoolean("laTrucOnCall");

                CaLamViecBEAN caLamViecBEAN = new CaLamViecBEAN(idCaLam, idNhanVien, tenNhanVien, ngayLamViec, 
                                                      tenCa, moTaCa, gioBatDauThucTe, gioKetThucThucTe, 
                                                      gioNghiBatDau, gioNghiKetThuc, donVi, ghiChu, laTrucOnCall);

                caLamViecList.add(caLamViecBEAN);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return caLamViecList;
    }

    public void capNhatCaLamViec(CaLamViecBEAN caLamViec) {
        String updateShiftSQL = "UPDATE ca_lam_viec SET " +
                                "IDNhanVien = ?, tenCa = ?, moTaCa = ?, GioBatDauThucTe = ?, GioKetThucThucTe = ?, " +
                                "GioNghiBatDau = ?, GioNghiKetThuc = ?, DonVi = ?, GhiChu = ?, laTrucOnCall = ? " +
                                "WHERE IDCaLam = ?";

        try {
            connection.setAutoCommit(false);

            String gioBatDauThucTeWithDate = caLamViec.getGioBatDauThucTe();
            String gioKetThucThucTeWithDate = caLamViec.getGioKetThucThucTe();
            String gioNghiBatDauWithDate = caLamViec.getGioNghiBatDau();
            String gioNghiKetThucWithDate = caLamViec.getGioNghiKetThuc();

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateShiftSQL)) {
                preparedStatement.setInt(1, caLamViec.getIdNhanVien());
                preparedStatement.setString(2, caLamViec.getTenCa());
                preparedStatement.setString(3, caLamViec.getMoTaCa());
                preparedStatement.setString(4, gioBatDauThucTeWithDate);
                preparedStatement.setString(5, gioKetThucThucTeWithDate);
                preparedStatement.setString(6, gioNghiBatDauWithDate);
                preparedStatement.setString(7, gioNghiKetThucWithDate);
                preparedStatement.setString(8, caLamViec.getDonVi());
                preparedStatement.setString(9, caLamViec.getGhiChu());
                preparedStatement.setBoolean(10, caLamViec.isLaTrucOnCall());
                preparedStatement.setInt(11, caLamViec.getIdCaLam());

                preparedStatement.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public CaLamViecBEAN layCaLamViecTheoIdCaLam(int idCaLam) {
        CaLamViecBEAN caLamViecBEAN = null;
        String sql = "SELECT cl.IDCaLam, nv.IDNhanVien, nv.HoTen, cl.NgayLamViec, cl.tenCa, cl.moTaCa, " +
                     "cl.GioBatDauThucTe, cl.GioKetThucThucTe, cl.GioNghiBatDau, cl.GioNghiKetThuc, " +
                     "cl.DonVi, cl.GhiChu, cl.laTrucOnCall " +
                     "FROM ca_lam_viec cl " +
                     "JOIN nhan_vien nv ON cl.IDNhanVien = nv.IDNhanVien " +
                     "WHERE cl.IDCaLam = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, idCaLam);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                int idNhanVien = rs.getInt("IDNhanVien");
                String tenNhanVien = rs.getString("HoTen");
                String ngayLamViec = rs.getString("NgayLamViec");
                String tenCa = rs.getString("tenCa");
                String moTaCa = rs.getString("moTaCa");
                String gioBatDauThucTe = rs.getString("GioBatDauThucTe");
                String gioKetThucThucTe = rs.getString("GioKetThucThucTe");
                String gioNghiBatDau = rs.getString("GioNghiBatDau");
                String gioNghiKetThuc = rs.getString("GioNghiKetThuc");
                String donVi = rs.getString("DonVi");
                String ghiChu = rs.getString("GhiChu");
                boolean laTrucOnCall = rs.getBoolean("laTrucOnCall");

                caLamViecBEAN = new CaLamViecBEAN(idCaLam, idNhanVien, tenNhanVien, ngayLamViec, 
                                                  tenCa, moTaCa, gioBatDauThucTe, gioKetThucThucTe, 
                                                  gioNghiBatDau, gioNghiKetThuc, donVi, ghiChu, laTrucOnCall);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return caLamViecBEAN;
    }
    
    public void xoaCaLamViecTheoIdCaLam(int idCaLam) {
        String deleteShiftSQL = "DELETE FROM ca_lam_viec WHERE IDCaLam = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteShiftSQL)) {
            preparedStatement.setInt(1, idCaLam);
            int rowsAffected = preparedStatement.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Xóa ca làm việc thành công.");
            } else {
                System.out.println("Không tìm thấy ca làm việc với IDCaLam: " + idCaLam);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void themCaLamViec(CaLamViecBEAN caLamViec) {
        if (!doesIdNhanVienExist(caLamViec.getIdNhanVien())) {
            System.out.println("Skipping entry with IDNhanVien: " + caLamViec.getIdNhanVien() + " because it does not exist.");
            return;  // Skip inserting if IDNhanVien does not exist in nhan_vien
        }

        if (doesRecordExist(caLamViec.getIdNhanVien(), caLamViec.getNgayLamViec(), caLamViec.getTenCa())) {
            System.out.println("Duplicate entry found. Skipping insert.");
            return; 
        }

        String insertShiftSQL = "INSERT INTO ca_lam_viec (IDNhanVien, tenCa, moTaCa, GioBatDauThucTe, GioKetThucThucTe, " +
                                "GioNghiBatDau, GioNghiKetThuc, DonVi, GhiChu, laTrucOnCall, NgayLamViec) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertShiftSQL)) {
            preparedStatement.setInt(1, caLamViec.getIdNhanVien());
            preparedStatement.setString(2, caLamViec.getTenCa());
            preparedStatement.setString(3, caLamViec.getMoTaCa());
            preparedStatement.setString(4, caLamViec.getGioBatDauThucTe());
            preparedStatement.setString(5, caLamViec.getGioKetThucThucTe());
            preparedStatement.setString(6, caLamViec.getGioNghiBatDau());
            preparedStatement.setString(7, caLamViec.getGioNghiKetThuc());
            preparedStatement.setString(8, caLamViec.getDonVi());
            preparedStatement.setString(9, caLamViec.getGhiChu());
            preparedStatement.setBoolean(10, caLamViec.isLaTrucOnCall());
            preparedStatement.setString(11, caLamViec.getNgayLamViec());

            preparedStatement.executeUpdate();
            System.out.println("Ca làm việc được thêm thành công!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean doesIdNhanVienExist(int idNhanVien) {
        String query = "SELECT COUNT(*) FROM nhan_vien WHERE IDNhanVien = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, idNhanVien);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;  
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  
    }


    private boolean doesRecordExist(int idNhanVien, String ngayLamViec, String tenCa) {
        String query = "SELECT COUNT(*) FROM ca_lam_viec WHERE IDNhanVien = ? AND NgayLamViec = ? AND tenCa = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, idNhanVien);
            preparedStatement.setString(2, ngayLamViec);
            preparedStatement.setString(3, tenCa);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0; 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public void xoaCaLamViecTheoThangNam(String monthYear) {
        String deleteShiftSQL = "DELETE FROM ca_lam_viec WHERE DATE_FORMAT(NgayLamViec, '%Y-%m') = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteShiftSQL)) {
            preparedStatement.setString(1, monthYear);
            int rowsAffected = preparedStatement.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("All shifts for " + monthYear + " were deleted successfully.");
            } else {
                System.out.println("No shifts found for " + monthYear);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
