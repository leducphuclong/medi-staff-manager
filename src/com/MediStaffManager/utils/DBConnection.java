package com.MediStaffManager.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    
    public static Connection connect() {
        Connection connection = null;
        
        // Database credentials
<<<<<<< HEAD
        String url = "jdbc:mysql://localhost:3306/PBL3";  // Replace 'localhost' and 'PBL3' with your server's IP and database name
=======
        String url = "jdbc:mysql://localhost:3306/pbl3";  // Replace 'localhost' and 'PBL3' with your server's IP and database name
>>>>>>> ThaoDuyen-QuanLyLuong-ThongKe
        String username = "root";  // MySQL username, replace with your username if needed
        String password = "root";  // MySQL password, replace with your password

        try {
            // Load and register MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the connection
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connection successful!");

        } catch (SQLException e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found!");
            e.printStackTrace();
        }

        return connection;
    }

    public static void main(String[] args) {
        // Test the connection
        Connection conn = connect();
        if (conn != null) {
            try {
                conn.close();  // Don't forget to close the connection after using it
                System.out.println("Connection closed!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
<<<<<<< HEAD
}
=======

	public static void closeConnection() {
		// TODO Auto-generated method stub
		
	}
}

//package com.MediStaffManager.utils;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
//public class DBConnection {
//    public static Connection connect() {
//        try {
//            // 1) Load driver
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            // 2) Tạo URL (tùy theo database name, timezone, SSL…)
//            String url = "jdbc:mysql://localhost:3306/pbl3?useSSL=false&serverTimezone=UTC";
//            String user = "root";
//            String pass = "root";
//            // 3) Kết nối
//            return DriverManager.getConnection(url, user, pass);
//        } catch (ClassNotFoundException | SQLException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//    // main() test connection giữ nguyên
//}

>>>>>>> ThaoDuyen-QuanLyLuong-ThongKe
