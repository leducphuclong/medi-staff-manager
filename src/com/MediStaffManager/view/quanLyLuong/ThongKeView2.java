package com.MediStaffManager.view.quanLyLuong;

import com.MediStaffManager.controller.LuongNhanVienController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class ThongKeView2 extends Application {

    private WebEngine engine;

    @Override
    public void start(Stage primaryStage) {
        LuongNhanVienController ctrl = new LuongNhanVienController();
        ThongKeLuongBridge bridge = new ThongKeLuongBridge(ctrl, this);

        WebView view = new WebView();
        engine = view.getEngine();
        engine.setJavaScriptEnabled(true);

        // Khi trang load xong thì inject bridge và gọi init
        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject win = (JSObject) engine.executeScript("window");
                win.setMember("thongKeBridge", bridge);
                System.out.println("✅ JavaScript bridge 'thongKeBridge' đã inject.");
            }
        });

        // Đọc HTML và CSS, rồi nhúng CSS vào <style> để đảm bảo áp dụng
        try {
            String htmlPath = "src/com/MediStaffManager/view/quanLyLuong/html/ThongKe.html";
            String cssPath  = "src/com/MediStaffManager/view/quanLyLuong/css/ThongKe.css";
            String jsPath   = "src/com/MediStaffManager/view/quanLyLuong/js/ThongKe.js";

            String html = Files.readString(Paths.get(htmlPath), StandardCharsets.UTF_8);
            String css  = Files.readString(Paths.get(cssPath), StandardCharsets.UTF_8);
            String js   = Files.readString(Paths.get(jsPath), StandardCharsets.UTF_8);

            // Nhúng CSS và JS vào thẳng HTML
            html = html.replaceFirst("(?i)</head>", "<style>\n" + css + "\n</style>\n</head>");
            html = html.replace("</body>", "<script>\n" + js + "\n</script>\n</body>");


            engine.loadContent(html);
            System.out.println("✅ Load đầy đủ HTML + CSS + JS thành công.");

        } catch (Exception e) {
            e.printStackTrace();
            engine.loadContent("<html><body><h2>Lỗi khi tải giao diện thống kê</h2><p>" + e.getMessage() + "</p></body></html>");
        }


        primaryStage.setTitle("Thống Kê Lương");
        primaryStage.setScene(new Scene(new StackPane(view), 1200, 750));
        primaryStage.show();
    }

    public WebEngine getWebEngine() {
        return engine;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
