package com.example.vehicleidentificationsystem.controllers;

import com.example.vehicleidentificationsystem.Main;
import com.example.vehicleidentificationsystem.dao.AdminDAO;
import com.example.vehicleidentificationsystem.models.AdminUser;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;

public class AdminController {

    private static AdminDAO adminDAO = new AdminDAO();
    private static AdminUser currentUser = null;
    private static Stage primaryStage;

    // Module list for permissions
    private static final String[] ALL_MODULES = {
            "vehicles", "customers", "workshop", "queries", "insurance", "police", "reports", "admin_panel"
    };

    private static final String[] MODULE_DISPLAY_NAMES = {
            "🚗 Vehicles", "👥 Customers", "🔧 Workshop", "❓ Queries", "📋 Insurance", "👮 Police", "📊 Reports", "🔐 Admin Panel"
    };

    // Role list with their default permissions
    private static final String[] ROLES = {"user", "police", "insurance", "admin"};
    private static final String[] ROLE_DISPLAY_NAMES = {"👤 User", "👮 Police Officer", "📋 Insurance Agent", "🔐 Administrator"};

    // Default permissions for each role
    private static final Map<String, String[]> ROLE_DEFAULT_PERMISSIONS = new HashMap<>();
    static {
        ROLE_DEFAULT_PERMISSIONS.put("user", new String[]{"vehicles", "customers", "workshop", "queries"});
        ROLE_DEFAULT_PERMISSIONS.put("police", new String[]{"police"});
        ROLE_DEFAULT_PERMISSIONS.put("insurance", new String[]{"insurance"});
        ROLE_DEFAULT_PERMISSIONS.put("admin", new String[]{"vehicles", "customers", "workshop", "queries", "insurance", "police", "reports", "admin_panel"});
    }

    // ==================== VALIDATION METHODS ====================

