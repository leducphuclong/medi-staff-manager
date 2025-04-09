package com.MediStaffManager.view;

import com.MediStaffManager.controller.NhanVienController;
import com.MediStaffManager.bean.NhanVien;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.toedter.calendar.JDateChooser;

public class InDanhSachNhanVien extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private NhanVienController controller;

    //controller.layToanBoNhanVien() trả về danh sách các đối tượng NhanVien.
    //Controller đã chuyển tiếp yêu cầu lấy dữ liệu đến tầng BO, từ đó BO gọi DAO để truy xuất dữ liệu từ cơ sở dữ liệu.
    public InDanhSachNhanVien() {
        controller = new NhanVienController();
        setTitle("Danh sách nhân viên");
        setSize(1000, 500);  // Adjust size for more fields
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the table to display data
        String[] columnNames = {"ID", "CCCD", "Tên", "Sdt", "Email", "Giới tính", "Năm sinh", "Chức vụ", "Phòng ban"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Thêm Panel chứa các nút CRUD
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton ("Thêm nhân viên");
        JButton deleteButton = new JButton("Xóa nhân viên");
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Tải dữ liệu danh sách nhân viên từ CSDL
        loadEmployeeData();
        
        // Add action listener to the delete button
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteEmployee();
            }
        });
 
    	// Add action listener to the add button
    	addButton.addActionListener(new ActionListener() {
    		@Override
    		public void actionPerformed(ActionEvent e) {
    			addEmployee();
    		}
    	});
}
    

    private void loadEmployeeData() {
    	 // Xóa sạch dữ liệu cũ
        tableModel.setRowCount(0);
        List<NhanVien> employees = controller.layToanBoNhanVien();
        for (NhanVien employee : employees) {
            Object[] row = {
                employee.getIdNhanVien(),
                employee.getCccd(),
                employee.getHoTen(),
                employee.getSdt(),
                employee.getEmail(),
                employee.getGioiTinh(),
                employee.getNgaySinh(),
                employee.getTenChucVu(),
                employee.getTenPhongBan()
            };
            tableModel.addRow(row);
        }
    }

    // Hàm xử lý chức năng thêm nhân viên
    private void addEmployee() {
        // Tạo JDialog với modal (không cho phép tương tác với cửa sổ chính khi dialog mở)
        JDialog addDialog = new JDialog(this, "Thêm nhân viên", true);
        addDialog.setSize(400, 450);
        addDialog.setLayout(new GridLayout(9, 2, 5, 5));
        addDialog.setLocationRelativeTo(this);

        // Tạo các nhãn và ô nhập liệu cho thông tin nhân viên
        JLabel lblCCCD = new JLabel("CCCD:");
        JTextField tfCCCD = new JTextField();
        JLabel lblHoTen = new JLabel("Họ Tên:");
        JTextField tfHoTen = new JTextField();
        JLabel lblSdt = new JLabel("Sđt:");
        JTextField tfSdt = new JTextField();
        JLabel lblEmail = new JLabel("Email:");
        JTextField tfEmail = new JTextField();
        JLabel lblGioiTinh = new JLabel("Giới tính:");
        JTextField tfGioiTinh = new JTextField();
        JLabel lblNgaySinh = new JLabel("Ngày sinh:");
        JDateChooser dateChooserNgaySinh = new JDateChooser();
        dateChooserNgaySinh.setDateFormatString("yyyy-MM-dd");  // Định dạng hiển thị theo chuẩn MySQL
        JLabel lblIDChucVu = new JLabel("ID Chức vụ:");
        JTextField tfIDChucVu = new JTextField();
        JLabel lblIDPhongBan = new JLabel("ID Phòng ban:");
        JTextField tfIDPhongBan = new JTextField();

        // Nút thực hiện thêm và nút hủy
        JButton btnSubmit = new JButton("Thêm");
        JButton btnCancel = new JButton("Hủy");

        // Thêm các thành phần vào dialog
        addDialog.add(lblCCCD);
        addDialog.add(tfCCCD);
        addDialog.add(lblHoTen);
        addDialog.add(tfHoTen);
        addDialog.add(lblSdt);
        addDialog.add(tfSdt);
        addDialog.add(lblEmail);
        addDialog.add(tfEmail);
        addDialog.add(lblGioiTinh);
        addDialog.add(tfGioiTinh);
        addDialog.add(lblNgaySinh);
        addDialog.add(dateChooserNgaySinh);
        addDialog.add(lblIDChucVu);
        addDialog.add(tfIDChucVu);
        addDialog.add(lblIDPhongBan);
        addDialog.add(tfIDPhongBan);
        addDialog.add(btnSubmit);
        addDialog.add(btnCancel);

        // Xử lý sự kiện cho nút Thêm
        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Lấy dữ liệu từ các trường nhập
                String cccd = tfCCCD.getText();
                String hoTen = tfHoTen.getText();
                String sdt = tfSdt.getText();
                String email = tfEmail.getText();
                String gioiTinh = tfGioiTinh.getText();
                // Lấy ngày sinh từ JDateChooser
                Date selectedDate = dateChooserNgaySinh.getDate();
                if(selectedDate == null) {
                    JOptionPane.showMessageDialog(addDialog, "Bạn cần chọn ngày sinh!");
                    return;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String ngaySinh = sdf.format(selectedDate);
                
                int idChucVu, idPhongBan;
                try {
                    idChucVu = Integer.parseInt(tfIDChucVu.getText());
                    idPhongBan = Integer.parseInt(tfIDPhongBan.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(addDialog, "ID Chức vụ và ID Phòng ban phải là số!");
                    return;
                }

                // Tạo đối tượng nhân viên, IDNhanVien = 0 vì CSDL sẽ tự tạo giá trị
                NhanVien nv = new NhanVien(0, cccd, hoTen, sdt, email, gioiTinh, ngaySinh, idChucVu, idPhongBan, null, null);

                // Gọi controller để thực hiện thêm nhân viên
                boolean success = controller.themNhanVien(nv);
                if (success) {
                    JOptionPane.showMessageDialog(addDialog, "Thêm nhân viên thành công!");
                    addDialog.dispose();
                    // Tải lại dữ liệu vào bảng sau khi thêm thành công
                    loadEmployeeData();
                } else {
                    JOptionPane.showMessageDialog(addDialog, "Có lỗi xảy ra khi thêm nhân viên!");
                }
            }
        });

        // Xử lý nút Hủy: đóng dialog
        btnCancel.addActionListener(e -> addDialog.dispose());

        addDialog.setVisible(true);
    }

    private void deleteEmployee() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int idNhanVien = (int) tableModel.getValueAt(selectedRow, 0);  // Get the ID of the selected employee

            // Confirm deletion
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa nhân viên này?", 
                                                        "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Call controller to delete the employee
                boolean success = controller.xoaNhanVien(idNhanVien);
                if (success) {
                    // Remove the row from the table
                    tableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi xóa nhân viên.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhân viên để xóa.");
        }
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InDanhSachNhanVien view = new InDanhSachNhanVien();
            view.setVisible(true);
        });
    }
}
