//package com.MediStaffManager.bo;
//
//import com.MediStaffManager.dao.EmployeeDAO;
//import com.MediStaffManager.bean.Employee;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Pattern;
//
//public class EmployeeBO {
//    private EmployeeDAO employeeDAO;
//
//    public EmployeeBO() {
//        this.employeeDAO = new EmployeeDAO();
//    }
//
//    // Validation methods (similar to AddEmployeeController)
//    private boolean isValidCCCD(String cccd) {
//        return cccd != null && Pattern.matches("\\d{12}", cccd);
//    }
//
//    private boolean isValidSDT(String sdt) {
//        return sdt != null && Pattern.matches("\\d{10,11}", sdt);
//    }
//
//    private boolean isValidEmail(String email) {
//        if (email == null || email.trim().isEmpty()) return false; // Email có thể không bắt buộc, tùy yêu cầu
//        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
//        return Pattern.matches(emailRegex, email);
//    }
//
//    private boolean isValidNgaySinh(String ngaySinh) { // YYYY-MM-DD
//        if (ngaySinh == null || ngaySinh.trim().isEmpty()) return false;
//        // Có thể thêm kiểm tra logic ngày tháng hợp lệ (ví dụ: không phải ngày 30/02)
//        return Pattern.matches("^\\d{4}-\\d{2}-\\d{2}$", ngaySinh);
//    }
//
//
//    private void validateEmployeeData(Employee employee, boolean isUpdate) {
//        if (employee == null) {
//            throw new IllegalArgumentException("Dữ liệu nhân viên không được để trống.");
//        }
//        if (employee.getHoTen() == null || employee.getHoTen().trim().isEmpty()) {
//            throw new IllegalArgumentException("Họ tên không được để trống.");
//        }
//        if (!isValidCCCD(employee.getCccd())) {
//            throw new IllegalArgumentException("CCCD không hợp lệ (phải là 12 chữ số).");
//        }
//        if (!isValidSDT(employee.getSdt())) {
//            throw new IllegalArgumentException("Số điện thoại không hợp lệ (phải là 10-11 chữ số).");
//        }
//        if (employee.getEmail() != null && !employee.getEmail().trim().isEmpty() && !isValidEmail(employee.getEmail())) {
//            throw new IllegalArgumentException("Email không đúng định dạng.");
//        }
//        if (!isValidNgaySinh(employee.getNgaySinh())) {
//            throw new IllegalArgumentException("Ngày sinh không hợp lệ (định dạng YYYY-MM-DD).");
//        }
//        if (employee.getTenChucVu() == null || employee.getTenChucVu().trim().isEmpty()) {
//            throw new IllegalArgumentException("Tên chức vụ không được để trống.");
//        }
//        if (employee.getTenPhongBan() == null || employee.getTenPhongBan().trim().isEmpty()) {
//            throw new IllegalArgumentException("Tên phòng ban không được để trống.");
//        }
//
//        // Kiểm tra trùng lặp
//        Integer employeeIdForCheck = isUpdate ? employee.getIdNhanVien() : null;
//        try {
//            String duplicateMessage = employeeDAO.checkDuplicate(employee.getCccd(), employee.getSdt(), employee.getEmail(), employeeIdForCheck);
//            if (duplicateMessage != null) {
//                throw new IllegalArgumentException(duplicateMessage);
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException("Lỗi khi kiểm tra trùng lặp dữ liệu: " + e.getMessage(), e);
//        }
//    }
//
//    public List<Employee> getAllEmployees() {
//        try {
//            return employeeDAO.getAllEmployees();
//        } catch (SQLException e) {
//            System.err.println("Error in EmployeeBO (getAllEmployees): " + e.getMessage());
//            throw new RuntimeException("Không thể tải danh sách nhân viên: " + e.getMessage(), e);
//        }
//    }
//
//    public List<Employee> searchEmployees(String keyword, String criteria) {
//        try {
//            if (criteria == null || criteria.trim().isEmpty()) {
//                 throw new IllegalArgumentException("Tiêu chí tìm kiếm không được để trống.");
//            }
//            return employeeDAO.searchEmployees(keyword, criteria);
//        } catch (SQLException e) {
//            System.err.println("Error in EmployeeBO (searchEmployees): " + e.getMessage());
//            throw new RuntimeException("Lỗi khi tìm kiếm nhân viên: " + e.getMessage(), e);
//        }
//    }
//
//    public void updateEmployee(Employee employee) {
//        try {
//            if (employee.getIdNhanVien() <= 0) {
//                 throw new IllegalArgumentException("ID nhân viên không hợp lệ để cập nhật.");
//            }
//            validateEmployeeData(employee, true); // true for update
//            employeeDAO.updateEmployee(employee);
//        } catch (IllegalArgumentException e) {
//            throw e; // Ném lại lỗi validation
//        } catch (SQLException e) {
//            System.err.println("Error in EmployeeBO (updateEmployee): " + e.getMessage());
//            throw new RuntimeException("Lỗi khi cập nhật nhân viên: " + e.getMessage(), e);
//        }
//    }
//
//    public void deleteEmployee(int idNhanVien) {
//        try {
//            if (idNhanVien <= 0) {
//                throw new IllegalArgumentException("ID nhân viên không hợp lệ để xóa.");
//            }
//            employeeDAO.deleteEmployee(idNhanVien);
//        } catch (IllegalArgumentException e) {
//            throw e;
//        } catch (SQLException e) {
//            System.err.println("Error in EmployeeBO (deleteEmployee): " + e.getMessage());
//            throw new RuntimeException("Lỗi khi xóa nhân viên: " + e.getMessage(), e);
//        }
//    }
//
//    public int addEmployee(Employee employee) {
//        try {
//            validateEmployeeData(employee, false); // false for add
//            int newId = employeeDAO.addEmployee(employee);
//            employee.setIdNhanVien(newId); // Cập nhật ID cho object employee sau khi thêm
//            return newId;
//        } catch (IllegalArgumentException e) {
//            throw e;
//        } catch (SQLException e) {
//            System.err.println("Error in EmployeeBO (addEmployee): " + e.getMessage());
//            throw new RuntimeException("Lỗi khi thêm nhân viên: " + e.getMessage(), e);
//        }
//    }
//
//    // Lấy danh sách tên cho dropdowns
//    public List<String> getAllTenChucVu() {
//        try {
//            return employeeDAO.getAllDistinctTenChucVu();
//        } catch (SQLException e) {
//            System.err.println("Error in EmployeeBO (getAllTenChucVu): " + e.getMessage());
//            throw new RuntimeException("Không thể tải danh sách chức vụ: " + e.getMessage(), e);
//        }
//    }
//
//    public List<String> getAllTenPhongBan() {
//         try {
//            return employeeDAO.getAllDistinctTenPhongBan();
//        } catch (SQLException e) {
//            System.err.println("Error in EmployeeBO (getAllTenPhongBan): " + e.getMessage());
//            throw new RuntimeException("Không thể tải danh sách phòng ban: " + e.getMessage(), e);
//        }
//    }
//}
//
package com.MediStaffManager.bo;

