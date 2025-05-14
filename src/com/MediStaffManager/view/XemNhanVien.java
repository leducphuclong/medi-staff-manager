package com.MediStaffManager.view;

import com.MediStaffManager.controller.NhanVienController;
import com.MediStaffManager.bean.NhanVien;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import java.util.ArrayList;
import java.util.List;

public class XemNhanVien extends Application {
    private TableView<NhanVien> table;
    private ObservableList<NhanVien> employeeData;
    private NhanVienController controller;
    private int idPhongBan;
    private String tenPhongBan;
    private CheckBox selectAllCheckBox; 

    public XemNhanVien(int idPhongBan, String tenPhongBan) {
        this.idPhongBan = idPhongBan;
        this.tenPhongBan = tenPhongBan;
    }


    @Override
    public void start(Stage primaryStage) {
        controller = new NhanVienController();
        primaryStage.setTitle(tenPhongBan);

        // Nút "Trở lại"
        Button troLaiButton = new Button("Trở lại");
        troLaiButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        troLaiButton.setOnAction(e -> {
            try {
                new XemPhongBan().start(new Stage()); // Mở giao diện XemPhongBan
                primaryStage.close(); // Đóng giao diện hiện tại
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Tiêu đề cho TableView
        Label tableTitle = new Label(tenPhongBan);
        tableTitle.setMaxWidth(Double.MAX_VALUE);
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
        table = new TableView<>();
        employeeData = FXCollections.observableArrayList();
        table.setItems(employeeData);

        // Checkbox tổng
        selectAllCheckBox = new CheckBox("Chọn tất cả");
        selectAllCheckBox.setOnAction(e -> {
            boolean isSelected = selectAllCheckBox.isSelected();
            for (NhanVien employee : employeeData) {
                employee.setSelected(isSelected);
            }
            table.refresh();
        });

        // Cột checkbox
        TableColumn<NhanVien, Boolean> selectColumn = new TableColumn<>();
        selectColumn.setGraphic(selectAllCheckBox);
        selectColumn.setPrefWidth(100);
        table.setEditable(true);
        selectColumn.setEditable(true);
        selectColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(index -> table.getItems().get(index).selectedProperty()));


        // Cột "ID"
        TableColumn<NhanVien, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setPrefWidth(100);
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idNhanVienProperty().asObject());

        // Cột "Tên"
        TableColumn<NhanVien, String> nameColumn = new TableColumn<>("Tên");
        nameColumn.setPrefWidth(240);
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().hoTenProperty());

        // Cột "Sdt"
        TableColumn<NhanVien, String> phoneColumn = new TableColumn<>("Sdt");
        phoneColumn.setPrefWidth(100);
        phoneColumn.setCellValueFactory(cellData -> cellData.getValue().sdtProperty());

        // Cột "Email"
        TableColumn<NhanVien, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setPrefWidth(270);
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());

        // Cột "Chức vụ"
        TableColumn<NhanVien, String> positionColumn = new TableColumn<>("Chức vụ");
        positionColumn.setPrefWidth(166);
        positionColumn.setCellValueFactory(cellData -> cellData.getValue().tenChucVuProperty());

        table.getColumns().addAll(selectColumn, idColumn, nameColumn, phoneColumn, emailColumn, positionColumn);

        // Load employee data for the selected department
        loadNhanVienTheoPhongBan();

        // Nút "Xóa"
        Button xoaButton = new Button("Xóa");
        xoaButton.setStyle("-fx-background-color: #DC3545; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        xoaButton.setOnAction(e -> xoaNhanVien());

        // Nút "Chuyển phòng ban"
        Button chuyenPhongBanButton = new Button("Chuyển phòng ban");
        chuyenPhongBanButton.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        chuyenPhongBanButton.setOnAction(e -> chuyenPhongBan());

        // Layout cho nút "Xóa" và "Chuyển phòng ban"
        HBox buttonBox = new HBox(10, xoaButton, chuyenPhongBanButton);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
        buttonBox.setPadding(new Insets(10));

        // Layout chính
        VBox tableWithTitle = new VBox(0, tableTitle, table);

        VBox vbox = new VBox(10, troLaiButton, tableWithTitle, buttonBox);
        vbox.setPadding(new Insets(10));


        BorderPane root = new BorderPane();
        root.setCenter(vbox);

        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
        
    private void loadNhanVienTheoPhongBan() {
    List<NhanVien> employees = controller.layNhanVienTheoPhongBan(tenPhongBan);
    employeeData.setAll(employees);

    // Listener cho từng nhân viên
    for (NhanVien employee : employeeData) {
        employee.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                // Nếu một checkbox lẻ bị bỏ tick, bỏ tick checkbox tổng
                selectAllCheckBox.setSelected(false);
            } else {
                // Nếu tất cả checkbox lẻ được tick, tick checkbox tổng
                boolean allSelected = employeeData.stream().allMatch(NhanVien::isSelected);
                selectAllCheckBox.setSelected(allSelected);
            }
        });
    }

    if (employees.isEmpty()) {
        showAlert(Alert.AlertType.INFORMATION, "Không tìm thấy nhân viên nào trong phòng ban này.");
    }
}

    private void xoaNhanVien() {
        List<NhanVien> selectedEmployees = new ArrayList<>();
        for (NhanVien employee : employeeData) {
            if (employee.isSelected()) {
                selectedEmployees.add(employee);
            }
        }

        if (selectedEmployees.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Vui lòng chọn ít nhất một nhân viên để xóa!");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc chắn muốn xóa nhân viên đã chọn?", ButtonType.YES, ButtonType.NO);
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                for (NhanVien employee : selectedEmployees) {
                    boolean success = controller.xoaNhanVienTrongPhongBan(employee.getIdNhanVien(), idPhongBan);
                    if (success) {
                        employeeData.remove(employee);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Có lỗi xảy ra khi xóa nhân viên: " + employee.getHoTen());
                    }
                }
                showAlert(Alert.AlertType.INFORMATION, "Xóa nhân viên thành công!");
            }
        });
    }

    private void chuyenPhongBan() {
        List<NhanVien> selectedEmployees = new ArrayList<>();
        for (NhanVien employee : employeeData) {
            if (employee.isSelected()) {
                selectedEmployees.add(employee);
            }
        }

        if (selectedEmployees.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Vui lòng chọn ít nhất một nhân viên để chuyển phòng ban!");
            return;
        }

        ComboBox<String> phongBanMoiComboBox = new ComboBox<>();
        List<Object[]> phongBanList = controller.layDanhSachPhongBan();
        for (Object[] phongBan : phongBanList) {
            phongBanMoiComboBox.getItems().add((String) phongBan[1]);
        }

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Chọn phòng ban mới");
        dialog.getDialogPane().setContent(phongBanMoiComboBox);
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String tenPhongBanMoi = phongBanMoiComboBox.getValue();
                if (tenPhongBanMoi == null || tenPhongBanMoi.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Tên phòng ban không được để trống!");
                    return;
                }

                List<Integer> danhSachIdNhanVien = new ArrayList<>();
                for (NhanVien employee : selectedEmployees) {
                    danhSachIdNhanVien.add(employee.getIdNhanVien());
                }

                boolean success = controller.chuyenPhongBan(danhSachIdNhanVien, controller.layIdPhongBanTheoTen(tenPhongBanMoi));
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Chuyển phòng ban thành công!");
                    loadNhanVienTheoPhongBan();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Có lỗi xảy ra khi chuyển phòng ban.");
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