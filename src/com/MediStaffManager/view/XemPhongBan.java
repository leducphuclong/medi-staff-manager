package com.MediStaffManager.view;

import com.MediStaffManager.controller.NhanVienController;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import java.util.List;

public class XemPhongBan extends Application {
    private TableView<Object[]> phongBanTable;
    private ObservableList<Object[]> phongBanData;
    private NhanVienController controller;

    @Override
    public void start(Stage primaryStage) {
        controller = new NhanVienController();
        primaryStage.setTitle("Quản lý Phòng Ban");

        // Navigation bar
        HBox navBar = new HBox(30);
        navBar.setPadding(new Insets(14, 30, 14, 30));
        navBar.setStyle("-fx-background-color: #001f4d;");
        navBar.setPrefWidth(Double.MAX_VALUE);
        navBar.setAlignment(Pos.CENTER_LEFT);

        // Tiêu đề bệnh viện
        Label hospitalLabel = new Label("Family Hospital");
        hospitalLabel.setStyle(
            "-fx-text-fill: #ffffff;" +
            "-fx-font-size: 20px;" +
            "-fx-font-family: 'Segoe UI';" +
            "-fx-font-weight: bold;"
        );

        // Các nút điều hướng (mỏng hơn, màu dịu hơn)
        Button nhanVienButton = new Button("Nhân viên");
        Button phongBanButton = new Button("Phòng ban");
        Button lichLamViecButton = new Button("Lịch làm việc");

        styleLightNavButton(nhanVienButton);
        styleLightNavButton(phongBanButton);
        styleLightNavButton(lichLamViecButton);

        // Sự kiện cho nút "Phòng ban"
        phongBanButton.setOnAction(e -> {
            try {
                new XemPhongBan().start(new Stage());
                primaryStage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        navBar.getChildren().addAll(hospitalLabel, nhanVienButton, phongBanButton, lichLamViecButton);


        // Title for TableView
        Label tableTitle = new Label("Quản lý phòng ban");
        tableTitle.setMaxWidth(Double.MAX_VALUE);
        tableTitle.setAlignment(Pos.CENTER_LEFT);
        tableTitle.setStyle(
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12px;" +
            "-fx-background-color: #f2f2f2;" +
            "-fx-border-color: #cccccc;" +
            "-fx-border-width: 1px 1px 1px 1px;" +
            "-fx-background-radius: 10px 10px 0 0;" +
            "-fx-border-radius: 10px 10px 0 0;"
        );

        

        // TableView setup
        phongBanTable = new TableView<>();
        phongBanData = FXCollections.observableArrayList();
        phongBanTable.setItems(phongBanData);

        phongBanTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Object[], Integer> idColumn = new TableColumn<>("ID phòng ban");
        idColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>((Integer) cellData.getValue()[0]));
        idColumn.setPrefWidth(150);
        idColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<Object[], String> nameColumn = new TableColumn<>("Tên phòng ban");
        nameColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>((String) cellData.getValue()[1]));
        nameColumn.setPrefWidth(300);
        nameColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<Object[], String> actionColumn = new TableColumn<>("Thao tác");
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button suaButton = new Button("Sửa");
            private final Button xoaButton = new Button("Xóa");
            private final HBox actionBox = new HBox(10, suaButton, xoaButton);

            {
                suaButton.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
                xoaButton.setStyle("-fx-background-color: #DC3545; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-border-radius: 5px; -fx-background-radius: 5px;");

                suaButton.setOnAction(e -> suaPhongBan(getTableRow().getItem()));
                xoaButton.setOnAction(e -> xoaPhongBan(getTableRow().getItem()));
                actionBox.setStyle("-fx-alignment: CENTER;");
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(actionBox);
                }
            }
        });

        // Sự kiện nhấn đúp chuột vào hàng trong TableView
        phongBanTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Object[] selectedPhongBan = phongBanTable.getSelectionModel().getSelectedItem();
                if (selectedPhongBan != null) {
                    int idPhongBan = (int) selectedPhongBan[0];
                    String tenPhongBan = (String) selectedPhongBan[1];

                    // Mở giao diện XemNhanVien với thông tin phòng ban
                    XemNhanVien xemNhanVien = new XemNhanVien(idPhongBan, tenPhongBan);
                    try {
                        xemNhanVien.start(new Stage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        actionColumn.setPrefWidth(200);
        actionColumn.setStyle("-fx-alignment: CENTER;");

        phongBanTable.getColumns().addAll(idColumn, nameColumn, actionColumn);

        // Add padding around TableView
        VBox tableContainer = new VBox(0, tableTitle, phongBanTable);
        tableContainer.setPadding(new Insets(15));
        tableContainer.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        // Add button "Thêm" at bottom-right
        Button themButton = new Button("Thêm");
        themButton.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        themButton.setOnAction(e -> themPhongBan());

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(tableContainer, themButton);
        StackPane.setMargin(themButton, new Insets(0, 15, 15, 0));
        StackPane.setAlignment(themButton, Pos.BOTTOM_RIGHT);

        // Layout
        BorderPane layout = new BorderPane();
        layout.setTop(navBar);
        layout.setCenter(stackPane);
        layout.setStyle("-fx-background-color: #F8F9FA;");

        Scene scene = new Scene(layout, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Load data
        xemDanhSachPhongBan();
    }

    private void styleLightNavButton(Button button) {
        button.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #dddddd;" + // màu sáng nhưng không trắng
            "-fx-font-size: 16px;" +
            "-fx-font-family: 'Segoe UI';" +
            "-fx-font-weight: normal;" +
            "-fx-cursor: hand;"
        );
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: white;" + // hover sáng hơn
            "-fx-font-size: 16px;" +
            "-fx-font-family: 'Segoe UI';" +
            "-fx-font-weight: normal;" +
            "-fx-cursor: hand;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #dddddd;" +
            "-fx-font-size: 16px;" +
            "-fx-font-family: 'Segoe UI';" +
            "-fx-font-weight: normal;" +
            "-fx-cursor: hand;"
        ));
    }

    
    private void styleNavButton(Button button) {
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #555555; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;"));
    }

    private void xemDanhSachPhongBan() {
        List<Object[]> phongBanList = controller.layDanhSachPhongBan();
        phongBanData.setAll(phongBanList);

        if (phongBanList.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Không có phòng ban nào!");
        }
    }

    private void themPhongBan() {
        TextField idPhongBanField = new TextField();
        TextField tenPhongBanField = new TextField();

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Thêm phòng ban mới");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        grid.add(new Label("ID phòng ban:"), 0, 0);
        grid.add(idPhongBanField, 1, 0);
        grid.add(new Label("Tên phòng ban:"), 0, 1);
        grid.add(tenPhongBanField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    int idPhongBan = Integer.parseInt(idPhongBanField.getText().trim());
                    String tenPhongBan = tenPhongBanField.getText().trim();

                    if (tenPhongBan.isEmpty()) {
                        showAlert(Alert.AlertType.WARNING, "Tên phòng ban không được để trống!");
                        return;
                    }

                    boolean success = controller.themPhongBan(idPhongBan, tenPhongBan);
                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION, "Thêm phòng ban thành công!");
                        xemDanhSachPhongBan();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Thêm phòng ban thất bại! ID có thể đã tồn tại.");
                    }
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "ID phòng ban phải là số nguyên!");
                }
            }
        });
    }

    private void suaPhongBan(Object[] phongBan) {
        TextField idPhongBanCuField = new TextField(String.valueOf(phongBan[0]));
        TextField idPhongBanMoiField = new TextField();
        TextField tenPhongBanMoiField = new TextField();

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Sửa phòng ban");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        grid.add(new Label("ID phòng ban cũ:"), 0, 0);
        grid.add(idPhongBanCuField, 1, 0);
        grid.add(new Label("ID phòng ban mới:"), 0, 1);
        grid.add(idPhongBanMoiField, 1, 1);
        grid.add(new Label("Tên phòng ban mới:"), 0, 2);
        grid.add(tenPhongBanMoiField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    int idPhongBanCu = Integer.parseInt(idPhongBanCuField.getText().trim());
                    int idPhongBanMoi = Integer.parseInt(idPhongBanMoiField.getText().trim());
                    String tenPhongBanMoi = tenPhongBanMoiField.getText().trim();

                    if (tenPhongBanMoi.isEmpty()) {
                        showAlert(Alert.AlertType.WARNING, "Tên phòng ban không được để trống!");
                        return;
                    }

                    boolean success = controller.suaPhongBan(idPhongBanCu, idPhongBanMoi, tenPhongBanMoi);
                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION, "Sửa phòng ban thành công!");
                        xemDanhSachPhongBan();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Sửa phòng ban thất bại! Kiểm tra lại thông tin.");
                    }
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "ID phòng ban phải là số nguyên!");
                }
            }
        });
    }

    private void xoaPhongBan(Object[] phongBan) {
        String tenPhongBan = (String) phongBan[1];

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc chắn muốn xóa phòng ban này?", ButtonType.YES, ButtonType.NO);
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                boolean success = controller.xoaPhongBan(tenPhongBan);
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Xóa phòng ban thành công!");
                    xemDanhSachPhongBan();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Không thể xóa phòng ban vì vẫn còn nhân viên!");
                }
            }
        });
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType, message, ButtonType.OK);
        alert.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}