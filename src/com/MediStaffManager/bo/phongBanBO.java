package com.MediStaffManager.bo;

import com.MediStaffManager.dao.phongBanDAO;
import com.MediStaffManager.bean.NhanVien;
import com.MediStaffManager.dao.NhanVienDAO;

import java.util.List;

public class phongBanBO {
	private NhanVienDAO nhanVienDAO;
    private phongBanDAO PhongBanDAO;

    public phongBanBO() {
        this.PhongBanDAO = new phongBanDAO();
        this.nhanVienDAO = new NhanVienDAO();
    }
    
    // Phương thức tìm kiếm phòng ban theo tên
    public List<Object[]> timKiemPhongBanTheoTen(String tenPhongBan) {
        return PhongBanDAO.timKiemPhongBanTheoTen(tenPhongBan);
    }
    
    // Phương thức tìm kiếm phòng ban theo ID
    public List<Object[]> timKiemPhongBanTheoId(int idPhongBan) {
        return PhongBanDAO.timKiemPhongBanTheoId(idPhongBan);
    }
    
    public List<Object[]> layDanhSachPhongBan() {
        return PhongBanDAO.layDanhSachPhongBan();
    }

    public boolean xoaPhongBan(String tenPhongBan) {
        return PhongBanDAO.xoaPhongBan(tenPhongBan);
    }

    public boolean xoaPhongBanById(int idPhongBan) {
        return PhongBanDAO.xoaPhongBanById(idPhongBan);
    }
    
    public List<NhanVien> layNhanVienTheoPhongBan(String tenPhongBan) {
        return nhanVienDAO.layNhanVienTheoPhongBan(tenPhongBan);
    }
    
    public boolean themPhongBan(int idPhongBan, String tenPhongBan) {
        return PhongBanDAO.themPhongBan(idPhongBan, tenPhongBan);
    }
    
    public boolean suaPhongBan(int idPhongBanCu, int idPhongBanMoi, String tenPhongBanMoi) {
        return PhongBanDAO.suaPhongBan(idPhongBanCu, idPhongBanMoi, tenPhongBanMoi);
    }

    public int layIdPhongBanTheoTen(String tenPhongBan) {
        return PhongBanDAO.layIdPhongBanTheoTen(tenPhongBan);
    }
}