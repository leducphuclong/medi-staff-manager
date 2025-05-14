### JavaFX Application Code

```java
package com.MediStaffManager.view;

import com.MediStaffManager.controller.NhanVienController;
import com.MediStaffManager.bean.NhanVien;
import javafx.application.Application;
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

        // TableView for displaying employee data
        tableView = new TableView<>();
        setupTableView();

        // Buttons for actions
        Button xemButton = new Button("Xem");
        xemButton.setOnAction(e -> xemNhanVienTheoPhongBan());

        Button xoaTatCaButton = new Button("Xóa tất cả");
        xoaTatCaButton.setOnAction(e -> xoaTatCaNhanVienTrongPhongBan());

        Button xoaNhanVienButton = new Button("Xóa nhân viên");
        xoaNhanVienButton.setOnAction(e -> xoaNhanVienTrongPhongBan());

        Button chuyenPhongBanButton = new Button("Chuyển phòng ban");
        chuyenPhongBanButton.setOnAction(e -> chuyenPhongBan());

        // Layout
        FlowPane inputPanel = new FlowPane();
        inputPanel.getChildren().addAll(phongBanComboBox, xemButton, xoaTatCaButton, xoaNhanVienButton, chuyenPhongBanButton);

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
        selectColumn.setCellValueFactory(param -> new SimpleBooleanProperty(false));
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));

        TableColumn<NhanVien, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getIdNhanVien()).asObject());

        TableColumn<NhanVien, String> cccdColumn = new TableColumn<>("CCCD");
        cccdColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getCccd()));

        TableColumn<NhanVien, String> tenColumn = new TableColumn<>("Tên");
        tenColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getHoTen()));

        // Add more columns as needed...

        tableView.getColumns().addAll(selectColumn, idColumn, cccdColumn, tenColumn);
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
    }

    private void xoaTatCaNhanVienTrongPhongBan() {
        String tenPhongBan = phongBanComboBox.getValue();
        if (tenPhongBan == null || tenPhongBan.isEmpty()) {
            showAlert("Vui lòng chọn phòng ban!");
            return;
        }

        // Confirm deletion and perform action...
    }

    private void xoaNhanVienTrongPhongBan() {
        // Logic to delete selected employee...
    }

    private void chuyenPhongBan() {
        // Logic to transfer selected employees to a new department...
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
```

### Explanation of the Code

1. **Main Class**: The `XemNhanVienFX` class extends `Application`, which is the entry point for JavaFX applications.

2. **UI Components**: We create a `ComboBox` for selecting departments, a `TableView` for displaying employee data, and buttons for various actions.

3. **Loading Data**: The `loadPhongBanComboBox` method populates the department ComboBox with data from the controller.

4. **TableView Setup**: The `setupTableView` method defines the columns for the `TableView`.

5. **Event Handling**: Each button has an action handler that calls the appropriate method to perform the desired action.

6. **Alerts**: The `showAlert` method is used to display informational messages to the user.

### Next Steps

- Implement the logic for deleting employees and transferring them to a new department.
- Customize the UI further to match the desired design.
- Test the application to ensure it works as expected.

This code provides a basic structure for the JavaFX application. You can expand upon it by adding more features and refining the user interface as needed.