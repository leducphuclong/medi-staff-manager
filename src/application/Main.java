package application;

import com.MediStaffManager.view.Bridge;
import com.MediStaffManager.view.dangNhap.DangNhapView;

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
                System.out.println("Kết nối với bridge tổng thành công!");
            }
        });

//        bridge.getQuanLyNhanSuBridge().taiTrang(primaryStage, webView);
        bridge.getDangNhapBridge().taiTrang(primaryStage, webView);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
