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

public class QuenMatKhauBridge {
    private WebEngine webEngine;
    final String basePath = "./src/com/MediStaffManager/view/";

    public QuenMatKhauBridge(WebEngine webEngine) {
        this.webEngine = webEngine;
    }

    public void chuyenDenTrangDangNhap() {
        Platform.runLater(() -> {
            System.out.println("Chuẩn bị chuyển đến trang Đăng Nhập");
            try {
                File htmlFile = new File(basePath + "dangNhap/dangNhap.html");
                if (htmlFile.exists()) {
                    URL url = htmlFile.toURI().toURL();
                    webEngine.load(url.toExternalForm());
                } else {
                    System.out.println("Không tìm thấy trang Đăng Nhập");
                    webEngine.loadContent("<html><body><h1>Error</h1><p> Không tìm thấy Trang Đăng Nhập</p></body></html>");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                webEngine.loadContent("<html><body><h1>Lỗi</h1><p>URL bị sai cú pháp cho Trang Đăng Nhập</p></body></html>");
            }
        });
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
}
