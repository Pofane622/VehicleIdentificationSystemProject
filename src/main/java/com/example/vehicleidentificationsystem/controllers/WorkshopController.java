package com.example.vehicleidentificationsystem.controllers;

import com.example.vehicleidentificationsystem.dao.WorkshopDAO;
import com.example.vehicleidentificationsystem.dao.VehicleDAO;
import com.example.vehicleidentificationsystem.models.ServiceRecord;
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

public class WorkshopController {
    private static WorkshopDAO workshopDAO = new WorkshopDAO();
    private static VehicleDAO vehicleDAO = new VehicleDAO();
    private static ObservableList<ServiceRecord> recordList = FXCollections.observableArrayList();
    private static TableView<ServiceRecord> tableView = new TableView<>();

    public static VBox getView() {
        VBox view = new VBox(10);
        view.setPadding(new Insets(15));

        Label title = new Label("Workshop Module - Vehicle Service History");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        setupTableView();
        refreshTable();

        GridPane form = createServiceForm();

        HBox buttonBox = new HBox(10);
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10px;");
        refreshBtn.setOnAction(e -> refreshTable());

        Button deleteBtn = new Button("Delete Selected Record");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 10px;");
        deleteBtn.setOnAction(e -> deleteRecord());

        buttonBox.getChildren().addAll(refreshBtn, deleteBtn);

        view.getChildren().addAll(title, tableView, form, buttonBox);
        return view;
    }

    private static void setupTableView() {
        TableColumn<ServiceRecord, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("serviceId"));
        idCol.setPrefWidth(50);

        TableColumn<ServiceRecord, String> vehicleCol = new TableColumn<>("Vehicle");
        vehicleCol.setCellValueFactory(new PropertyValueFactory<>("vehicleInfo"));
        vehicleCol.setPrefWidth(150);

        TableColumn<ServiceRecord, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("serviceDate"));
        dateCol.setPrefWidth(100);

        TableColumn<ServiceRecord, String> typeCol = new TableColumn<>("Service Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("serviceType"));
        typeCol.setPrefWidth(120);

        TableColumn<ServiceRecord, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(200);

        TableColumn<ServiceRecord, Double> costCol = new TableColumn<>("Cost");
        costCol.setCellValueFactory(new PropertyValueFactory<>("cost"));
        costCol.setPrefWidth(80);

        TableColumn<ServiceRecord, String> mechanicCol = new TableColumn<>("Mechanic");
        mechanicCol.setCellValueFactory(new PropertyValueFactory<>("mechanicName"));
        mechanicCol.setPrefWidth(120);

        tableView.getColumns().addAll(idCol, vehicleCol, dateCol, typeCol, descCol, costCol, mechanicCol);
        tableView.setPrefHeight(400);
    }

    private static GridPane createServiceForm() {
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(15));
        form.setStyle("-fx-border-color: #dee2e6; -fx-border-radius: 5px;");

        ComboBox<Vehicle> vehicleCombo = new ComboBox<>();
        vehicleCombo.setPromptText("Select Vehicle");
        refreshVehicleCombo(vehicleCombo);

        DatePicker datePicker = new DatePicker(LocalDate.now());

        TextField typeField = new TextField();
        typeField.setPromptText("Service Type (e.g., Oil Change)");

        TextField descField = new TextField();
        descField.setPromptText("Description");

        TextField costField = new TextField();
        costField.setPromptText("Cost");

        TextField mechanicField = new TextField();
        mechanicField.setPromptText("Mechanic Name");

        Button addBtn = new Button("Add Service Record");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-font-size: 12px;");

        addBtn.setOnAction(e -> {
            Vehicle selectedVehicle = vehicleCombo.getValue();
            String type = typeField.getText().trim();
            String desc = descField.getText().trim();
            String costStr = costField.getText().trim();
            String mechanic = mechanicField.getText().trim();
            LocalDate date = datePicker.getValue();

            if (selectedVehicle == null || type.isEmpty() || costStr.isEmpty()) {
                statusLabel.setText("❌ Vehicle, Service Type, and Cost are required!");
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                return;
            }

            try {
                double cost = Double.parseDouble(costStr);

                ServiceRecord record = new ServiceRecord();
                record.setVehicleId(selectedVehicle.getVehicleId());
                record.setServiceDate(date);
                record.setServiceType(type);
                record.setDescription(desc);
                record.setCost(cost);
                record.setMechanicName(mechanic);

                if (workshopDAO.addServiceRecord(record)) {
                    refreshTable();
                    vehicleCombo.setValue(null);
                    typeField.clear();
                    descField.clear();
                    costField.clear();
                    mechanicField.clear();
                    datePicker.setValue(LocalDate.now());
                    statusLabel.setText("✅ Service record added successfully!");
                    statusLabel.setStyle("-fx-text-fill: #27ae60;");
                } else {
                    statusLabel.setText("❌ Failed to add service record");
                    statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("❌ Cost must be a valid number!");
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            }
        });

        form.addRow(0, new Label("Vehicle:*"), vehicleCombo);
        form.addRow(1, new Label("Service Date:"), datePicker);
        form.addRow(2, new Label("Service Type:*"), typeField);
        form.addRow(3, new Label("Description:"), descField);
        form.addRow(4, new Label("Cost:*"), costField);
        form.addRow(5, new Label("Mechanic:"), mechanicField);
        form.addRow(6, addBtn);
        form.addRow(7, statusLabel);

        return form;
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

    private static void deleteRecord() {
        ServiceRecord selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Delete");
            confirm.setHeaderText("Delete Service Record");
            confirm.setContentText("Are you sure you want to delete this service record?");

            if (confirm.showAndWait().get() == ButtonType.OK) {
                if (workshopDAO.deleteServiceRecord(selected.getServiceId())) {
                    refreshTable();
                    showAlert("Success", "Service record deleted successfully!");
                } else {
                    showAlert("Error", "Could not delete record");
                }
            }
        } else {
            showAlert("Error", "Please select a record to delete");
        }
    }

    private static void refreshTable() {
        recordList.clear();
        recordList.addAll(workshopDAO.getAllServiceRecords());
        tableView.setItems(recordList);
    }

    private static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}