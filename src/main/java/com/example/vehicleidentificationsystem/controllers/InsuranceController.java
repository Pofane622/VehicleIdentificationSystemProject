package com.example.vehicleidentificationsystem.controllers;

import com.example.vehicleidentificationsystem.dao.InsuranceDAO;
import com.example.vehicleidentificationsystem.dao.VehicleDAO;
import com.example.vehicleidentificationsystem.models.InsurancePolicy;
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

public class InsuranceController {
    private static InsuranceDAO insuranceDAO = new InsuranceDAO();
    private static VehicleDAO vehicleDAO = new VehicleDAO();
    private static ObservableList<InsurancePolicy> policyList = FXCollections.observableArrayList();
    private static TableView<InsurancePolicy> tableView = new TableView<>();

    public static VBox getView() {
        VBox view = new VBox(10);
        view.setPadding(new Insets(15));
        view.setStyle("-fx-background-color: transparent;");

        Label title = new Label("Insurance Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #5dade2;");

        Label subtitle = new Label("Manage vehicle insurance policies and claims");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #85c1e9;");

        setupTableView();
        refreshTable();

        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: transparent;");

        // Insurance Policies Tab
        Tab policyTab = new Tab("Insurance Policies");
        policyTab.setContent(createPolicyForm());
        policyTab.setClosable(false);

        // Claims Tab
        Tab claimTab = new Tab("Claims Management");
        claimTab.setContent(createClaimForm());
        claimTab.setClosable(false);

        tabPane.getTabs().addAll(policyTab, claimTab);

        HBox buttonBox = new HBox(10);
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10px; -fx-background-radius: 8px;");
        refreshBtn.setOnAction(e -> refreshTable());

        Button deleteBtn = new Button("Delete Selected Policy");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 10px; -fx-background-radius: 8px;");
        deleteBtn.setOnAction(e -> deletePolicy());

        buttonBox.getChildren().addAll(refreshBtn, deleteBtn);

        // Add everything to a VBox
        VBox content = new VBox(10);
        content.getChildren().addAll(title, subtitle, tableView, tabPane, buttonBox);

        // Wrap everything in a ScrollPane for scrolling
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        scrollPane.setPadding(new Insets(5));

        // Create main view with ScrollPane
        VBox mainView = new VBox(scrollPane);
        mainView.setStyle("-fx-background-color: transparent;");

        return mainView;
    }

