package com.MediStaffManager.view;

import com.MediStaffManager.controller.NhanVienController;
import com.MediStaffManager.bean.NhanVien;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

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

        FlowPane inputPanel = new FlowPane(10, 10);
        inputPanel.setPadding(new Insets(10));
        inputPanel.getChildren().addAll(phongBanComboBox, xemButton, xoaTatCaButton, xoaNhanVienButton, chuyenPhongBanButton);

        // TableView to display employee data
        tableView = new TableView<>();
        TableColumn<NhanVien, Boolean> selectColumn = new TableColumn<>("Chọn");
        selectColumn.setCellValueFactory(param -> new SimpleBooleanProperty(false));
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));

        TableColumn<NhanVien, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getIdNhanVien()).asObject());

        TableColumn<NhanVien, String> cccdColumn = new TableColumn<>("CCCD");
        cccdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCccd()));

        TableColumn<NhanVien, String> nameColumn = new TableColumn<>("Tên");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getHoTen()));

        // Add other columns similarly...

        tableView.getColumns().addAll(selectColumn, idColumn, cccdColumn, nameColumn);
        loadEmployeeData();

        BorderPane root = new BorderPane();
        root.setTop(inputPanel);
        root.setCenter(tableView);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadPhongBanComboBox() {
        List<Object[]> phongBanList = controller.layDanhSachPhongBan();
        for (Object[] phongBan : phongBanList) {
            phongBanComboBox.getItems().add((String) phongBan[1]);
        }
    }

    private void loadEmployeeData() {
        String tenPhongBan = (String) phongBanComboBox.getValue();
        List<NhanVien> employees = controller.layNhanVienTheoPhongBan(tenPhongBan);
        ObservableList<NhanVien> employeeData = FXCollections.observableArrayList(employees);
        tableView.setItems(employeeData);
    }

    private void xemNhanVienTheoPhongBan() {
        loadEmployeeData();
    }

    private void xoaTatCaNhanVienTrongPhongBan() {
        // Implement deletion logic
    }

    private void xoaNhanVienTrongPhongBan() {
        // Implement deletion logic
    }

    private void chuyenPhongBan() {
        // Implement transfer logic
    }
}