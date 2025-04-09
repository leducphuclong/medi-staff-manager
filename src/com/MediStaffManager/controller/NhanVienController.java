package com.MediStaffManager.controller;

import com.MediStaffManager.bo.NhanVienBO;  // Updated BO import
import com.MediStaffManager.bean.NhanVien;  // Updated bean import
import java.util.List;

public class NhanVienController {
    private NhanVienBO nhanVienBO;

    // Constructor to initialize the BO (Business Object)
    public NhanVienController() {
        this.nhanVienBO = new NhanVienBO();  // Initializes the NhanVienBO to handle business logic
    }

    // Method to retrieve all employees from the BO
    public List<NhanVien> layToanBoNhanVien() {
        return nhanVienBO.layToanBoNhanVien();  // Call the BO to fetch employee data
    }
    
    public boolean xoaNhanVien(int idNhanVien) {
    	return nhanVienBO.xoaNhanVien(idNhanVien);
    }

    // Optionally, you can add more methods to handle specific operations:
    // Example: public NhanVien getEmployeeById(int id) { return nhanVienBO.getEmployeeById(id); }
    
    public List<NhanVien> layNhanVienTheoPhongBan(String tenPhongBan) {
    	return nhanVienBO.layNhanVienTheoPhongBan(tenPhongBan);
    }
    
    public boolean xoaTatCaNhanVienTrongPhongBan(String tenPhongBan) {
    	return nhanVienBO.xoaNhanVienTrongPhongBan(tenPhongBan);
    }
}
