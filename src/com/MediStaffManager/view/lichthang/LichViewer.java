package com.MediStaffManager.view.lichthang;

import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.swing.*;

public class LichViewer { // Giả sử tên class là LichViewer

    // Phương thức này bây giờ sẽ tạo một cửa sổ hoàn toàn mới
    public void showLich() { // Không cần truyền primaryStage vào nữa
        System.out.println("chuan bi show Lich trong cửa sổ mới");

        // 1. Tạo một Stage (cửa sổ) mới, thay vì dùng primaryStage
        Stage newStage = new Stage();

        final SwingNode swingNode = new SwingNode();

        SwingUtilities.invokeLater(() -> {
            // Giả sử TrangChinhLichThang.createMainPanel() tồn tại và trả về một JPanel
            JPanel swingPanel = TrangChinhLichThang.createMainPanel();
            swingNode.setContent(swingPanel);
        });

        StackPane root = new StackPane();
        root.getChildren().add(swingNode);

        Scene scene = new Scene(root, TrangChinhLichThang.PARENT_WIDTH, TrangChinhLichThang.PARENT_HEIGHT + 100);

        // 2. Tất cả các thiết lập bây giờ áp dụng cho newStage
        newStage.setTitle("Medi Staff Manager - Trang Quản Lý Lịch Làm Việc");
        newStage.setScene(scene);

        // Căn giữa cửa sổ trên màn hình
        newStage.setWidth(TrangChinhLichThang.PARENT_WIDTH);
        newStage.setHeight(TrangChinhLichThang.PARENT_HEIGHT);
        // Lấy kích thước màn hình và căn giữa
        double screenWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        newStage.setX((screenWidth - newStage.getWidth()) / 2);
        newStage.setY(20); // Cách lề trên 20px

        // 3. Hiển thị cửa sổ mới
        newStage.show();
    }
}