package com.MediStaffManager.controller;

import com.MediStaffManager.bo.EmployeeBO;
import com.MediStaffManager.bean.Employee;
import com.MediStaffManager.view.AddEmployeeView;
import javax.swing.*;
import java.util.List;
import java.util.regex.Pattern;

public class AddEmployeeController {
    private enum Mode {
        VIEW, ADD, EDIT, DELETE
    }

    private EmployeeBO employeeBO;
    private AddEmployeeView view;
    private Mode currentMode;
    private Employee selectedEmployee;

    public AddEmployeeController(AddEmployeeView view) {
        this.employeeBO = new EmployeeBO();
        this.view = view;
        this.currentMode = Mode.VIEW;
        this.selectedEmployee = null;

        // Ban đầu ở chế độ xem
        updateUIForMode();

        // Tải dữ liệu ban đầu
        loadEmployeeData();

        // Gán sự kiện cho các nút
        view.getSearchButton().addActionListener(e -> searchEmployees());
        view.getAddButton().addActionListener(e -> switchToAddMode());
        view.getEditButton().addActionListener(e -> switchToEditMode());
        view.getDeleteButton().addActionListener(e -> switchToDeleteMode());
        view.getSaveButton().addActionListener(e -> saveAction());
        view.getCancelButton().addActionListener(e -> cancelAction());
        view.getEmployeeTable().getSelectionModel().addListSelectionListener(e -> tableSelectionChanged());
    }

    private void loadEmployeeData() {
        try {
            List<Employee> employees = employeeBO.getAllEmployees();
            view.setEmployeeData(employees);
        } catch (Exception e) {
            System.out.println("Error in AddEmployeeController: Unable to load employees!");
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi khi tải danh sách nhân viên: " + e.getMessage());
        }
    }

    private void searchEmployees() {
        String keyword = view.getSearchField().getText().trim();
        String criteria = (String) view.getSearchCriteriaComboBox().getSelectedItem();
        try {
            List<Employee> employees;
            if (keyword.isEmpty()) {
                employees = employeeBO.getAllEmployees();
            } else {
                employees = employeeBO.searchEmployees(keyword, criteria);
            }
            view.setEmployeeData(employees);
        } catch (Exception e) {
            System.out.println("Error in AddEmployeeController: Unable to search employees!");
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi khi tìm kiếm nhân viên: " + e.getMessage());
        }
    }

    private void switchToAddMode() {
        currentMode = Mode.ADD;
        selectedEmployee = null;
        updateUIForMode();
        view.clearInputFields();
    }

    private void switchToEditMode() {
        if (selectedEmployee == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một nhân viên để chỉnh sửa!");
            return;
        }
        currentMode = Mode.EDIT;
        updateUIForMode();
        view.setInputEmployee(selectedEmployee);
    }

    private void switchToDeleteMode() {
        if (selectedEmployee == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một nhân viên để xóa!");
            return;
        }
        currentMode = Mode.DELETE;
        updateUIForMode();
        view.setInputEmployee(selectedEmployee);
        view.setInputFieldsEditable(false);
    }

    private void switchToViewMode() {
        currentMode = Mode.VIEW;
        selectedEmployee = null;
        updateUIForMode();
        view.clearInputFields();
        loadEmployeeData();
    }

    private void updateUIForMode() {
        switch (currentMode) {
            case VIEW:
                view.setInputPanelVisible(false);
                view.getAddButton().setEnabled(true);
                view.getEditButton().setEnabled(true);
                view.getDeleteButton().setEnabled(true);
                break;
            case ADD:
                view.setInputPanelVisible(true);
                view.setInputFieldsEditable(true);
                view.getAddButton().setEnabled(false);
                view.getEditButton().setEnabled(false);
                view.getDeleteButton().setEnabled(false);
                break;
            case EDIT:
                view.setInputPanelVisible(true);
                view.setInputFieldsEditable(true);
                view.getAddButton().setEnabled(false);
                view.getEditButton().setEnabled(false);
                view.getDeleteButton().setEnabled(false);
                break;
            case DELETE:
                view.setInputPanelVisible(true);
                view.setInputFieldsEditable(false);
                view.getAddButton().setEnabled(false);
                view.getEditButton().setEnabled(false);
                view.getDeleteButton().setEnabled(false);
                break;
        }
    }

