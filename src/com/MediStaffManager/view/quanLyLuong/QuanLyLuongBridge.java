package com.MediStaffManager.view.quanLyLuong;

import com.MediStaffManager.bean.LuongNhanVienBEAN;
import com.MediStaffManager.bean.LuongNhanVienBEAN;
import com.MediStaffManager.controller.LuongNhanVienController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

public class QuanLyLuongBridge {
    private final WebEngine webEngine;
    private final LuongNhanVienController controller;
    private final Gson gson;

    public QuanLyLuongBridge(WebEngine webEngine) {
        this.webEngine = webEngine;
        this.controller = new LuongNhanVienController();
        this.gson = new GsonBuilder().create();
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
        	webView.getEngine().loadContent("<html><body><h1>Lỗi khởi tạo</h1><p>Không thể tìm thấy Trang Quên Mật Khẩu</p></body></html>");
        }
        
        StackPane root = new StackPane();
        root.getChildren().add(webView);

        Scene scene = new Scene(root, 1400, 800);
        primaryStage.setTitle("Medi Staff Manager - Quản Lý Tài Khoản");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public List<LuongNhanVienBEAN> layLuongTheoThangVaPhongBan(String thangNam, int idPhongBan) {
        return controller.layLuongTheoThangVaPhongBan(thangNam, idPhongBan);
    }
    
    
    public String layTatCaPhongBan() {
    	return controller.layTatCaPhongBan();
    }

    public String layLuongTheoThang(String thangNam) {
        log("Java: Requesting salary data for " + thangNam);
        List<LuongNhanVienBEAN> beanList = controller.layLuongTheoThang(thangNam);
        return gson.toJson(beanList);
    }

    public void luuLuong(String luongJson) {
        try {
            // Deserialize vào đối tượng LuongNhanVien để lưu vào DB
            LuongNhanVienBEAN luong = gson.fromJson(luongJson, LuongNhanVienBEAN.class);
            
            // Deserialize vào BEAN để có thể tính tổng lương
            LuongNhanVienBEAN beanForCalc = gson.fromJson(luongJson, LuongNhanVienBEAN.class);
            BigDecimal tongLuong = controller.tinhTongLuong(beanForCalc);
            luong.setTongLuong(tongLuong);

            boolean success = controller.capNhatLuong(luong);
            String message = (luong.getIdLuong() > 0) ? "Cập nhật" : "Thêm";
            
            if (success) {
                callJsCallback("showNotification('%s lương thành công!', 'success'); closeModal(); window.loadData();", message);
            } else {
                callJsCallback("showNotification('%s lương thất bại. Có thể bản ghi đã tồn tại.', 'error');", message);
            }
        } catch (Exception e) {
            handleException("luuLuong", e);
        }
    }

    public void xoaLuong(int idLuong) {
        log("Java: Deleting salary with ID: " + idLuong);
        try {
            boolean success = controller.xoaLuong(idLuong);
            if (success) {
                callJsCallback("showNotification('Xóa bảng lương thành công.', 'success'); window.loadData();");
            } else {
                callJsCallback("showNotification('Xóa lương thất bại.', 'error');");
            }
        } catch (Exception e) {
            handleException("xoaLuong", e);
        }
    }

    public void capNhatLuongCoBanChuan(String luongCoBanChuanStr) {
        log("Java: Updating standard base salary to: " + luongCoBanChuanStr);
        try {
            BigDecimal newLuong = new BigDecimal(luongCoBanChuanStr);
            boolean success = controller.capNhatLuongCoBanChuanChoTatCa(newLuong);
            if (success) {
                callJsCallback("showNotification('Cập nhật LCB chuẩn thành công! Bảng lương sẽ được làm mới.', 'success'); window.loadData();");
            } else {
                callJsCallback("showNotification('Cập nhật LCB chuẩn thất bại.', 'error');");
            }
        } catch (Exception e) {
            handleException("capNhatLuongCoBanChuan", e);
        }
    }
    
    // Các hàm helper giữ nguyên
    private void handleException(String ctx, Exception e) { e.printStackTrace(); callJsCallback("showNotification('Lỗi hệ thống: %s', 'error');", e.getMessage()); }
    private void callJsCallback(String script, Object... args) { Platform.runLater(() -> webEngine.executeScript(String.format(script, args))); }
    public void log(String message) { System.out.println("[JS->Java] " + message); }
}