package com.MediStaffManager.view;

import com.MediStaffManager.controller.NhanVienController;
import com.MediStaffManager.bean.NhanVien;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class InDanhSachNhanVien extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private NhanVienController controller;

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

        // Add delete button
        JPanel buttonPanel = new JPanel();
        JButton deleteButton = new JButton("Xóa nhân viên");
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load employee data from the database
        loadEmployeeData();

        // Add action listener to the delete button
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteEmployee();
            }
        });
    }

    private void loadEmployeeData() {
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

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            InDanhSachNhanVien view = new InDanhSachNhanVien();
//            view.setVisible(true);
//        });
//    }
}
