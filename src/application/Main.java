package application;

import java.sql.Connection;

import com.MediStaffManager.utils.DBConnection;
import com.MediStaffManager.view.Bridge;

import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class Main extends Application {
    
    private Bridge bridge;

    @SuppressWarnings("unused")
	@Override
    public void start(Stage primaryStage) {
        WebView webView = new WebView();
        
        WebEngine webEngine = webView.getEngine();
        
        webEngine.setJavaScriptEnabled(true);

        bridge = new Bridge(webView, primaryStage);

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("bridge", bridge);
                String url = webEngine.getLocation();
                String fileName = extractFileNameFromUrl(url);
                System.out.println("Kết nối " + fileName + " với bridge tổng thành công!");
            }
        });
        
//        bridge.getQuanLyNhanSuBridge().taiTrang(primaryStage, webView);
        bridge.getDangNhapBridge().taiTrang(primaryStage, webView);
//        bridge.getQuanLyNhanVienBridge().taiTrang(primaryStage, webView);
//        bridge.getQuanLyPhongBanBridge().taiTrang(primaryStage, webView);
//        bridge.showLich(primaryStage);
//        bridge.getQuanLyLuongBridge().taiTrang(primaryStage, webView);
//        bridge.getThongKeLuongBridge().taiTrang(primaryStage, webView);
//        bridge.getKeToanBridge().taiTrang(primaryStage, webView);
    }

    private String extractFileNameFromUrl(String url) {
        try {
            String fileName = url.substring(url.lastIndexOf("/") + 1);
            return fileName;
        } catch (Exception e) {
            System.err.println("Lỗi khi trích xuất tên file: " + e.getMessage());
            return "Không thể xác định tên file";
        }
    }

	public static void main(String[] args) {
        launch(args);
    }
}
