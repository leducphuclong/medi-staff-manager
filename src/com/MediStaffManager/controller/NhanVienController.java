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
    
    // Method to delete an employee from a department
    public boolean xoaNhanVienTrongPhongBan(int idNhanVien, int idPhongBan) {
        return nhanVienBO.xoaNhanVienTrongPhongBan(idNhanVien, idPhongBan); // Gọi phương thức từ BO
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
    
    public boolean xoaNhieuNhanVien(List<Integer> ids) {
        return nhanVienBO.xoaNhieuNhanVien(ids);
    }
    
    public List<NhanVien> layNhanVienTheoPhongBan(String tenPhongBan) {
    	return phongBanBO.layNhanVienTheoPhongBan(tenPhongBan);
    }
    
    public boolean xoaTatCaNhanVienTrongPhongBan(String tenPhongBan) {
    	return nhanVienBO.xoaNhanVienTrongPhongBan(tenPhongBan);
    }
    
    // Sửa đổi để gọi xoaPhongBanById từ phongBanBO
    public boolean xoaPhongBan(String idPhongBanStr) { // Nhận ID dưới dạng String từ Bridge
        try {
            int idPhongBan = Integer.parseInt(idPhongBanStr);
            return phongBanBO.xoaPhongBanById(idPhongBan); // Gọi phương thức mới trong BO
        } catch (NumberFormatException e) {
            System.err.println("Controller: ID phòng ban không hợp lệ: " + idPhongBanStr);
            return false;
        }
    }
    
    public List<NhanVien> layNhanVienTheoIdPhongBan(int idPhongBan) {
        return nhanVienBO.layNhanVienTheoIdPhongBan(idPhongBan);
    }

    
    public boolean themPhongBan(int idPhongBan, String tenPhongBan) {
        return phongBanBO.themPhongBan(idPhongBan, tenPhongBan);
    }
    
    public boolean suaPhongBan(int idPhongBanCu, int idPhongBanMoi, String tenPhongBanMoi) {
        return phongBanBO.suaPhongBan(idPhongBanCu, idPhongBanMoi, tenPhongBanMoi);
    }

    public int layIdPhongBanTheoTen(String tenPhongBan) {
        return phongBanBO.layIdPhongBanTheoTen(tenPhongBan);
    }
    
    public boolean chuyenPhongBan(List<Integer> danhSachIdNhanVien, int idPhongBanMoi) {
        return nhanVienBO.chuyenPhongBan(danhSachIdNhanVien, idPhongBanMoi);
    }
    
    // Phương thức tìm kiếm phòng ban theo tên
    public List<Object[]> timKiemPhongBanTheoTen(String tenPhongBan) {
        return phongBanBO.timKiemPhongBanTheoTen(tenPhongBan);
    }
    
    // Phương thức tìm kiếm phòng ban theo ID
    public List<Object[]> timKiemPhongBanTheoId(int idPhongBan) {
        try {
            return phongBanBO.timKiemPhongBanTheoId(idPhongBan);
        } catch (NumberFormatException e) {
            System.err.println("Controller: ID phòng ban không hợp lệ: " + idPhongBan);
            return null;
        }
    }
}

