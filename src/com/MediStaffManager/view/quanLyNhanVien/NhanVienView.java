package com.MediStaffManager.view.quanLyNhanVien;

import com.MediStaffManager.controller.NhanVienController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class NhanVienView extends Application {

    private WebView webView;
    private WebEngine webEngine;
    private NhanVienBridge2 nhanVienBridge;

    // Bỏ biến initialPage ở đây, việc load sẽ do NhanVienBridge xử lý
    // final String initialPage = "./src/com/MediStaffManager/view/quanLyNhanVien/quanLyNhanVien.html";

    @Override
    public void start(Stage primaryStage) {
        NhanVienController nhanVienController = new NhanVienController();
        // Truyền 'this' (NhanVienView instance) cho NhanVienBridge
        nhanVienBridge = new NhanVienBridge2(nhanVienController, this);

        webView = new WebView();
        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);

        webEngine.getLoadWorker().stateProperty().addListener(
            (obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    JSObject window = (JSObject) webEngine.executeScript("window");
                    if (window != null) {
                        window.setMember("nhanVienBridge", nhanVienBridge);
                        System.out.println("NhanVienView: 'nhanVienBridge' injected into: " + webEngine.getLocation());

                        // Thiết lập console.log bridge
                        window.setMember("javaConsole", nhanVienBridge);
                        webEngine.executeScript("console.log = function(message) { try { javaConsole.log(String(message)); } catch(e) { System.err.println('JS console.log error: ' + e); } };");
                        webEngine.executeScript("console.error = function(message) { try { javaConsole.log('ERROR: ' + String(message)); } catch(e) { System.err.println('JS console.error error: ' + e); } };");
                        webEngine.executeScript("console.warn = function(message) { try { javaConsole.log('WARN: ' + String(message)); } catch(e) { System.err.println('JS console.warn error: ' + e); } };");
                        webEngine.executeScript("console.info = function(message) { try { javaConsole.log('INFO: ' + String(message)); } catch(e) { System.err.println('JS console.info error: ' + e); } };");


                        String scriptToExecute = "if(typeof initializeNhanVienPageAfterJavaBridgeInjection === 'function') {" +
                                                 "    initializeNhanVienPageAfterJavaBridgeInjection();" +
                                                 "} else { console.error('HTML: initializeNhanVienPageAfterJavaBridgeInjection function not found.'); }";
                        webEngine.executeScript(scriptToExecute);
                    } else {
                         System.err.println("NhanVienView: JSObject 'window' is null. Bridge not injected.");
                    }
                } else if (newState == Worker.State.FAILED) {
                    System.err.println("NhanVienView: WebEngine failed to load page.");
                    final Throwable exception = webEngine.getLoadWorker().getException();
                    if (exception != null) {
                        System.err.println("NhanVienView: Exception during page load: ");
                        exception.printStackTrace();
                    }
                    // Hiển thị lỗi trên WebView nếu cần thiết
                    // webEngine.loadContent("<html><body><h1>Error loading page. Check Java console for details.</h1><p>" + (exception != null ? exception.getMessage() : "Unknown error") + "</p></body></html>");
                }
            });

        webEngine.setOnAlert(event -> System.out.println("JS Alert: " + event.getData()));
        webEngine.setConfirmHandler(message -> {
            System.out.println("JS Confirm: " + message);
            // Trả về true/false dựa trên lựa chọn người dùng nếu bạn muốn hiển thị dialog JavaFX
            return true;
        });

        // Gọi phương thức load trang từ NhanVienBridge
        nhanVienBridge.loadInitialPage();

        StackPane root = new StackPane();
        root.getChildren().add(webView);

        Scene scene = new Scene(root, 1200, 750);
        primaryStage.setTitle("Medi Staff Manager - Quản Lý Nhân Viên");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.show();
    }

    public WebEngine getWebEngine() {
        return this.webEngine;
    }

    public static void main(String[] args) {
        launch(args);
    }
}