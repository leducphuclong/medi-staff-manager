package com.MediStaffManager.view.quanLyTaiKhoan;
import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class QuanLyTaiKhoanView extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create the WebView component
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        webEngine.setJavaScriptEnabled(true);

        QuanLyTaiKhoanBridge quanLyTaiKhoanBridge = new QuanLyTaiKhoanBridge(webEngine);

        webEngine.getLoadWorker().stateProperty().addListener(
            (obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    JSObject window = (JSObject) webEngine.executeScript("window");
                    window.setMember("javaBridge", quanLyTaiKhoanBridge);

                    // You can also add a console listener to see JS console.log in your IDE's console
                    webEngine.executeScript("console.log = function(message) { javaBridge.log(message); };");
                }
            }
        );

        // Use the method from the bridge to load the HTML file and set up the stage.
        // This keeps the QuanLyTaiKhoanView class clean and delegates view setup to the specific view's bridge.
        quanLyTaiKhoanBridge.taiTrang(primaryStage, webView);
    }

    public static void main(String[] args) {
        launch(args);
    }
}