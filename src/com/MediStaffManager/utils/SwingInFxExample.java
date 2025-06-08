package com.MediStaffManager.utils;

import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.*;

public class SwingInFxExample extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Call the function to display the Swing Panel
        displaySwingPanel(primaryStage);
    }

    // This is the function that replaces your WebView logic with SwingNode logic
    public void displaySwingPanel(Stage primaryStage) {
        // 1. Create the SwingNode
        final SwingNode swingNode = new SwingNode();

        // 2. Create and set the Swing content on the EDT
        SwingUtilities.invokeLater(() -> {
            JPanel swingPanel = createSwingPanel(); // Create our custom JPanel
            swingNode.setContent(swingPanel);
        });

        // 3. Add the SwingNode to a JavaFX layout pane
        StackPane root = new StackPane();
        root.getChildren().add(swingNode);

        // 4. Create and show the JavaFX scene
        Scene scene = new Scene(root, 800, 600); // Adjusted size for this example
        primaryStage.setTitle("Medi Staff Manager - Trang Quản Lý Nhân Sự (Swing)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    // Helper method from above
    private JPanel createSwingPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Add some Swing components
        JLabel titleLabel = new JLabel("Đây là Panel Quản Lý Nhân Sự (Swing)");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JButton addButton = new JButton("Thêm Nhân Viên Mới");
        addButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(panel, "Nút 'Thêm Nhân Viên' đã được nhấn!");
        });

        JTextArea infoArea = new JTextArea("Thông tin nhân viên sẽ được hiển thị ở đây...");
        infoArea.setEditable(false);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(infoArea), BorderLayout.CENTER);
        panel.add(addButton, BorderLayout.SOUTH);

        return panel;
    }

    public static void main(String[] args) {
        launch(args);
    }
}