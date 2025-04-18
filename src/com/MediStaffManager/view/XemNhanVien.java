package com.MediStaffManager.view;

import com.MediStaffManager.controller.NhanVienController;
import com.MediStaffManager.bean.NhanVien;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class XemNhanVien extends JFrame {
    private JComboBox<String> phongBanComboBox;
    private JTable table;
    private DefaultTableModel tableModel;
    private NhanVienController controller;

    public XemNhanVien() {
        controller = new NhanVienController();
        setTitle("Xem Nhân Viên Theo Phòng Ban");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel for department selection
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());
        JLabel phongBanLabel = new JLabel("Chọn phòng ban:");
        phongBanComboBox = new JComboBox<>();
        loadPhongBanComboBox(); // Load department list into ComboBox
        JButton xemButton = new JButton("Xem");
        JButton xoaTatCaButton = new JButton("Xóa tất cả");

        inputPanel.add(phongBanLabel);
        inputPanel.add(phongBanComboBox);
        inputPanel.add(xemButton);
        inputPanel.add(xoaTatCaButton);
        add(inputPanel, BorderLayout.NORTH);

        // Table to display employee data
        String[] columnNames = {"ID", "CCCD", "Tên", "Sdt", "Email", "Giới tính", "Năm sinh", "Chức vụ", "Phòng ban"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Event for the "Xem" button
        xemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xemNhanVienTheoPhongBan();
            }
        });

        // Event for the "Xóa tất cả" button
        xoaTatCaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xoaTatCaNhanVienTrongPhongBan();
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

    private void xemNhanVienTheoPhongBan() {
        String tenPhongBan = (String) phongBanComboBox.getSelectedItem();
        if (tenPhongBan == null || tenPhongBan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng ban!");
            return;
        }

        List<NhanVien> employees = controller.layNhanVienTheoPhongBan(tenPhongBan);
        tableModel.setRowCount(0); // Clear old data in the table
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

    private void xoaTatCaNhanVienTrongPhongBan() {
        String tenPhongBan = (String) phongBanComboBox.getSelectedItem();
        if (tenPhongBan == null || tenPhongBan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng ban!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa tất cả nhân viên trong phòng ban này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
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
            XemNhanVien frame = new XemNhanVien();
            frame.setVisible(true);
        });
    }
}