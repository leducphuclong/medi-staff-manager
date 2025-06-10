package com.MediStaffManager.view.trangChu;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class QuanLyNhanSuBridge {
	private WebEngine webEngine;

	public QuanLyNhanSuBridge(WebEngine webEngine) {
		this.webEngine = webEngine;
	}

	public void taiTrang(Stage primaryStage, WebView webView) {
		final String basePath = "./src/com/MediStaffManager/view/trangChu/";
		final String fileName = "quanLyNhanSu.html";
		String filePath = basePath + fileName;
		File htmlFile = new File(filePath);

		if (htmlFile.exists() && htmlFile.isFile()) {
			String url = htmlFile.toURI().toString();
			webView.getEngine().load(url);
		} else {
			webView.getEngine().loadContent(
					"<html><body><h1>Lỗi khởi tạo</h1><p>Không thể tìm thấy Trang Quản Lý Nhân Sự</p></body></html>");
		}

		StackPane root = new StackPane();
		root.getChildren().add(webView);

		Scene scene = new Scene(root, 1400, 800);
		primaryStage.setTitle("Medi Staff Manager - Trang Chủ Quản Lý Nhân Sự");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public void hello() {
		System.out.println("hello");
	}
}
