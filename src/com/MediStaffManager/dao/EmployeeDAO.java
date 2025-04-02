package com.MediStaffManager.dao;

import java.sql.*;
import java.util.*;
import com.MediStaffManager.bean.Employee;
import com.MediStaffManager.utils.DBConnection;

public class EmployeeDAO {
    private Connection connection;

    public EmployeeDAO() {
        this.connection = DBConnection.connect();
    }

    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        try {
            String query = "SELECT * FROM nhan_vien";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                int id = rs.getInt("IDNhanVien");
                String name = rs.getString("HoTen");
                String position = rs.getString("IDChucVu");
                String email = rs.getString("Email");

                Employee employee = new Employee(id, name, position, email);
                employees.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }
}
