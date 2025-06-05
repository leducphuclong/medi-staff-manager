package com.MediStaffManager.view;

// Standard Java imports
import java.io.File;
import java.net.MalformedURLException;
import java.util.List;
import java.util.stream.Collectors;

// JavaFX imports
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

// JavaScript bridge (requires jdk.jsobject module if using Java 9+ modules)
import netscape.javascript.JSObject;

// External library for JSON processing (ensure Gson is in your project's classpath)
import com.google.gson.Gson;

// Local project imports
import com.MediStaffManager.controller.NhanVienController;

public class XemPhongBan extends Application {
    private NhanVienController controller;
    private WebView webView;
    private WebEngine webEngine;
    private Stage primaryStage;

    // Path to the HTML file relative to the classpath root.
    private static final String HTML_FILE_PATH = "/com/MediStaffManager/view/phongBan.html";

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.controller = new NhanVienController(); // Ensure NhanVienController is initialized
        primaryStage.setTitle("Quản lý Phòng Ban - Family Hospital");

        this.webView = new WebView();
        this.webEngine = webView.getEngine();

        webEngine.setJavaScriptEnabled(true);

        // Log JavaScript console messages to Java console for easier debugging
        webEngine.setOnConsoleMessage(event -> {
            System.out.println("JS Console: [" + event.sourceID() + ":" + event.lineNumber() + "] " + event.message());
        });

        // Handle JavaScript alert() calls by showing a JavaFX Alert
        webEngine.setOnAlert(event -> showAlertInJava(AlertType.INFORMATION, "Thông báo từ Web", event.getData()));

        // Handle JavaScript confirm() calls using a JavaFX Alert
        webEngine.setConfirmHandler(message -> {
            Alert confirmDialog = new Alert(AlertType.CONFIRMATION);
            confirmDialog.setTitle("Xác nhận");
            confirmDialog.setHeaderText(null);
            confirmDialog.setContentText(message);
            styleAlertDialog(confirmDialog);
            return confirmDialog.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
        });

        // Load HTML content
        try {
            // Correctly load HTML file from classpath
            java.net.URL htmlUrl = getClass().getResource(HTML_FILE_PATH);
            if (htmlUrl == null) {
                String errorMsg = "Không thể tìm thấy tệp giao diện: " + HTML_FILE_PATH +
                                  "\nVui lòng kiểm tra đường dẫn và cấu trúc project." +
                                  "\nĐường dẫn tìm kiếm từ gốc classpath: " + HTML_FILE_PATH;
                System.err.println(errorMsg);
                showAlertInJava(AlertType.ERROR, "Lỗi nghiêm trọng", errorMsg);
                return;
            }
            String localUrl = htmlUrl.toExternalForm();
            System.out.println("Java: Loading HTML from: " + localUrl);
            webEngine.load(localUrl);
        } catch (Exception e) { // Catch generic Exception for any loading issues
            e.printStackTrace();
            showAlertInJava(AlertType.ERROR, "Lỗi nghiêm trọng", "Không thể tải giao diện người dùng: " + e.getMessage());
            return;
        }

