package com.MediStaffManager.view.quanLyPhongBan;

import javafx.util.Callback;
import javafx.scene.web.PromptData;
import com.MediStaffManager.controller.NhanVienController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Callback;
import netscape.javascript.JSObject;

public class QuanLyPhongBanView extends Application {

    private WebView webView;
    private WebEngine webEngine;
    private QuanLyPhongBanBridge quanLyPhongBanBridge;

    final String initialPage = "./src/com/MediStaffManager/view/quanLyPhongBan/html/trang_quan_ly_phong_ban.html";

    @SuppressWarnings("unused")
	@Override
    public void start(Stage primaryStage) {
        quanLyPhongBanBridge = new QuanLyPhongBanBridge(new NhanVienController(), this);

        webView = new WebView();
        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        
        webEngine.getLoadWorker().stateProperty().addListener(
            (obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    JSObject window = (JSObject) webEngine.executeScript("window");
                    window.setMember("quanLyPhongBanBridge", quanLyPhongBanBridge);
                    System.out.println("quanLyPhongBanBridge: 'quanLyPhongBanBridge' injected into: " + webEngine.getLocation());

                    String scriptToExecute = "if(typeof initializePageAfterJavaBridgeInjection === 'function') {" +
                                             "    initializePageAfterJavaBridgeInjection();" +
                                             "}";
                    webEngine.executeScript(scriptToExecute);

                }
            });

        quanLyPhongBanBridge.loadPage(initialPage);

        StackPane root = new StackPane();
        root.getChildren().add(webView);

        Scene scene = new Scene(root, 1150, 700);
        primaryStage.setTitle("Medi Staff Manager");
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