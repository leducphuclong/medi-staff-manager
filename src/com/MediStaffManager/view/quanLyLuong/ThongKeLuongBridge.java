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

public class ThongKeLuongBridge {
	private WebEngine webEngine;
	private LuongNhanVienController luongNhanVienController;

	public ThongKeLuongBridge(WebEngine webEngine) {
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
			try {
	            String htmlPath = "src/com/MediStaffManager/view/quanLyLuong/html/ThongKe.html";
	            String cssPath  = "src/com/MediStaffManager/view/quanLyLuong/css/ThongKe.css";
	            String jsPath   = "src/com/MediStaffManager/view/quanLyLuong/js/ThongKe.js";

	            String html = Files.readString(Paths.get(htmlPath), StandardCharsets.UTF_8);
	            String css  = Files.readString(Paths.get(cssPath), StandardCharsets.UTF_8);
	            String js   = Files.readString(Paths.get(jsPath), StandardCharsets.UTF_8);

	            html = html.replaceFirst("(?i)</head>", "<style>\n" + css + "\n</style>\n</head>");
	            html = html.replace("</body>", "<script>\n" + js + "\n</script>\n</body>");


	            webView.getEngine().loadContent(html);
	            System.out.println("✅ Load đầy đủ HTML + CSS + JS thành công.");

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		} else {
			webView.getEngine().loadContent(
					"<html><body><h1>Lỗi khởi tạo</h1><p>Không thể tìm thấy Trang Quản Lý Lương</p></body></html>");
		}

		StackPane root = new StackPane();
		root.getChildren().add(webView);

		Scene scene = new Scene(root, 1900, 1000);
		primaryStage.setTitle("Medi Staff Manager - Trang Thống Kê Lương");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public String thongKeTheoThang(String thangNam) {
        try {
            ThongKeResult tk = luongNhanVienController.thongKeTheoThang(thangNam);
            return "[{"
                + "\"soNhanVien\":" + tk.getSoNhanVien() + ","
                + "\"luongTrungBinh\":" + tk.getLuongTrungBinh() + ","
                + "\"tongLuong\":" + tk.getTongLuong()
                + "}]";
        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }

    // Thống kê theo quý
    public String thongKeTheoQuy(int nam, int quy) {
        try {
            ThongKeResult tk = luongNhanVienController.thongKeTheoQuy(nam, quy);
            return "[{"
                + "\"soNhanVien\":" + tk.getSoNhanVien() + ","
                + "\"luongTrungBinh\":" + tk.getLuongTrungBinh() + ","
                + "\"tongLuong\":" + tk.getTongLuong()
                + "}]";
        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }

    // Thống kê theo năm
    public String thongKeTheoNam(int nam) {
        try {
            ThongKeResult tk = luongNhanVienController.thongKeTheoNam(nam);
            return "[{"
                + "\"soNhanVien\":" + tk.getSoNhanVien() + ","
                + "\"luongTrungBinh\":" + tk.getLuongTrungBinh() + ","
                + "\"tongLuong\":" + tk.getTongLuong()
                + "}]";
        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }
}
