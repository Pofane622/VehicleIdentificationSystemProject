package com.example.vehicleidentificationsystem.controllers;

import com.example.vehicleidentificationsystem.dao.CustomerQueryDAO;
import com.example.vehicleidentificationsystem.dao.CustomerDAO;
import com.example.vehicleidentificationsystem.dao.VehicleDAO;
import com.example.vehicleidentificationsystem.models.CustomerQuery;
import com.example.vehicleidentificationsystem.models.Customer;
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

public class CustomerQueryController {
    private static CustomerQueryDAO queryDAO = new CustomerQueryDAO();
    private static CustomerDAO customerDAO = new CustomerDAO();
    private static VehicleDAO vehicleDAO = new VehicleDAO();
    private static ObservableList<CustomerQuery> queryList = FXCollections.observableArrayList();
    private static TableView<CustomerQuery> tableView = new TableView<>();

    public static VBox getView() {
        VBox view = new VBox(10);
        view.setPadding(new Insets(15));

        Label title = new Label("Customer Queries Module");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label subtitle = new Label("Customer questions and responses about vehicle condition");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        setupTableView();
        refreshTable();

        TabPane tabPane = new TabPane();

        // Add Query Tab
        Tab addTab = new Tab("Add New Query");
        addTab.setContent(createAddQueryForm());
        addTab.setClosable(false);

        // Respond to Query Tab
        Tab respondTab = new Tab("Respond to Query");
        respondTab.setContent(createRespondForm());
        respondTab.setClosable(false);

        tabPane.getTabs().addAll(addTab, respondTab);

        HBox buttonBox = new HBox(10);
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10px;");
        refreshBtn.setOnAction(e -> refreshTable());

        Button deleteBtn = new Button("Delete Selected Query");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 10px;");
        deleteBtn.setOnAction(e -> deleteQuery());

        buttonBox.getChildren().addAll(refreshBtn, deleteBtn);

        view.getChildren().addAll(title, subtitle, tableView, tabPane, buttonBox);
        return view;
    }

