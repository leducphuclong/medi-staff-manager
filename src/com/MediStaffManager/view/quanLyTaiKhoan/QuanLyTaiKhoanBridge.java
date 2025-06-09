package com.MediStaffManager.view.quanLyTaiKhoan;

import com.MediStaffManager.bean.Employee;
import com.MediStaffManager.bean.TaiKhoan;
import com.MediStaffManager.controller.NhanVienController;
import com.MediStaffManager.controller.TaiKhoanController;
import com.google.gson.Gson;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class QuanLyTaiKhoanBridge {
    private WebEngine webEngine;
    private Gson gson;
    private TaiKhoanController taiKhoanController;
    private NhanVienController nhanVienController;

    public QuanLyTaiKhoanBridge(WebEngine webEngine) {
        this.webEngine = webEngine;
        this.taiKhoanController = new TaiKhoanController();
        this.nhanVienController = new NhanVienController();
        this.gson = new Gson();
    }
    
    public void taiTrang(Stage primaryStage, WebView webView) {
        this.webEngine = webView.getEngine();

        // Thiết lập cầu nối từ JavaScript sang Java
        JSObject window = (JSObject) webEngine.executeScript("window");
        window.setMember("javaBridge", this);

        // Thiết lập Confirm Handler để xử lý hàm confirm() của JavaScript
        webEngine.setConfirmHandler(message -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận");
            alert.setHeaderText(null);
            alert.setContentText(message);

            Optional<ButtonType> result = alert.showAndWait();

            return result.isPresent() && result.get() == ButtonType.OK;
        });
        
        // ---- ĐÃ XÓA PHẦN setAlertHandler GÂY LỖI ----
        // webEngine.setAlertHandler(message -> {
        //     Alert alert = new Alert(Alert.AlertType.INFORMATION);
        //     alert.setTitle("Thông báo");
        //     alert.setHeaderText(null);
        //     alert.setContentText((String) message.getData());
        //     alert.showAndWait();
        // });
        // ------------------------------------------------

        final String basePath = "./src/com/MediStaffManager/view/quanLyTaiKhoan/";
        final String fileName = "quanLyTaiKhoan.html";
        String filePath = basePath + fileName;
        File htmlFile = new File(filePath);
 
        if (htmlFile.exists() && htmlFile.isFile()) {
            String url = htmlFile.toURI().toString();
            webEngine.load(url);
        } else {
            webEngine.loadContent("<html><body><h1>Lỗi khởi tạo</h1><p>Không thể tìm thấy Quản Lý Tài Khoản</p></body></html>");
        }
 
        StackPane root = new StackPane();
        root.getChildren().add(webView);
 
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setTitle("Medi Staff Manager - Quản Lý Tài Khoản");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
 
    // Các phương thức còn lại giữ nguyên...
    public String getAllTaiKhoan() {
        List<TaiKhoan> danhSachTaiKhoan = taiKhoanController.getAllTaiKhoan();
        return gson.toJson(danhSachTaiKhoan);
    }
    public String getTaiKhoanByTenDangNhap(String tenDangNhap) {
        return taiKhoanController.getTaiKhoanByTenDangNhap(tenDangNhap);
    }
    public String addTaiKhoan(String taiKhoanJson) {
        return taiKhoanController.addTaiKhoan(taiKhoanJson);
    }
    public String updateTaiKhoan(String taiKhoanJson) {
        return taiKhoanController.updateTaiKhoan(taiKhoanJson);
    }
    public String deleteTaiKhoan(String tenDangNhap) {
        return taiKhoanController.deleteTaiKhoan(tenDangNhap);
    }
    public String getAllVaiTro() {
        List<String> vaiTroList = taiKhoanController.getAllVaiTro();
        return gson.toJson(vaiTroList);
    }
 
    public String getAllEmployeesForSelection() {
        try {
            List<Employee> list = nhanVienController.getAllEmployees(); 
            return gson.toJson(list);
        } catch (RuntimeException e) {
            System.err.println("Bridge: Error getting Employee list for selection: " + e.getMessage());
            return gson.toJson(Collections.emptyList());
        }
    }
    
    public void log(String message) {
        System.out.println("JS Log: " + message);
    }
}