    private static void setupTableView() {
        TableColumn<InsurancePolicy, Integer> idCol = new TableColumn<>("Policy ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("policyId"));
        idCol.setPrefWidth(70);

        TableColumn<InsurancePolicy, String> vehicleCol = new TableColumn<>("Vehicle");
        vehicleCol.setCellValueFactory(new PropertyValueFactory<>("vehicleInfo"));
        vehicleCol.setPrefWidth(150);

        TableColumn<InsurancePolicy, String> companyCol = new TableColumn<>("Insurance Company");
        companyCol.setCellValueFactory(new PropertyValueFactory<>("insuranceCompany"));
        companyCol.setPrefWidth(150);

        TableColumn<InsurancePolicy, String> policyNumCol = new TableColumn<>("Policy Number");
        policyNumCol.setCellValueFactory(new PropertyValueFactory<>("policyNumber"));
        policyNumCol.setPrefWidth(120);

        TableColumn<InsurancePolicy, LocalDate> startCol = new TableColumn<>("Start Date");
        startCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        startCol.setPrefWidth(100);

        TableColumn<InsurancePolicy, LocalDate> endCol = new TableColumn<>("End Date");
        endCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        endCol.setPrefWidth(100);

        TableColumn<InsurancePolicy, String> coverageCol = new TableColumn<>("Coverage");
        coverageCol.setCellValueFactory(new PropertyValueFactory<>("coverageDetails"));
        coverageCol.setPrefWidth(200);

        tableView.getColumns().addAll(idCol, vehicleCol, companyCol, policyNumCol, startCol, endCol, coverageCol);
        tableView.setPrefHeight(300);
        tableView.setStyle("-fx-background-color: white; -fx-border-color: #3498db; -fx-border-radius: 8px;");
    }

    private static VBox createPolicyForm() {
        VBox formBox = new VBox(10);
        formBox.setPadding(new Insets(15));
        formBox.setStyle("-fx-background-color: white; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label formTitle = new Label("Add New Insurance Policy");
        formTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        ComboBox<Vehicle> vehicleCombo = new ComboBox<>();
        vehicleCombo.setPromptText("Select Vehicle");
        vehicleCombo.setStyle("-fx-text-fill: #2c3e50;");
        refreshVehicleCombo(vehicleCombo);

        TextField companyField = new TextField();
        companyField.setPromptText("Insurance Company (e.g., Geico)");
        companyField.setStyle("-fx-text-fill: #2c3e50;");

        TextField policyNumField = new TextField();
        policyNumField.setPromptText("Policy Number");
        policyNumField.setStyle("-fx-text-fill: #2c3e50;");

        DatePicker startDatePicker = new DatePicker(LocalDate.now());
        startDatePicker.setStyle("-fx-text-fill: #2c3e50;");

        DatePicker endDatePicker = new DatePicker(LocalDate.now().plusYears(1));
        endDatePicker.setStyle("-fx-text-fill: #2c3e50;");

        TextArea coverageField = new TextArea();
        coverageField.setPromptText("Coverage Details (e.g., Full Coverage, Liability Only)");
        coverageField.setPrefRowCount(2);
        coverageField.setPrefWidth(300);
        coverageField.setStyle("-fx-text-fill: #2c3e50;");

        Button addBtn = new Button("Add Insurance Policy");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 10px; -fx-background-radius: 8px;");
        addBtn.setMaxWidth(200);

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-font-size: 12px;");
        statusLabel.setWrapText(true);

        addBtn.setOnAction(e -> {
            Vehicle selectedVehicle = vehicleCombo.getValue();
            String company = companyField.getText().trim();
            String policyNum = policyNumField.getText().trim();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            String coverage = coverageField.getText().trim();

            if (selectedVehicle == null || company.isEmpty() || policyNum.isEmpty() || startDate == null || endDate == null) {
                statusLabel.setText("❌ Vehicle, Company, Policy Number, and Dates are required!");
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                return;
            }

            if (endDate.isBefore(startDate)) {
                statusLabel.setText("❌ End date must be after start date!");
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                return;
            }

            InsurancePolicy policy = new InsurancePolicy();
            policy.setVehicleId(selectedVehicle.getVehicleId());
            policy.setInsuranceCompany(company);
            policy.setPolicyNumber(policyNum);
            policy.setStartDate(startDate);
            policy.setEndDate(endDate);
            policy.setCoverageDetails(coverage);

            if (insuranceDAO.addPolicy(policy)) {
                refreshTable();
                vehicleCombo.setValue(null);
                companyField.clear();
                policyNumField.clear();
                startDatePicker.setValue(LocalDate.now());
                endDatePicker.setValue(LocalDate.now().plusYears(1));
                coverageField.clear();
                statusLabel.setText("✅ Insurance policy added successfully!");
                statusLabel.setStyle("-fx-text-fill: #27ae60;");
            } else {
                statusLabel.setText("❌ Failed to add policy. Policy number may already exist.");
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            }
        });

        form.addRow(0, new Label("Vehicle:*"), vehicleCombo);
        form.addRow(1, new Label("Insurance Company:*"), companyField);
        form.addRow(2, new Label("Policy Number:*"), policyNumField);
        form.addRow(3, new Label("Start Date:*"), startDatePicker);
        form.addRow(4, new Label("End Date:*"), endDatePicker);
        form.addRow(5, new Label("Coverage Details:"), coverageField);
        form.addRow(6, addBtn);

        formBox.getChildren().addAll(formTitle, form, statusLabel);
        return formBox;
    }

    private static VBox createClaimForm() {
        VBox formBox = new VBox(10);
        formBox.setPadding(new Insets(15));
        formBox.setStyle("-fx-background-color: white; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label formTitle = new Label("File a Claim");
        formTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        ComboBox<InsurancePolicy> policyCombo = new ComboBox<>();
        policyCombo.setPromptText("Select Insurance Policy");
        policyCombo.setStyle("-fx-text-fill: #2c3e50;");
        refreshPolicyCombo(policyCombo);

        TextField amountField = new TextField();
        amountField.setPromptText("Claim Amount");
        amountField.setStyle("-fx-text-fill: #2c3e50;");

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Pending", "Approved", "Rejected");
        statusCombo.setValue("Pending");
        statusCombo.setStyle("-fx-text-fill: #2c3e50;");

        Button addBtn = new Button("Submit Claim");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 10px; -fx-background-radius: 8px;");
        addBtn.setMaxWidth(200);

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-font-size: 12px;");
        statusLabel.setWrapText(true);

        addBtn.setOnAction(e -> {
            InsurancePolicy selectedPolicy = policyCombo.getValue();
            String amountStr = amountField.getText().trim();
            String status = statusCombo.getValue();

            if (selectedPolicy == null || amountStr.isEmpty()) {
                statusLabel.setText("❌ Policy and Claim Amount are required!");
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr);

                if (insuranceDAO.addClaim(selectedPolicy.getPolicyId(), amount, status)) {
                    refreshTable();
                    policyCombo.setValue(null);
                    amountField.clear();
                    statusLabel.setText("✅ Claim submitted successfully!");
                    statusLabel.setStyle("-fx-text-fill: #27ae60;");
                    showAlert("Success", "Claim has been submitted for review!");
                } else {
                    statusLabel.setText("❌ Failed to submit claim");
                    statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("❌ Claim amount must be a valid number!");
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            }
        });

        form.addRow(0, new Label("Insurance Policy:*"), policyCombo);
        form.addRow(1, new Label("Claim Amount:*"), amountField);
        form.addRow(2, new Label("Status:"), statusCombo);
        form.addRow(3, addBtn);

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

    private static void refreshPolicyCombo(ComboBox<InsurancePolicy> combo) {
        combo.getItems().clear();
        for (InsurancePolicy policy : insuranceDAO.getAllPolicies()) {
            combo.getItems().add(policy);
        }
        combo.setCellFactory(param -> new ListCell<InsurancePolicy>() {
            @Override
            protected void updateItem(InsurancePolicy item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getPolicyId() + " - " + item.getVehicleInfo() + " - " + item.getInsuranceCompany());
                }
            }
        });
    }

    private static void deletePolicy() {
        InsurancePolicy selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Delete");
            confirm.setHeaderText("Delete Insurance Policy");
            confirm.setContentText("Are you sure you want to delete this insurance policy?");

            if (confirm.showAndWait().get() == ButtonType.OK) {
                if (insuranceDAO.deletePolicy(selected.getPolicyId())) {
                    refreshTable();
                    showAlert("Success", "Insurance policy deleted successfully!");
                } else {
                    showAlert("Error", "Could not delete policy");
                }
            }
        } else {
            showAlert("Error", "Please select a policy to delete");
        }
    }

    private static void refreshTable() {
        policyList.clear();
        policyList.addAll(insuranceDAO.getAllPolicies());
        tableView.setItems(policyList);
    }

    private static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}