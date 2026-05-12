package com.example.vehicleidentificationsystem.controllers;

import com.example.vehicleidentificationsystem.dao.CustomerDAO;
import com.example.vehicleidentificationsystem.models.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class CustomerController {
    private static CustomerDAO customerDAO = new CustomerDAO();
    private static ObservableList<Customer> customerList = FXCollections.observableArrayList();
    private static TableView<Customer> tableView = new TableView<>();

    // Validation methods
    private static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false; // Email is required for customers
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex) && email.contains(".");
    }

    private static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false; // Phone is required for customers
        }
        String phoneRegex = "^[0-9\\+\\-\\s]+$";
        return phone.matches(phoneRegex);
    }

    private static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        String nameRegex = "^[A-Za-z\\s\\-']+$";
        return name.matches(nameRegex);
    }

    public static VBox getView() {
        VBox view = new VBox(10);
        view.setPadding(new Insets(15));

        Label title = new Label("Customer Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        setupTableView();
        refreshTable();

        // Form for adding/editing customers
        GridPane form = createCustomerForm();

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10px;");
        refreshBtn.setOnAction(e -> refreshTable());

        view.getChildren().addAll(title, tableView, form, refreshBtn);
        return view;
    }

    private static GridPane createCustomerForm() {
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(15));
        form.setStyle("-fx-border-color: #dee2e6; -fx-border-radius: 5px;");

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name (letters only)");

        TextField addressField = new TextField();
        addressField.setPromptText("Address");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone (numbers only)");

        TextField emailField = new TextField();
        emailField.setPromptText("Email (name@domain.com)");

        Button addBtn = new Button("Add Customer");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-font-size: 12px;");

        addBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String address = addressField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();

            // Validate
            if (!isValidName(name)) {
                statusLabel.setText("❌ Name can only contain letters, spaces, hyphens, and apostrophes!");
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                return;
            }

            if (!isValidPhone(phone)) {
                statusLabel.setText("❌ Phone can only contain numbers, spaces, hyphens, and plus sign!");
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                return;
            }

            if (!isValidEmail(email)) {
                statusLabel.setText("❌ Invalid email format! Example: name@domain.com");
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                return;
            }

            Customer customer = new Customer(name, address, phone, email);
            if (customerDAO.addCustomer(customer)) {
                refreshTable();
                nameField.clear();
                addressField.clear();
                phoneField.clear();
                emailField.clear();
                statusLabel.setText("✅ Customer added successfully!");
                statusLabel.setStyle("-fx-text-fill: #27ae60;");
            } else {
                statusLabel.setText("❌ Failed to add customer. Email may already exist.");
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            }
        });

        form.addRow(0, new Label("Name:*"), nameField);
        form.addRow(1, new Label("Address:"), addressField);
        form.addRow(2, new Label("Phone:*"), phoneField);
        form.addRow(3, new Label("Email:*"), emailField);
        form.addRow(4, addBtn);
        form.addRow(5, statusLabel);

        return form;
    }

    private static void setupTableView() {
        TableColumn<Customer, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(150);

        TableColumn<Customer, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        addressCol.setPrefWidth(200);

        TableColumn<Customer, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneCol.setPrefWidth(120);

        TableColumn<Customer, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);

        tableView.getColumns().addAll(idCol, nameCol, addressCol, phoneCol, emailCol);
        tableView.setPrefHeight(400);
    }

    private static void refreshTable() {
        customerList.clear();
        customerList.addAll(customerDAO.getAllCustomers());
        tableView.setItems(customerList);
    }
}