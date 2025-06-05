package application;
	
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Medi-Staff Manager");

        // Root layout
        BorderPane root = new BorderPane();

        // ======== Navigation Bar ========
        HBox navBar = new HBox(20);
        navBar.setPadding(new Insets(15));
        navBar.setStyle("-fx-background-color: #2c3e50;");

        String[] navLabels = {"Quản lý nhân viên", "Quản lý phòng ban", "Quản lý lương", "Quản lý lịch làm việc"};
        for (String label : navLabels) {
            Button navButton = createNavButton(label);
            navBar.getChildren().add(navButton);
        }

        // ======== Top: Search + Buttons ========
        HBox topControls = new HBox(10);
        topControls.setPadding(new Insets(10));
        topControls.setAlignment(Pos.CENTER);
        topControls.setStyle("-fx-background-color: #ecf0f1;");

        TextField searchField = new TextField();
        searchField.setPromptText("Nhập từ khóa tìm kiếm...");
        searchField.setPrefWidth(200);

        ComboBox<String> criteriaCombo = new ComboBox<>();
        criteriaCombo.getItems().addAll("Tất cả", "Tên", "Số CCCD", "Số điện thoại", "Phòng ban");
        criteriaCombo.setValue("Tất cả");

        Button searchBtn = createModernButton("Tìm kiếm", "#3498db", "#2980b9");
        Button addBtn = createModernButton("Thêm nhân viên", "#2ecc71", "#27ae60");
        Button editBtn = createModernButton("Chỉnh sửa", "#2980b9", "#3498db");
        Button deleteBtn = createModernButton("Xóa", "#e74c3c", "#c0392b");

        topControls.getChildren().addAll(new Label("Tìm kiếm:"), searchField, criteriaCombo, searchBtn, addBtn, editBtn, deleteBtn);

        // ======== Center: Employee Table ========
        TableView<Employee> employeeTable = new TableView<>();

        String[] colNames = {"ID", "CCCD", "Họ Tên", "Số ĐT", "Email", "Giới tính", "Ngày sinh", "Chức vụ", "Phòng ban"};
        String[] colFields = {"id", "cccd", "hoTen", "sdt", "email", "gioiTinh", "ngaySinh", "chucVu", "phongBan"};

        for (int i = 0; i < colNames.length; i++) {
            TableColumn<Employee, String> col = new TableColumn<>(colNames[i]);
            col.setCellValueFactory(new PropertyValueFactory<>(colFields[i]));
            col.setMinWidth(100);
            employeeTable.getColumns().add(col);
        }

        employeeTable.setPrefHeight(400);

        // ======== Layout Setup ========
        root.setTop(new VBox(navBar, topControls));
        root.setCenter(new StackPane(employeeTable));

        // ======== Scene với Gradient Background ========
        Scene scene = new Scene(root, 1200, 700);
        root.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#2193b0")),
                        new Stop(1, Color.web("#6dd5ed"))),
                CornerRadii.EMPTY, Insets.EMPTY)));

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createNavButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        btn.setFont(Font.font("Arial", 14));
        btn.setCursor(javafx.scene.Cursor.HAND);
        return btn;
    }

    private Button createModernButton(String text, String color, String hoverColor) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Arial", 14));
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 20;");
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setEffect(new DropShadow());

        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + hoverColor + "; -fx-text-fill: white; -fx-background-radius: 20;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 20;"));

        return btn;
    }

    // === Dummy class để biên dịch được ===
    public static class Employee {
        private String id, cccd, hoTen, sdt, email, gioiTinh, ngaySinh, chucVu, phongBan;

        // Getter & Setter (hoặc sử dụng Lombok nếu muốn)
        public String getId() { return id; }
        public String getCccd() { return cccd; }
        public String getHoTen() { return hoTen; }
        public String getSdt() { return sdt; }
        public String getEmail() { return email; }
        public String getGioiTinh() { return gioiTinh; }
        public String getNgaySinh() { return ngaySinh; }
        public String getChucVu() { return chucVu; }
        public String getPhongBan() { return phongBan; }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

