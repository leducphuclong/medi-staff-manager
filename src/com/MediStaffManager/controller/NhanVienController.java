//package com.MediStaffManager.controller;
//
//import com.MediStaffManager.bean.Employee;
//import com.MediStaffManager.bo.EmployeeBO;
//import com.google.gson.Gson;
//import com.google.gson.JsonSyntaxException;
//
//import java.util.Collections;
//import java.util.List;
//
//public class NhanVienController {
//
//    private EmployeeBO employeeBO;
//    private Gson gson; // Sử dụng GSON để parse JSON
//
//    public NhanVienController() {
//        this.employeeBO = new EmployeeBO();
//        this.gson = new Gson();
//    }
//
//    public List<Employee> getAllEmployees() {
//        try {
//            return employeeBO.getAllEmployees();
//        } catch (RuntimeException e) {
//            System.err.println("Controller: Error getting all employees: " + e.getMessage());
//            // e.printStackTrace(); // BO đã log rồi
//            return Collections.emptyList(); // Trả về danh sách rỗng nếu có lỗi
//        }
//    }
//
//    public String addEmployee(String employeeJson) {
//        try {
//            if (employeeJson == null || employeeJson.trim().isEmpty()) {
//                return "Error: Dữ liệu nhân viên không được để trống.";
//            }
//            Employee newEmployee = gson.fromJson(employeeJson, Employee.class);
//            // ID sẽ được tạo bởi CSDL và set lại bởi BO
//            int newId = employeeBO.addEmployee(newEmployee);
//            return "Success: Thêm nhân viên thành công! ID: " + newId;
//        } catch (JsonSyntaxException e) {
//            System.err.println("Controller: Invalid JSON format for addEmployee: " + e.getMessage());
//            return "Error: Dữ liệu gửi lên không đúng định dạng.";
//        } catch (IllegalArgumentException e) { // Lỗi validation từ BO
//            return "Error: " + e.getMessage();
//        } catch (RuntimeException e) { // Lỗi chung từ BO (bao gồm lỗi CSDL)
//            System.err.println("Controller: Runtime error in addEmployee: " + e.getMessage());
//            return "Error: " + e.getMessage(); // Hiển thị thông báo lỗi từ BO
//        } catch (Exception e) {
//            System.err.println("Controller: Unexpected error in addEmployee: " + e.getMessage());
//            e.printStackTrace();
//            return "Error: Lỗi không xác định khi thêm nhân viên.";
//        }
//    }
//
//    public String updateEmployee(String employeeJson) {
//        try {
//             if (employeeJson == null || employeeJson.trim().isEmpty()) {
//                return "Error: Dữ liệu nhân viên không được để trống.";
//            }
//            Employee employeeToUpdate = gson.fromJson(employeeJson, Employee.class);
//            if (employeeToUpdate.getIdNhanVien() <= 0) {
//                 return "Error: ID nhân viên không hợp lệ để cập nhật.";
//            }
//            employeeBO.updateEmployee(employeeToUpdate);
//            return "Success: Cập nhật nhân viên thành công!";
//        } catch (JsonSyntaxException e) {
//            System.err.println("Controller: Invalid JSON format for updateEmployee: " + e.getMessage());
//            return "Error: Dữ liệu gửi lên không đúng định dạng.";
//        } catch (IllegalArgumentException e) {
//            return "Error: " + e.getMessage();
//        } catch (RuntimeException e) {
//            System.err.println("Controller: Runtime error in updateEmployee: " + e.getMessage());
//            return "Error: " + e.getMessage();
//        } catch (Exception e) {
//            System.err.println("Controller: Unexpected error in updateEmployee: " + e.getMessage());
//            e.printStackTrace();
//            return "Error: Lỗi không xác định khi cập nhật nhân viên.";
//        }
//    }
//
//    public String deleteEmployee(int idNhanVien) {
//        try {
//            if (idNhanVien <= 0) {
//                return "Error: ID nhân viên không hợp lệ để xóa.";
//            }
//            employeeBO.deleteEmployee(idNhanVien);
//            return "Success: Xóa nhân viên thành công!";
//        } catch (IllegalArgumentException e) {
//            return "Error: " + e.getMessage();
//        } catch (RuntimeException e) {
//            System.err.println("Controller: Runtime error in deleteEmployee: " + e.getMessage());
//            return "Error: " + e.getMessage();
//        } catch (Exception e) {
//            System.err.println("Controller: Unexpected error in deleteEmployee: " + e.getMessage());
//            e.printStackTrace();
//            return "Error: Lỗi không xác định khi xóa nhân viên.";
//        }
//    }
//
//    public List<Employee> searchEmployees(String keyword, String criteria) {
//        try {
//            // criteria từ frontend là: "hoTen", "cccd", "sdt", "email"
//            return employeeBO.searchEmployees(keyword, criteria);
//        } catch (IllegalArgumentException e){
//            System.err.println("Controller: Invalid search criteria: " + e.getMessage());
//            return Collections.emptyList();
//        } catch (RuntimeException e) {
//            System.err.println("Controller: Error searching employees: " + e.getMessage());
//            return Collections.emptyList();
//        }
//    }
//
//    public List<String> getAllTenChucVu() {
//        try {
//            return employeeBO.getAllTenChucVu();
//        } catch (RuntimeException e) {
//            System.err.println("Controller: Error getting ChucVu list: " + e.getMessage());
//            return Collections.emptyList();
//        }
//    }
//
//    public List<String> getAllTenPhongBan() {
//        try {
//            return employeeBO.getAllTenPhongBan();
//        } catch (RuntimeException e) {
//            System.err.println("Controller: Error getting PhongBan list: " + e.getMessage());
//            return Collections.emptyList();
//        }
//    }
//}
package com.MediStaffManager.controller;

