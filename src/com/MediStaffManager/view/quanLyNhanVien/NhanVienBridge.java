//package com.MediStaffManager.view.quanLyNhanVien;
//
//import com.MediStaffManager.bean.Employee;
//import com.MediStaffManager.controller.NhanVienController;
//import com.google.gson.Gson;
//import javafx.application.Platform;
//import java.net.URL; // Sử dụng java.net.URL
//import java.util.List;
//// Bỏ import Map và File nếu không dùng cách load cũ
//
//public class NhanVienBridge {
//
//    private NhanVienController nhanVienController;
//    private NhanVienView view; // Để lấy WebEngine
//    private Gson gson;
//
//    public NhanVienBridge(NhanVienController controller, NhanVienView view) {
//        this.nhanVienController = controller;
//        this.view = view;
//        this.gson = new Gson();
//    }
//
//    // Sử dụng ClassLoader để load trang HTML
//    public void loadInitialPage() {
//        Platform.runLater(() -> {
//            final String htmlResourceName = "trang_quan_ly_nhan_vien.html"; // Chỉ tên file nếu cùng package
//            try {
//                // getClass().getResource() tìm file trong cùng package với lớp NhanVienBridge
//                URL pageUrl = getClass().getResource(htmlResourceName);
//
//                if (pageUrl == null) {
//                    String attemptedPath = "/" + getClass().getPackage().getName().replace('.', '/') + "/" + htmlResourceName;
//                    System.err.println("NhanVienBridge: Cannot find HTML resource: " + htmlResourceName + " (attempted path: " + attemptedPath + ")");
//                    view.getWebEngine().loadContent("<html><body><h1>Lỗi: Không tìm thấy file HTML " + htmlResourceName + "</h1></body></html>");
//                    return;
//                }
//
//                view.getWebEngine().load(pageUrl.toExternalForm());
//                System.out.println("NhanVienBridge: Loaded page from resource: " + pageUrl.toExternalForm());
//
//            } catch (Exception e) {
//                System.err.println("NhanVienBridge: Error loading page from resource: " + htmlResourceName);
//                e.printStackTrace();
//                view.getWebEngine().loadContent("<html><body><h1>Lỗi khi tải trang</h1><p>" + e.getMessage() + "</p></body></html>");
//            }
//        });
//    }
//
//
//    // --- Methods callable from JavaScript ---
//
//    public String getAllEmployees() {
//        List<Employee> employees = nhanVienController.getAllEmployees();
//        return gson.toJson(employees);
//    }
//
//    // Nhận trực tiếp chuỗi JSON từ JavaScript
//    public String addEmployee(String employeeJson) {
//        return nhanVienController.addEmployee(employeeJson);
//    }
//
//    // Nhận trực tiếp chuỗi JSON từ JavaScript
//    public String updateEmployee(String employeeJson) {
//        return nhanVienController.updateEmployee(employeeJson);
//    }
//
//    // Các phương thức khác giữ nguyên cách gọi
//    public String deleteEmployee(int idNhanVien) {
//        return nhanVienController.deleteEmployee(idNhanVien);
//    }
//
//    public String searchEmployees(String keyword, String criteria) {
//        List<Employee> employees = nhanVienController.searchEmployees(keyword, criteria);
//        return gson.toJson(employees);
//    }
//
//    public String getAllTenChucVu() {
//        List<String> chucVuList = nhanVienController.getAllTenChucVu();
//        return gson.toJson(chucVuList);
//    }
//
//    public String getAllTenPhongBan() {
//        List<String> phongBanList = nhanVienController.getAllTenPhongBan();
//        return gson.toJson(phongBanList);
//    }
//
//    public void showAlert(String type, String message) {
//        Platform.runLater(() -> {
//            String script = String.format("if(typeof showAlertOnPage === 'function') { showAlertOnPage('%s', '%s'); } else { alert('%s: %s'); }",
//                                          type, message.replace("'", "\\'").replace("\n", "\\n"),
//                                          type, message.replace("'", "\\'").replace("\n", "\\n"));
//            try {
//                if (view != null && view.getWebEngine() != null) {
//                    view.getWebEngine().executeScript(script);
//                } else {
//                    System.err.println("NhanVienBridge.showAlert: WebEngine or View is null.");
//                }
//            } catch (Exception e) {
//                System.err.println("NhanVienBridge.showAlert: Error executing script: " + e.getMessage());
//            }
//        });
//    }
//
//     public void log(String message) {
//        System.out.println("JS Log: " + message);
//    }
//}
package com.MediStaffManager.view.quanLyNhanVien;

import com.MediStaffManager.bean.Employee;
import com.MediStaffManager.controller.NhanVienController;
import com.google.gson.Gson;
import javafx.application.Platform;
import java.net.URL;
import java.util.List;

public class NhanVienBridge {

    private NhanVienController nhanVienController;
    private NhanVienView view;
    private Gson gson;

    public NhanVienBridge(NhanVienController controller, NhanVienView view) {
        this.nhanVienController = controller;
        this.view = view;
        this.gson = new Gson();
    }

    public void loadInitialPage() {
        // ... (giữ nguyên)
        Platform.runLater(() -> {
            final String htmlResourceName = "trang_quan_ly_nhan_vien.html";
            try {
                URL pageUrl = getClass().getResource(htmlResourceName);
                if (pageUrl == null) {
                    String attemptedPath = "/" + getClass().getPackage().getName().replace('.', '/') + "/" + htmlResourceName;
                    System.err.println("NhanVienBridge: Cannot find HTML resource: " + htmlResourceName + " (attempted path: " + attemptedPath + ")");
                    view.getWebEngine().loadContent("<html><body><h1>Lỗi: Không tìm thấy file HTML " + htmlResourceName + "</h1></body></html>");
                    return;
                }
                view.getWebEngine().load(pageUrl.toExternalForm());
                System.out.println("NhanVienBridge: Loaded page from resource: " + pageUrl.toExternalForm());
            } catch (Exception e) {
                System.err.println("NhanVienBridge: Error loading page from resource: " + htmlResourceName);
                e.printStackTrace();
                view.getWebEngine().loadContent("<html><body><h1>Lỗi khi tải trang</h1><p>" + e.getMessage() + "</p></body></html>");
            }
        });
    }

    public String getAllEmployees() {
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

    // MỚI: Phương thức để JS lấy HeSoLuong
    public String getHeSoLuongByTenChucVu(String tenChucVu) {
        return nhanVienController.getHeSoLuongByTenChucVu(tenChucVu);
    }

    public void showAlert(String type, String message) {
        // ... (giữ nguyên)
        Platform.runLater(() -> {
            String script = String.format("if(typeof showAlertOnPage === 'function') { showAlertOnPage('%s', '%s'); } else { alert('%s: %s'); }",
                                          type, message.replace("'", "\\'").replace("\n", "\\n"),
                                          type, message.replace("'", "\\'").replace("\n", "\\n"));
            try {
                if (view != null && view.getWebEngine() != null) {
                    view.getWebEngine().executeScript(script);
                } else {
                    System.err.println("NhanVienBridge.showAlert: WebEngine or View is null.");
                }
            } catch (Exception e) {
                System.err.println("NhanVienBridge.showAlert: Error executing script: " + e.getMessage());
            }
        });
    }

     public void log(String message) {
        System.out.println("JS Log: " + message);
    }
}