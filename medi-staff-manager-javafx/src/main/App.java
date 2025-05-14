package com.MediStaffManager.view;

import com.MediStaffManager.controller.NhanVienController;
import com.MediStaffManager.bean.NhanVien;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class XemNhanVienFX extends Application {
    private ComboBox<String> phongBanComboBox;
    private TableView<NhanVien> tableView;
    private NhanVienController controller;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        controller = new NhanVienController();
        primaryStage.setTitle("Xem Nhân Viên Theo Phòng Ban");

        // Layout
        VBox root = new VBox(10);
        HBox inputPanel = new HBox(10);
        
        // ComboBox for departments
        phongBanComboBox = new ComboBox<>();
        loadPhongBanComboBox();

        // Buttons
        Button xemButton = new Button("Xem");
        Button xoaTatCaButton = new Button("Xóa tất cả");
        Button xoaNhanVienButton = new Button("Xóa nhân viên");
        Button chuyenPhongBanButton = new Button("Chuyển phòng ban");

        inputPanel.getChildren().addAll(new Label("Chọn phòng ban:"), phongBanComboBox, xemButton, xoaTatCaButton, xoaNhanVienButton, chuyenPhongBanButton);
        root.getChildren().addAll(inputPanel);

        // TableView for employees
        tableView = new TableView<>();
        setupTableView();
        root.getChildren().add(tableView);

        // Set actions for buttons
        xemButton.setOnAction(e -> xemNhanVienTheoPhongBan());
        xoaTatCaButton.setOnAction(e -> xoaTatCaNhanVienTrongPhongBan());
        xoaNhanVienButton.setOnAction(e -> xoaNhanVienTrongPhongBan());
        chuyenPhongBanButton.setOnAction(e -> chuyenPhongBan());

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadPhongBanComboBox() {
        phongBanComboBox.getItems().clear();
        List<Object[]> phongBanList = controller.layDanhSachPhongBan();
        for (Object[] phongBan : phongBanList) {
            String tenPhongBan = (String) phongBan[1];
            phongBanComboBox.getItems().add(tenPhongBan);
        }
    }

    private void setupTableView() {
        TableColumn<NhanVien, Boolean> selectColumn = new TableColumn<>("Chọn");
        selectColumn.setCellValueFactory(param -> new SimpleBooleanProperty(false));
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));

        TableColumn<NhanVien, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getIdNhanVien()).asObject());

        TableColumn<NhanVien, String> cccdColumn = new TableColumn<>("CCCD");
        cccdColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getCccd()));

        TableColumn<NhanVien, String> nameColumn = new TableColumn<>("Tên");
        nameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getHoTen()));

        // Add other columns similarly...

        tableView.getColumns().addAll(selectColumn, idColumn, cccdColumn, nameColumn);
    }

    private void xemNhanVienTheoPhongBan() {
        String tenPhongBan = phongBanComboBox.getValue();
        if (tenPhongBan == null || tenPhongBan.isEmpty()) {
            showAlert("Vui lòng chọn phòng ban!");
            return;
        }

        List<NhanVien> employees = controller.layNhanVienTheoPhongBan(tenPhongBan);
        tableView.getItems().clear();
        tableView.getItems().addAll(employees);

        if (employees.isEmpty()) {
            showAlert("Không tìm thấy nhân viên nào trong phòng ban này.");
        }
    }

    private void xoaTatCaNhanVienTrongPhongBan() {
        String tenPhongBan = phongBanComboBox.getValue();
        if (tenPhongBan == null || tenPhongBan.isEmpty()) {
            showAlert("Vui lòng chọn phòng ban!");
            return;
        }

        // Confirm and delete logic...
    }

    private void xoaNhanVienTrongPhongBan() {
        // Logic to delete selected employee...
    }

    private void chuyenPhongBan() {
        // Logic to change department...
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}