package com.MediStaffManager.view.quanLyNhanVien;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import com.google.gson.Gson;

import java.io.File;
import java.util.List;

import com.MediStaffManager.bean.Employee;
import com.MediStaffManager.controller.NhanVienController;

public class QuanLyNhanVienBridge {
    private WebEngine webEngine;
    private Gson gson;
    private NhanVienController nhanVienController;

    public QuanLyNhanVienBridge(WebEngine webEngine) {
        this.webEngine = webEngine;
        this.nhanVienController = new NhanVienController();
        this.gson = new Gson();
    }
    
    public void taiTrang(Stage primaryStage, WebView webView) {
    	final String basePath = "./src/com/MediStaffManager/view/quanLyNhanVien/";
        final String fileName = "quanLyNhanVien.html";
        String filePath = basePath + fileName;
        File htmlFile = new File(filePath);

        if (htmlFile.exists() && htmlFile.isFile()) {
            String url = htmlFile.toURI().toString();
            webView.getEngine().load(url);
        } else {
        	webView.getEngine().loadContent("<html><body><h1>Lỗi khởi tạo</h1><p>Không thể tìm thấy Quản Lý Nhân Viên</p></body></html>");
        }
        
        StackPane root = new StackPane();
        root.getChildren().add(webView);

        Scene scene = new Scene(root, 1900, 1000);
        primaryStage.setTitle("Medi Staff Manager - Quản Lý Nhân Viên");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public String getAllEmployees() {
    	System.out.println("da goi!");
        List<Employee> employees = nhanVienController.getAllEmployees();
        return gson.toJson(employees);
    }
    
    public String addEmployee(String employeeJson) {
        return nhanVienController.addEmployee(employeeJson);
    }

    public String updateEmployee(String employeeJson) {
        return nhanVienController.updateEmployee(employeeJson);
    }

    public String deleteEmployee(int idNhanVien) {
        return nhanVienController.deleteEmployee(idNhanVien);
    }

    public String searchEmployees(String keyword, String criteria) {
        List<Employee> employees = nhanVienController.searchEmployees(keyword, criteria);
        return gson.toJson(employees);
    }

    public String getAllTenChucVu() {
        List<String> chucVuList = nhanVienController.getAllTenChucVu();
        return gson.toJson(chucVuList);
    }

    public String getAllTenPhongBan() {
        List<String> phongBanList = nhanVienController.getAllTenPhongBan();
        return gson.toJson(phongBanList);
    }

    public String getHeSoLuongByTenChucVu(String tenChucVu) {
        return nhanVienController.getHeSoLuongByTenChucVu(tenChucVu);
    }

    
    public void hello() {
        System.out.println("hello");
    }
}
