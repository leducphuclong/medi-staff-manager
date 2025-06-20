package com.MediStaffManager.utils;

import com.MediStaffManager.view.lichthang.TrangChinhLichThang;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class SwingWindowUtil {

    public static void showLichThangWindow(Window owner) {

        // BƯỚC 1: Yêu cầu luồng Swing (EDT) tạo ra JPanel.
        // SwingUtilities.invokeLater sẽ xếp hàng tác vụ này để chạy trên EDT.
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("THREAD CHECK: Đang chạy trên luồng Swing (EDT) để tạo Panel.");
                
                // Công việc nặng nhọc được thực hiện ở đây, trên đúng luồng của nó.
                final JPanel swingPanel = TrangChinhLichThang.createMainPanel();

                if (swingPanel == null) {
                    System.err.println("LỖI NGHIÊM TRỌNG: createMainPanel() trả về null!");
                    return;
                }
                
                System.out.println("THREAD CHECK: Đã tạo xong JPanel trên EDT.");

                // BƯỚC 2: Khi đã có JPanel, yêu cầu luồng JavaFX hiển thị nó.
                // Platform.runLater sẽ xếp hàng tác vụ này để chạy trên Luồng JavaFX.
                Platform.runLater(() -> {
                    System.out.println("THREAD CHECK: Đã quay lại luồng JavaFX để xây dựng Stage.");
                    
                    final Stage popupStage = new Stage();
                    popupStage.setTitle("Lịch Tháng");
                    popupStage.initModality(Modality.WINDOW_MODAL);
                    popupStage.initOwner(owner);

                    final SwingNode swingNode = new SwingNode();
                    swingNode.setContent(swingPanel); // Bây giờ việc này hoàn toàn an toàn.

                    StackPane rootPane = new StackPane();
                    rootPane.getChildren().add(swingNode);

                    Scene scene = new Scene(rootPane, TrangChinhLichThang.PARENT_WIDTH, TrangChinhLichThang.PARENT_HEIGHT);
                    popupStage.setScene(scene);

                    System.out.println("THREAD CHECK: Mọi thứ đã sẵn sàng. Chuẩn bị hiển thị cửa sổ...");
                    popupStage.showAndWait();
                });

            } catch (Exception e) {
                // Thêm khối try-catch để bắt bất kỳ lỗi nào xảy ra trong quá trình tạo Panel
                System.err.println("Đã xảy ra lỗi nghiêm trọng bên trong TrangChinhLichThang.createMainPanel():");
                e.printStackTrace();
            }
        });
    }
}