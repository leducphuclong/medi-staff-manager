package com.MediStaffManager.view.quanLyLuong;

import com.MediStaffManager.controller.LuongNhanVienController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.event.EventHandler;
import javafx.scene.web.WebEvent;
import javafx.scene.web.PromptData;
import javafx.util.Callback;
import netscape.javascript.JSObject; // For bridge injection

public class QuanLyLuongView extends Application {

    private WebView webView;
    private WebEngine webEngine;
    private LuongNhanVienController luongController;
    private QuanLyLuongBridge luongBridge;

    public WebEngine getWebEngine() {
        return webEngine;
    }

    @Override
    public void start(Stage primaryStage) {
        luongController = new LuongNhanVienController();
        luongBridge = new QuanLyLuongBridge(luongController, this);

        webView = new WebView();
        webEngine = webView.getEngine();
        
     // Bắt tất cả window.alert(...) từ JS
        webEngine.setOnAlert(new EventHandler<WebEvent<String>>() {
            @Override
            public void handle(WebEvent<String> event) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Thông báo từ JavaScript");
                    alert.setHeaderText(null);
                    alert.setContentText(event.getData());
                    alert.showAndWait();
                });
            }
        });

        // (Tùy chọn) Bắt window.confirm(...)
        webEngine.setConfirmHandler(param -> {
            // Tạo dialog confirm JavaFX hoặc đơn giản return true/false
            return true;  // luôn OK
        });

        // (Tùy chọn) Bắt window.prompt(...)
        webEngine.setPromptHandler(new Callback<PromptData,String>() {
            @Override
            public String call(PromptData prompt) {
                // hiện TextInputDialog nếu cần
                return "";
            }
        });


        // Enable JavaScript
        webEngine.setJavaScriptEnabled(true);

        // Inject Java objects into JavaScript context when page is loaded
        webEngine.getLoadWorker().stateProperty().addListener(
            new ChangeListener<Worker.State>() {
                @Override
                public void changed(ObservableValue<? extends Worker.State> ov,
                                    Worker.State oldState, Worker.State newState) {
                    if (newState == Worker.State.SUCCEEDED) {
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        window.setMember("luongNhanVienBridge", luongBridge); // đây là tên cầu
                        System.out.println("QuanLyLuongView: Java bridge 'luongNhanVienBridge' injected into JavaScript.");

                        // Automatically call JS initialization after bridge injection
                        String scriptToExecute = "if(typeof initializePage === 'function') {" +
                                                 "    initializePage();" +
                                                 "}";
                        webEngine.executeScript(scriptToExecute);
                    } else if (newState == Worker.State.FAILED) {
                         System.err.println("QuanLyLuongView: WebView failed to load page.");
                         webEngine.loadContent("<html><body><h1>Page Load Failed</h1><p>Could not load the requested HTML page. Check console for errors.</p></body></html>");
                    }
                }
            }
        );

        // Load the HTML page
        luongBridge.loadPage("Main.html");

        StackPane root = new StackPane(webView);
        Scene scene = new Scene(root, 1200, 750);

        primaryStage.setTitle("Quản Lý Lương Nhân Viên - MediStaffManager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

//	public void setVisible(boolean b) {
//		// TODO Auto-generated method stub
//		
//	}
}
