package com.MediStaffManager.view.dangNhap;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.MediStaffManager.utils.DBConnection;

public class DangNhapBridge {
	private WebEngine webEngine;
	final String basePath = "./src/com/MediStaffManager/view/";
	private Connection conn;

	public DangNhapBridge(WebEngine webEngine) {
		this.webEngine = webEngine;
		this.conn = DBConnection.connect();
	}

	public void taiTrang(Stage primaryStage, WebView webView) {
		final String basePath = "./src/com/MediStaffManager/view/dangNhap/";
		final String fileName = "dangNhap.html";
		String filePath = basePath + fileName;
		File htmlFile = new File(filePath);

		if (htmlFile.exists() && htmlFile.isFile()) {
			String url = htmlFile.toURI().toString();
			webView.getEngine().load(url);
		} else {
			webView.getEngine().loadContent(
					"<html><body><h1>Lỗi khởi tạo</h1><p>Không thể tìm thấy Trang Đăng Nhập</p></body></html>");
		}

		StackPane root = new StackPane();
		root.getChildren().add(webView);

		Scene scene = new Scene(root, 1900, 1000);
		primaryStage.setTitle("Medi Staff Manager - Đăng Nhập");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public boolean authDangNhap(String rawPassword, String encodedPassword) {
		try {
		    Class.forName("org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder");
		    // If the class is found, proceed with your code
		    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		    if (passwordEncoder.matches(rawPassword, encodedPassword)) {
		        return true;
		    } else {
		    	return false;
		    }
		} catch (ClassNotFoundException e) {
		    e.printStackTrace(); 
		}
		return false;
	}

	public boolean dangNhap(String username, String password) throws SQLException {
	    String query = "SELECT MatKhau FROM tai_khoan WHERE TenDangNhap = ?";

	    try (PreparedStatement statement = conn.prepareStatement(query)) {
	        statement.setString(1, username);
	        
	        try (ResultSet resultSet = statement.executeQuery()) {
	            if (resultSet.next()) {
	                String storedPassword = resultSet.getString("MatKhau");
	                return authDangNhap(password, storedPassword);
	            }
	        }
	    }
	    return false;
	}
	
	public String getVaiTroByUsername(String username) throws SQLException {
	    String query = "SELECT VaiTro FROM tai_khoan WHERE TenDangNhap = ?";

	    try (PreparedStatement statement = conn.prepareStatement(query)) {
	        statement.setString(1, username);

	        try (ResultSet resultSet = statement.executeQuery()) {
	            if (resultSet.next()) {
	                return resultSet.getString("VaiTro");
	            }
	        }
	    }
	    return null;
	}
	
	 public static void main(String[] args) {
	        Scanner scanner = new Scanner(System.in);

	        // Get username and password from the user
	        System.out.print("Nhập tên đăng nhập: ");
	        String username = scanner.nextLine();

	        System.out.print("Nhập mật khẩu: ");
	        String password = scanner.nextLine();

	        // Create DangNhapBridge instance and test login
	        DangNhapBridge dangNhapBridge = new DangNhapBridge(null);  // Pass null for WebEngine since we don't need it in this test

	        try {
	            if (dangNhapBridge.dangNhap(username, password)) {
	                System.out.println("Đăng nhập thành công!");
	            } else {
	                System.out.println("Sai tên đăng nhập hoặc mật khẩu!");
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }

	        scanner.close();
	    }
}
