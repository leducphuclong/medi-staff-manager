package com.MediStaffManager.controller;

import com.MediStaffManager.bo.NhanVienBO;  // Updated BO import
import com.MediStaffManager.bean.NhanVien;  // Updated bean import
import java.util.List;
import com.MediStaffManager.bo.phongBanBO;
import com.MediStaffManager.bean.NhanVien;


public class NhanVienController {
    private NhanVienBO nhanVienBO;
    private phongBanBO phongBanBO;

    // Constructor to initialize the BO (Business Object)
    public NhanVienController() {
        this.nhanVienBO = new NhanVienBO();  // Initializes the NhanVienBO to handle business logic
        this.phongBanBO = new phongBanBO();
    }

    // Method to retrieve all employees from the BO
    public List<NhanVien> layToanBoNhanVien() {
        return nhanVienBO.layToanBoNhanVien();  // Call the BO to fetch employee data
    }
    
    // Method to retrieve all department from the BO
    public List<Object[]> layDanhSachPhongBan() {
        return phongBanBO.layDanhSachPhongBan();
    }
    
    public boolean xoaNhanVien(int idNhanVien) {
    	return nhanVienBO.xoaNhanVien(idNhanVien);
    }

    // Optionally, you can add more methods to handle specific operations:
    // Example: public NhanVien getEmployeeById(int id) { return nhanVienBO.getEmployeeById(id); }
    
    public List<NhanVien> layNhanVienTheoPhongBan(String tenPhongBan) {
    	return phongBanBO.layNhanVienTheoPhongBan(tenPhongBan);
    }
    
    public boolean xoaTatCaNhanVienTrongPhongBan(String tenPhongBan) {
    	return nhanVienBO.xoaNhanVienTrongPhongBan(tenPhongBan);
    }
    
    public boolean xoaPhongBan(String tenPhongBan) {
        return phongBanBO.xoaPhongBan(tenPhongBan);
    }
    
    public boolean themPhongBan(int idPhongBan, String tenPhongBan) {
        return phongBanBO.themPhongBan(idPhongBan, tenPhongBan);
    }
    
    public boolean suaPhongBan(int idPhongBanCu, int idPhongBanMoi, String tenPhongBanMoi) {
        return phongBanBO.suaPhongBan(idPhongBanCu, idPhongBanMoi, tenPhongBanMoi);
    }
}
