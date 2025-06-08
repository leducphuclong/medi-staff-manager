package com.MediStaffManager.view.quanLyPhongBan;

import com.MediStaffManager.controller.NhanVienController; // Your controller
import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.io.File;

public class QuanLyPhongBanView2 extends Application {

    private WebView webView;
    private WebEngine webEngine;
    private QuanLyPhongBanBridge quanLyPhongBanBridge;
    private NhanVienController nhanVienController;

    // Adjust this path to where your nhanVienView.html will be located
    final String basePath = "./src/com/MediStaffManager/view/quanLyPhongBan/"; 
    final String htmlPage = "quanLyPhongBan.html"; // The HTML file for this view

    @Override
    public void start(Stage primaryStage) {
        // Initialize your controller
        nhanVienController = new NhanVienController();
        // Initialize the bridge with the controller
        quanLyPhongBanBridge = new QuanLyPhongBanBridge(nhanVienController);

        webView = new WebView();
        webEngine = webView.getEngine();

        // Enable JavaScript
        webEngine.setJavaScriptEnabled(true);

        // Expose the NhanVienBridge object to JavaScript when the page is loaded
        webEngine.getLoadWorker().stateProperty().addListener(
            (obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    JSObject window = (JSObject) webEngine.executeScript("window");
                    // JavaScript will be able to call methods on 'javaAppNhanVien'
                    window.setMember("javaAppNhanVien", quanLyPhongBanBridge); 
                    System.out.println("JavaScript bridge 'javaAppNhanVien' injected into: " + webEngine.getLocation());
                    // You can also call a JS function here to signal that Java is ready
                    // webEngine.executeScript("javaReady();"); 
                } else if (newState == Worker.State.FAILED) {
                    System.err.println("WebEngine failed to load page: " + webEngine.getLocation());
                    if (webEngine.getLoadWorker().getException() != null) {
                        webEngine.getLoadWorker().getException().printStackTrace();
                    }
                }
            });

        // Optional: Handle JavaScript alert() calls in Java console
        webEngine.setOnAlert(event -> System.out.println("JS Alert (NhanVienView): " + event.getData()));
        
        // Optional: Handle JavaScript console.log() in Java console
        JSObject console = (JSObject) webEngine.executeScript("console");
        console.setMember("log", new JSConsoleLogger());


        loadPage(htmlPage);

        StackPane root = new StackPane();
        root.getChildren().add(webView);

        Scene scene = new Scene(root, 1200, 800); // Adjust size as needed

        primaryStage.setTitle("Medi Staff Manager - Quản Lý Nhân Viên");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadPage(String pageName) {
        String filePath = basePath + pageName;
        File htmlFile = new File(filePath);

        if (htmlFile.exists() && htmlFile.isFile()) {
            String url = htmlFile.toURI().toString();
            webEngine.load(url);
            System.out.println("Loading page: " + url);
        } else {
            System.err.println("FATAL: Cannot find HTML file: " + filePath);
            System.err.println("Current working directory: " + new File(".").getAbsolutePath());
            // Load a simple error message if the HTML file is not found
            webEngine.loadContent(
                "<html><body><h1>Error</h1><p>Could not find page: " + pageName + " at " + filePath + "</p>" +
                "<p>Current dir: " + new File(".").getAbsolutePath() + "</p></body></html>"
            );
        }
    }
    
    // Inner class to handle console.log from JavaScript
    public static class JSConsoleLogger {
        public void log(String message) {
            System.out.println("JS Console: " + message);
        }
        public void error(String message) {
            System.err.println("JS Error: " + message);
        }
         public void warn(String message) {
            System.out.println("JS Warn: " + message);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}