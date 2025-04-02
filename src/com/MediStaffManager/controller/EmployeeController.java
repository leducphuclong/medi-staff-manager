package com.MediStaffManager.controller;

import com.MediStaffManager.bo.EmployeeBO;
import com.MediStaffManager.bean.Employee;
import java.util.List;

public class EmployeeController {
    private EmployeeBO employeeBO;

    // Constructor khởi tạo đối tượng BO
    public EmployeeController() {
        this.employeeBO = new EmployeeBO();
    }

    // Phương thức lấy danh sách nhân viên
    public List<Employee> getAllEmployees() {
        return employeeBO.getAllEmployees();
    }
}
