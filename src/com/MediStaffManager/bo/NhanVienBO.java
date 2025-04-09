package com.MediStaffManager.bo;
//NhanVienBO (Business Object) đóng vai trò làm tầng trung gian giữa giao diện (View) và DAO. 

import com.MediStaffManager.dao.NhanVienDAO;
import com.MediStaffManager.bean.NhanVien;  // Using NhanVien bean
import java.util.List;

public class NhanVienBO {

    private NhanVienDAO employeeDAO;

    // Constructor: initializes the DAO
    public NhanVienBO() {
        this.employeeDAO = new NhanVienDAO();  // Initializes the DAO to interact with DB
    }

    // Method to retrieve all employees from the DAO
    public List<NhanVien> layToanBoNhanVien() {
        return employeeDAO.layToanBoNhanVien();  // Calls the DAO to get employee data
    }
    
    //Phuong thuc them nhan vien
    public boolean themNhanVien(NhanVien nv) {
        return employeeDAO.themNhanVien(nv);
    }

    
    public boolean xoaNhanVien(int idNhanVien) {
    	return employeeDAO.xoaNhanVien(idNhanVien);
    }
    
}
