package com.MediStaffManager.view;

import com.MediStaffManager.controller.EmployeeController;
import com.MediStaffManager.bean.Employee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class getAllEmployees extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private EmployeeController controller;

    public getAllEmployees() {
        controller = new EmployeeController();
        setTitle("Danh sách nhân viên");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Tạo bảng hiển thị dữ liệu
        String[] columnNames = {"ID", "Tên", "Vị trí", "Email"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Nạp dữ liệu từ cơ sở dữ liệu
        loadEmployeeData();
    }

    private void loadEmployeeData() {
        List<Employee> employees = controller.getAllEmployees();
        for (Employee employee : employees) {
            Object[] row = {employee.getId(), employee.getName(), employee.getPosition(), employee.getEmail()};
            tableModel.addRow(row);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        	getAllEmployees view = new getAllEmployees();
            view.setVisible(true);
        });
    }
}
