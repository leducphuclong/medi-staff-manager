package com.MediStaffManager.view.quanLyLuong;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.File;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.MediStaffManager.bean.LuongNhanVienBEAN;
import com.MediStaffManager.bean.ThongKeResult;
import com.MediStaffManager.controller.LuongNhanVienController;

public class ThongKeView {
	private WebEngine webEngine;
	private LuongNhanVienController luongNhanVienController;

	public ThongKeView(WebEngine webEngine) {
		this.webEngine = webEngine;
		this.luongNhanVienController = new LuongNhanVienController();
	}

	public void taiTrang(Stage primaryStage, WebView webView) {
		final String basePath = "./src/com/MediStaffManager/view/quanLyLuong/html/";
		final String fileName = "QuanLyLuong.html";
		String filePath = basePath + fileName;
		File htmlFile = new File(filePath);

		if (htmlFile.exists() && htmlFile.isFile()) {
			String url = htmlFile.toURI().toString();
			webView.getEngine().load(url);
		} else {
			webView.getEngine().loadContent(
					"<html><body><h1>Lỗi khởi tạo</h1><p>Không thể tìm thấy Trang Quản Lý Lương</p></body></html>");
		}

		StackPane root = new StackPane();
		root.getChildren().add(webView);

		Scene scene = new Scene(root, 1900, 1000);
		primaryStage.setTitle("Medi Staff Manager - Trang Quản Lý Lương");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	
}