        // Bridge Java and JavaScript once the page is loaded
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaConnector", new JavaConnector());
                System.out.println("Java: JavaConnector injected into JavaScript.");
            } else if (newState == Worker.State.FAILED) {
                System.err.println("Java: WebEngine failed to load the page.");
                showAlertInJava(AlertType.ERROR, "Lỗi WebEngine", "Không thể tải trang. Kiểm tra console (System.err) để biết thêm chi tiết.");
                if (webEngine.getLoadWorker().getException() != null) {
                    webEngine.getLoadWorker().getException().printStackTrace();
                }
            }
        });

        BorderPane mainLayout = new BorderPane();
        mainLayout.setCenter(webView);

        Scene scene = new Scene(mainLayout, 950, 650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Inner class to expose Java methods to JavaScript
    public class JavaConnector {
        private final Gson gson = new Gson();

        public void loadPhongBanData() {
            System.out.println("JavaConnector: loadPhongBanData called by JavaScript."); // Log added
            Platform.runLater(() -> {
                try {
                    if (controller == null) {
                         System.err.println("JavaConnector: NhanVienController is null in loadPhongBanData.");
                         showAlertInJava(AlertType.ERROR, "Lỗi", "Controller chưa được khởi tạo.");
                         return;
                    }
                    List<Object[]> phongBanListRaw = controller.layDanhSachPhongBan();
                    List<PhongBanSimple> phongBanList = phongBanListRaw.stream()
                        .map(objArray -> new PhongBanSimple((Integer)objArray[0], (String)objArray[1]))
                        .collect(Collectors.toList());
                    String jsonData = gson.toJson(phongBanList);
                    System.out.println("JavaConnector: Sending data to JS populateTableWithData: " + (jsonData.length() > 100 ? jsonData.substring(0, 100) + "..." : jsonData)); // Log added

                    String script = String.format("if(typeof window.populateTableWithData === 'function') { window.populateTableWithData('%s'); } else { console.error('JavaScript function populateTableWithData not found.'); }", escapeJsString(jsonData));
                    webEngine.executeScript(script);

                } catch (Exception e) {
                    System.err.println("JavaConnector: Exception in loadPhongBanData: " + e.getMessage()); // Log added
                    e.printStackTrace();
                    showAlertInJava(AlertType.ERROR, "Lỗi", "Không thể cập nhật danh sách phòng ban: " + e.getMessage());
                }
            });
        }

        public boolean themPhongBan(int id, String ten) {
            System.out.println("JavaConnector: themPhongBan called with ID: " + id + ", Ten: " + ten); // Log added
            if (controller == null) {
                System.err.println("JavaConnector: NhanVienController is null in themPhongBan.");
                showAlertInJava(AlertType.ERROR, "Lỗi", "Controller chưa được khởi tạo.");
                return false;
            }
            boolean result = false;
            try {
                result = controller.themPhongBan(id, ten);
            } catch (Exception e) {
                System.err.println("JavaConnector: Exception in controller.themPhongBan: " + e.getMessage());
                e.printStackTrace();
                showAlertInJava(AlertType.ERROR, "Lỗi Controller", "Lỗi khi thêm phòng ban: " + e.getMessage());
                return false;
            }
            System.out.println("JavaConnector: themPhongBan result from controller: " + result); // Log added
            return result;
        }

        public boolean suaPhongBan(int idCu, int idMoi, String tenMoi) {
            System.out.println("JavaConnector: suaPhongBan called with ID Cũ: " + idCu + ", ID Mới: " + idMoi + ", Tên Mới: " + tenMoi); // Log added
            if (controller == null) {
                System.err.println("JavaConnector: NhanVienController is null in suaPhongBan.");
                showAlertInJava(AlertType.ERROR, "Lỗi", "Controller chưa được khởi tạo.");
                return false;
            }
            boolean result = false;
            try {
                result = controller.suaPhongBan(idCu, idMoi, tenMoi);
            } catch (Exception e) {
                System.err.println("JavaConnector: Exception in controller.suaPhongBan: " + e.getMessage());
                e.printStackTrace();
                showAlertInJava(AlertType.ERROR, "Lỗi Controller", "Lỗi khi sửa phòng ban: " + e.getMessage());
                return false;
            }
            System.out.println("JavaConnector: suaPhongBan result from controller: " + result); // Log added
            return result;
        }

        public boolean xoaPhongBan(int id, String ten) {
            System.out.println("JavaConnector: xoaPhongBan called with ID: " + id + ", Ten (for context): " + ten); // Log added
            if (controller == null) {
                System.err.println("JavaConnector: NhanVienController is null in xoaPhongBan.");
                showAlertInJava(AlertType.ERROR, "Lỗi", "Controller chưa được khởi tạo.");
                return false;
            }
            boolean result = false;
            try {
                result = controller.xoaPhongBan(id);
            } catch (Exception e) {
                System.err.println("JavaConnector: Exception in controller.xoaPhongBan: " + e.getMessage());
                e.printStackTrace();
                showAlertInJava(AlertType.ERROR, "Lỗi Controller", "Lỗi khi xóa phòng ban: " + e.getMessage());
                return false;
            }
            System.out.println("JavaConnector: xoaPhongBan result from controller: " + result); // Log added
            return result;
        }

        public void navigateToNhanVien() {
            Platform.runLater(() -> {
                showAlertInJava(AlertType.INFORMATION, "Thông báo", "Chuyển đến trang Nhân viên (chưa triển khai).");
            });
        }

        public void navigateToPhongBan() {
            Platform.runLater(() -> {
                loadPhongBanData();
            });
        }

        public void navigateToLichLamViec() {
            Platform.runLater(() -> {
                showAlertInJava(AlertType.INFORMATION, "Thông báo", "Chức năng Lịch Làm Việc chưa được triển khai.");
            });
        }

        public void logout() {
             Platform.runLater(() -> {
                showAlertInJava(AlertType.INFORMATION, "Thông báo", "Đăng xuất (chưa triển khai).");
            });
        }

        public void viewNhanVienTheoPhongBan(int id, String ten) {
            Platform.runLater(() -> {
                showAlertInJava(AlertType.INFORMATION, "Thông báo", 
                    "Xem nhân viên theo phòng ban: " + ten + " (ID: " + id + ") (chưa triển khai).");
            });
        }

        public void showAlert(String type, String message) {
            Platform.runLater(() -> {
                AlertType alertTypeEnum;
                switch (type.toUpperCase()) {
                    case "ERROR": alertTypeEnum = AlertType.ERROR; break;
                    case "WARNING": alertTypeEnum = AlertType.WARNING; break;
                    case "INFORMATION":
                    default: alertTypeEnum = AlertType.INFORMATION; break;
                }
                showAlertInJava(alertTypeEnum, type.substring(0,1).toUpperCase() + type.substring(1).toLowerCase() + " Dialog", message);
            });
        }
    }

    public static class PhongBanSimple {
        int id;
        String ten;
        public PhongBanSimple(int id, String ten) {
            this.id = id;
            this.ten = ten;
        }
        public int getId() { return id; }
        public String getTen() { return ten; }
    }

    private void showAlertInJava(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        styleAlertDialog(alert);
        alert.showAndWait();
    }

    private void styleAlertDialog(Alert alert) {
        alert.getDialogPane().setStyle("-fx-font-family: 'System', 'Segoe UI', sans-serif; -fx-font-size: 13px;");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    }

    private String escapeJsString(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                .replace("\b", "\\b")
                .replace("\f", "\\f");
    }

    public static void main(String[] args) {
        launch(args);
    }
}