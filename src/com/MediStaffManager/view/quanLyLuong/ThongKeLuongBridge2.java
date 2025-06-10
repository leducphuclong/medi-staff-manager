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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.MediStaffManager.bean.LuongNhanVien;
import com.MediStaffManager.bean.ThongKeResult;
import com.MediStaffManager.controller.LuongNhanVienController;

public class ThongKeLuongBridge2 {
	private WebEngine webEngine;
	private LuongNhanVienController luongNhanVienController;

	public ThongKeLuongBridge2(WebEngine webEngine) {
		this.webEngine = webEngine;
		this.luongNhanVienController = new LuongNhanVienController();
	}

	public void taiTrang(Stage primaryStage, WebView webView) {
		final String basePath = "./src/com/MediStaffManager/view/quanLyLuong/html/";
		final String fileName = "ThongKe.html";
		String filePath = basePath + fileName;
		File htmlFile = new File(filePath);

		if (htmlFile.exists() && htmlFile.isFile()) {
			String url = htmlFile.toURI().toString();
			webView.getEngine().load(url);
		} else {
			webView.getEngine().loadContent("<html><body><h1>Lỗi khởi tạo</h1><p>Không thể tìm thấy Trang Quản Lý Lương</p></body></html>");
		}

		StackPane root = new StackPane();
		root.getChildren().add(webView);

		Scene scene = new Scene(root, 1400, 800);
		primaryStage.setTitle("Medi Staff Manager - Trang Thống Kê Lương");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public String thongKeTheoThang(String thangNam) {
		try {
			ThongKeResult tk = luongNhanVienController.thongKeTheoThang(thangNam);
			return "[{" + "\"soNhanVien\":" + tk.getSoNhanVien() + "," + "\"luongTrungBinh\":" + tk.getLuongTrungBinh()
					+ "," + "\"tongLuong\":" + tk.getTongLuong() + "}]";
		} catch (Exception e) {
			e.printStackTrace();
			return "[]";
		}
	}

	// Thống kê theo quý
	public String thongKeTheoQuy(int nam, int quy) {
		try {
			ThongKeResult tk = luongNhanVienController.thongKeTheoQuy(nam, quy);
			return "[{" + "\"soNhanVien\":" + tk.getSoNhanVien() + "," + "\"luongTrungBinh\":" + tk.getLuongTrungBinh()
					+ "," + "\"tongLuong\":" + tk.getTongLuong() + "}]";
		} catch (Exception e) {
			e.printStackTrace();
			return "[]";
		}
	}

	// Thống kê theo năm
	public String thongKeTheoNam(int nam) {
		try {
			ThongKeResult tk = luongNhanVienController.thongKeTheoNam(nam);
			return "[{" + "\"soNhanVien\":" + tk.getSoNhanVien() + "," + "\"luongTrungBinh\":" + tk.getLuongTrungBinh()
					+ "," + "\"tongLuong\":" + tk.getTongLuong() + "}]";
		} catch (Exception e) {
			e.printStackTrace();
			return "[]";
		}
	}
	
	public void log(String mess) {
		System.out.println(mess);
	}
}
