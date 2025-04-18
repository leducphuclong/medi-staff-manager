package com.MediStaffManager.bo;

import com.MediStaffManager.dao.EmployeeDAO;
import com.MediStaffManager.bean.Employee;
import java.util.ArrayList;
import java.util.List;

public class EmployeeBO {
    private EmployeeDAO employeeDAO;

    public EmployeeBO() {
        this.employeeDAO = new EmployeeDAO();
    }

    public List<Employee> getAllEmployees() {
        try {
            return employeeDAO.getAllEmployees();
        } catch (Exception e) {
            System.out.println("Error in EmployeeBO: Unable to retrieve employees!");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Employee> searchEmployees(String keyword, String criteria) {
        try {
            return employeeDAO.searchEmployees(keyword, criteria);
        } catch (Exception e) {
            System.out.println("Error in EmployeeBO: Unable to search employees!");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void updateEmployee(Employee employee) {
        try {
            employeeDAO.updateEmployee(employee);
        } catch (Exception e) {
            System.out.println("Error in EmployeeBO: Unable to update employee!");
            e.printStackTrace();
        }
    }

    public void deleteEmployee(int idNhanVien) {
        try {
            employeeDAO.deleteEmployee(idNhanVien);
        } catch (Exception e) {
            System.out.println("Error in EmployeeBO: Unable to delete employee!");
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi xóa nhân viên!");
        }
    }

    public int addEmployee(Employee employee) {
        try {
            return employeeDAO.addEmployee(employee);
        } catch (Exception e) {
            System.out.println("Error in EmployeeBO: Unable to add employee!");
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi thêm nhân viên: " + e.getMessage());
        }
    }

    public String checkDuplicate(String cccd, String sdt, String email) {
        try {
            return employeeDAO.checkDuplicate(cccd, sdt, email);
        } catch (Exception e) {
            System.out.println("Error in EmployeeBO: Unable to check duplicate!");
            e.printStackTrace();
            return "Lỗi khi kiểm tra trùng lặp!";
        }
    }
}
//package com.MediStaffManager.bo;
//
//import com.MediStaffManager.dao.EmployeeDAO;
//import com.MediStaffManager.bean.Employee;
//import java.util.ArrayList;
//import java.util.List;
//
//public class EmployeeBO {
//    private EmployeeDAO employeeDAO;
//
//    public EmployeeBO() {
//        this.employeeDAO = new EmployeeDAO();
//    }
//
//    public List<Employee> getAllEmployees() {
//        try {
//            return employeeDAO.getAllEmployees();
//        } catch (Exception e) {
//            System.out.println("Error in EmployeeBO: Unable to retrieve employees!");
//            e.printStackTrace();
//            return new ArrayList<>();
//        }
//    }
//
//    public List<Employee> searchEmployees(String keyword, String criteria) {
//        try {
//            return employeeDAO.searchEmployees(keyword, criteria);
//        } catch (Exception e) {
//            System.out.println("Error in EmployeeBO: Unable to search employees!");
//            e.printStackTrace();
//            return new ArrayList<>();
//        }
//    }
//
//    public void updateEmployee(Employee employee) {
//        try {
//            employeeDAO.updateEmployee(employee);
//        } catch (Exception e) {
//            System.out.println("Error in EmployeeBO: Unable to update employee!");
//            e.printStackTrace();
//        }
//    }
//
//    public void deleteEmployee(int idNhanVien) {
//        try {
//            employeeDAO.deleteEmployee(idNhanVien);
//        } catch (Exception e) {
//            System.out.println("Error in EmployeeBO: Unable to delete employee!");
//            e.printStackTrace();
//            throw new RuntimeException("Lỗi khi xóa nhân viên!");
//        }
//    }
//
//    public String checkDuplicate(String cccd, String sdt, String email) {
//        try {
//            return employeeDAO.checkDuplicate(cccd, sdt, email);
//        } catch (Exception e) {
//            System.out.println("Error in EmployeeBO: Unable to check duplicate!");
//            e.printStackTrace();
//            return "Lỗi khi kiểm tra trùng lặp!";
//        }
//    }
//}