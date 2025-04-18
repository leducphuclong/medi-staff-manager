package com.MediStaffManager.view;

import com.MediStaffManager.controller.NhanVienController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class XemPhongBan extends JFrame {
    private JTable phongBanTable;
    private DefaultTableModel phongBanTableModel;
    private NhanVienController controller;

    public XemPhongBan() {
        controller = new NhanVienController();
        setTitle("Quản lý Phòng Ban");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        JButton xemButton = new JButton("Xem");
        JButton themButton = new JButton("Thêm");
        JButton suaButton = new JButton("Sửa");
        JButton xoaButton = new JButton("Xóa");

        buttonPanel.add(xemButton);
        buttonPanel.add(themButton);
        buttonPanel.add(suaButton);
        buttonPanel.add(xoaButton);
        add(buttonPanel, BorderLayout.NORTH);

        // Table to display department data
        String[] columnNames = {"ID phòng ban", "Tên phòng ban"};
        phongBanTableModel = new DefaultTableModel(columnNames, 0);
        phongBanTable = new JTable(phongBanTableModel);
        JScrollPane scrollPane = new JScrollPane(phongBanTable);
        add(scrollPane, BorderLayout.CENTER);

        // Event for the "Xem" button
        xemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xemDanhSachPhongBan();
            }
        });

        // Event for the "Thêm" button
        themButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                themPhongBan();
            }
        });

        // Event for the "Sửa" button
        suaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                suaPhongBan();
            }
        });

        // Event for the "Xóa" button
        xoaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xoaPhongBan();
            }
        });
    }

    private void xemDanhSachPhongBan() {
        List<Object[]> phongBanList = controller.layDanhSachPhongBan();
        phongBanTableModel.setRowCount(0); // Clear old data
        for (Object[] phongBan : phongBanList) {
            phongBanTableModel.addRow(phongBan);
        }

        if (phongBanList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có phòng ban nào!");
        }
    }

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
                    xemDanhSachPhongBan();
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm phòng ban thất bại! ID có thể đã tồn tại.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "ID phòng ban phải là số nguyên!");
            }
        }
    }

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
                    xemDanhSachPhongBan();
                } else {
                    JOptionPane.showMessageDialog(this, "Sửa phòng ban thất bại! Kiểm tra lại thông tin.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "ID phòng ban phải là số nguyên!");
            }
        }
    }

    private void xoaPhongBan() {
        int selectedRow = phongBanTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng ban để xóa!");
            return;
        }

        String tenPhongBan = (String) phongBanTableModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa phòng ban này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.xoaPhongBan(tenPhongBan);
            if (success) {
                JOptionPane.showMessageDialog(this, "Xóa phòng ban thành công!");
                xemDanhSachPhongBan();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa phòng ban vì vẫn còn nhân viên!");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            XemPhongBan frame = new XemPhongBan();
            frame.setVisible(true);
        });
    }
}