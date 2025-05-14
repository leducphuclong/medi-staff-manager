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
        
        Button xemButton = new Button("Xem");
        Button xoaTatCaButton = new Button("Xóa tất cả");
        Button xoaNhanVienButton = new Button("Xóa nhân viên");
        Button chuyenPhongBanButton = new Button("Chuyển phòng ban");

        inputPanel.getChildren().addAll(new Label("Chọn phòng ban:"), phongBanComboBox, xemButton, xoaTatCaButton, xoaNhanVienButton, chuyenPhongBanButton);
        root.getChildren().addAll(inputPanel, createTableView());

        // Button actions
        xemButton.setOnAction(e -> xemNhanVienTheoPhongBan());
        xoaTatCaButton.setOnAction(e -> xoaTatCaNhanVienTrongPhongBan());
        xoaNhanVienButton.setOnAction(e -> xoaNhanVienTrongPhongBan());
        chuyenPhongBanButton.setOnAction(e -> chuyenPhongBan());

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private TableView<NhanVien> createTableView() {
        tableView = new TableView<>();
        TableColumn<NhanVien, Boolean> selectColumn = new TableColumn<>("Chọn");
        TableColumn<NhanVien, Integer> idColumn = new TableColumn<>("ID");
        TableColumn<NhanVien, String> cccdColumn = new TableColumn<>("CCCD");
        TableColumn<NhanVien, String> tenColumn = new TableColumn<>("Tên");
        TableColumn<NhanVien, String> sdtColumn = new TableColumn<>("Sdt");
        TableColumn<NhanVien, String> emailColumn = new TableColumn<>("Email");
        TableColumn<NhanVien, String> gioiTinhColumn = new TableColumn<>("Giới tính");
        TableColumn<NhanVien, String> ngaySinhColumn = new TableColumn<>("Năm sinh");
        TableColumn<NhanVien, String> chucVuColumn = new TableColumn<>("Chức vụ");
        TableColumn<NhanVien, String> phongBanColumn = new TableColumn<>("Phòng ban");

        // Set cell value factories
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getIdNhanVien()).asObject());
        cccdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCccd()));
        tenColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getHoTen()));
        sdtColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSdt()));
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        gioiTinhColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGioiTinh()));
        ngaySinhColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNgaySinh()));
        chucVuColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTenChucVu()));
        phongBanColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTenPhongBan()));

        // Add columns to table
        tableView.getColumns().addAll(selectColumn, idColumn, cccdColumn, tenColumn, sdtColumn, emailColumn, gioiTinhColumn, ngaySinhColumn, chucVuColumn, phongBanColumn);
        return tableView;
    }

    private void loadPhongBanComboBox() {
        phongBanComboBox.getItems().clear();
        List<Object[]> phongBanList = controller.layDanhSachPhongBan();
        for (Object[] phongBan : phongBanList) {
            String tenPhongBan = (String) phongBan[1];
            phongBanComboBox.getItems().add(tenPhongBan);
        }
    }

    private void loadEmployeeData() {
        String tenPhongBan = phongBanComboBox.getValue();
        List<NhanVien> employees = controller.layNhanVienTheoPhongBan(tenPhongBan);
        tableView.getItems().clear();
        tableView.getItems().addAll(employees);
        
        if (employees.isEmpty()) {
            showAlert("Không tìm thấy nhân viên nào trong phòng ban này.");
        }
    }

    private void xemNhanVienTheoPhongBan() {
        if (phongBanComboBox.getValue() == null || phongBanComboBox.getValue().isEmpty()) {
            showAlert("Vui lòng chọn phòng ban!");
            return;
        }
        loadEmployeeData();
    }

    private void xoaTatCaNhanVienTrongPhongBan() {
        // Implement the logic to delete all employees in the selected department
    }

    private void xoaNhanVienTrongPhongBan() {
        // Implement the logic to delete a selected employee
    }

    private void chuyenPhongBan() {
        // Implement the logic to transfer employees to a new department
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}