    private static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return true;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex) && email.contains(".");
    }

    private static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return true;
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

    private static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        String usernameRegex = "^[A-Za-z0-9_]+$";
        return username.matches(usernameRegex);
    }

    private static boolean isValidPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        return password.length() >= 4;
    }

    // ==================== LOGIN METHODS ====================

    public static void showLoginScreen(Stage stage) {
        primaryStage = stage;

        VBox loginPanel = new VBox(15);
        loginPanel.setAlignment(Pos.CENTER);
        loginPanel.setPadding(new Insets(50));
        loginPanel.setStyle("-fx-background-color: linear-gradient(to bottom, #1a2639, #2c3e50, #1a2639);");

        Label title = new Label("Vehicle Identification System");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #5dade2; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 0, 1);");

        Label subtitle = new Label("Please Login to Continue");
        subtitle.setStyle("-fx-font-size: 18px; -fx-text-fill: #85c1e9;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(300);
        usernameField.setStyle("-fx-padding: 10px; -fx-font-size: 14px; -fx-background-radius: 8px; -fx-background-color: white; -fx-text-fill: #2c3e50;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);
        passwordField.setStyle("-fx-padding: 10px; -fx-font-size: 14px; -fx-background-radius: 8px; -fx-background-color: white; -fx-text-fill: #2c3e50;");

        Button loginBtn = new Button("Login");
        loginBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 30px; -fx-background-radius: 25px;");
        loginBtn.setMaxWidth(300);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #e74c3c;");

        loginBtn.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            AdminUser user = adminDAO.authenticate(username, password);
            if (user != null) {
                currentUser = user;
                launchMainApplication();
            } else {
                errorLabel.setText("Invalid username or password!");
            }
        });

        loginPanel.getChildren().addAll(title, subtitle, usernameField, passwordField, loginBtn, errorLabel);

        Scene loginScene = new Scene(loginPanel, 1200, 800);
        stage.setScene(loginScene);
        stage.setTitle("Vehicle Identification System - Login");
        stage.show();
    }

    private static void launchMainApplication() {
        Main mainApp = new Main();
        try {
            primaryStage.setMaximized(true);
            mainApp.start(primaryStage);
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Error", "Failed to start application: " + ex.getMessage());
        }
    }

    public static void logout() {
        currentUser = null;
        showLoginScreen(primaryStage);
    }

    // ==================== ADMIN VIEW ====================

    public static VBox getAdminView() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        view.setStyle("-fx-background-color: transparent;");

        Label title = new Label("Admin Panel - User Access Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #5dade2;");

        Label subtitle = new Label("Assign Roles and Grant/Deny access to specific modules for each user");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #85c1e9;");

        Label userInfo = new Label("🔐 Logged in as: " + currentUser.getUsername() + " (" + getRoleDisplayFromValue(currentUser.getRole()) + ")");
        userInfo.setStyle("-fx-font-size: 14px; -fx-text-fill: #27ae60; -fx-font-weight: bold;");

        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.setDividerPositions(0.5);
        splitPane.setStyle("-fx-background-color: transparent;");

        VBox leftPanel = createUserManagementPanel();
        VBox rightPanel = createPermissionManagementPanel();

        splitPane.getItems().addAll(leftPanel, rightPanel);

        Button logoutBtn = new Button("🚪 Logout");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px; -fx-background-radius: 25px;");
        logoutBtn.setMaxWidth(200);
        logoutBtn.setOnAction(e -> logout());

        VBox bottomBox = new VBox();
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(10));
        bottomBox.getChildren().add(logoutBtn);

        view.getChildren().addAll(title, subtitle, userInfo, splitPane, bottomBox);

        ScrollPane scrollPane = new ScrollPane(view);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        return new VBox(scrollPane);
    }

    private static String getRoleDisplayFromValue(String roleValue) {
        switch(roleValue) {
            case "user": return "👤 User";
            case "police": return "👮 Police Officer";
            case "insurance": return "📋 Insurance Agent";
            case "admin": return "🔐 Administrator";
            default: return "👤 User";
        }
    }

    // ==================== USER MANAGEMENT PANEL ====================

    private static VBox createUserManagementPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));
        panel.setStyle("-fx-background-color: white; -fx-border-color: #3498db; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label panelTitle = new Label("📝 USER MANAGEMENT");
        panelTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label giveAccessLabel = new Label("GIVE ACCESS - Create New User");
        giveAccessLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60;");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10, 0, 10, 0));

        // Labels with dark text
        Label usernameLabel = new Label("Username:*");
        usernameLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        Label passwordLabel = new Label("Password:*");
        passwordLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        Label nameLabel = new Label("Full Name:*");
        nameLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        Label phoneLabel = new Label("Phone:");
        phoneLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username (letters, numbers, underscore only)");
        usernameField.setPrefWidth(250);
        usernameField.setStyle("-fx-text-fill: #2c3e50; -fx-prompt-text-fill: #95a5a6; -fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-radius: 5px;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password (minimum 4 characters)");
        passwordField.setPrefWidth(250);
        passwordField.setStyle("-fx-text-fill: #2c3e50; -fx-prompt-text-fill: #95a5a6; -fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-radius: 5px;");

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name (letters and spaces only)");
        nameField.setStyle("-fx-text-fill: #2c3e50; -fx-prompt-text-fill: #95a5a6; -fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-radius: 5px;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email (name@domain.com)");
        emailField.setStyle("-fx-text-fill: #2c3e50; -fx-prompt-text-fill: #95a5a6; -fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-radius: 5px;");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone (numbers only)");
        phoneField.setStyle("-fx-text-fill: #2c3e50; -fx-prompt-text-fill: #95a5a6; -fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-radius: 5px;");

        // Role selection
        Label roleLabel = new Label("Assign Role:*");
        roleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll(ROLE_DISPLAY_NAMES);
        roleCombo.setValue("👤 User");
        roleCombo.setPrefWidth(200);
        roleCombo.setStyle("-fx-text-fill: #2c3e50; -fx-background-color: white;");

        form.addRow(0, usernameLabel, usernameField);
        form.addRow(1, passwordLabel, passwordField);
        form.addRow(2, nameLabel, nameField);
        form.addRow(3, emailLabel, emailField);
        form.addRow(4, phoneLabel, phoneField);
        form.addRow(5, roleLabel, roleCombo);

        // Role info label
        Label roleInfoLabel = new Label();
        roleInfoLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #2980b9;");
        updateRoleInfo(roleCombo.getValue(), roleInfoLabel);

        roleCombo.setOnAction(e -> updateRoleInfo(roleCombo.getValue(), roleInfoLabel));

        // Permissions checkboxes
        Label permissionsLabel = new Label("Or Customize Module Access:");
        permissionsLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-margin-top: 10px;");

        GridPane permissionGrid = new GridPane();
        permissionGrid.setHgap(15);
        permissionGrid.setVgap(8);
        permissionGrid.setPadding(new Insets(10, 0, 10, 0));

        Map<String, CheckBox> permissionCheckboxes = new HashMap<>();
        for (int i = 0; i < ALL_MODULES.length; i++) {
            CheckBox cb = new CheckBox(MODULE_DISPLAY_NAMES[i]);
            cb.setStyle("-fx-font-size: 13px; -fx-text-fill: #2c3e50;");
            permissionCheckboxes.put(ALL_MODULES[i], cb);
            permissionGrid.add(cb, i % 2, i / 2);
        }

        // Button to apply role defaults
        Button applyRoleBtn = new Button("📋 Apply Role Default Permissions");
        applyRoleBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5px 10px; -fx-background-radius: 5px;");
        applyRoleBtn.setOnAction(e -> {
            String selectedRole = getRoleValue(roleCombo.getValue());
            String[] defaultPerms = ROLE_DEFAULT_PERMISSIONS.get(selectedRole);
            for (String module : ALL_MODULES) {
                boolean hasAccess = false;
                for (String perm : defaultPerms) {
                    if (perm.equals(module)) {
                        hasAccess = true;
                        break;
                    }
                }
                permissionCheckboxes.get(module).setSelected(hasAccess);
            }
            showAlert("Role Applied", "Default permissions for " + roleCombo.getValue() + " have been applied!");
        });

        Button addBtn = new Button("➕ CREATE USER & GIVE ACCESS");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px; -fx-background-radius: 8px;");
        addBtn.setMaxWidth(Double.MAX_VALUE);

        Label addStatus = new Label();
        addStatus.setStyle("-fx-font-size: 12px;");
        addStatus.setWrapText(true);

        Label existingLabel = new Label("DENY ACCESS - Existing Users");
        existingLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #e74c3c; -fx-margin-top: 20px;");

        ListView<String> userList = new ListView<>();
        userList.setPrefHeight(200);
        userList.setStyle("-fx-background-color: white; -fx-text-fill: #2c3e50;");
        refreshUserList(userList);

        Button deleteBtn = new Button("❌ DELETE SELECTED USER (Deny All Access)");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px; -fx-background-radius: 8px;");
        deleteBtn.setMaxWidth(Double.MAX_VALUE);

        deleteBtn.setOnAction(e -> {
            String selected = userList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                String[] parts = selected.split(" - ");
                try {
                    int userId = Integer.parseInt(parts[0]);
                    if (adminDAO.deleteUser(userId)) {
                        refreshUserList(userList);
                        refreshUserCombo(null);
                        showAlert("Success", "User access DENIED! User can no longer login.");
                    } else {
                        showAlert("Error", "Could not delete user");
                    }
                } catch (NumberFormatException ex) {
                    showAlert("Error", "Invalid user selection");
                }
            } else {
                showAlert("Error", "Please select a user to deny access");
            }
        });

        // VALIDATED ADD USER ACTION
        addBtn.setOnAction(e -> {
            addStatus.setStyle("-fx-text-fill: #e74c3c;");

            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String selectedRole = getRoleValue(roleCombo.getValue());

            if (!isValidUsername(username)) {
                addStatus.setText("❌ Username can only contain letters, numbers, and underscore. No spaces!");
                return;
            }

            if (!isValidPassword(password)) {
                addStatus.setText("❌ Password must be at least 4 characters long!");
                return;
            }

            if (!isValidName(name)) {
                addStatus.setText("❌ Name can only contain letters, spaces, hyphens, and apostrophes. No numbers!");
                return;
            }

            if (name.isEmpty()) {
                addStatus.setText("❌ Name is required!");
                return;
            }

            if (!email.isEmpty() && !isValidEmail(email)) {
                addStatus.setText("❌ Invalid email format! Example: name@domain.com");
                return;
            }

            if (!phone.isEmpty() && !isValidPhone(phone)) {
                addStatus.setText("❌ Phone can only contain numbers, spaces, hyphens, and plus sign!");
                return;
            }

            AdminUser newUser = new AdminUser();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setPhone(phone);
            newUser.setRole(selectedRole);

            // Build permissions map
            Map<String, Boolean> permissions = new HashMap<>();
            for (String module : ALL_MODULES) {
                permissions.put(module, permissionCheckboxes.get(module).isSelected());
            }

            if (adminDAO.addUser(newUser, permissions)) {
                refreshUserList(userList);
                refreshUserCombo(null);
                usernameField.clear();
                passwordField.clear();
                nameField.clear();
                emailField.clear();
                phoneField.clear();
                for (CheckBox cb : permissionCheckboxes.values()) {
                    cb.setSelected(false);
                }
                roleCombo.setValue("👤 User");
                addStatus.setText("✅ User created successfully! Role: " + selectedRole);
                addStatus.setStyle("-fx-text-fill: #27ae60;");
            } else {
                addStatus.setText("❌ Failed to create user. Username may already exist.");
                addStatus.setStyle("-fx-text-fill: #e74c3c;");
            }
        });

        panel.getChildren().addAll(
                panelTitle, giveAccessLabel, form, roleInfoLabel, permissionsLabel, permissionGrid,
                applyRoleBtn, addBtn, addStatus, new Separator(), existingLabel, userList, deleteBtn
        );

        return panel;
    }

    private static void updateRoleInfo(String roleDisplay, Label infoLabel) {
        String role = getRoleValue(roleDisplay);
        String[] perms = ROLE_DEFAULT_PERMISSIONS.get(role);
        StringBuilder permList = new StringBuilder();
        for (String perm : perms) {
            switch(perm) {
                case "vehicles": permList.append("Vehicles "); break;
                case "customers": permList.append("Customers "); break;
                case "workshop": permList.append("Workshop "); break;
                case "queries": permList.append("Queries "); break;
                case "insurance": permList.append("Insurance "); break;
                case "police": permList.append("Police "); break;
                case "reports": permList.append("Reports "); break;
                case "admin_panel": permList.append("Admin Panel "); break;
            }
        }
        infoLabel.setText("ℹ️ " + roleDisplay + " role has default access to: " + permList.toString());
    }

    private static String getRoleValue(String roleDisplay) {
        switch(roleDisplay) {
            case "👤 User": return "user";
            case "👮 Police Officer": return "police";
            case "📋 Insurance Agent": return "insurance";
            case "🔐 Administrator": return "admin";
            default: return "user";
        }
    }

    // ==================== PERMISSION MANAGEMENT PANEL ====================

    private static VBox createPermissionManagementPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));
        panel.setStyle("-fx-background-color: white; -fx-border-color: #3498db; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label panelTitle = new Label("🔧 MANAGE PERMISSIONS");
        panelTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label infoLabel = new Label("Grant or Deny module access for existing users");
        infoLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #3498db;");

        HBox selectionBox = new HBox(10);
        selectionBox.setAlignment(Pos.CENTER_LEFT);

        Label userLabel = new Label("User:");
        userLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");

        ComboBox<String> userCombo = new ComboBox<>();
        userCombo.setPromptText("Select User");
        userCombo.setPrefWidth(250);
        userCombo.setStyle("-fx-font-size: 14px; -fx-background-color: white; -fx-text-fill: #2c3e50;");

        Button loadBtn = new Button("📂 Load Permissions");
        loadBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 8px 15px; -fx-background-radius: 8px;");

        selectionBox.getChildren().addAll(userLabel, userCombo, loadBtn);

        Label currentPermissionsLabel = new Label("Current Permissions for Selected User:");
        currentPermissionsLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-margin-top: 15px;");

        GridPane permissionGrid = new GridPane();
        permissionGrid.setHgap(20);
        permissionGrid.setVgap(12);
        permissionGrid.setPadding(new Insets(15));
        permissionGrid.setStyle("-fx-border-color: #3498db; -fx-border-radius: 8px; -fx-background-color: #f0f8ff;");

        Map<String, CheckBox> permissionCheckboxes = new HashMap<>();
        for (int i = 0; i < ALL_MODULES.length; i++) {
            CheckBox cb = new CheckBox(MODULE_DISPLAY_NAMES[i]);
            cb.setStyle("-fx-font-size: 14px; -fx-padding: 5px; -fx-text-fill: #2c3e50;");
            permissionCheckboxes.put(ALL_MODULES[i], cb);
            permissionGrid.add(cb, i % 2, i / 2);
        }

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 10, 0));

        Button saveBtn = new Button("💾 SAVE PERMISSIONS");
        saveBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-background-radius: 8px;");

        Button refreshBtn = new Button("🔄 Refresh List");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 8px;");
        refreshBtn.setOnAction(e -> refreshUserCombo(userCombo));

        buttonBox.getChildren().addAll(saveBtn, refreshBtn);

        Label saveStatus = new Label();
        saveStatus.setStyle("-fx-font-size: 12px;");
        saveStatus.setWrapText(true);

        final int[] selectedUserId = {-1};
        final String[] selectedUsername = {""};

        loadBtn.setOnAction(e -> {
            String selected = userCombo.getValue();
            if (selected != null) {
                String[] parts = selected.split(" - ");
                try {
                    selectedUserId[0] = Integer.parseInt(parts[0]);
                    selectedUsername[0] = parts[1];
                    Map<String, Boolean> permissions = adminDAO.getUserPermissions(selectedUserId[0]);
                    for (String module : ALL_MODULES) {
                        permissionCheckboxes.get(module).setSelected(permissions.getOrDefault(module, false));
                    }
                    saveStatus.setText("✅ Permissions loaded for: " + parts[1]);
                    saveStatus.setStyle("-fx-text-fill: #27ae60;");
                } catch (NumberFormatException ex) {
                    saveStatus.setText("❌ Error loading user permissions");
                    saveStatus.setStyle("-fx-text-fill: #e74c3c;");
                }
            } else {
                saveStatus.setText("❌ Please select a user first");
                saveStatus.setStyle("-fx-text-fill: #e74c3c;");
            }
        });

        saveBtn.setOnAction(e -> {
            if (selectedUserId[0] != -1) {
                boolean allSuccess = true;
                for (String module : ALL_MODULES) {
                    boolean hasAccess = permissionCheckboxes.get(module).isSelected();
                    if (!adminDAO.updatePermissions(selectedUserId[0], module, hasAccess)) {
                        allSuccess = false;
                    }
                }
                if (allSuccess) {
                    saveStatus.setText("✅ Permissions saved successfully for: " + selectedUsername[0]);
                    saveStatus.setStyle("-fx-text-fill: #27ae60;");
                    refreshUserCombo(userCombo);
                } else {
                    saveStatus.setText("⚠️ Some permissions failed to save");
                    saveStatus.setStyle("-fx-text-fill: #e74c3c;");
                }
            } else {
                saveStatus.setText("❌ Please load a user first");
                saveStatus.setStyle("-fx-text-fill: #e74c3c;");
            }
        });

        refreshUserCombo(userCombo);

        panel.getChildren().addAll(
                panelTitle, infoLabel, selectionBox, currentPermissionsLabel,
                permissionGrid, buttonBox, saveStatus
        );

        return panel;
    }

    // ==================== HELPER METHODS ====================

    private static void refreshUserList(ListView<String> userList) {
        userList.getItems().clear();
        for (AdminUser user : adminDAO.getAllUsers()) {
            String roleDisplay = getRoleDisplayFromValue(user.getRole());
            userList.getItems().add(user.getId() + " - " + user.getUsername() + " - " + roleDisplay + " - " + user.getName());
        }
    }

    private static void refreshUserCombo(ComboBox<String> userCombo) {
        if (userCombo != null) {
            userCombo.getItems().clear();
            for (AdminUser user : adminDAO.getAllUsers()) {
                String roleDisplay = getRoleDisplayFromValue(user.getRole());
                userCombo.getItems().add(user.getId() + " - " + user.getUsername() + " - " + roleDisplay);
            }
        }
    }

    private static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // ==================== GETTER METHODS ====================

    public static AdminUser getCurrentUser() {
        return currentUser;
    }

    public static boolean hasAccess(String module) {
        if (currentUser == null) return false;
        return currentUser.hasPermission(module);
    }
}