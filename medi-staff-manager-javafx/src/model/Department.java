package com.MediStaffManager.view;

import com.MediStaffManager.controller.NhanVienController;
import com.MediStaffManager.bean.NhanVien;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
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

        // ComboBox for department selection
        phongBanComboBox = new ComboBox<>();
        loadPhongBanComboBox();

        Button xemButton = new Button("Xem");
        xemButton.setOnAction(e -> xemNhanVienTheoPhongBan());

        Button xoaTatCaButton = new Button("Xóa tất cả");
        xoaTatCaButton.setOnAction(e -> xoaTatCaNhanVienTrongPhongBan());

        Button xoaNhanVienButton = new Button("Xóa nhân viên");
        xoaNhanVienButton.setOnAction(e -> xoaNhanVienTrongPhongBan());

        Button chuyenPhongBanButton = new Button("Chuyển phòng ban");
        chuyenPhongBanButton.setOnAction(e -> chuyenPhongBan());

        FlowPane inputPanel = new FlowPane();
        inputPanel.getChildren().addAll(phongBanComboBox, xemButton, xoaTatCaButton, xoaNhanVienButton, chuyenPhongBanButton);

        // TableView to display employee data
        tableView = new TableView<>();
        setupTableView();

        BorderPane root = new BorderPane();
        root.setTop(inputPanel);
        root.setCenter(tableView);

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
        selectColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(false));
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));

        TableColumn<NhanVien, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getIdNhanVien()).asObject());

        TableColumn<NhanVien, String> cccdColumn = new TableColumn<>("CCCD");
        cccdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCccd()));

        TableColumn<NhanVien, String> nameColumn = new TableColumn<>("Tên");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getHoTen()));

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

        // Confirm deletion and perform deletion logic...
    }

    private void xoaNhanVienTrongPhongBan() {
        NhanVien selectedEmployee = tableView.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) {
            showAlert("Vui lòng chọn một nhân viên để xóa!");
            return;
        }

        // Perform deletion logic...
    }

    private void chuyenPhongBan() {
        // Logic for transferring employees to a new department...
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}