import com.MediStaffManager.bean.Employee;
import com.MediStaffManager.bo.EmployeeBO;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class NhanVienController {

    private EmployeeBO employeeBO;
    private Gson gson;

    public NhanVienController() {
        this.employeeBO = new EmployeeBO();
        this.gson = new Gson();
    }

    public List<Employee> getAllEmployees() {
        try {
            return employeeBO.getAllEmployees();
        } catch (RuntimeException e) {
            System.err.println("Controller: Error getting all employees: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public String addEmployee(String employeeJson) {
        try {
            if (employeeJson == null || employeeJson.trim().isEmpty()) {
                return "Error: Dữ liệu nhân viên không được để trống.";
            }
            Employee newEmployee = gson.fromJson(employeeJson, Employee.class);
            // HeSoLuong sẽ được lấy từ TenChucVu nếu không được cung cấp hoặc có thể được set từ JSON
            if (newEmployee.getHeSoLuong() == null && newEmployee.getTenChucVu() != null) {
                 BigDecimal hsl = employeeBO.getHeSoLuongByTenChucVu(newEmployee.getTenChucVu());
                 newEmployee.setHeSoLuong(hsl);
            }

            int newId = employeeBO.addEmployee(newEmployee);
            return "Success: Thêm nhân viên thành công! ID: " + newId;
        } catch (JsonSyntaxException e) {
            System.err.println("Controller: Invalid JSON format for addEmployee: " + e.getMessage());
            return "Error: Dữ liệu gửi lên không đúng định dạng.";
        } catch (IllegalArgumentException e) {
            return "Error: " + e.getMessage();
        } catch (RuntimeException e) {
            System.err.println("Controller: Runtime error in addEmployee: " + e.getMessage());
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            System.err.println("Controller: Unexpected error in addEmployee: " + e.getMessage());
            e.printStackTrace();
            return "Error: Lỗi không xác định khi thêm nhân viên.";
        }
    }

    public String updateEmployee(String employeeJson) {
        try {
             if (employeeJson == null || employeeJson.trim().isEmpty()) {
                return "Error: Dữ liệu nhân viên không được để trống.";
            }
            Employee employeeToUpdate = gson.fromJson(employeeJson, Employee.class);
            if (employeeToUpdate.getIdNhanVien() <= 0) {
                 return "Error: ID nhân viên không hợp lệ để cập nhật.";
            }
            // Tương tự, cập nhật HeSoLuong từ TenChucVu nếu cần
            if (employeeToUpdate.getHeSoLuong() == null && employeeToUpdate.getTenChucVu() != null) {
                 BigDecimal hsl = employeeBO.getHeSoLuongByTenChucVu(employeeToUpdate.getTenChucVu());
                 employeeToUpdate.setHeSoLuong(hsl);
            }

            employeeBO.updateEmployee(employeeToUpdate);
            return "Success: Cập nhật nhân viên thành công!";
        } catch (JsonSyntaxException e) {
            System.err.println("Controller: Invalid JSON format for updateEmployee: " + e.getMessage());
            return "Error: Dữ liệu gửi lên không đúng định dạng.";
        } catch (IllegalArgumentException e) {
            return "Error: " + e.getMessage();
        } catch (RuntimeException e) {
            System.err.println("Controller: Runtime error in updateEmployee: " + e.getMessage());
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            System.err.println("Controller: Unexpected error in updateEmployee: " + e.getMessage());
            e.printStackTrace();
            return "Error: Lỗi không xác định khi cập nhật nhân viên.";
        }
    }

    public String deleteEmployee(int idNhanVien) {
        try {
            if (idNhanVien <= 0) {
                return "Error: ID nhân viên không hợp lệ để xóa.";
            }
            employeeBO.deleteEmployee(idNhanVien);
            return "Success: Xóa nhân viên thành công!";
        } catch (IllegalArgumentException e) {
            return "Error: " + e.getMessage();
        } catch (RuntimeException e) {
            System.err.println("Controller: Runtime error in deleteEmployee: " + e.getMessage());
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            System.err.println("Controller: Unexpected error in deleteEmployee: " + e.getMessage());
            e.printStackTrace();
            return "Error: Lỗi không xác định khi xóa nhân viên.";
        }
    }

    public List<Employee> searchEmployees(String keyword, String criteria) {
        try {
            return employeeBO.searchEmployees(keyword, criteria);
        } catch (IllegalArgumentException e){
            System.err.println("Controller: Invalid search criteria: " + e.getMessage());
            return Collections.emptyList();
        } catch (RuntimeException e) {
            System.err.println("Controller: Error searching employees: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<String> getAllTenChucVu() {
        try {
            return employeeBO.getAllTenChucVu();
        } catch (RuntimeException e) {
            System.err.println("Controller: Error getting ChucVu list: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<String> getAllTenPhongBan() {
        try {
            return employeeBO.getAllTenPhongBan();
        } catch (RuntimeException e) {
            System.err.println("Controller: Error getting PhongBan list: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // MỚI: Trả về HeSoLuong (dưới dạng String để Gson dễ xử lý)
    public String getHeSoLuongByTenChucVu(String tenChucVu) {
        try {
            BigDecimal hsl = employeeBO.getHeSoLuongByTenChucVu(tenChucVu);
            return (hsl != null) ? gson.toJson(hsl.toString()) : gson.toJson(null);
        } catch (RuntimeException e) {
            System.err.println("Controller: Error getting HeSoLuong: " + e.getMessage());
            return gson.toJson(null); // Trả về null (dưới dạng JSON string) nếu lỗi
        }
    }
}