    private void tableSelectionChanged() {
        int selectedRow = view.getEmployeeTable().getSelectedRow();
        if (selectedRow >= 0) {
            int idNhanVien = (int) view.getTableModel().getValueAt(selectedRow, 0);
            String cccd = (String) view.getTableModel().getValueAt(selectedRow, 1);
            String hoTen = (String) view.getTableModel().getValueAt(selectedRow, 2);
            String sdt = (String) view.getTableModel().getValueAt(selectedRow, 3);
            String email = (String) view.getTableModel().getValueAt(selectedRow, 4); // Lấy Email
            String gioiTinh = (String) view.getTableModel().getValueAt(selectedRow, 5); // Lấy Giới tính
            String ngaySinh = (String) view.getTableModel().getValueAt(selectedRow, 6); // Lấy Ngày sinh
            String tenChucVu = (String) view.getTableModel().getValueAt(selectedRow, 7);
            String tenPhongBan = (String) view.getTableModel().getValueAt(selectedRow, 8);

            selectedEmployee = new Employee();
            selectedEmployee.setIdNhanVien(idNhanVien);
            selectedEmployee.setCccd(cccd);
            selectedEmployee.setHoTen(hoTen);
            selectedEmployee.setSdt(sdt);
            selectedEmployee.setEmail(email); // Gán Email
            selectedEmployee.setGioiTinh(gioiTinh); // Gán Giới tính
            selectedEmployee.setNgaySinh(ngaySinh); // Gán Ngày sinh
            selectedEmployee.setTenChucVu(tenChucVu);
            selectedEmployee.setTenPhongBan(tenPhongBan);
        } else {
            selectedEmployee = null;
        }
    }

