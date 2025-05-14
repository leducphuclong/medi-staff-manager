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
    
    public List<Object[]> layDanhSachPhongBan() {
        return PhongBanDAO.layDanhSachPhongBan();
    }

    public boolean xoaPhongBan(String tenPhongBan) {
        return PhongBanDAO.xoaPhongBan(tenPhongBan);
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