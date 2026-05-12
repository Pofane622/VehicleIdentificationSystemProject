package com.example.vehicleidentificationsystem.controllers;

import com.example.vehicleidentificationsystem.dao.PoliceDAO;
import com.example.vehicleidentificationsystem.dao.VehicleDAO;
import com.example.vehicleidentificationsystem.models.PoliceReport;
import com.example.vehicleidentificationsystem.models.Violation;
import com.example.vehicleidentificationsystem.models.Vehicle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import java.time.LocalDate;

public class PoliceController {
    private static PoliceDAO policeDAO = new PoliceDAO();
    private static VehicleDAO vehicleDAO = new VehicleDAO();
    private static ObservableList<PoliceReport> reportList = FXCollections.observableArrayList();
    private static TableView<PoliceReport> tableView = new TableView<>();

    public static VBox getView() {
        VBox view = new VBox(10);
        view.setPadding(new Insets(15));

        Label title = new Label("Police Records Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label subtitle = new Label("Track police reports, accidents, thefts, and traffic violations");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        setupTableView();
        refreshTable();

        TabPane tabPane = new TabPane();

        // Police Reports Tab
        Tab reportTab = new Tab("Police Reports");
        reportTab.setContent(createReportForm());
        reportTab.setClosable(false);

        // Violations Tab
        Tab violationTab = new Tab("Traffic Violations");
        violationTab.setContent(createViolationForm());
        violationTab.setClosable(false);

        tabPane.getTabs().addAll(reportTab, violationTab);

        HBox buttonBox = new HBox(10);
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10px;");
        refreshBtn.setOnAction(e -> refreshTable());

        Button deleteBtn = new Button("Delete Selected Report");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 10px;");
        deleteBtn.setOnAction(e -> deleteReport());

        buttonBox.getChildren().addAll(refreshBtn, deleteBtn);

        view.getChildren().addAll(title, subtitle, tableView, tabPane, buttonBox);
        return view;
    }

    private static void setupTableView() {
        TableColumn<PoliceReport, Integer> idCol = new TableColumn<>("Report ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("reportId"));
        idCol.setPrefWidth(70);

        TableColumn<PoliceReport, String> vehicleCol = new TableColumn<>("Vehicle");
        vehicleCol.setCellValueFactory(new PropertyValueFactory<>("vehicleInfo"));
        vehicleCol.setPrefWidth(150);

        TableColumn<PoliceReport, LocalDate> dateCol = new TableColumn<>("Report Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("reportDate"));
        dateCol.setPrefWidth(100);

        TableColumn<PoliceReport, String> typeCol = new TableColumn<>("Report Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("reportType"));
        typeCol.setPrefWidth(100);

        TableColumn<PoliceReport, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(250);

        TableColumn<PoliceReport, String> officerCol = new TableColumn<>("Officer Name");
        officerCol.setCellValueFactory(new PropertyValueFactory<>("officerName"));
        officerCol.setPrefWidth(120);

        tableView.getColumns().addAll(idCol, vehicleCol, dateCol, typeCol, descCol, officerCol);
        tableView.setPrefHeight(300);
    }

    private static VBox createReportForm() {
        VBox formBox = new VBox(10);
        formBox.setPadding(new Insets(15));

        Label formTitle = new Label("File New Police Report");
        formTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        ComboBox<Vehicle> vehicleCombo = new ComboBox<>();
        vehicleCombo.setPromptText("Select Vehicle");
        refreshVehicleCombo(vehicleCombo);

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Accident", "Theft", "Stolen", "Recovered", "Vandalism", "Hit and Run");
        typeCombo.setPromptText("Report Type");

        TextArea descField = new TextArea();
        descField.setPromptText("Description of incident...");
        descField.setPrefRowCount(3);
        descField.setPrefWidth(300);

        TextField officerField = new TextField();
        officerField.setPromptText("Officer Name");

        DatePicker datePicker = new DatePicker(LocalDate.now());

        Button addBtn = new Button("File Report");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 10px;");

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-font-size: 12px;");

        addBtn.setOnAction(e -> {
            Vehicle selectedVehicle = vehicleCombo.getValue();
            String reportType = typeCombo.getValue();
            String description = descField.getText().trim();
            String officerName = officerField.getText().trim();
            LocalDate reportDate = datePicker.getValue();

            if (selectedVehicle == null || reportType == null || description.isEmpty() || officerName.isEmpty()) {
                statusLabel.setText("❌ Vehicle, Report Type, Description, and Officer Name are required!");
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                return;
            }

            PoliceReport report = new PoliceReport();
            report.setVehicleId(selectedVehicle.getVehicleId());
            report.setReportType(reportType);
            report.setDescription(description);
            report.setOfficerName(officerName);
            report.setReportDate(reportDate);

            if (policeDAO.addPoliceReport(report)) {
                refreshTable();
                vehicleCombo.setValue(null);
                typeCombo.setValue(null);
                descField.clear();
                officerField.clear();
                datePicker.setValue(LocalDate.now());
                statusLabel.setText("✅ Police report filed successfully!");
                statusLabel.setStyle("-fx-text-fill: #27ae60;");
            } else {
                statusLabel.setText("❌ Failed to file report");
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            }
        });

        form.addRow(0, new Label("Vehicle:*"), vehicleCombo);
        form.addRow(1, new Label("Report Type:*"), typeCombo);
        form.addRow(2, new Label("Description:*"), descField);
        form.addRow(3, new Label("Officer Name:*"), officerField);
        form.addRow(4, new Label("Report Date:"), datePicker);
        form.addRow(5, addBtn);

        formBox.getChildren().addAll(formTitle, form, statusLabel);
        return formBox;
    }

    private static VBox createViolationForm() {
        VBox formBox = new VBox(10);
        formBox.setPadding(new Insets(15));

        Label formTitle = new Label("Record Traffic Violation");
        formTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        ComboBox<Vehicle> vehicleCombo = new ComboBox<>();
        vehicleCombo.setPromptText("Select Vehicle");
        refreshVehicleCombo(vehicleCombo);

        ComboBox<String> violationTypeCombo = new ComboBox<>();
        violationTypeCombo.getItems().addAll("Speeding", "Running Red Light", "Illegal Parking", "No Seatbelt",
                "Expired Registration", "Driving Without License", "DUI", "Reckless Driving");
        violationTypeCombo.setPromptText("Violation Type");

        TextField fineField = new TextField();
        fineField.setPromptText("Fine Amount");

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Unpaid", "Paid", "Pending");
        statusCombo.setValue("Unpaid");

        DatePicker datePicker = new DatePicker(LocalDate.now());

        Button addBtn = new Button("Record Violation");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 10px;");

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-font-size: 12px;");

        addBtn.setOnAction(e -> {
            Vehicle selectedVehicle = vehicleCombo.getValue();
            String violationType = violationTypeCombo.getValue();
            String fineStr = fineField.getText().trim();
            String status = statusCombo.getValue();
            LocalDate violationDate = datePicker.getValue();

            if (selectedVehicle == null || violationType == null || fineStr.isEmpty()) {
                statusLabel.setText("❌ Vehicle, Violation Type, and Fine Amount are required!");
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                return;
            }

            try {
                double fine = Double.parseDouble(fineStr);

                Violation violation = new Violation();
                violation.setVehicleId(selectedVehicle.getVehicleId());
                violation.setViolationType(violationType);
                violation.setFineAmount(fine);
                violation.setStatus(status);
                violation.setViolationDate(violationDate);

                if (policeDAO.addViolation(violation)) {
                    refreshViolationsTable();
                    vehicleCombo.setValue(null);
                    violationTypeCombo.setValue(null);
                    fineField.clear();
                    datePicker.setValue(LocalDate.now());
                    statusLabel.setText("✅ Violation recorded successfully!");
                    statusLabel.setStyle("-fx-text-fill: #27ae60;");
                    showAlert("Success", "Traffic violation has been recorded!");
                } else {
                    statusLabel.setText("❌ Failed to record violation");
                    statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("❌ Fine amount must be a valid number!");
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            }
        });

        form.addRow(0, new Label("Vehicle:*"), vehicleCombo);
        form.addRow(1, new Label("Violation Type:*"), violationTypeCombo);
        form.addRow(2, new Label("Fine Amount:*"), fineField);
        form.addRow(3, new Label("Status:"), statusCombo);
        form.addRow(4, new Label("Violation Date:"), datePicker);
        form.addRow(5, addBtn);

        formBox.getChildren().addAll(formTitle, form, statusLabel);
        return formBox;
    }

    private static void refreshVehicleCombo(ComboBox<Vehicle> combo) {
        combo.getItems().clear();
        for (Vehicle vehicle : vehicleDAO.getAllVehicles()) {
            combo.getItems().add(vehicle);
        }
        combo.setCellFactory(param -> new ListCell<Vehicle>() {
            @Override
            protected void updateItem(Vehicle item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getRegistrationNumber() + " - " + item.getMake() + " " + item.getModel());
                }
            }
        });
    }

    private static void deleteReport() {
        PoliceReport selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Delete");
            confirm.setHeaderText("Delete Police Report");
            confirm.setContentText("Are you sure you want to delete this police report?");

            if (confirm.showAndWait().get() == ButtonType.OK) {
                if (policeDAO.deletePoliceReport(selected.getReportId())) {
                    refreshTable();
                    showAlert("Success", "Police report deleted successfully!");
                } else {
                    showAlert("Error", "Could not delete report");
                }
            }
        } else {
            showAlert("Error", "Please select a report to delete");
        }
    }

    private static void refreshViolationsTable() {
        // This would refresh a separate violations table if you add one
        // For now, just refresh the main table
        refreshTable();
    }

    private static void refreshTable() {
        reportList.clear();
        reportList.addAll(policeDAO.getAllPoliceReports());
        tableView.setItems(reportList);
    }

    private static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}