    private static void setupTableView() {
        TableColumn<CustomerQuery, Integer> idCol = new TableColumn<>("Query ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("queryId"));
        idCol.setPrefWidth(70);

        TableColumn<CustomerQuery, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerCol.setPrefWidth(120);

        TableColumn<CustomerQuery, String> vehicleCol = new TableColumn<>("Vehicle");
        vehicleCol.setCellValueFactory(new PropertyValueFactory<>("vehicleInfo"));
        vehicleCol.setPrefWidth(150);

        TableColumn<CustomerQuery, LocalDate> dateCol = new TableColumn<>("Query Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("queryDate"));
        dateCol.setPrefWidth(100);

        TableColumn<CustomerQuery, String> queryCol = new TableColumn<>("Customer Query");
        queryCol.setCellValueFactory(new PropertyValueFactory<>("queryText"));
        queryCol.setPrefWidth(250);

        TableColumn<CustomerQuery, String> responseCol = new TableColumn<>("Response");
        responseCol.setCellValueFactory(new PropertyValueFactory<>("responseText"));
        responseCol.setPrefWidth(200);

        TableColumn<CustomerQuery, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(80);

        tableView.getColumns().addAll(idCol, customerCol, vehicleCol, dateCol, queryCol, responseCol, statusCol);
        tableView.setPrefHeight(300);
    }

    private static VBox createAddQueryForm() {
        VBox formBox = new VBox(10);
        formBox.setPadding(new Insets(15));

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        ComboBox<Customer> customerCombo = new ComboBox<>();
        customerCombo.setPromptText("Select Customer");
        refreshCustomerCombo(customerCombo);

        ComboBox<Vehicle> vehicleCombo = new ComboBox<>();
        vehicleCombo.setPromptText("Select Vehicle");

        customerCombo.setOnAction(e -> {
            Customer selected = customerCombo.getValue();
            if (selected != null) {
                refreshVehicleCombo(vehicleCombo, selected.getId());
            }
        });

        TextArea queryField = new TextArea();
        queryField.setPromptText("Enter customer query...");
        queryField.setPrefRowCount(3);
        queryField.setPrefWidth(400);

        Button addBtn = new Button("Submit Query");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-font-size: 12px;");

        addBtn.setOnAction(e -> {
            Customer customer = customerCombo.getValue();
            Vehicle vehicle = vehicleCombo.getValue();
            String query = queryField.getText().trim();

            if (customer == null || vehicle == null || query.isEmpty()) {
                statusLabel.setText("❌ Customer, Vehicle, and Query are required!");
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                return;
            }

            CustomerQuery newQuery = new CustomerQuery();
            newQuery.setCustomerId(customer.getId());
            newQuery.setVehicleId(vehicle.getVehicleId());
            newQuery.setQueryText(query);
            newQuery.setStatus("Pending");

            if (queryDAO.addQuery(newQuery)) {
                refreshTable();
                customerCombo.setValue(null);
                vehicleCombo.getItems().clear();
                queryField.clear();
                statusLabel.setText("✅ Query submitted successfully!");
                statusLabel.setStyle("-fx-text-fill: #27ae60;");
            } else {
                statusLabel.setText("❌ Failed to submit query");
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            }
        });

        form.addRow(0, new Label("Customer:*"), customerCombo);
        form.addRow(1, new Label("Vehicle:*"), vehicleCombo);
        form.addRow(2, new Label("Query:*"), queryField);
        form.addRow(3, addBtn);

        formBox.getChildren().addAll(form, statusLabel);
        return formBox;
    }

    private static VBox createRespondForm() {
        VBox formBox = new VBox(10);
        formBox.setPadding(new Insets(15));

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        ComboBox<CustomerQuery> queryCombo = new ComboBox<>();
        queryCombo.setPromptText("Select Query to Respond");
        refreshQueryCombo(queryCombo);

        TextArea responseField = new TextArea();
        responseField.setPromptText("Enter response...");
        responseField.setPrefRowCount(3);
        responseField.setPrefWidth(400);

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Pending", "In Progress", "Resolved");
        statusCombo.setValue("Resolved");

        Button respondBtn = new Button("Submit Response");
        respondBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-font-size: 12px;");

        respondBtn.setOnAction(e -> {
            CustomerQuery selected = queryCombo.getValue();
            String response = responseField.getText().trim();
            String status = statusCombo.getValue();

            if (selected == null || response.isEmpty()) {
                statusLabel.setText("❌ Please select a query and enter a response!");
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                return;
            }

            if (queryDAO.updateResponse(selected.getQueryId(), response, status)) {
                refreshTable();
                refreshQueryCombo(queryCombo);
                responseField.clear();
                statusLabel.setText("✅ Response submitted successfully!");
                statusLabel.setStyle("-fx-text-fill: #27ae60;");
            } else {
                statusLabel.setText("❌ Failed to submit response");
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            }
        });

        form.addRow(0, new Label("Select Query:*"), queryCombo);
        form.addRow(1, new Label("Response:*"), responseField);
        form.addRow(2, new Label("Status:"), statusCombo);
        form.addRow(3, respondBtn);

        formBox.getChildren().addAll(form, statusLabel);
        return formBox;
    }

    private static void refreshCustomerCombo(ComboBox<Customer> combo) {
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

    private static void refreshVehicleCombo(ComboBox<Vehicle> combo, int customerId) {
        combo.getItems().clear();
        // Get vehicles owned by this customer
        for (Vehicle v : vehicleDAO.getAllVehicles()) {
            if (v.getOwnerId() == customerId) {
                combo.getItems().add(v);
            }
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

    private static void refreshQueryCombo(ComboBox<CustomerQuery> combo) {
        combo.getItems().clear();
        for (CustomerQuery q : queryDAO.getAllQueries()) {
            if (q.getResponseText() == null || q.getResponseText().isEmpty()) {
                combo.getItems().add(q);
            }
        }
        combo.setCellFactory(param -> new ListCell<CustomerQuery>() {
            @Override
            protected void updateItem(CustomerQuery item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("#" + item.getQueryId() + " - " + item.getCustomerName() + " - " + item.getQueryText());
                }
            }
        });
    }

    private static void deleteQuery() {
        CustomerQuery selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Delete");
            confirm.setHeaderText("Delete Query");
            confirm.setContentText("Are you sure you want to delete this query?");

            if (confirm.showAndWait().get() == ButtonType.OK) {
                if (queryDAO.deleteQuery(selected.getQueryId())) {
                    refreshTable();
                    showAlert("Success", "Query deleted successfully!");
                } else {
                    showAlert("Error", "Could not delete query");
                }
            }
        } else {
            showAlert("Error", "Please select a query to delete");
        }
    }

    private static void refreshTable() {
        queryList.clear();
        queryList.addAll(queryDAO.getAllQueries());
        tableView.setItems(queryList);
    }

    private static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}