package com.MediStaffManager.dao;

import java.sql.*;
import java.util.*;
import com.MediStaffManager.bean.NhanVien;
import com.MediStaffManager.utils.DBConnection;

public class NhanVienDAO {
    private Connection connection;

    // Constructor để thiết lập kết nối cơ sở dữ liệu
    public NhanVienDAO() {
        this.connection = DBConnection.connect();
    }

    // Phương thức lấy tất cả nhân viên, bao gồm cả thông tin chức vụ và phòng ban
    public List<NhanVien> layToanBoNhanVien() {
        List<NhanVien> employees = new ArrayList<>();
        String query = "SELECT nv.IDNhanVien, nv.CCCD, nv.HoTen, nv.Sdt, nv.Email, nv.GioiTinh, nv.NgaySinh, " +
                       "nv.IDChucVu, nv.IDPhongBan, cv.TenChucVu, pb.TenPhongBan " +
                       "FROM nhan_vien nv " +
                       "JOIN chuc_vu cv ON nv.IDChucVu = cv.IDChucVu " +
                       "JOIN phong_ban pb ON nv.IDPhongBan = pb.IDPhongBan";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int idNhanVien = rs.getInt("IDNhanVien");
                String cccd = rs.getString("CCCD");
                String hoTen = rs.getString("HoTen");
                String sdt = rs.getString("Sdt");
                String email = rs.getString("Email");
                String gioiTinh = rs.getString("GioiTinh");
                String ngaySinh = rs.getString("NgaySinh");
                int idChucVu = rs.getInt("IDChucVu");
                int idPhongBan = rs.getInt("IDPhongBan");
                String tenChucVu = rs.getString("TenChucVu");
                String tenPhongBan = rs.getString("TenPhongBan");

                // Tạo đối tượng NhanVien và thêm vào danh sách
                NhanVien nhanVien = new NhanVien(idNhanVien, cccd, hoTen, sdt, email, gioiTinh, ngaySinh, 
                                                 idChucVu, idPhongBan, tenChucVu, tenPhongBan);
                employees.add(nhanVien);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    
    // Phương thức xóa nhân viên
    public boolean xoaNhanVien(int idNhanVien) {
        String query = "DELETE FROM nhan_vien WHERE IDNhanVien = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idNhanVien);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Phương thức lấy thông tin một nhân viên theo ID
    public NhanVien getNhanVienById(int idNhanVien) {
        String query = "SELECT nv.IDNhanVien, nv.CCCD, nv.HoTen, nv.Sdt, nv.Email, nv.GioiTinh, nv.NgaySinh, " +
                       "nv.IDChucVu, nv.IDPhongBan, cv.TenChucVu, pb.TenPhongBan " +
                       "FROM nhan_vien nv " +
                       "JOIN chuc_vu cv ON nv.IDChucVu = cv.IDChucVu " +
                       "JOIN phong_ban pb ON nv.IDPhongBan = pb.IDPhongBan " +
                       "WHERE nv.IDNhanVien = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idNhanVien);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String cccd = rs.getString("CCCD");
                    String hoTen = rs.getString("HoTen");
                    String sdt = rs.getString("Sdt");
                    String email = rs.getString("Email");
                    String gioiTinh = rs.getString("GioiTinh");
                    String ngaySinh = rs.getString("NgaySinh");
                    int idChucVu = rs.getInt("IDChucVu");
                    int idPhongBan = rs.getInt("IDPhongBan");
                    String tenChucVu = rs.getString("TenChucVu");
                    String tenPhongBan = rs.getString("TenPhongBan");

                    return new NhanVien(idNhanVien, cccd, hoTen, sdt, email, gioiTinh, ngaySinh, 
                                        idChucVu, idPhongBan, tenChucVu, tenPhongBan);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
