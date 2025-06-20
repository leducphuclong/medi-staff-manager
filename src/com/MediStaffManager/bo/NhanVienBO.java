package com.MediStaffManager.bo;

import com.MediStaffManager.dao.NhanVienDAO2;
import com.MediStaffManager.bo.PhongBanBO;
import com.MediStaffManager.bean.NhanVienBEAN;  // Using NhanVien bean
import java.util.List;

public class NhanVienBO {

    private NhanVienDAO2 employeeDAO;

    // Constructor: initializes the DAO
    public NhanVienBO() {
        this.employeeDAO = new NhanVienDAO2();  // Initializes the DAO to interact with DB
    }

    // Method to retrieve all employees from the DAO
    public List<NhanVienBEAN> layToanBoNhanVien() {
        return employeeDAO.layToanBoNhanVien();  // Calls the DAO to get employee data
    }
    public boolean xoaNhieuNhanVien(List<Integer> ids) {
        return employeeDAO.xoaNhieuNhanVien(ids);
    }
    
    public boolean xoaNhanVien(int idNhanVien) {
    	return employeeDAO.xoaNhanVien(idNhanVien);
    }
    
    public boolean xoaNhanVienTrongPhongBan(int idNhanVien, int idPhongBan) {
        return employeeDAO.xoaNhanVienTrongPhongBan(idNhanVien, idPhongBan);
    }
    
    public List<NhanVienBEAN> layNhanVienTheoPhongBan(String tenPhongBan) {
    	return employeeDAO.layNhanVienTheoPhongBan(tenPhongBan);
    }
    
    public boolean xoaNhanVienTrongPhongBan(String tenPhongBan) {
    	return employeeDAO.xoaTatCaNhanVienTrongPhongBan(tenPhongBan);
    }
    
    public boolean chuyenPhongBan(List<Integer> danhSachIdNhanVien, int idPhongBanMoi) {
        return employeeDAO.chuyenPhongBan(danhSachIdNhanVien, idPhongBanMoi);
    }
    
    public List<NhanVienBEAN> layNhanVienTheoIdPhongBan(int idPhongBan) {
        return employeeDAO.layNhanVienTheoIdPhongBan(idPhongBan);
    }

}
