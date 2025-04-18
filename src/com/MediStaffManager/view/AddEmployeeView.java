package com.MediStaffManager.view;

import com.MediStaffManager.bean.Employee;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AddEmployeeView extends JFrame {
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> searchCriteriaComboBox;
    private JButton searchButton;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JPanel inputPanel;
    private JTextField cccdField;
    private JTextField hoTenField;
    private JTextField sdtField;
    private JTextField emailField;
    private JComboBox<String> gioiTinhComboBox;
    private JTextField ngaySinhField;
    private JTextField chucVuField;
    private JTextField phongBanField;
    private JButton saveButton;
    private JButton cancelButton;

    public AddEmployeeView() {
        setTitle("Quản lý nhân viên");
        setSize(1200, 700); // Tăng chiều rộng để chứa thêm cột
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel chính
        setLayout(new BorderLayout());

        // Panel tìm kiếm (phía trên)
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchField = new JTextField(20);
        String[] criteria = {"Tất cả", "Tên", "Số CCCD", "Số điện thoại", "Phòng ban"};
        searchCriteriaComboBox = new JComboBox<>(criteria);
        searchButton = new JButton("Tìm kiếm");
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(searchField);
        searchPanel.add(searchCriteriaComboBox);
        searchPanel.add(searchButton);

        // Panel nút chức năng (Thêm, Chỉnh sửa, Xóa)
        JPanel buttonPanel = new JPanel(new FlowLayout());
        addButton = new JButton("Thêm nhân viên");
        editButton = new JButton("Chỉnh sửa nhân viên");
        deleteButton = new JButton("Xóa nhân viên");
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        // Bảng danh sách nhân viên
        String[] columnNames = {"ID", "CCCD", "Họ Tên", "Số điện thoại", "Email", "Giới tính", "Ngày sinh", "Chức vụ", "Phòng ban"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.setRowHeight(25);
        // Điều chỉnh độ rộng cột
        employeeTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        employeeTable.getColumnModel().getColumn(1).setPreferredWidth(100); // CCCD
        employeeTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Họ Tên
        employeeTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Số điện thoại
        employeeTable.getColumnModel().getColumn(4).setPreferredWidth(200); // Email
        employeeTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Giới tính
        employeeTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Ngày sinh
        employeeTable.getColumnModel().getColumn(7).setPreferredWidth(100); // Chức vụ
        employeeTable.getColumnModel().getColumn(8).setPreferredWidth(120); // Phòng ban
        JScrollPane tableScrollPane = new JScrollPane(employeeTable);

        // Panel nhập liệu (dùng cho chế độ Thêm và Chỉnh sửa)
        inputPanel = new JPanel(new GridLayout(9, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Thông tin nhân viên"));
        inputPanel.setPreferredSize(new Dimension(0, 300));

        inputPanel.add(new JLabel("CCCD:"));
        cccdField = new JTextField();
        inputPanel.add(cccdField);

        inputPanel.add(new JLabel("Họ Tên:"));
        hoTenField = new JTextField();
        inputPanel.add(hoTenField);

        inputPanel.add(new JLabel("Số điện thoại:"));
        sdtField = new JTextField();
        inputPanel.add(sdtField);

        inputPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        inputPanel.add(emailField);

        inputPanel.add(new JLabel("Giới tính:"));
        String[] gioiTinhOptions = {"Nam", "Nữ", "Khác"};
        gioiTinhComboBox = new JComboBox<>(gioiTinhOptions);
        inputPanel.add(gioiTinhComboBox);

        inputPanel.add(new JLabel("Ngày sinh (YYYY-MM-DD):"));
        ngaySinhField = new JTextField();
        inputPanel.add(ngaySinhField);

        inputPanel.add(new JLabel("Chức vụ:"));
        chucVuField = new JTextField();
        inputPanel.add(chucVuField);

        inputPanel.add(new JLabel("Phòng ban:"));
        phongBanField = new JTextField();
        inputPanel.add(phongBanField);

        saveButton = new JButton("Lưu");
        cancelButton = new JButton("Hủy");
        inputPanel.add(saveButton);
        inputPanel.add(cancelButton);

        // Ban đầu ẩn panel nhập liệu
        inputPanel.setVisible(false);

        // Thêm các thành phần vào giao diện
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
    }

    public JTable getEmployeeTable() {
        return employeeTable;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public JComboBox<String> getSearchCriteriaComboBox() {
        return searchCriteriaComboBox;
    }

    public JButton getSearchButton() {
        return searchButton;
    }

    public JButton getAddButton() {
        return addButton;
    }

    public JButton getEditButton() {
        return editButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }

    public JButton getSaveButton() {
        return saveButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public void setEmployeeData(List<Employee> employees) {
        tableModel.setRowCount(0);
        for (Employee employee : employees) {
            Object[] row = {
                employee.getIdNhanVien(),
                employee.getCccd(),
                employee.getHoTen(),
                employee.getSdt(),
                employee.getEmail(),      // Thêm Email
                employee.getGioiTinh(),  // Thêm Giới tính
                employee.getNgaySinh(),  // Thêm Ngày sinh
                employee.getTenChucVu(),
                employee.getTenPhongBan()
            };
            tableModel.addRow(row);
        }
    }

    public Employee getInputEmployee() {
        Employee employee = new Employee();
        employee.setCccd(cccdField.getText().trim());
        employee.setHoTen(hoTenField.getText().trim());
        employee.setSdt(sdtField.getText().trim());
        employee.setEmail(emailField.getText().trim());
        employee.setGioiTinh((String) gioiTinhComboBox.getSelectedItem());
        employee.setNgaySinh(ngaySinhField.getText().trim());
        employee.setTenChucVu(chucVuField.getText().trim());
        employee.setTenPhongBan(phongBanField.getText().trim());
        return employee;
    }

    public void setInputEmployee(Employee employee) {
        cccdField.setText(employee.getCccd());
        hoTenField.setText(employee.getHoTen());
        sdtField.setText(employee.getSdt());
        emailField.setText(employee.getEmail());
        gioiTinhComboBox.setSelectedItem(employee.getGioiTinh());
        ngaySinhField.setText(employee.getNgaySinh());
        chucVuField.setText(employee.getTenChucVu());
        phongBanField.setText(employee.getTenPhongBan());
    }

    public void clearInputFields() {
        cccdField.setText("");
        hoTenField.setText("");
        sdtField.setText("");
        emailField.setText("");
        gioiTinhComboBox.setSelectedIndex(0);
        ngaySinhField.setText("");
        chucVuField.setText("");
        phongBanField.setText("");
    }

    public void setInputPanelVisible(boolean visible) {
        inputPanel.setVisible(visible);
        revalidate();
        repaint();
    }

    public void setInputFieldsEditable(boolean editable) {
        cccdField.setEditable(editable);
        hoTenField.setEditable(editable);
        sdtField.setEditable(editable);
        emailField.setEditable(editable);
        gioiTinhComboBox.setEnabled(editable);
        ngaySinhField.setEditable(editable);
        chucVuField.setEditable(editable);
        phongBanField.setEditable(editable);
    }
}
//package com.MediStaffManager.view;
//
//import com.MediStaffManager.bean.Employee;
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.util.List;
//
//public class AddEmployeeView extends JFrame {
//    private JTable employeeTable;
//    private DefaultTableModel tableModel;
//    private JTextField searchField;
//    private JComboBox<String> searchCriteriaComboBox;
//    private JButton searchButton;
//    private JButton editButton;
//    private JButton deleteButton;
//    private JPanel inputPanel;
//    private JTextField cccdField;
//    private JTextField hoTenField;
//    private JTextField sdtField;
//    private JTextField emailField;
//    private JComboBox<String> gioiTinhComboBox;
//    private JTextField ngaySinhField;
//    private JTextField chucVuField;
//    private JTextField phongBanField;
//    private JButton saveButton;
//    private JButton cancelButton;
//
//    public AddEmployeeView() {
//        setTitle("Quản lý nhân viên");
//        setSize(1200, 700);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setLocationRelativeTo(null);
//
//        // Panel chính
//        setLayout(new BorderLayout());
//
//        // Panel tìm kiếm (phía trên)
//        JPanel searchPanel = new JPanel(new FlowLayout());
//        searchField = new JTextField(20);
//        String[] criteria = {"Tất cả", "Tên", "Số CCCD", "Số điện thoại", "Phòng ban"};
//        searchCriteriaComboBox = new JComboBox<>(criteria);
//        searchButton = new JButton("Tìm kiếm");
//        searchPanel.add(new JLabel("Tìm kiếm:"));
//        searchPanel.add(searchField);
//        searchPanel.add(searchCriteriaComboBox);
//        searchPanel.add(searchButton);
//
//        // Panel nút chức năng (Chỉnh sửa, Xóa)
//        JPanel buttonPanel = new JPanel(new FlowLayout());
//        editButton = new JButton("Chỉnh sửa nhân viên");
//        deleteButton = new JButton("Xóa nhân viên");
//        buttonPanel.add(editButton);
//        buttonPanel.add(deleteButton);
//
//        // Bảng danh sách nhân viên
//        String[] columnNames = {"ID", "CCCD", "Họ Tên", "Số điện thoại", "Email", "Giới tính", "Ngày sinh", "Chức vụ", "Phòng ban"};
//        tableModel = new DefaultTableModel(columnNames, 0) {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                return false;
//            }
//        };
//        employeeTable = new JTable(tableModel);
//        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        employeeTable.setRowHeight(25);
//        employeeTable.getColumnModel().getColumn(0).setPreferredWidth(50);
//        employeeTable.getColumnModel().getColumn(1).setPreferredWidth(100);
//        employeeTable.getColumnModel().getColumn(2).setPreferredWidth(150);
//        employeeTable.getColumnModel().getColumn(3).setPreferredWidth(100);
//        employeeTable.getColumnModel().getColumn(4).setPreferredWidth(200);
//        employeeTable.getColumnModel().getColumn(5).setPreferredWidth(80);
//        employeeTable.getColumnModel().getColumn(6).setPreferredWidth(100);
//        employeeTable.getColumnModel().getColumn(7).setPreferredWidth(100);
//        employeeTable.getColumnModel().getColumn(8).setPreferredWidth(120);
//        JScrollPane tableScrollPane = new JScrollPane(employeeTable);
//
//        // Panel nhập liệu (dùng cho chế độ Chỉnh sửa)
//        inputPanel = new JPanel(new GridLayout(9, 2, 10, 10));
//        inputPanel.setBorder(BorderFactory.createTitledBorder("Thông tin nhân viên"));
//        inputPanel.setPreferredSize(new Dimension(0, 300));
//
//        inputPanel.add(new JLabel("CCCD:"));
//        cccdField = new JTextField();
//        inputPanel.add(cccdField);
//
//        inputPanel.add(new JLabel("Họ Tên:"));
//        hoTenField = new JTextField();
//        inputPanel.add(hoTenField);
//
//        inputPanel.add(new JLabel("Số điện thoại:"));
//        sdtField = new JTextField();
//        inputPanel.add(sdtField);
//
//        inputPanel.add(new JLabel("Email:"));
//        emailField = new JTextField();
//        inputPanel.add(emailField);
//
//        inputPanel.add(new JLabel("Giới tính:"));
//        String[] gioiTinhOptions = {"Nam", "Nữ", "Khác"};
//        gioiTinhComboBox = new JComboBox<>(gioiTinhOptions);
//        inputPanel.add(gioiTinhComboBox);
//
//        inputPanel.add(new JLabel("Ngày sinh (YYYY-MM-DD):"));
//        ngaySinhField = new JTextField();
//        inputPanel.add(ngaySinhField);
//
//        inputPanel.add(new JLabel("Chức vụ:"));
//        chucVuField = new JTextField();
//        inputPanel.add(chucVuField);
//
//        inputPanel.add(new JLabel("Phòng ban:"));
//        phongBanField = new JTextField();
//        inputPanel.add(phongBanField);
//
//        saveButton = new JButton("Lưu");
//        cancelButton = new JButton("Hủy");
//        inputPanel.add(saveButton);
//        inputPanel.add(cancelButton);
//
//        // Ban đầu ẩn panel nhập liệu
//        inputPanel.setVisible(false);
//
//        // Thêm các thành phần vào giao diện
//        JPanel topPanel = new JPanel(new BorderLayout());
//        topPanel.add(searchPanel, BorderLayout.NORTH);
//        topPanel.add(buttonPanel, BorderLayout.CENTER);
//        add(topPanel, BorderLayout.NORTH);
//        add(tableScrollPane, BorderLayout.CENTER);
//        add(inputPanel, BorderLayout.SOUTH);
//    }
//
//    public JTable getEmployeeTable() {
//        return employeeTable;
//    }
//
//    public DefaultTableModel getTableModel() {
//        return tableModel;
//    }
//
//    public JTextField getSearchField() {
//        return searchField;
//    }
//
//    public JComboBox<String> getSearchCriteriaComboBox() {
//        return searchCriteriaComboBox;
//    }
//
//    public JButton getSearchButton() {
//        return searchButton;
//    }
//
//    public JButton getEditButton() {
//        return editButton;
//    }
//
//    public JButton getDeleteButton() {
//        return deleteButton;
//    }
//
//    public JButton getSaveButton() {
//        return saveButton;
//    }
//
//    public JButton getCancelButton() {
//        return cancelButton;
//    }
//
//    public void setEmployeeData(List<Employee> employees) {
//        tableModel.setRowCount(0);
//        for (Employee employee : employees) {
//            Object[] row = {
//                employee.getIdNhanVien(),
//                employee.getCccd(),
//                employee.getHoTen(),
//                employee.getSdt(),
//                employee.getEmail(),
//                employee.getGioiTinh(),
//                employee.getNgaySinh(),
//                employee.getTenChucVu(),
//                employee.getTenPhongBan()
//            };
//            tableModel.addRow(row);
//        }
//    }
//
//    public Employee getInputEmployee() {
//        Employee employee = new Employee();
//        employee.setCccd(cccdField.getText().trim());
//        employee.setHoTen(hoTenField.getText().trim());
//        employee.setSdt(sdtField.getText().trim());
//        employee.setEmail(emailField.getText().trim());
//        employee.setGioiTinh((String) gioiTinhComboBox.getSelectedItem());
//        employee.setNgaySinh(ngaySinhField.getText().trim());
//        employee.setTenChucVu(chucVuField.getText().trim());
//        employee.setTenPhongBan(phongBanField.getText().trim());
//        return employee;
//    }
//
//    public void setInputEmployee(Employee employee) {
//        cccdField.setText(employee.getCccd());
//        hoTenField.setText(employee.getHoTen());
//        sdtField.setText(employee.getSdt());
//        emailField.setText(employee.getEmail());
//        gioiTinhComboBox.setSelectedItem(employee.getGioiTinh());
//        ngaySinhField.setText(employee.getNgaySinh());
//        chucVuField.setText(employee.getTenChucVu());
//        phongBanField.setText(employee.getTenPhongBan());
//    }
//
//    public void clearInputFields() {
//        cccdField.setText("");
//        hoTenField.setText("");
//        sdtField.setText("");
//        emailField.setText("");
//        gioiTinhComboBox.setSelectedIndex(0);
//        ngaySinhField.setText("");
//        chucVuField.setText("");
//        phongBanField.setText("");
//    }
//
//    public void setInputPanelVisible(boolean visible) {
//        inputPanel.setVisible(visible);
//        revalidate();
//        repaint();
//    }
//
//    public void setInputFieldsEditable(boolean editable) {
//        cccdField.setEditable(editable);
//        hoTenField.setEditable(editable);
//        sdtField.setEditable(editable);
//        emailField.setEditable(editable);
//        gioiTinhComboBox.setEnabled(editable);
//        ngaySinhField.setEditable(editable);
//        chucVuField.setEditable(editable);
//        phongBanField.setEditable(editable);
//    }
//}