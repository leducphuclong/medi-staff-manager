package com.MediStaffManager.view.quenMatKhau;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.MediStaffManager.utils.DBConnection;

public class QuenMatKhauBridge {
    private WebEngine webEngine;
    final String basePath = "./src/com/MediStaffManager/view/";
    private Connection conn;

    public QuenMatKhauBridge(WebEngine webEngine) {
        this.webEngine = webEngine;
        this.conn = DBConnection.connect();
    }
    
    public String getPasswordByEmail(String email) throws SQLException {
        String query = "SELECT MatKhau FROM tai_khoan WHERE email = ?";  

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("MatKhau");  
                }
            }
        }
        return null;  
    }
    
    public void sendEmail(String to, String subject, String messageContent) {
        String from = "MediStaffManager"; // Replace with your email address
        String host = "smtp.gmail.com"; // Gmail SMTP server

        final String username = "leducphuclong@gmail.com"; // Replace with your email
        final String appPassword = "ayew szdc aztf gnmt"; // Replace with your App Password

        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true"); // Use TLS

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, appPassword); // Use App Password
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(from));

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            message.setSubject(subject);

            message.setText(messageContent);

            Transport.send(message);
            System.out.println("Message sent successfully...");

        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
    
    public boolean sendPasswordByEmail(String email) {
        try {
            String password = getPasswordByEmail(email);
            if (password != null) {
                String subject = "Mật khẩu của bạn";
                String messageContent = "Mật khẩu của bạn là: " + password;

                sendEmail(email, subject, messageContent); 
                return true;
            } else {
                System.out.println("Không tìm thấy tài khoản với email này.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
		return false;
    }
    
    public void taiTrang(Stage primaryStage, WebView webView) {
    	final String basePath = "./src/com/MediStaffManager/view/quenMatKhau/";
        final String fileName = "quenMatKhau.html";
        String filePath = basePath + fileName;
        File htmlFile = new File(filePath);

        if (htmlFile.exists() && htmlFile.isFile()) {
            String url = htmlFile.toURI().toString();
            webView.getEngine().load(url);
        } else {
        	webView.getEngine().loadContent("<html><body><h1>Lỗi khởi tạo</h1><p>Không thể tìm thấy Trang Quên Mật Khẩu</p></body></html>");
        }
        
        StackPane root = new StackPane();
        root.getChildren().add(webView);

        Scene scene = new Scene(root, 1900, 1000);
        primaryStage.setTitle("Medi Staff Manager - Quên Mật Khẩu");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void hello() {
        System.out.println("hello");
    }

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}
}
