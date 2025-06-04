package com.MediStaffManager.view.dangNhap;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class DangNhapBridge {
	private WebEngine webEngine;
	final String basePath = "./src/com/MediStaffManager/view/";

	public DangNhapBridge(WebEngine webEngine) {
		this.webEngine = webEngine;
	}

//    public void chuyenDenTrangQuenMatKhau() {
//        Platform.runLater(() -> {
//            System.out.println("Chuẩn bị chuyển đến trang Quên mật khẩu");
//            try {
//                File htmlFile = new File(basePath + "quenMatKhau/quenMatKhau.html");
//                if (htmlFile.exists()) {
//                    URL url = htmlFile.toURI().toURL();
//                    webEngine.load(url.toExternalForm());
//                } else {
//                    System.out.println("Không tìm thấy trang Quên Mật Khẩu");
//                    webEngine.loadContent("<html><body><h1>Error</h1><p> Không tìm thấy Trang Quên Mật Khẩu</p></body></html>");
//                }
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//                webEngine.loadContent("<html><body><h1>Lỗi</h1><p>URL bị sai cú pháp cho Trang Quên Mật Khẩu</p></body></html>");
//            }
//        });
//    }

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

	public void hello() {
		System.out.println("hello");
	}
}