    private void saveAction() {
        if (currentMode == Mode.ADD) {
            Employee employee = view.getInputEmployee();

            // Kiểm tra các trường bắt buộc
            if (employee.getCccd().isEmpty() || employee.getHoTen().isEmpty() || employee.getSdt().isEmpty() ||
                employee.getEmail().isEmpty() || employee.getNgaySinh().isEmpty() ||
                employee.getTenChucVu().isEmpty() || employee.getTenPhongBan().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng điền đầy đủ thông tin!");
                return;
            }

            // Kiểm tra định dạng
            if (!isValidCCCD(employee.getCccd())) {
                JOptionPane.showMessageDialog(view, "CCCD phải là số và có độ dài 12 chữ số!");
                return;
            }
            if (!isValidSDT(employee.getSdt())) {
                JOptionPane.showMessageDialog(view, "Số điện thoại phải là số và có độ dài 10-11 chữ số!");
                return;
            }
            if (!isValidEmail(employee.getEmail())) {
                JOptionPane.showMessageDialog(view, "Email không đúng định dạng!");
                return;
            }
            if (!isValidNgaySinh(employee.getNgaySinh())) {
                JOptionPane.showMessageDialog(view, "Ngày sinh phải có định dạng YYYY-MM-DD!");
                return;
            }

            // Kiểm tra trùng lặp
            String duplicateMessage = employeeBO.checkDuplicate(employee.getCccd(), employee.getSdt(), employee.getEmail());
            if (duplicateMessage != null) {
                JOptionPane.showMessageDialog(view, duplicateMessage);
                return;
            }

            // Thêm nhân viên
            try {
                int idNhanVien = employeeBO.addEmployee(employee);
                JOptionPane.showMessageDialog(view, "Thêm nhân viên thành công! ID: " + idNhanVien);
                switchToViewMode();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Lỗi khi thêm nhân viên: " + e.getMessage());
            }
        } else if (currentMode == Mode.EDIT) {
            Employee employee = view.getInputEmployee();
            employee.setIdNhanVien(selectedEmployee.getIdNhanVien());

            // Kiểm tra các trường bắt buộc
            if (employee.getCccd().isEmpty() || employee.getHoTen().isEmpty() || employee.getSdt().isEmpty() ||
                employee.getEmail().isEmpty() || employee.getNgaySinh().isEmpty() ||
                employee.getTenChucVu().isEmpty() || employee.getTenPhongBan().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng điền đầy đủ thông tin!");
                return;
            }

            // Kiểm tra định dạng
            if (!isValidCCCD(employee.getCccd())) {
                JOptionPane.showMessageDialog(view, "CCCD phải là số và có độ dài 12 chữ số!");
                return;
            }
            if (!isValidSDT(employee.getSdt())) {
                JOptionPane.showMessageDialog(view, "Số điện thoại phải là số và có độ dài 10-11 chữ số!");
                return;
            }
            if (!isValidEmail(employee.getEmail())) {
                JOptionPane.showMessageDialog(view, "Email không đúng định dạng!");
                return;
            }
            if (!isValidNgaySinh(employee.getNgaySinh())) {
                JOptionPane.showMessageDialog(view, "Ngày sinh phải có định dạng YYYY-MM-DD!");
                return;
            }

            // Kiểm tra trùng lặp (bỏ qua chính nhân viên đang chỉnh sửa)
            String duplicateMessage = employeeBO.checkDuplicate(employee.getCccd(), employee.getSdt(), employee.getEmail());
            if (duplicateMessage != null && !employee.getCccd().equals(selectedEmployee.getCccd()) &&
                !employee.getSdt().equals(selectedEmployee.getSdt()) && !employee.getEmail().equals(selectedEmployee.getEmail())) {
                JOptionPane.showMessageDialog(view, duplicateMessage);
                return;
            }

            // Cập nhật nhân viên
            try {
                employeeBO.updateEmployee(employee);
                JOptionPane.showMessageDialog(view, "Cập nhật nhân viên thành công!");
                switchToViewMode();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Lỗi khi cập nhật nhân viên: " + e.getMessage());
            }
        } else if (currentMode == Mode.DELETE) {
            int confirm = JOptionPane.showConfirmDialog(view, "Bạn có chắc chắn muốn xóa nhân viên này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    employeeBO.deleteEmployee(selectedEmployee.getIdNhanVien());
                    JOptionPane.showMessageDialog(view, "Xóa nhân viên thành công!");
                    switchToViewMode();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(view, "Lỗi khi xóa nhân viên: " + e.getMessage());
                }
            }
        }
    }

    private void cancelAction() {
        switchToViewMode();
    }

    private boolean isValidCCCD(String cccd) {
        return Pattern.matches("\\d{12}", cccd);
    }

    private boolean isValidSDT(String sdt) {
        return Pattern.matches("\\d{10,11}", sdt);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.matches(emailRegex, email);
    }

    private boolean isValidNgaySinh(String ngaySinh) {
        String dateRegex = "^\\d{4}-\\d{2}-\\d{2}$";
        return Pattern.matches(dateRegex, ngaySinh);
    }
}
//package com.MediStaffManager.controller;
//
//import com.MediStaffManager.bo.EmployeeBO;
//import com.MediStaffManager.bean.Employee;
//import com.MediStaffManager.view.AddEmployeeView;
//import javax.swing.*;
//import java.util.List;
//import java.util.regex.Pattern;
//
//public class AddEmployeeController {
//    private enum Mode {
//        VIEW, EDIT, DELETE
//    }
//
//    private EmployeeBO employeeBO;
//    private AddEmployeeView view;
//    private Mode currentMode;
//    private Employee selectedEmployee;
//
//    public AddEmployeeController(AddEmployeeView view) {
//        this.employeeBO = new EmployeeBO();
//        this.view = view;
//        this.currentMode = Mode.VIEW;
//        this.selectedEmployee = null;
//
//        // Ban đầu ở chế độ xem
//        updateUIForMode();
//
//        // Tải dữ liệu ban đầu
//        loadEmployeeData();
//
//        // Gán sự kiện cho các nút
//        view.getSearchButton().addActionListener(e -> searchEmployees());
//        view.getEditButton().addActionListener(e -> switchToEditMode());
//        view.getDeleteButton().addActionListener(e -> switchToDeleteMode());
//        view.getSaveButton().addActionListener(e -> saveAction());
//        view.getCancelButton().addActionListener(e -> cancelAction());
//        view.getEmployeeTable().getSelectionModel().addListSelectionListener(e -> tableSelectionChanged());
//    }
//
//    private void loadEmployeeData() {
//        try {
//            List<Employee> employees = employeeBO.getAllEmployees();
//            view.setEmployeeData(employees);
//        } catch (Exception e) {
//            System.out.println("Error in AddEmployeeController: Unable to load employees!");
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(view, "Lỗi khi tải danh sách nhân viên: " + e.getMessage());
//        }
//    }
//
//    private void searchEmployees() {
//        String keyword = view.getSearchField().getText().trim();
//        String criteria = (String) view.getSearchCriteriaComboBox().getSelectedItem();
//        try {
//            List<Employee> employees;
//            if (keyword.isEmpty()) {
//                employees = employeeBO.getAllEmployees();
//            } else {
//                employees = employeeBO.searchEmployees(keyword, criteria);
//            }
//            view.setEmployeeData(employees);
//        } catch (Exception e) {
//            System.out.println("Error in AddEmployeeController: Unable to search employees!");
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(view, "Lỗi khi tìm kiếm nhân viên: " + e.getMessage());
//        }
//    }
//
//    private void switchToEditMode() {
//        if (selectedEmployee == null) {
//            JOptionPane.showMessageDialog(view, "Vui lòng chọn một nhân viên để chỉnh sửa!");
//            return;
//        }
//        currentMode = Mode.EDIT;
//        updateUIForMode();
//        view.setInputEmployee(selectedEmployee);
//    }
//
//    private void switchToDeleteMode() {
//        if (selectedEmployee == null) {
//            JOptionPane.showMessageDialog(view, "Vui lòng chọn một nhân viên để xóa!");
//            return;
//        }
//        currentMode = Mode.DELETE;
//        updateUIForMode();
//        view.setInputEmployee(selectedEmployee);
//        view.setInputFieldsEditable(false);
//    }
//
//    private void switchToViewMode() {
//        currentMode = Mode.VIEW;
//        selectedEmployee = null;
//        updateUIForMode();
//        view.clearInputFields();
//        loadEmployeeData();
//    }
//
//    private void updateUIForMode() {
//        switch (currentMode) {
//            case VIEW:
//                view.setInputPanelVisible(false);
//                view.getEditButton().setEnabled(true);
//                view.getDeleteButton().setEnabled(true);
//                break;
//            case EDIT:
//                view.setInputPanelVisible(true);
//                view.setInputFieldsEditable(true);
//                view.getEditButton().setEnabled(false);
//                view.getDeleteButton().setEnabled(false);
//                break;
//            case DELETE:
//                view.setInputPanelVisible(true);
//                view.setInputFieldsEditable(false);
//                view.getEditButton().setEnabled(false);
//                view.getDeleteButton().setEnabled(false);
//                break;
//        }
//    }
//
//    private void tableSelectionChanged() {
//        int selectedRow = view.getEmployeeTable().getSelectedRow();
//        if (selectedRow >= 0) {
//            int idNhanVien = (int) view.getTableModel().getValueAt(selectedRow, 0);
//            String cccd = (String) view.getTableModel().getValueAt(selectedRow, 1);
//            String hoTen = (String) view.getTableModel().getValueAt(selectedRow, 2);
//            String sdt = (String) view.getTableModel().getValueAt(selectedRow, 3);
//            String email = (String) view.getTableModel().getValueAt(selectedRow, 4);
//            String gioiTinh = (String) view.getTableModel().getValueAt(selectedRow, 5);
//            String ngaySinh = (String) view.getTableModel().getValueAt(selectedRow, 6);
//            String tenChucVu = (String) view.getTableModel().getValueAt(selectedRow, 7);
//            String tenPhongBan = (String) view.getTableModel().getValueAt(selectedRow, 8);
//
//            selectedEmployee = new Employee();
//            selectedEmployee.setIdNhanVien(idNhanVien);
//            selectedEmployee.setCccd(cccd);
//            selectedEmployee.setHoTen(hoTen);
//            selectedEmployee.setSdt(sdt);
//            selectedEmployee.setEmail(email);
//            selectedEmployee.setGioiTinh(gioiTinh);
//            selectedEmployee.setNgaySinh(ngaySinh);
//            selectedEmployee.setTenChucVu(tenChucVu);
//            selectedEmployee.setTenPhongBan(tenPhongBan);
//        } else {
//            selectedEmployee = null;
//        }
//    }
//
//    private void saveAction() {
//        if (currentMode == Mode.EDIT) {
//            Employee employee = view.getInputEmployee();
//            employee.setIdNhanVien(selectedEmployee.getIdNhanVien());
//
//            // Kiểm tra các trường bắt buộc
//            if (employee.getCccd().isEmpty() || employee.getHoTen().isEmpty() || employee.getSdt().isEmpty() ||
//                employee.getEmail().isEmpty() || employee.getNgaySinh().isEmpty() ||
//                employee.getTenChucVu().isEmpty() || employee.getTenPhongBan().isEmpty()) {
//                JOptionPane.showMessageDialog(view, "Vui lòng điền đầy đủ thông tin!");
//                return;
//            }
//
//            // Kiểm tra định dạng
//            if (!isValidCCCD(employee.getCccd())) {
//                JOptionPane.showMessageDialog(view, "CCCD phải là số và có độ dài 12 chữ số!");
//                return;
//            }
//            if (!isValidSDT(employee.getSdt())) {
//                JOptionPane.showMessageDialog(view, "Số điện thoại phải là số và có độ dài 10-11 chữ số!");
//                return;
//            }
//            if (!isValidEmail(employee.getEmail())) {
//                JOptionPane.showMessageDialog(view, "Email không đúng định dạng!");
//                return;
//            }
//            if (!isValidNgaySinh(employee.getNgaySinh())) {
//                JOptionPane.showMessageDialog(view, "Ngày sinh phải có định dạng YYYY-MM-DD!");
//                return;
//            }
//
//            // Kiểm tra trùng lặp (bỏ qua chính nhân viên đang chỉnh sửa)
//            String duplicateMessage = employeeBO.checkDuplicate(employee.getCccd(), employee.getSdt(), employee.getEmail());
//            if (duplicateMessage != null && !employee.getCccd().equals(selectedEmployee.getCccd()) &&
//                !employee.getSdt().equals(selectedEmployee.getSdt()) && !employee.getEmail().equals(selectedEmployee.getEmail())) {
//                JOptionPane.showMessageDialog(view, duplicateMessage);
//                return;
//            }
//
//            // Cập nhật nhân viên
//            try {
//                employeeBO.updateEmployee(employee);
//                JOptionPane.showMessageDialog(view, "Cập nhật nhân viên thành công!");
//                switchToViewMode();
//            } catch (Exception e) {
//                JOptionPane.showMessageDialog(view, "Lỗi khi cập nhật nhân viên: " + e.getMessage());
//            }
//        } else if (currentMode == Mode.DELETE) {
//            int confirm = JOptionPane.showConfirmDialog(view, "Bạn có chắc chắn muốn xóa nhân viên này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
//            if (confirm == JOptionPane.YES_OPTION) {
//                try {
//                    employeeBO.deleteEmployee(selectedEmployee.getIdNhanVien());
//                    JOptionPane.showMessageDialog(view, "Xóa nhân viên thành công!");
//                    switchToViewMode();
//                } catch (Exception e) {
//                    JOptionPane.showMessageDialog(view, "Lỗi khi xóa nhân viên: " + e.getMessage());
//                }
//            }
//        }
//    }
//
//    private void cancelAction() {
//        switchToViewMode();
//    }
//
//    private boolean isValidCCCD(String cccd) {
//        return Pattern.matches("\\d{12}", cccd);
//    }
//
//    private boolean isValidSDT(String sdt) {
//        return Pattern.matches("\\d{10,11}", sdt);
//    }
//
//    private boolean isValidEmail(String email) {
//        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
//        return Pattern.matches(emailRegex, email);
//    }
//
//    private boolean isValidNgaySinh(String ngaySinh) {
//        String dateRegex = "^\\d{4}-\\d{2}-\\d{2}$";
//        return Pattern.matches(dateRegex, ngaySinh);
//    }
//}