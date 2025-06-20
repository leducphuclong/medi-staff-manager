package com.MediStaffManager.bo;

import com.MediStaffManager.dao.PhongBanDAO;
import com.MediStaffManager.bean.NhanVienBEAN;
import com.MediStaffManager.dao.NhanVienDAO2;

import java.util.List;

public class PhongBanBO {
	private NhanVienDAO2 nhanVienDAO;
    private PhongBanDAO PhongBanDAO;

    public PhongBanBO() {
        this.PhongBanDAO = new PhongBanDAO();
        this.nhanVienDAO = new NhanVienDAO2();
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
    
    public List<NhanVienBEAN> layNhanVienTheoPhongBan(String tenPhongBan) {
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