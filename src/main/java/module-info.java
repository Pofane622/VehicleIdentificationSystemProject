module com.example.vehicleidentificationsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.vehicleidentificationsystem to javafx.graphics;
    opens com.example.vehicleidentificationsystem.models to javafx.base;
    opens com.example.vehicleidentificationsystem.controllers to javafx.fxml;

    exports com.example.vehicleidentificationsystem;
    exports com.example.vehicleidentificationsystem.utils;
    opens com.example.vehicleidentificationsystem.utils to javafx.graphics;
}