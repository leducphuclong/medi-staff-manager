package com.MediStaffManager.view.quanLyLuong;

import com.MediStaffManager.controller.LuongNhanVienController;
import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import java.net.URL;

public class QuanLyLuongView extends Application {

    @Override
    public void start(Stage primaryStage) {
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);

        // Khởi tạo các thành phần backend
        LuongNhanVienController controller = new LuongNhanVienController();
        QuanLyLuongBridge bridge = new QuanLyLuongBridge(webView, controller);

        // Lắng nghe trạng thái tải trang của WebView - Đây là chìa khóa
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            // Chỉ thực hiện "bắt tay" khi trang đã tải thành công HOÀN TOÀN
            if (newState == Worker.State.SUCCEEDED) {
                // Bước 1: Inject đối tượng Java vào môi trường JavaScript.
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaBridge", bridge);

                // Bước 2: Sau khi bridge đã chắc chắn tồn tại, gọi hàm khởi tạo phía JS.
                // Đây là tín hiệu "đánh thức" ứng dụng JavaScript.
                webEngine.executeScript("initializeApplication()");
            }
        });

        // Chuyển hướng các alert() của JS ra console Java để debug,
        // tránh các popup native xấu xí và gây lỗi chồng chéo.
        webEngine.setOnAlert(event -> System.out.println("JS Alert: " + event.getData()));

        // Tải trang HTML
        loadPage(webEngine);

        // Cấu hình Stage và Scene
        StackPane root = new StackPane(webView);
        Scene scene = new Scene(root, 1280, 800);
        
        primaryStage.setTitle("Medi Staff Manager - Quản Lý Lương");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadPage(WebEngine webEngine) {
        try {
            URL url = getClass().getResource("html/QuanLyLuong.html");
            if (url == null) {
                throw new RuntimeException("Lỗi nghiêm trọng: Không tìm thấy file QuanLyLuong.html. Hãy kiểm tra đường dẫn trong resources.");
            }
            webEngine.load(url.toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
            webEngine.loadContent(String.format("<html><body><h1>Lỗi Tải Giao Diện</h1><p>%s</p></body></html>", e.getMessage()));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}