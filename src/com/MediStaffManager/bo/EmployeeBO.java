package com.MediStaffManager.bo;

import com.MediStaffManager.dao.EmployeeDAO;
import com.MediStaffManager.bean.Employee;
import java.util.List;

public class EmployeeBO {
    private EmployeeDAO employeeDAO;

    public EmployeeBO() {
        this.employeeDAO = new EmployeeDAO();
    }

    public List<Employee> getAllEmployees() {
        return employeeDAO.getAllEmployees();
    }
}
