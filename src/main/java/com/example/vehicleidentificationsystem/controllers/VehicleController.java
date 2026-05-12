package com.example.vehicleidentificationsystem.controllers;

import com.example.vehicleidentificationsystem.dao.VehicleDAO;
import com.example.vehicleidentificationsystem.dao.CustomerDAO;
import com.example.vehicleidentificationsystem.models.Vehicle;
import com.example.vehicleidentificationsystem.models.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

public class VehicleController {
    private static VehicleDAO vehicleDAO = new VehicleDAO();
    private static CustomerDAO customerDAO = new CustomerDAO();
    private static ObservableList<Vehicle> vehicleList = FXCollections.observableArrayList();
    private static TableView<Vehicle> tableView = new TableView<>();

    public static VBox getView() {
        VBox view = new VBox(10);
        view.setPadding(new Insets(15));

        Label title = new Label("Vehicle Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        setupTableView();
        refreshTable();

        // Form for adding vehicles
        GridPane form = createVehicleForm();

        HBox buttonBox = new HBox(10);
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10px;");
        refreshBtn.setOnAction(e -> refreshTable());

        Button deleteBtn = new Button("Delete Selected Vehicle");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 10px;");
        deleteBtn.setOnAction(e -> deleteVehicle());

        buttonBox.getChildren().addAll(refreshBtn, deleteBtn);

        view.getChildren().addAll(title, tableView, form, buttonBox);
        return view;
    }

    private static void setupTableView() {
        TableColumn<Vehicle, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        idCol.setPrefWidth(50);

        TableColumn<Vehicle, String> regCol = new TableColumn<>("Registration");
        regCol.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        regCol.setPrefWidth(120);

        TableColumn<Vehicle, String> makeCol = new TableColumn<>("Make");
        makeCol.setCellValueFactory(new PropertyValueFactory<>("make"));
        makeCol.setPrefWidth(100);

        TableColumn<Vehicle, String> modelCol = new TableColumn<>("Model");
        modelCol.setCellValueFactory(new PropertyValueFactory<>("model"));
        modelCol.setPrefWidth(100);

        TableColumn<Vehicle, Integer> yearCol = new TableColumn<>("Year");
        yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));
        yearCol.setPrefWidth(80);

        TableColumn<Vehicle, String> ownerCol = new TableColumn<>("Owner");
        ownerCol.setCellValueFactory(new PropertyValueFactory<>("ownerName"));
        ownerCol.setPrefWidth(150);

        tableView.getColumns().addAll(idCol, regCol, makeCol, modelCol, yearCol, ownerCol);
        tableView.setPrefHeight(400);
    }

    private static GridPane createVehicleForm() {
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(15));
        form.setStyle("-fx-border-color: #dee2e6; -fx-border-radius: 5px;");

        TextField regField = new TextField();
        regField.setPromptText("Registration Number");

        TextField makeField = new TextField();
        makeField.setPromptText("Make (e.g., Toyota)");

        TextField modelField = new TextField();
        modelField.setPromptText("Model (e.g., Camry)");

        TextField yearField = new TextField();
        yearField.setPromptText("Year");

        ComboBox<Customer> ownerCombo = new ComboBox<>();
        ownerCombo.setPromptText("Select Owner");
        refreshOwnerCombo(ownerCombo);

        Button addBtn = new Button("Add Vehicle");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-font-size: 12px;");

        addBtn.setOnAction(e -> {
            String reg = regField.getText().trim();
            String make = makeField.getText().trim();
            String model = modelField.getText().trim();
            String yearStr = yearField.getText().trim();
            Customer selectedOwner = ownerCombo.getValue();

            if (reg.isEmpty() || make.isEmpty() || model.isEmpty() || yearStr.isEmpty() || selectedOwner == null) {
                statusLabel.setText("❌ All fields are required!");
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                return;
            }

            try {
                int year = Integer.parseInt(yearStr);
                if (year < 1900 || year > 2025) {
                    statusLabel.setText("❌ Year must be between 1900 and 2025!");
                    return;
                }

                Vehicle vehicle = new Vehicle(reg, make, model, year, selectedOwner.getId());
                if (vehicleDAO.addVehicle(vehicle)) {
                    refreshTable();
                    regField.clear();
                    makeField.clear();
                    modelField.clear();
                    yearField.clear();
                    ownerCombo.setValue(null);
                    statusLabel.setText("✅ Vehicle added successfully!");
                    statusLabel.setStyle("-fx-text-fill: #27ae60;");
                } else {
                    statusLabel.setText("❌ Failed to add vehicle. Registration may already exist.");
                    statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("❌ Year must be a valid number!");
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            }
        });

        form.addRow(0, new Label("Registration:*"), regField);
        form.addRow(1, new Label("Make:*"), makeField);
        form.addRow(2, new Label("Model:*"), modelField);
        form.addRow(3, new Label("Year:*"), yearField);
        form.addRow(4, new Label("Owner:*"), ownerCombo);
        form.addRow(5, addBtn);
        form.addRow(6, statusLabel);

        return form;
    }

    private static void refreshOwnerCombo(ComboBox<Customer> combo) {
        combo.getItems().clear();
        for (Customer customer : customerDAO.getAllCustomers()) {
            combo.getItems().add(customer);
        }
        combo.setCellFactory(param -> new ListCell<Customer>() {
            @Override
            protected void updateItem(Customer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getId() + " - " + item.getName());
                }
            }
        });
    }

    private static void deleteVehicle() {
        Vehicle selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Delete");
            confirm.setHeaderText("Delete Vehicle");
            confirm.setContentText("Are you sure you want to delete " + selected.getMake() + " " + selected.getModel() + "?");

            if (confirm.showAndWait().get() == ButtonType.OK) {
                if (vehicleDAO.deleteVehicle(selected.getVehicleId())) {
                    refreshTable();
                    showAlert("Success", "Vehicle deleted successfully!");
                } else {
                    showAlert("Error", "Could not delete vehicle");
                }
            }
        } else {
            showAlert("Error", "Please select a vehicle to delete");
        }
    }

    private static void refreshTable() {
        vehicleList.clear();
        vehicleList.addAll(vehicleDAO.getAllVehicles());
        tableView.setItems(vehicleList);
    }

    private static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}