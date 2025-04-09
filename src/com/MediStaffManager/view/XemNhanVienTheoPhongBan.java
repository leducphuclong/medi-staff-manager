package com.MediStaffManager.view;

import com.MediStaffManager.controller.NhanVienController;
import com.MediStaffManager.bean.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class XemNhanVienTheoPhongBan extends JFrame{
	private JTextField phongBanField;
    private JTable table;
    private DefaultTableModel tableModel;
    private NhanVienController controller;
    
    
    public XemNhanVienTheoPhongBan() {
    	controller = new NhanVienController();
    	setTitle("Xem Nhân Viên Theo Từng Phòng Ban");
    	setSize(800, 600);
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	setLocationRelativeTo(null);
    	
    	// Panel for entering the department name
    	JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());
        JLabel phongBanLabel = new JLabel("Tên phòng ban:");
        phongBanField = new JTextField(20);
        JButton xemButton = new JButton("Xem");
        JButton xoaTatCaButton = new JButton("Xoa tat ca");
        inputPanel.add(phongBanLabel);
        inputPanel.add(phongBanField);
        inputPanel.add(xemButton);
        inputPanel.add(xoaTatCaButton);
        add(inputPanel, BorderLayout.NORTH);
        
        // Create the table to display data
        String[] columnNames = {"ID", "CCCD", "Tên", "Sdt", "Email", "Giới tính", "Năm sinh", "Chức vụ", "Phòng ban"};  
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        
        
        // Event for the "View" button
        xemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xemNhanVienTheoPhongBan();
            }
        });
        
        // // Event for the "Delete All" button
        xoaTatCaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xoaTatCaNhanVienTrongPhongBan();
            }
        });
        
    }
    // Method to view employees by department
    private void xemNhanVienTheoPhongBan() {
    	String tenPhongBan = phongBanField.getText().trim();
    	if (tenPhongBan.isEmpty()) {
    		JOptionPane.showMessageDialog(this, "Vui lòng nhập tên phòng ban!");
    		return;
    	}
    	
    	List<NhanVien> employees = controller.layNhanVienTheoPhongBan(tenPhongBan);
        tableModel.setRowCount(0);	// Clear old data in the table
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

        if (employees.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên nào trong phòng ban này.");
        }
    }
    
    // Method to delete all employees in a department
    private void xoaTatCaNhanVienTrongPhongBan() {
    	String tenPhongBan = phongBanField.getText().trim();
    	if (tenPhongBan.isEmpty()) {
    		JOptionPane.showMessageDialog(this, "Vui lòng nhập tên phòng ban!");
    		return;
    	}
    	
    	int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa tất cả nhân viên trong phòng ban này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
    	
    	if (confirm == JOptionPane.YES_NO_OPTION) {
    		boolean success = controller.xoaTatCaNhanVienTrongPhongBan(tenPhongBan);
    		if (success) {
    			JOptionPane.showMessageDialog(this, "Đã xóa tất cả nhân viên trong phòng ban!");
    			tableModel.setRowCount(0);
    		} else {
    			JOptionPane.showMessageDialog(this, "Xóa thất bại!");
    		}
    	}
    	
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            XemNhanVienTheoPhongBan frame = new XemNhanVienTheoPhongBan();
            frame.setVisible(true);
        });
    }
}
