package com.MediStaffManager.dao;

import java.sql.*;
import java.util.*;
import java.math.BigDecimal;
import com.MediStaffManager.bean.LuongNhanVien;
import com.MediStaffManager.bean.ThongKeResult;
import com.MediStaffManager.utils.DBConnection;

public class LuongNhanVienDAO {
    private Connection connection;

    public LuongNhanVienDAO() {
        this.connection = DBConnection.connect();
    }
    public BigDecimal layHeSoLuongTheoChucVu(int idChucVu) {
        String sql = "SELECT HeSoLuong FROM chuc_vu WHERE IDChucVu = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idChucVu);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("HeSoLuong");
                } else {
                    System.err.println("Không tìm thấy HeSoLuong cho IDChucVu: " + idChucVu);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy HeSoLuong: " + e.getMessage());
        }

        return BigDecimal.ZERO;  // Trả về 0 nếu không tìm thấy
    }

    
    public String layHoTenTheoIDNhanVien(int idNhanVien) {
    	String sql = "SELECT HoTen FROM nhan_vien WHERE IDNhanVien=?";
    	try (PreparedStatement stmt = connection.prepareStatement(sql)){
    		stmt.setInt(1,idNhanVien);
    		try (ResultSet rs = stmt.executeQuery()){
    			if(rs.next()) {
    				return rs.getString("HoTen");
    			}
    			else {
    				System.err.println("Không tìm thấy HoTen cho IDNhanVien: " + idNhanVien);
    			}
    		}
    	}
    		catch (SQLException e) {
    			 System.err.println("Lỗi khi lấy HoTen: " + e.getMessage());
    		}
    		return null;
    }
    
    public List<LuongNhanVien> timKiemLuongTheoIDNhanVien(int idNhanVien) {
        List<LuongNhanVien> list = new ArrayList<>();

        String sql = """
            SELECT l.IDLuong, l.IDNhanVien, n.HoTen, l.IDChucVu, l.ThangNam, l.LuongThuNhap, 
                   l.Thuong, l.PhuCap, l.TangCa, l.TongLuong
            FROM luong_nhan_vien l
            JOIN nhan_vien n ON l.IDNhanVien = n.IDNhanVien
            WHERE l.IDNhanVien = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idNhanVien);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int idChucVu = rs.getInt("IDChucVu");
                    LuongNhanVien luong = new LuongNhanVien(
                    	rs.getInt("IDLuong"),
                    	idChucVu,
                        rs.getInt("IDNhanVien"),
                        rs.getString("ThangNam"),
                        rs.getBigDecimal("LuongThuNhap"),
                        rs.getBigDecimal("Thuong"),
                        rs.getBigDecimal("PhuCap"),
                        rs.getBigDecimal("TangCa"),
                       BigDecimal.ZERO // Tổng lương sẽ được tính sau
                    );
                    BigDecimal tongLuong = tinhTongLuong(luong);
                    luong.setTongLuong(tongLuong);
                    list.add(luong);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm nhân viên: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    
    // Tính tổng lương
    public BigDecimal tinhTongLuong(LuongNhanVien luong) {
        BigDecimal tongLuong = BigDecimal.ZERO;
        try {
            // Lấy HeSoLuong từ phương thức `layHeSoLuongTheoChucVu()`
            BigDecimal heSoLuong = layHeSoLuongTheoChucVu(luong.getIdChucVu());

            if (heSoLuong == null) {
                System.err.println("Không tìm thấy HeSoLuong cho IDChucVu: " + luong.getIdChucVu());
                return tongLuong;
            }

            BigDecimal luongCoBan = luong.getLuongThuNhap() != null ? luong.getLuongThuNhap() : BigDecimal.ZERO;
            BigDecimal thuong = luong.getThuong() != null ? luong.getThuong() : BigDecimal.ZERO;
            BigDecimal phuCap = luong.getPhuCap() != null ? luong.getPhuCap() : BigDecimal.ZERO;
            BigDecimal tangCa = luong.getTangCa() != null ? luong.getTangCa() : BigDecimal.ZERO;

            // Tính tổng lương
            tongLuong = luongCoBan.multiply(heSoLuong)
                        .add(thuong)
                        .add(phuCap)
                        .add(tangCa); // nghiên cứu lại luong co ban/22/8 -> 1h

        } catch (Exception e) {
            System.err.println("Lỗi khi tính tổng lương: " + e.getMessage());
            e.printStackTrace();
        }

        return tongLuong;
    }


    public List<LuongNhanVien> layLuongTheoThang(String thangNam) {
        List<LuongNhanVien> list = new ArrayList<>();
        String sql = """
            SELECT IDLuong, IDChucVu, IDNhanVien, ThangNam, LuongThuNhap, Thuong, PhuCap, TangCa, TongLuong
            FROM luong_nhan_vien
            WHERE ThangNam = ?
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, thangNam);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int idChucVu = rs.getInt("IDChucVu");
                    BigDecimal heSoLuong = layHeSoLuongTheoChucVu(idChucVu);
                    
                    LuongNhanVien luong = new LuongNhanVien(
                        rs.getInt("IDLuong"),
                        idChucVu,
                        rs.getInt("IDNhanVien"),
                        rs.getString("ThangNam"),
                        rs.getBigDecimal("LuongThuNhap"),
                        rs.getBigDecimal("Thuong"),
                        rs.getBigDecimal("PhuCap"),
                        rs.getBigDecimal("TangCa"),
                    	BigDecimal.ZERO // Tổng lương sẽ được tính sau
                    );
                    BigDecimal tongLuong = tinhTongLuong(luong);
                    luong.setTongLuong(tongLuong);
                    
                    list.add(luong);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi truy vấn bảng lương: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    //thêm một bảng lương mới
    public boolean themLuong(LuongNhanVien luong) {
        String sql = "INSERT INTO luong_nhan_vien (IDChucVu, IDNhanVien, ThangNam, LuongThuNhap, Thuong, PhuCap, TangCa, TongLuong) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, luong.getIdChucVu());
            stmt.setInt(2, luong.getIdNhanVien());
            stmt.setString(3, luong.getThangNam());
            stmt.setBigDecimal(4, luong.getLuongThuNhap());
            stmt.setBigDecimal(5, luong.getThuong());
            stmt.setBigDecimal(6, luong.getPhuCap());
            stmt.setBigDecimal(7, luong.getTangCa());
            
            // Tính tổng lương trước khi thêm
            luong.setTongLuong(tinhTongLuong(luong));
            stmt.setBigDecimal(8, luong.getTongLuong()); 

            return stmt.executeUpdate() > 0;
        } 
        catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.err.println("Lỗi: bản ghi lương đã tồn tại!");
            } else {
                System.err.println("Lỗi khi thêm lương: " + e.getMessage());
            }
            return false;
        }
    }


    public boolean capNhatLuong(LuongNhanVien luong) {
        // Tính lại tổng lương (nếu cần)
        BigDecimal tongLuong = tinhTongLuong(luong);
        luong.setTongLuong(tongLuong);
        String sql = """
            UPDATE luong_nhan_vien
            SET IDChucVu    = ?,
                LuongThuNhap = ?,
                Thuong       = ?,
                PhuCap       = ?,
                TangCa       = ?,
                TongLuong    = ?
            WHERE IDLuong     = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, luong.getIdChucVu());
            stmt.setBigDecimal(2, luong.getLuongThuNhap());
            stmt.setBigDecimal(3, luong.getThuong());
            stmt.setBigDecimal(4, luong.getPhuCap());
            stmt.setBigDecimal(5, luong.getTangCa());
            stmt.setBigDecimal(6, luong.getTongLuong());
            stmt.setInt(7, luong.getIdLuong());           // idLuong định danh bản ghi

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật lương: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    //xóa lương theo ID
    public boolean xoaLuong(int idLuong) {
        String query = "DELETE FROM luong_nhan_vien WHERE IDLuong = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idLuong);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa lương: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    //tìm kiếm lương theo Chức vụ
    /**
     * Tìm kiếm bảng lương theo ID Chức Vụ.
     * @param idChucVu mã chức vụ
     * @return danh sách LuongNhanVien thỏa mãn
     */
    public List<LuongNhanVien> timKiemLuongTheoChucVu(int idChucVu) {
        List<LuongNhanVien> list = new ArrayList<>();
        String sql = """
            SELECT l.IDLuong, l.IDChucVu, l.IDNhanVien, n.HoTen,
                   l.ThangNam, l.LuongThuNhap, l.Thuong, l.PhuCap, l.TangCa
            FROM luong_nhan_vien l
            JOIN nhan_vien n ON l.IDNhanVien = n.IDNhanVien
            WHERE l.IDChucVu = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idChucVu);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LuongNhanVien luong = new LuongNhanVien(
                        rs.getInt("IDLuong"),
                        rs.getInt("IDChucVu"),
                        rs.getInt("IDNhanVien"),
                        rs.getString("ThangNam"),
                        rs.getBigDecimal("LuongThuNhap"),
                        rs.getBigDecimal("Thuong"),
                        rs.getBigDecimal("PhuCap"),
                        rs.getBigDecimal("TangCa"),
                        BigDecimal.ZERO  // tính sau
                    );
                    luong.setTongLuong(tinhTongLuong(luong));
                    list.add(luong);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm theo Chức Vụ: " + e.getMessage());
        }
        return list;
    }


    
    //Các chức năng thống kê
    /**
     * Thống kê lương theo Tháng.
     * @param thangNam Chuỗi "YYYY-MM", ví dụ "2025-06"
     * @return ThongKeResult chứa (soNhanVien, luongTrungBinh, tongLuong)
     */
    public ThongKeResult thongKeTheoThang(String thangNam) {
        ThongKeResult result = new ThongKeResult(0, BigDecimal.ZERO, BigDecimal.ZERO);
        String sql = """
            SELECT 
              COUNT(DISTINCT l.IDNhanVien) AS soNV,
              AVG(l.TongLuong)            AS luongTB,
              SUM(l.TongLuong)            AS tongLuong
            FROM luong_nhan_vien l
            WHERE l.ThangNam = ?
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, thangNam);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int soNV = rs.getInt("soNV");
                    BigDecimal luongTB = rs.getBigDecimal("luongTB");
                    BigDecimal tongLuong = rs.getBigDecimal("tongLuong");
                    if (luongTB == null) luongTB = BigDecimal.ZERO;
                    if (tongLuong == null) tongLuong = BigDecimal.ZERO;
                    result = new ThongKeResult(soNV, luongTB, tongLuong);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi thống kê theo Tháng: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Thống kê lương theo Quý.
     * Quý 1: tháng 01-03
     * Quý 2: tháng 04-06
     * Quý 3: tháng 07-09
     * Quý 4: tháng 10-12
     * @param nam  Năm, ví dụ 2025
     * @param quy  Số quý: 1, 2, 3 hoặc 4
     * @return ThongKeResult chứa (soNhanVien, luongTrungBinh, tongLuong)
     */
    public ThongKeResult thongKeTheoQuy(int nam, int quy) {
        ThongKeResult result = new ThongKeResult(0, BigDecimal.ZERO, BigDecimal.ZERO);
        // Xác định khoảng tháng cho quý
        String startMonth, endMonth;
        switch (quy) {
            case 1:
                startMonth = nam + "-01";
                endMonth   = nam + "-03";
                break;
            case 2:
                startMonth = nam + "-04";
                endMonth   = nam + "-06";
                break;
            case 3:
                startMonth = nam + "-07";
                endMonth   = nam + "-09";
                break;
            case 4:
                startMonth = nam + "-10";
                endMonth   = nam + "-12";
                break;
            default:
                return result; // Nếu quý không hợp lệ
        }

        String sql = """
            SELECT
              COUNT(DISTINCT l.IDNhanVien) AS soNV,
              AVG(l.TongLuong)            AS luongTB,
              SUM(l.TongLuong)            AS tongLuong
            FROM luong_nhan_vien l
            WHERE l.ThangNam >= ? AND l.ThangNam <= ?
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, startMonth);
            stmt.setString(2, endMonth);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int soNV = rs.getInt("soNV");
                    BigDecimal luongTB = rs.getBigDecimal("luongTB");
                    BigDecimal tongLuong = rs.getBigDecimal("tongLuong");
                    if (luongTB == null) luongTB = BigDecimal.ZERO;
                    if (tongLuong == null) tongLuong = BigDecimal.ZERO;
                    result = new ThongKeResult(soNV, luongTB, tongLuong);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi thống kê theo Quý: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Thống kê lương theo Năm.
     * @param nam  Năm, ví dụ 2025
     * @return ThongKeResult chứa (soNhanVien, luongTrungBinh, tongLuong)
     */
    public ThongKeResult thongKeTheoNam(int nam) {
        ThongKeResult result = new ThongKeResult(0, BigDecimal.ZERO, BigDecimal.ZERO);
        String sql = """
            SELECT
              COUNT(DISTINCT l.IDNhanVien) AS soNV,
              AVG(l.TongLuong)            AS luongTB,
              SUM(l.TongLuong)            AS tongLuong
            FROM luong_nhan_vien l
            WHERE l.ThangNam LIKE ?
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nam + "-%"); // Ví dụ "2025-%"
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int soNV = rs.getInt("soNV");
                    BigDecimal luongTB = rs.getBigDecimal("luongTB");
                    BigDecimal tongLuong = rs.getBigDecimal("tongLuong");
                    if (luongTB == null) luongTB = BigDecimal.ZERO;
                    if (tongLuong == null) tongLuong = BigDecimal.ZERO;
                    result = new ThongKeResult(soNV, luongTB, tongLuong);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi thống kê theo Năm: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
    
    
    



}