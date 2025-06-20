package com.MediStaffManager.utils;

import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane; // Using BorderPane now
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MixedFxSwingExample { // Assuming this is part of a larger class

    public void taiTrangWithSwingPanel(Stage primaryStage, WebView webView) {
        // --- Part 1: Your original WebView setup ---
        final String basePath = "./src/com/MediStaffManager/view/trangChu/";
        final String fileName = "quanLyNhanSu.html";
        String filePath = basePath + fileName;
        File htmlFile = new File(filePath);

        if (htmlFile.exists() && htmlFile.isFile()) {
            String url = htmlFile.toURI().toString();
            webView.getEngine().load(url);
        } else {
            webView.getEngine().loadContent(
                    "<html><body><h1>Lỗi</h1><p>Không tìm thấy file HTML</p></body></html>");
        }

        // --- Part 2: Create and add the Swing Panel ---
        final SwingNode swingNode = new SwingNode();
        SwingUtilities.invokeLater(() -> {
            swingNode.setContent(createSwingPanel()); // Using the same helper method
        });

        // --- Part 3: Combine them in a layout ---
        BorderPane root = new BorderPane();
        root.setCenter(webView); // Put WebView in the center
        root.setRight(swingNode);  // Put Swing Panel on the right side

        // You could also use a SplitPane for a draggable divider
        // SplitPane root = new SplitPane();
        // root.getItems().addAll(webView, swingNode);
        // root.setDividerPositions(0.7); // 70% for webview, 30% for swing

        Scene scene = new Scene(root, 1900, 1000);
        primaryStage.setTitle("Medi Staff Manager - Trang Chủ (FX + Swing)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private JPanel createSwingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(300, 0)); // Give it a default width
        panel.setBorder(BorderFactory.createTitledBorder("Bảng Điều Khiển Swing"));
        
        JButton actionButton = new JButton("Thực hiện tác vụ Swing");
        actionButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "Tác vụ đã thực hiện!"));
        
        panel.add(actionButton, BorderLayout.NORTH);
        return panel;
    }
}