import com.MediStaffManager.dao.EmployeeDAO;
import com.MediStaffManager.bean.Employee;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class EmployeeBO {
    private EmployeeDAO employeeDAO;

    public EmployeeBO() {
        this.employeeDAO = new EmployeeDAO();
    }

    // ... (các hàm validation giữ nguyên)
    private boolean isValidCCCD(String cccd) {
        return cccd != null && Pattern.matches("\\d{12}", cccd);
    }

    private boolean isValidSDT(String sdt) {
        return sdt != null && Pattern.matches("\\d{10,11}", sdt);
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return true; // Email có thể không bắt buộc
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.matches(emailRegex, email);
    }

    private boolean isValidNgaySinh(String ngaySinh) { // YYYY-MM-DD
        if (ngaySinh == null || ngaySinh.trim().isEmpty()) return false;
        return Pattern.matches("^\\d{4}-\\d{2}-\\d{2}$", ngaySinh);
    }


    private void validateEmployeeData(Employee employee, boolean isUpdate) {
        if (employee == null) {
            throw new IllegalArgumentException("Dữ liệu nhân viên không được để trống.");
        }
        if (employee.getHoTen() == null || employee.getHoTen().trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên không được để trống.");
        }
        if (!isValidCCCD(employee.getCccd())) {
            throw new IllegalArgumentException("CCCD không hợp lệ (phải là 12 chữ số).");
        }
        if (!isValidSDT(employee.getSdt())) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ (phải là 10-11 chữ số).");
        }
        if (employee.getEmail() != null && !employee.getEmail().trim().isEmpty() && !isValidEmail(employee.getEmail())) {
            throw new IllegalArgumentException("Email không đúng định dạng.");
        }
        if (!isValidNgaySinh(employee.getNgaySinh())) {
            throw new IllegalArgumentException("Ngày sinh không hợp lệ (định dạng YYYY-MM-DD).");
        }
        if (employee.getTenChucVu() == null || employee.getTenChucVu().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên chức vụ không được để trống.");
        }
        if (employee.getTenPhongBan() == null || employee.getTenPhongBan().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên phòng ban không được để trống.");
        }
        // HeSoLuong sẽ được lấy từ ChucVu, không cần validate trực tiếp ở đây trừ khi nó được phép sửa đổi độc lập
        // if (employee.getHeSoLuong() == null || employee.getHeSoLuong().compareTo(BigDecimal.ZERO) <= 0) {
        //     throw new IllegalArgumentException("Hệ số lương không hợp lệ.");
        // }


        Integer employeeIdForCheck = isUpdate ? employee.getIdNhanVien() : null;
        try {
            String duplicateMessage = employeeDAO.checkDuplicate(employee.getCccd(), employee.getSdt(), employee.getEmail(), employeeIdForCheck);
            if (duplicateMessage != null) {
                throw new IllegalArgumentException(duplicateMessage);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi kiểm tra trùng lặp dữ liệu: " + e.getMessage(), e);
        }
    }


    public List<Employee> getAllEmployees() {
        try {
            return employeeDAO.getAllEmployees();
        } catch (SQLException e) {
            System.err.println("Error in EmployeeBO (getAllEmployees): " + e.getMessage());
            throw new RuntimeException("Không thể tải danh sách nhân viên: " + e.getMessage(), e);
        }
    }

    public List<Employee> searchEmployees(String keyword, String criteria) {
        try {
            if (criteria == null || criteria.trim().isEmpty()) {
                 throw new IllegalArgumentException("Tiêu chí tìm kiếm không được để trống.");
            }
            return employeeDAO.searchEmployees(keyword, criteria);
        } catch (SQLException e) {
            System.err.println("Error in EmployeeBO (searchEmployees): " + e.getMessage());
            throw new RuntimeException("Lỗi khi tìm kiếm nhân viên: " + e.getMessage(), e);
        }
    }

    public void updateEmployee(Employee employee) {
        try {
            if (employee.getIdNhanVien() <= 0) {
                 throw new IllegalArgumentException("ID nhân viên không hợp lệ để cập nhật.");
            }
            validateEmployeeData(employee, true);
            employeeDAO.updateEmployee(employee);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (SQLException e) {
            System.err.println("Error in EmployeeBO (updateEmployee): " + e.getMessage());
            throw new RuntimeException("Lỗi khi cập nhật nhân viên: " + e.getMessage(), e);
        }
    }

    public void deleteEmployee(int idNhanVien) {
        try {
            if (idNhanVien <= 0) {
                throw new IllegalArgumentException("ID nhân viên không hợp lệ để xóa.");
            }
            employeeDAO.deleteEmployee(idNhanVien);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (SQLException e) {
            System.err.println("Error in EmployeeBO (deleteEmployee): " + e.getMessage());
            // Kiểm tra xem có phải lỗi khóa ngoại không
            if (e.getSQLState().startsWith("23")) { // Mã lỗi SQLState cho vi phạm ràng buộc
                 throw new RuntimeException("Không thể xóa nhân viên này vì có dữ liệu liên quan (ví dụ: lương, lịch làm việc). Vui lòng xóa các dữ liệu liên quan trước.", e);
            }
            throw new RuntimeException("Lỗi khi xóa nhân viên: " + e.getMessage(), e);
        }
    }

    public int addEmployee(Employee employee) {
        try {
            validateEmployeeData(employee, false);
            int newId = employeeDAO.addEmployee(employee);
            employee.setIdNhanVien(newId);
            return newId;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (SQLException e) {
            System.err.println("Error in EmployeeBO (addEmployee): " + e.getMessage());
            throw new RuntimeException("Lỗi khi thêm nhân viên: " + e.getMessage(), e);
        }
    }

    public List<String> getAllTenChucVu() {
        try {
            return employeeDAO.getAllDistinctTenChucVu();
        } catch (SQLException e) {
            System.err.println("Error in EmployeeBO (getAllTenChucVu): " + e.getMessage());
            throw new RuntimeException("Không thể tải danh sách chức vụ: " + e.getMessage(), e);
        }
    }

    public List<String> getAllTenPhongBan() {
         try {
            return employeeDAO.getAllDistinctTenPhongBan();
        } catch (SQLException e) {
            System.err.println("Error in EmployeeBO (getAllTenPhongBan): " + e.getMessage());
            throw new RuntimeException("Không thể tải danh sách phòng ban: " + e.getMessage(), e);
        }
    }

    // MỚI: Lấy HeSoLuong theo TenChucVu
    public BigDecimal getHeSoLuongByTenChucVu(String tenChucVu) {
        try {
            return employeeDAO.getHeSoLuongByTenChucVu(tenChucVu);
        } catch (SQLException e) {
            System.err.println("Error in EmployeeBO (getHeSoLuongByTenChucVu): " + e.getMessage());
            throw new RuntimeException("Không thể tải hệ số lương cho chức vụ: " + e.getMessage(), e);
        }
    }
}
