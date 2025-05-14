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
        VBox vbox = new VBox(10);
        vbox.setPadding(new javafx.geometry.Insets(10));

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

        HBox hbox = new HBox(10, phongBanComboBox, xemButton, xoaTatCaButton, xoaNhanVienButton, chuyenPhongBanButton);
        vbox.getChildren().add(hbox);

        // TableView for displaying employee data
        tableView = new TableView<>();
        setupTableView();
        vbox.getChildren().add(tableView);

        Scene scene = new Scene(vbox, 800, 600);
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

        TableColumn<NhanVien, String> sdtColumn = new TableColumn<>("Sdt");
        sdtColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getSdt()));

        TableColumn<NhanVien, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getEmail()));

        TableColumn<NhanVien, String> genderColumn = new TableColumn<>("Giới tính");
        genderColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getGioiTinh()));

        TableColumn<NhanVien, String> dobColumn = new TableColumn<>("Năm sinh");
        dobColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getNgaySinh()));

        TableColumn<NhanVien, String> positionColumn = new TableColumn<>("Chức vụ");
        positionColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getTenChucVu()));

        TableColumn<NhanVien, String> departmentColumn = new TableColumn<>("Phòng ban");
        departmentColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getTenPhongBan()));

        tableView.getColumns().addAll(selectColumn, idColumn, cccdColumn, nameColumn, sdtColumn, emailColumn, genderColumn, dobColumn, positionColumn, departmentColumn);
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

        // Confirm deletion
        if (showConfirmation("Bạn có chắc chắn muốn xóa tất cả nhân viên trong phòng ban này?")) {
            boolean success = controller.xoaTatCaNhanVienTrongPhongBan(tenPhongBan);
            if (success) {
                showAlert("Đã xóa tất cả nhân viên trong phòng ban!");
                tableView.getItems().clear();
            } else {
                showAlert("Xóa thất bại!");
            }
        }
    }

    private void xoaNhanVienTrongPhongBan() {
        NhanVien selectedEmployee = tableView.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) {
            showAlert("Vui lòng chọn một nhân viên để xóa!");
            return;
        }

        String tenPhongBan = phongBanComboBox.getValue();
        if (tenPhongBan == null || tenPhongBan.trim().isEmpty()) {
            showAlert("Vui lòng chọn phòng ban!");
            return;
        }

        // Confirm deletion
        if (showConfirmation("Bạn có chắc chắn muốn xóa nhân viên này khỏi phòng ban?")) {
            int idPhongBan = controller.layIdPhongBanTheoTen(tenPhongBan);
            boolean success = controller.xoaNhanVienTrongPhongBan(selectedEmployee.getIdNhanVien(), idPhongBan);
            if (success) {
                tableView.getItems().remove(selectedEmployee);
                showAlert("Xóa nhân viên thành công!");
            } else {
                showAlert("Có lỗi xảy ra khi xóa nhân viên.");
            }
        }
    }

    private void chuyenPhongBan() {
        // Implementation for transferring employees to a new department
        // Similar to the original Swing implementation
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(message);
        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }
}