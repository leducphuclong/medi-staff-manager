module MediStaffManager {
    requires jdk.httpserver;
    requires java.desktop;
    requires java.sql;

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    opens com.MediStaffManager.view to javafx.fxml;

    exports com.MediStaffManager.view;
}
