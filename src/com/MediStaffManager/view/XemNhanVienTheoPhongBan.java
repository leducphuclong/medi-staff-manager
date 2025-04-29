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
	private JComboBox<String> phongBanComboBox;
	private JTable table;
	private DefaultTableModel phongBanTableModel;
	private JTable phongBanTable;
    private DefaultTableModel tableModel;
    private NhanVienController controller;
    
    
    public XemNhanVienTheoPhongBan() {
    	controller = new NhanVienController();
    	setTitle("Xem Nhân Viên Theo Từng Phòng Ban");
    	setSize(1000, 900);
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	setLocationRelativeTo(null);
    	
    	// Panel for entering the department name
    	JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());
        JLabel phongBanLabel = new JLabel("Chọn phòng ban:");
        phongBanComboBox = new JComboBox<>();
        loadPhongBanComboBox(); // Load danh sách phòng ban vào ComboBox
        JButton xemButton = new JButton("Xem");
        JButton xoaTatCaButton = new JButton("Xoa tat ca");
        JButton xoaPhongBanButton = new JButton("Xóa phòng ban");
        JButton themPhongBanButton = new JButton("Thêm phòng ban");
        JButton suaPhongBanButton = new JButton("Sửa phòng ban");
        JButton xemPhongBanButton = new JButton("Xem phòng ban"); 
        
        inputPanel.add(suaPhongBanButton);
        inputPanel.add(xoaPhongBanButton);
        inputPanel.add(phongBanLabel);
        inputPanel.add(phongBanComboBox);
        inputPanel.add(xemButton);
        inputPanel.add(themPhongBanButton);
        inputPanel.add(xoaTatCaButton);
        inputPanel.add(xemPhongBanButton);
        add(inputPanel, BorderLayout.NORTH);
        
        // Create the table to display data
        String[] columnNames = {"ID", "CCCD", "Tên", "Sdt", "Email", "Giới tính", "Năm sinh", "Chức vụ", "Phòng ban"};  
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        
        // Table to display department list
        String[] phongBanColumnNames = {"ID phòng ban", "Tên phòng ban"};
        phongBanTableModel = new DefaultTableModel(phongBanColumnNames, 0);
        phongBanTable = new JTable(phongBanTableModel);
        JScrollPane phongBanScrollPane = new JScrollPane(phongBanTable);
        add(phongBanScrollPane, BorderLayout.EAST);
        
     // Event for the "Thêm phòng ban" button
        themPhongBanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                themPhongBan();
            }
        });
        
        // Event for the "Xem" button
        xemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xemNhanVienTheoPhongBan();
            }
        });
        
        // Event for the "Xóa" button
        xoaTatCaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xoaTatCaNhanVienTrongPhongBan();
            }
        });
        
        // Event for the "Xóa phòng ban" button
        xoaPhongBanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xoaPhongBan();
            }
        });
        
        //Event for the "Sửa phòng ban" button
        suaPhongBanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                suaPhongBan();
            }
        });
        
        // Event for the "Xem phòng ban" button
        xemPhongBanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xemDanhSachPhongBan();
            }
        });
    }
    
    private void loadPhongBanComboBox() {
        phongBanComboBox.removeAllItems();
        List<Object[]> phongBanList = controller.layDanhSachPhongBan();
        for (Object[] phongBan : phongBanList) {
            String tenPhongBan = (String) phongBan[1];
            phongBanComboBox.addItem(tenPhongBan);
        }
    }
    
    // Method to view employees by department
    private void xemNhanVienTheoPhongBan() {
    	String tenPhongBan = (String) phongBanComboBox.getSelectedItem();
    	if (tenPhongBan == null || tenPhongBan.isEmpty()) {
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
        String tenPhongBan = (String) phongBanComboBox.getSelectedItem();
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
    
    // Method to delete a department
    private void xoaPhongBan() {
        String tenPhongBan = (String) phongBanComboBox.getSelectedItem();
        if (tenPhongBan == null || tenPhongBan.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng ban!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa phòng ban này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.xoaPhongBan(tenPhongBan.trim());
            if (success) {
                JOptionPane.showMessageDialog(this, "Xóa phòng ban thành công!");
                loadPhongBanComboBox();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa phòng ban vì vẫn còn nhân viên!");
            }
        }
    }
    
    // Method to add new department
    private void themPhongBan() {
        JTextField idPhongBanField = new JTextField();
        JTextField tenPhongBanField = new JTextField();
        Object[] message = {
            "ID phòng ban:", idPhongBanField,
            "Tên phòng ban:", tenPhongBanField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Thêm phòng ban mới", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int idPhongBan = Integer.parseInt(idPhongBanField.getText().trim());
                String tenPhongBan = tenPhongBanField.getText().trim();

                if (tenPhongBan.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tên phòng ban không được để trống!");
                    return;
                }

                boolean success = controller.themPhongBan(idPhongBan, tenPhongBan);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Thêm phòng ban thành công!");
                    loadPhongBanComboBox();
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm phòng ban thất bại! ID có thể đã tồn tại.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "ID phòng ban phải là số nguyên!");
            }
        }
    }
    
 // Method to load department list into the table
    private void xemDanhSachPhongBan() {
        List<Object[]> phongBanList = controller.layDanhSachPhongBan();
        phongBanTableModel.setRowCount(0);
        for (Object[] phongBan : phongBanList) {
            phongBanTableModel.addRow(phongBan);
        }

        if (phongBanList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có phòng ban nào!");
        }
    }
    
    // Method to edit a department
    private void suaPhongBan() {
        JTextField idPhongBanCuField = new JTextField();
        JTextField idPhongBanMoiField = new JTextField();
        JTextField tenPhongBanMoiField = new JTextField();
        Object[] message = {
            "ID phòng ban cũ:", idPhongBanCuField,
            "ID phòng ban mới:", idPhongBanMoiField,
            "Tên phòng ban mới:", tenPhongBanMoiField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Sửa phòng ban", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int idPhongBanCu = Integer.parseInt(idPhongBanCuField.getText().trim());
                int idPhongBanMoi = Integer.parseInt(idPhongBanMoiField.getText().trim());
                String tenPhongBanMoi = tenPhongBanMoiField.getText().trim();

                if (tenPhongBanMoi.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tên phòng ban không được để trống!");
                    return;
                }

                boolean success = controller.suaPhongBan(idPhongBanCu, idPhongBanMoi, tenPhongBanMoi);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Sửa phòng ban thành công!");
                    loadPhongBanComboBox();
                } else {
                    JOptionPane.showMessageDialog(this, "Sửa phòng ban thất bại! Kiểm tra lại thông tin.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "ID phòng ban phải là số nguyên!");
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
