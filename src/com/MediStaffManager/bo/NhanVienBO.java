package com.MediStaffManager.bo;

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
    
    public boolean xoaNhanVien(int idNhanVien) {
    	return employeeDAO.xoaNhanVien(idNhanVien);
    }
}
