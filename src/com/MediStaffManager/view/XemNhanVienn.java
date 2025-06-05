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
// import javafx.scene.layout.StackPane; // StackPane was not used
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class XemNhanVienn extends Application {
    private TableView<NhanVien> table;
    private ObservableList<NhanVien> employeeData;
    private NhanVienController controller;
    private int idPhongBan;
    private String tenPhongBan;
    private CheckBox selectAllCheckBox;

    // Style Constants
    private static final String PRIMARY_COLOR = "#007AFF"; // Professional Blue
    private static final String DANGER_COLOR = "#FF3B30";  // iOS Red for destructive actions
    private static final String SECONDARY_BUTTON_COLOR = "#6C757D"; // Grey for secondary actions
    private static final String BACKGROUND_COLOR = "#F4F4F8"; // Light grey background
    private static final String SURFACE_COLOR = "#FFFFFF";    // White for cards/surfaces
    private static final String BORDER_COLOR_LIGHT = "#E0E0E0"; // Light border for elements
    private static final String PRIMARY_TEXT_COLOR = "#333333"; // Dark grey for text
    private static final String SECONDARY_TEXT_COLOR = "#555555";

    private static final String COMMON_BUTTON_STYLE_BASE =
            "-fx-font-size: 14px; " +
            "-fx-padding: 10px 22px; " + // Adjusted padding
            "-fx-border-radius: 8px; " +
            "-fx-background-radius: 8px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: white; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 6, 0.1, 0, 2);"; // Subtle shadow

    private static final String CARD_STYLE =
            "-fx-background-color: " + SURFACE_COLOR + "; " +
            "-fx-background-radius: 10px; " +
            "-fx-border-color: " + BORDER_COLOR_LIGHT + "; " +
            "-fx-border-width: 1px; " +
            "-fx-border-radius: 10px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 12, 0.0, 0, 3);"; // Softer shadow

    public XemNhanVienn(int idPhongBan, String tenPhongBan) {
        this.idPhongBan = idPhongBan;
        this.tenPhongBan = tenPhongBan;
    }

    public XemNhanVienn() {
        // Default constructor for cases where it might be needed by JavaFX launch mechanism
        // Or if you plan to launch it without specific department info initially.
        // For this example, assuming it's always launched with department info.
        // If this constructor is used, ensure idPhongBan and tenPhongBan are handled.
    }


    @Override
    public void start(Stage primaryStage) {
        controller = new NhanVienController();
        primaryStage.setTitle("Quản lý nhân viên - " + tenPhongBan);

        // Nút "Trở lại"
        Button troLaiButton = new Button("Trở lại");
        troLaiButton.setStyle(COMMON_BUTTON_STYLE_BASE + "-fx-background-color: " + SECONDARY_BUTTON_COLOR + ";");
        troLaiButton.setOnAction(e -> {
            try {
                new XemPhongBann().start(new Stage());
                primaryStage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Không thể mở lại trang Phòng Ban.");
            }
        });
        HBox backButtonContainer = new HBox(troLaiButton);
        backButtonContainer.setPadding(new Insets(0, 0, 15, 0)); // Add some bottom margin

        // Tiêu đề cho TableView (Card Header)
        Label tableTitle = new Label(tenPhongBan);
        tableTitle.setMaxWidth(Double.MAX_VALUE);
        tableTitle.setAlignment(Pos.CENTER);
        tableTitle.setStyle(
                "-fx-font-size: 24px; " +
                "-fx-font-family: 'System', sans-serif; " + // Use system font
                "-fx-font-weight: bold; " +
                "-fx-text-fill: " + PRIMARY_TEXT_COLOR + "; " +
                "-fx-padding: 20px 25px; " + // Generous padding
                "-fx-border-color: transparent transparent " + BORDER_COLOR_LIGHT + " transparent; " + // Bottom border
                "-fx-border-width: 1px;"
        );

        // TableView setup
        table = new TableView<>();
        employeeData = FXCollections.observableArrayList();
        table.setItems(employeeData);
        table.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-selection-bar: " + PRIMARY_COLOR + ";" +
            "-fx-selection-bar-text: white;" +
            "-fx-background-color: transparent;" + // Table itself is transparent
            "-fx-table-cell-border-color: " + BORDER_COLOR_LIGHT + ";" + // Horizontal cell lines
            "-fx-padding: 0 1px 1px 1px;" // Padding to ensure borders are visible if card has padding
        );

        Label placeholderLabel = new Label("Không có nhân viên nào trong phòng ban này.");
        placeholderLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: " + SECONDARY_TEXT_COLOR + "; -fx-padding: 20px;");
        table.setPlaceholder(placeholderLabel);

        // Checkbox tổng
        selectAllCheckBox = new CheckBox("Chọn tất cả");
        selectAllCheckBox.setStyle("-fx-font-size: 13px; -fx-text-fill: " + PRIMARY_TEXT_COLOR +"; -fx-padding: 8px;");
        selectAllCheckBox.setOnAction(e -> {
            boolean isSelected = selectAllCheckBox.isSelected();
            for (NhanVien employee : employeeData) {
                employee.setSelected(isSelected);
            }
            table.refresh(); // Refresh table to show checkbox changes
        });

        // Cột checkbox
        TableColumn<NhanVien, Boolean> selectColumn = new TableColumn<>();
        selectColumn.setGraphic(selectAllCheckBox);
        selectColumn.setPrefWidth(120); // Slightly wider for "Chọn tất cả"
        selectColumn.setSortable(false);
        table.setEditable(true);
        selectColumn.setEditable(true);
        selectColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(index -> {
            if (index >= 0 && index < table.getItems().size()) {
                 return table.getItems().get(index).selectedProperty();
            }
            return null; // Should not happen with valid index
        }));
        selectColumn.setStyle("-fx-alignment: CENTER;");


        // Cột "ID"
        TableColumn<NhanVien, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setPrefWidth(80);
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idNhanVienProperty().asObject());
        idColumn.setStyle("-fx-alignment: CENTER_LEFT; -fx-padding: 8px;");

        // Cột "Tên"
        TableColumn<NhanVien, String> nameColumn = new TableColumn<>("Tên");
        nameColumn.setPrefWidth(220);
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().hoTenProperty());
        nameColumn.setStyle("-fx-padding: 8px;");


        // Cột "Sdt"
        TableColumn<NhanVien, String> phoneColumn = new TableColumn<>("Sdt");
        phoneColumn.setPrefWidth(120);
        phoneColumn.setCellValueFactory(cellData -> cellData.getValue().sdtProperty());
        phoneColumn.setStyle("-fx-alignment: CENTER_LEFT; -fx-padding: 8px;");

        // Cột "Email"
        TableColumn<NhanVien, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setPrefWidth(250);
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        emailColumn.setStyle("-fx-padding: 8px;");

        // Cột "Chức vụ"
        TableColumn<NhanVien, String> positionColumn = new TableColumn<>("Chức vụ");
        positionColumn.setPrefWidth(166); // Keep remaining width
        positionColumn.setCellValueFactory(cellData -> cellData.getValue().tenChucVuProperty());
        positionColumn.setStyle("-fx-padding: 8px;");

        table.getColumns().addAll(selectColumn, idColumn, nameColumn, phoneColumn, emailColumn, positionColumn);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);


        // Load employee data
        loadNhanVienTheoPhongBan();

        // Nút "Xóa"
        Button xoaButton = new Button("Xóa Nhân Viên");
        xoaButton.setStyle(COMMON_BUTTON_STYLE_BASE + "-fx-background-color: " + DANGER_COLOR + ";");
        xoaButton.setOnAction(e -> xoaNhanVien());

        // Nút "Chuyển phòng ban"
        Button chuyenPhongBanButton = new Button("Chuyển Phòng Ban");
        chuyenPhongBanButton.setStyle(COMMON_BUTTON_STYLE_BASE + "-fx-background-color: " + PRIMARY_COLOR + ";");
        chuyenPhongBanButton.setOnAction(e -> chuyenPhongBan());

        // Layout cho nút "Xóa" và "Chuyển phòng ban"
        HBox buttonBox = new HBox(15, xoaButton, chuyenPhongBanButton); // Increased spacing
        buttonBox.setAlignment(Pos.CENTER_RIGHT); // Align buttons to the right
        buttonBox.setPadding(new Insets(20, 0, 0, 0)); // Top padding to separate from table

        // Card Layout for Table and its Title
        VBox cardPane = new VBox(0, tableTitle, table); // 0 spacing, title handles its padding
        cardPane.setStyle(CARD_STYLE);

        // Layout chính
        VBox mainLayout = new VBox(20, backButtonContainer, cardPane, buttonBox); // Consistent spacing
        mainLayout.setPadding(new Insets(25)); // Overall padding for the scene content

        BorderPane root = new BorderPane();
        root.setCenter(mainLayout);
        root.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        Scene scene = new Scene(root, 1050, 700); // Slightly larger scene
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadNhanVienTheoPhongBan() {
        if (controller == null) controller = new NhanVienController(); // Ensure controller is initialized
        List<NhanVien> employees = controller.layNhanVienTheoPhongBan(tenPhongBan);
        employeeData.setAll(employees);
    
        // Reset selectAllCheckBox state based on new data
        if (employees.isEmpty()) {
            selectAllCheckBox.setSelected(false);
            selectAllCheckBox.setDisable(true); // Disable if no employees
             // The placeholder in TableView will be shown.
             // An initial alert might be good if this is the first load and it's empty.
            // showAlert(Alert.AlertType.INFORMATION, "Không tìm thấy nhân viên nào trong phòng ban này.");
        } else {
            selectAllCheckBox.setDisable(false);
            // Check if all currently loaded employees are selected (e.g., after an operation)
            boolean allCurrentlySelected = employeeData.stream().allMatch(NhanVien::isSelected);
            selectAllCheckBox.setSelected(allCurrentlySelected);
        }
    
        // Add listeners to individual employee selection state
        for (NhanVien employee : employeeData) {
            employee.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    selectAllCheckBox.setSelected(false);
                } else {
                    boolean allSelected = employeeData.stream().allMatch(NhanVien::isSelected);
                    selectAllCheckBox.setSelected(allSelected);
                }
            });
        }
        table.refresh(); // Refresh table after loading data and setting listeners
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

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa");
        confirmAlert.setHeaderText("Bạn có chắc chắn muốn xóa " + selectedEmployees.size() + " nhân viên đã chọn?");
        confirmAlert.setContentText("Hành động này không thể hoàn tác.");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) { // Changed from ButtonType.YES for standard Alert
                int successfulDeletes = 0;
                List<String> failedDeletes = new ArrayList<>();

                for (NhanVien employee : selectedEmployees) {
                    boolean success = controller.xoaNhanVienTrongPhongBan(employee.getIdNhanVien(), idPhongBan);
                    if (success) {
                        successfulDeletes++;
                    } else {
                        failedDeletes.add(employee.getHoTen());
                    }
                }
                
                // Reload data from database to reflect changes accurately
                loadNhanVienTheoPhongBan(); 

                if (successfulDeletes > 0 && failedDeletes.isEmpty()) {
                    showAlert(Alert.AlertType.INFORMATION, "Đã xóa thành công " + successfulDeletes + " nhân viên.");
                } else if (!failedDeletes.isEmpty()) {
                     showAlert(Alert.AlertType.ERROR, "Lỗi khi xóa nhân viên: " + String.join(", ", failedDeletes) +
                                (successfulDeletes > 0 ? ". Một số nhân viên khác có thể đã được xóa." : ""));
                }
                // No explicit removal from employeeData here, as loadNhanVienTheoPhongBan() handles it
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
        phongBanMoiComboBox.setPromptText("Chọn phòng ban đích");
        List<Object[]> phongBanList = controller.layDanhSachPhongBan();
        for (Object[] phongBan : phongBanList) {
            // Ensure not to add the current department to the list of choices
            if (!tenPhongBan.equals((String) phongBan[1])) {
                phongBanMoiComboBox.getItems().add((String) phongBan[1]);
            }
        }
        
        if (phongBanMoiComboBox.getItems().isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Không có phòng ban nào khác để chuyển đến.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Chuyển Phòng Ban");
        dialog.setHeaderText("Chọn phòng ban mới cho " + selectedEmployees.size() + " nhân viên đã chọn:");
        dialog.getDialogPane().setContent(phongBanMoiComboBox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Styling for ComboBox in Dialog (basic)
        phongBanMoiComboBox.setPrefWidth(300);
        VBox content = new VBox(10, new Label("Chuyển đến phòng ban:"), phongBanMoiComboBox);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);


        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String tenPhongBanMoi = phongBanMoiComboBox.getValue();
                if (tenPhongBanMoi == null || tenPhongBanMoi.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Vui lòng chọn một phòng ban đích!");
                    return;
                }
                if (tenPhongBanMoi.equals(this.tenPhongBan)) {
                    showAlert(Alert.AlertType.INFORMATION, "Nhân viên đã ở trong phòng ban này.");
                    return;
                }

                List<Integer> danhSachIdNhanVien = new ArrayList<>();
                for (NhanVien employee : selectedEmployees) {
                    danhSachIdNhanVien.add(employee.getIdNhanVien());
                }

                boolean success = controller.chuyenPhongBan(danhSachIdNhanVien, controller.layIdPhongBanTheoTen(tenPhongBanMoi));
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Chuyển phòng ban thành công!");
                    loadNhanVienTheoPhongBan(); // Refresh data
                } else {
                    showAlert(Alert.AlertType.ERROR, "Có lỗi xảy ra khi chuyển phòng ban.");
                }
            }
        });
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType.toString());
        alert.setHeaderText(null); // No header text for simple messages
        alert.setContentText(message);
        alert.showAndWait(); // Use showAndWait for modal behavior
    }

    public static void main(String[] args) {
        // For testing purposes, you might want to launch with sample data
        // For example: launch(new XemNhanVien(1, "Phòng Kế Toán"), args);
        // However, Application.launch(args) will call the default constructor
        // if it exists and is accessible, or fail if it needs args.
        // To run this directly, you need a main that can instantiate XemNhanVien with params
        // or a default constructor and a way to set idPhongBan/tenPhongBan.
        
        // This is a common way if you always want to start with a default/test view:
        Application.launch(TestApp.class, args); 
    }

    // Dummy class for testing launch if XemNhanVien requires constructor args
    // Or, modify main to pass arguments if your framework supports it.
    public static class TestApp extends Application {
        @Override
        public void start(Stage primaryStage) throws Exception {
            // Example: Launch with a default department for testing
             XemNhanVienn view = new XemNhanVienn(1, "Khoa Khám Bệnh"); // Example department
             view.start(primaryStage);
        }
    }
}