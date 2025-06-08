package com.MediStaffManager.view.trangChu;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class KeToanBridge {
	private WebEngine webEngine;

	public KeToanBridge(WebEngine webEngine) {
		this.webEngine = webEngine;
	}

	public void taiTrang(Stage primaryStage, WebView webView) {
		final String basePath = "./src/com/MediStaffManager/view/trangChu/";
		final String fileName = "keToan.html";
		String filePath = basePath + fileName;
		File htmlFile = new File(filePath);

		if (htmlFile.exists() && htmlFile.isFile()) {
			String url = htmlFile.toURI().toString();
			webView.getEngine().load(url);
		} else {
			webView.getEngine().loadContent(
					"<html><body><h1>Lỗi khởi tạo</h1><p>Không thể tìm thấy Trang Kế Toán</p></body></html>");
		}

		StackPane root = new StackPane();
		root.getChildren().add(webView);

		Scene scene = new Scene(root, 1900, 1000);
		primaryStage.setTitle("Medi Staff Manager - Trang Chủ Kế Toán");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public void hello() {
		System.out.println("hello");
	}
}
