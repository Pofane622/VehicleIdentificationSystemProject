package com.example.vehicleidentificationsystem;

import com.example.vehicleidentificationsystem.utils.Animations;
import com.example.vehicleidentificationsystem.controllers.VehicleController;
import com.example.vehicleidentificationsystem.controllers.CustomerController;
import com.example.vehicleidentificationsystem.controllers.InsuranceController;
import com.example.vehicleidentificationsystem.controllers.PoliceController;
import com.example.vehicleidentificationsystem.controllers.WorkshopController;
import com.example.vehicleidentificationsystem.controllers.CustomerQueryController;
import com.example.vehicleidentificationsystem.controllers.AdminController;
import com.example.vehicleidentificationsystem.dao.ReportsDAO;
import com.example.vehicleidentificationsystem.models.AdminUser;
import com.example.vehicleidentificationsystem.utils.DatabaseConnection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Map;

public class Main extends Application {

    private BorderPane mainLayout;
    private static Stage primaryStage;
    private AdminUser currentUser;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Vehicle Identification System");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.setMaximized(true);

        // Get the logged-in user from AdminController
        currentUser = AdminController.getCurrentUser();

        if (currentUser == null) {
            AdminController.showLoginScreen(primaryStage);
            return;
        }

        // Test database connection
        if (!DatabaseConnection.testConnection()) {
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Cannot connect to PostgreSQL database.\nPlease check:\n1. PostgreSQL is running\n2. Database 'vehicle_identification_system' exists\n3. Username/password is correct");
        }

        createMainLayout();

        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void createMainLayout() {
        mainLayout = new BorderPane();

        MenuBar menuBar = createMenuBar();
        mainLayout.setTop(menuBar);

        VBox sidebar = createSidebar();
        mainLayout.setLeft(sidebar);

        mainLayout.setCenter(createWelcomePanel());
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");

        MenuItem userInfoItem = new MenuItem("Logged in as: " + currentUser.getUsername());
        userInfoItem.setDisable(true);

        MenuItem logoutItem = new MenuItem("Logout");
        logoutItem.setOnAction(e -> {
            DatabaseConnection.closeConnection();
            AdminController.logout();
        });

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> {
            DatabaseConnection.closeConnection();
            Platform.exit();
        });

        fileMenu.getItems().addAll(userInfoItem, new SeparatorMenuItem(), logoutItem, exitItem);

        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAboutDialog());
        helpMenu.getItems().add(aboutItem);

        menuBar.getMenus().addAll(fileMenu, helpMenu);
        return menuBar;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(15));
        sidebar.setStyle("-fx-background-color: #1a2639; -fx-pref-width: 200px;");

        // Dashboard is always visible
        Button dashboardBtn = new Button("📊 Dashboard");
        dashboardBtn.setMaxWidth(Double.MAX_VALUE);
        dashboardBtn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: #85c1e9; -fx-font-size: 14px; -fx-padding: 10px; -fx-background-radius: 8px;");
        dashboardBtn.setOnAction(e -> mainLayout.setCenter(createWelcomePanel()));
        sidebar.getChildren().add(dashboardBtn);

        // Show modules based on user permissions
        if (currentUser.hasPermission("vehicles")) {
            Button btn = new Button("🚗 Vehicles");
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: #85c1e9; -fx-font-size: 14px; -fx-padding: 10px; -fx-background-radius: 8px;");
            btn.setOnAction(e -> mainLayout.setCenter(VehicleController.getView()));
            sidebar.getChildren().add(btn);
        }

        if (currentUser.hasPermission("customers")) {
            Button btn = new Button("👥 Customers");
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: #85c1e9; -fx-font-size: 14px; -fx-padding: 10px; -fx-background-radius: 8px;");
            btn.setOnAction(e -> mainLayout.setCenter(CustomerController.getView()));
            sidebar.getChildren().add(btn);
        }

        if (currentUser.hasPermission("workshop")) {
            Button btn = new Button("🔧 Workshop");
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: #85c1e9; -fx-font-size: 14px; -fx-padding: 10px; -fx-background-radius: 8px;");
            btn.setOnAction(e -> mainLayout.setCenter(WorkshopController.getView()));
            sidebar.getChildren().add(btn);
        }

        if (currentUser.hasPermission("queries")) {
            Button btn = new Button("❓ Queries");
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: #85c1e9; -fx-font-size: 14px; -fx-padding: 10px; -fx-background-radius: 8px;");
            btn.setOnAction(e -> mainLayout.setCenter(CustomerQueryController.getView()));
            sidebar.getChildren().add(btn);
        }

        if (currentUser.hasPermission("insurance")) {
            Button btn = new Button("📋 Insurance");
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: #85c1e9; -fx-font-size: 14px; -fx-padding: 10px; -fx-background-radius: 8px;");
            btn.setOnAction(e -> mainLayout.setCenter(InsuranceController.getView()));
            sidebar.getChildren().add(btn);
        }

        if (currentUser.hasPermission("police")) {
            Button btn = new Button("👮 Police");
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: #85c1e9; -fx-font-size: 14px; -fx-padding: 10px; -fx-background-radius: 8px;");
            btn.setOnAction(e -> mainLayout.setCenter(PoliceController.getView()));
            sidebar.getChildren().add(btn);
        }

        if (currentUser.hasPermission("reports")) {
            Button btn = new Button("📊 Reports");
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: #85c1e9; -fx-font-size: 14px; -fx-padding: 10px; -fx-background-radius: 8px;");
            btn.setOnAction(e -> mainLayout.setCenter(createReportsPanel()));
            sidebar.getChildren().add(btn);
        }

        // Admin Panel - only if user has admin_panel permission
        if (currentUser.hasPermission("admin_panel")) {
            Separator separator = new Separator();
            separator.setStyle("-fx-background-color: #555; -fx-padding: 10px 0;");
            sidebar.getChildren().add(separator);

            Button adminBtn = new Button("🔐 Admin Panel");
            adminBtn.setMaxWidth(Double.MAX_VALUE);
            adminBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px; -fx-background-radius: 8px;");
            adminBtn.setOnAction(e -> mainLayout.setCenter(AdminController.getAdminView()));
            sidebar.getChildren().add(adminBtn);
        }

        return sidebar;
    }

    private VBox createWelcomePanel() {
        VBox panel = new VBox(20);
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setPadding(new Insets(40, 50, 50, 50));
        panel.setStyle("-fx-background-color: transparent;");

        // Title
        Label title = new Label("Vehicle Identification System");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #5dade2; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 0, 1);");

        // Welcome message
        Label welcomeLabel = new Label("Welcome, " + (currentUser.getName() != null ? currentUser.getName() : currentUser.getUsername()) + "!");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #3498db;");

        // Role display
        String roleDisplay = getRoleDisplay(currentUser.getRole());
        Label roleLabel = new Label(roleDisplay);
        roleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #5dade2; -fx-background-color: rgba(52, 152, 219, 0.2); -fx-padding: 8px 20px; -fx-background-radius: 25px;");

        // Role description
        Label roleDescription = new Label("You are logged in as: " + getRoleDescription(currentUser.getRole()));
        roleDescription.setStyle("-fx-font-size: 14px; -fx-text-fill: #85c1e9;");

        // Accessible modules
        Label modulesLabel = new Label("Your Access: " + getAccessibleModules());
        modulesLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #85c1e9; -fx-padding: 10px; -fx-background-color: rgba(255,255,255,0.9); -fx-background-radius: 8px;");

        // ==================== PROGRESS INDICATOR (LARGER AND VISIBLE) ====================
        HBox progressIndicatorBox = new HBox(20);
        progressIndicatorBox.setAlignment(Pos.CENTER);
        progressIndicatorBox.setPadding(new Insets(20, 0, 10, 0));
        progressIndicatorBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 15px; -fx-padding: 20px;");

        // Progress Indicator - Circular (larger size)
        ProgressIndicator progressIndicator = new ProgressIndicator(0.75);
        progressIndicator.setPrefSize(120, 120);
        progressIndicator.setStyle("-fx-progress-color: #3498db;");

        VBox indicatorInfo = new VBox(5);
        indicatorInfo.setAlignment(Pos.CENTER_LEFT);
        Label indicatorLabel = new Label("System Status");
        indicatorLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #5dade2;");
        Label indicatorValue = new Label("75% Operational");
        indicatorValue.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
        Label indicatorDesc = new Label("All systems running normally");
        indicatorDesc.setStyle("-fx-font-size: 12px; -fx-text-fill: #85c1e9;");
        indicatorInfo.getChildren().addAll(indicatorLabel, indicatorValue, indicatorDesc);

        progressIndicatorBox.getChildren().addAll(progressIndicator, indicatorInfo);

        // ==================== PROGRESS BAR (LARGER AND VISIBLE) ====================
        VBox progressBarBox = new VBox(10);
        progressBarBox.setPadding(new Insets(15, 20, 15, 20));
        progressBarBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 15px;");

        Label progressBarLabel = new Label("Database Connection Status");
        progressBarLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #5dade2;");

        // Larger Progress Bar
        ProgressBar progressBar = new ProgressBar(0.92);
        progressBar.setPrefWidth(500);
        progressBar.setPrefHeight(20);
        progressBar.setStyle("-fx-accent: #27ae60; -fx-background-radius: 10px;");

        // Fixed: Changed HDBX to HBox
        HBox statusRow = new HBox(15);
        statusRow.setAlignment(Pos.CENTER_LEFT);
        Label dbLabel = new Label("Database Connection: Active");
        dbLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        Label dbPercent = new Label("92%");
        dbPercent.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 14px;");
        statusRow.getChildren().addAll(dbLabel, dbPercent);

        progressBarBox.getChildren().addAll(progressBarLabel, progressBar, statusRow);

        // ==================== SYSTEM METRICS ====================
        HBox metricsBox = new HBox(20);
        metricsBox.setAlignment(Pos.CENTER);
        metricsBox.setPadding(new Insets(20, 0, 20, 0));

        String[][] metrics = {
                {"📊", "System Uptime", "14 days", "#3498db"},
                {"💾", "Database Size", "24 MB", "#2ecc71"},
                {"👥", "Active Sessions", "1", "#f39c12"},
                {"✅", "Success Rate", "99.8%", "#27ae60"}
        };

        for (String[] metric : metrics) {
            VBox metricCard = new VBox(5);
            metricCard.setAlignment(Pos.CENTER);
            metricCard.setPadding(new Insets(10));
            metricCard.setPrefWidth(120);
            metricCard.setStyle("-fx-background-color: rgba(255,255,255,0.95); -fx-background-radius: 10px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

            Label iconLabel = new Label(metric[0]);
            iconLabel.setStyle("-fx-font-size: 24px;");
            Label metricTitle = new Label(metric[1]);
            metricTitle.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");
            Label metricValue = new Label(metric[2]);
            metricValue.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + metric[3] + ";");

            metricCard.getChildren().addAll(iconLabel, metricTitle, metricValue);
            metricsBox.getChildren().add(metricCard);
        }

        // ScrollPane
        ScrollPane scrollPane = createDummyDataScrollPane();
        scrollPane.setPrefHeight(200);
        scrollPane.setPrefWidth(600);
        scrollPane.setStyle("-fx-background-color: rgba(255,255,255,0.95); -fx-background-radius: 10px;");

        // Pagination
        Pagination pagination = createPagination();
        pagination.setPrefWidth(600);

        // Organize everything in a centered VBox
        VBox centerContent = new VBox(15);
        centerContent.setAlignment(Pos.TOP_CENTER);
        centerContent.getChildren().addAll(
                title, welcomeLabel, roleLabel, roleDescription, modulesLabel,
                progressIndicatorBox, progressBarBox, metricsBox, scrollPane, pagination
        );

        panel.getChildren().add(centerContent);

        // Make panel scrollable if content overflows
        ScrollPane mainScrollPane = new ScrollPane(panel);
        mainScrollPane.setFitToWidth(true);
        mainScrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        mainScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        return new VBox(mainScrollPane);
    }

    private String getRoleDisplay(String role) {
        switch(role) {
            case "admin":
                return "👑 ADMINISTRATOR";
            case "police":
                return "👮 POLICE OFFICER";
            case "insurance":
                return "📋 INSURANCE AGENT";
            case "user":
                return "👤 REGULAR USER";
            default:
                return "👤 USER";
        }
    }

    private String getRoleDescription(String role) {
        switch(role) {
            case "admin":
                return "Administrator - Full system access including user management";
            case "police":
                return "Police Officer - Access to police reports and violation records";
            case "insurance":
                return "Insurance Agent - Access to insurance policies and claims";
            case "user":
                return "Regular User - Access to vehicles, customers, workshop, and queries";
            default:
                return "User - Basic access";
        }
    }

    private String getAccessibleModules() {
        StringBuilder modules = new StringBuilder();
        if (currentUser.hasPermission("vehicles")) modules.append("Vehicles ");
        if (currentUser.hasPermission("customers")) modules.append("Customers ");
        if (currentUser.hasPermission("workshop")) modules.append("Workshop ");
        if (currentUser.hasPermission("queries")) modules.append("Queries ");
        if (currentUser.hasPermission("insurance")) modules.append("Insurance ");
        if (currentUser.hasPermission("police")) modules.append("Police ");
        if (currentUser.hasPermission("reports")) modules.append("Reports ");
        if (currentUser.hasPermission("admin_panel")) modules.append("Admin Panel ");

        if (modules.length() == 0) {
            return "No additional modules granted. Contact admin for access.";
        }
        return modules.toString();
    }

    private ScrollPane createDummyDataScrollPane() {
        VBox content = new VBox(5);
        content.setPadding(new Insets(10));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 8px;");

        String[] items = {
                "Vehicle Registration: ABC123 - Toyota Camry 2020",
                "Vehicle Registration: XYZ789 - Honda Civic 2021",
                "Vehicle Registration: DEF456 - Ford Mustang 2019",
                "Workshop Service: Toyota Camry - Oil Change - 01/10/2024",
                "Workshop Service: Honda Civic - Brake Service - 02/01/2024",
                "Workshop Service: Ford Mustang - Engine Check - 01/20/2024",
                "Customer Query: John Doe - When is my next oil change?",
                "Customer Query: Jane Smith - Check engine light is on",
                "Insurance Policy: POL001 - Geico - Active",
                "Insurance Policy: POL002 - State Farm - Active",
                "Police Report: ACC001 - Minor Accident - Resolved",
                "Violation: SPD001 - Speeding - Unpaid",
                "Customer: John Doe - 3 Vehicles Registered",
                "Customer: Jane Smith - 2 Vehicles Registered",
                "Insurance Claim: CLM002 - Approved - $2,500",
                "Police Report: THF001 - Theft Report - Under Investigation",
                "Vehicle Registration: GHI789 - Tesla Model 3 2022",
                "Workshop Service: Tesla Model 3 - Software Update - 02/15/2024",
                "Customer Query: Bob Johnson - Is my insurance active?",
                "Violation: RUN001 - Running Red Light - Unpaid"
        };

        for (String item : items) {
            Label label = new Label("• " + item);
            label.setStyle("-fx-padding: 5px; -fx-text-fill: #2c3e50; -fx-border-color: #dee2e6;");
            content.getChildren().add(label);
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white; -fx-border-color: #3498db; -fx-border-radius: 8px;");

        return scrollPane;
    }

    private Pagination createPagination() {
        Pagination pagination = new Pagination(5, 0);
        pagination.setPrefSize(600, 150);
        pagination.setStyle("-fx-background-color: transparent;");

        pagination.setPageFactory(pageIndex -> {
            VBox pageContent = new VBox(10);
            pageContent.setAlignment(Pos.CENTER);
            pageContent.setPadding(new Insets(15));
            pageContent.setStyle("-fx-background-color: white; -fx-border-color: #3498db; -fx-border-radius: 8px;");

            String[] pageTitles = {
                    "Vehicle Overview - Page 1",
                    "Workshop Services - Page 2",
                    "Customer Queries - Page 3",
                    "Insurance Summary - Page 4",
                    "Police Records - Page 5"
            };

            String[] pageData = {
                    "Total Vehicles: 45 | Active Vehicles: 38 | Pending Registration: 7",
                    "Services Completed: 12 | Pending Services: 3 | Total Revenue: $2,450",
                    "Open Queries: 4 | Resolved: 8 | Response Rate: 95%",
                    "Active Policies: 32 | Pending Claims: 5 | Total Coverage: $1.2M",
                    "Reports Filed: 12 | Open Cases: 3 | Resolved: 9"
            };

            Label titleLabel = new Label(pageTitles[pageIndex]);
            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2980b9;");

            Label dataLabel = new Label(pageData[pageIndex]);
            dataLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");

            pageContent.getChildren().addAll(titleLabel, dataLabel);
            return pageContent;
        });

        return pagination;
    }

    // ==================== DYNAMIC REPORTS PANEL ====================

    private VBox createReportsPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: transparent;");

        Label title = new Label("System Reports Dashboard");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #5dade2; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 0, 1);");

        Label subtitle = new Label("Real-time statistics from the database");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #85c1e9;");

        Button refreshBtn = new Button("🔄 Refresh Statistics");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-radius: 25px;");

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(15);
        statsGrid.setVgap(15);
        statsGrid.setPadding(new Insets(10));
        statsGrid.setStyle("-fx-background-color: transparent;");

        Label financialTitle = new Label("Financial Summary");
        financialTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #5dade2; -fx-padding: 20 0 10 0;");

        GridPane financialGrid = new GridPane();
        financialGrid.setHgap(15);
        financialGrid.setVgap(15);
        financialGrid.setPadding(new Insets(10));
        financialGrid.setStyle("-fx-background-color: transparent;");

        loadStats(statsGrid, financialGrid);

        refreshBtn.setOnAction(e -> {
            loadStats(statsGrid, financialGrid);
            showAlert(Alert.AlertType.INFORMATION, "Refreshed", "Statistics have been updated from the database!");
        });

        Button generateReportBtn = new Button("📊 Export Full Report");
        generateReportBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-radius: 25px;");
        Animations.addDropShadow(generateReportBtn);

        Button animatedBtn = new Button("✨ Live Status");
        animatedBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-radius: 25px;");
        Animations.addFadeTransition(animatedBtn);

        generateReportBtn.setOnAction(e -> generateFullReport());

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(refreshBtn, generateReportBtn, animatedBtn);

        panel.getChildren().addAll(title, subtitle, statsGrid, financialTitle, financialGrid, buttonBox);

        ScrollPane scrollPane = new ScrollPane(panel);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setPadding(new Insets(10));

        return new VBox(scrollPane);
    }

    private void loadStats(GridPane statsGrid, GridPane financialGrid) {
        ReportsDAO reportsDAO = new ReportsDAO();
        Map<String, Integer> stats = reportsDAO.getAllStats();
        Map<String, Double> financialStats = reportsDAO.getFinancialStats();

        statsGrid.getChildren().clear();
        financialGrid.getChildren().clear();

        String[][] statCards = {
                {"🚗 Total Vehicles", String.valueOf(stats.getOrDefault("totalVehicles", 0)), "#3498db"},
                {"👥 Total Customers", String.valueOf(stats.getOrDefault("totalCustomers", 0)), "#2ecc71"},
                {"🔧 Services Done", String.valueOf(stats.getOrDefault("totalServices", 0)), "#f39c12"},
                {"❓ Customer Queries", String.valueOf(stats.getOrDefault("totalQueries", 0)), "#e74c3c"},
                {"📋 Insurance Policies", String.valueOf(stats.getOrDefault("totalPolicies", 0)), "#9b59b6"},
                {"👮 Police Reports", String.valueOf(stats.getOrDefault("totalPoliceReports", 0)), "#1abc9c"},
                {"📊 Traffic Violations", String.valueOf(stats.getOrDefault("totalViolations", 0)), "#e67e22"},
                {"⏳ Pending Claims", String.valueOf(stats.getOrDefault("pendingClaims", 0)), "#e74c3c"},
                {"✅ Active Policies", String.valueOf(stats.getOrDefault("activePolicies", 0)), "#27ae60"},
                {"💰 Unpaid Fines", String.valueOf(stats.getOrDefault("unpaidViolations", 0)), "#c0392b"}
        };

        for (int i = 0; i < statCards.length; i++) {
            VBox card = createStatCard(statCards[i][0], statCards[i][1], statCards[i][2]);
            statsGrid.add(card, i % 3, i / 3);
        }

        String[][] financialCards = {
                {"💰 Total Approved Claims", "$" + String.format("%.2f", financialStats.getOrDefault("totalClaims", 0.0)), "#e74c3c"},
                {"⚠️ Unpaid Fines Total", "$" + String.format("%.2f", financialStats.getOrDefault("totalUnpaidFines", 0.0)), "#f39c12"},
                {"🔧 Service Revenue", "$" + String.format("%.2f", financialStats.getOrDefault("totalServiceRevenue", 0.0)), "#27ae60"}
        };

        for (int i = 0; i < financialCards.length; i++) {
            VBox card = createStatCard(financialCards[i][0], financialCards[i][1], financialCards[i][2]);
            financialGrid.add(card, i % 3, i / 3);
        }
    }

    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: rgba(255,255,255,0.95); -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        card.setPrefWidth(250);
        card.setMinWidth(200);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 14px; -fx-font-weight: normal;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 32px; -fx-font-weight: bold;");

        card.getChildren().addAll(titleLabel, valueLabel);

        return card;
    }

    private void generateFullReport() {
        ReportsDAO reportsDAO = new ReportsDAO();
        Map<String, Integer> stats = reportsDAO.getAllStats();
        Map<String, Double> financialStats = reportsDAO.getFinancialStats();

        StringBuilder report = new StringBuilder();
        report.append("========== VEHICLE IDENTIFICATION SYSTEM REPORT ==========\n\n");
        report.append("Generated on: ").append(java.time.LocalDateTime.now()).append("\n\n");

        report.append("--- SYSTEM STATISTICS ---\n");
        report.append("Total Vehicles: ").append(stats.getOrDefault("totalVehicles", 0)).append("\n");
        report.append("Total Customers: ").append(stats.getOrDefault("totalCustomers", 0)).append("\n");
        report.append("Total Service Records: ").append(stats.getOrDefault("totalServices", 0)).append("\n");
        report.append("Total Customer Queries: ").append(stats.getOrDefault("totalQueries", 0)).append("\n");
        report.append("Total Insurance Policies: ").append(stats.getOrDefault("totalPolicies", 0)).append("\n");
        report.append("Active Policies: ").append(stats.getOrDefault("activePolicies", 0)).append("\n");
        report.append("Total Police Reports: ").append(stats.getOrDefault("totalPoliceReports", 0)).append("\n");
        report.append("Total Violations: ").append(stats.getOrDefault("totalViolations", 0)).append("\n");
        report.append("Pending Claims: ").append(stats.getOrDefault("pendingClaims", 0)).append("\n");
        report.append("Unpaid Violations: ").append(stats.getOrDefault("unpaidViolations", 0)).append("\n\n");

        report.append("--- FINANCIAL SUMMARY ---\n");
        report.append("Total Approved Claims: $").append(String.format("%.2f", financialStats.getOrDefault("totalClaims", 0.0))).append("\n");
        report.append("Total Unpaid Fines: $").append(String.format("%.2f", financialStats.getOrDefault("totalUnpaidFines", 0.0))).append("\n");
        report.append("Total Service Revenue: $").append(String.format("%.2f", financialStats.getOrDefault("totalServiceRevenue", 0.0))).append("\n");

        report.append("\n========== END OF REPORT ==========");

        TextArea reportArea = new TextArea(report.toString());
        reportArea.setEditable(false);
        reportArea.setPrefWidth(600);
        reportArea.setPrefHeight(400);
        reportArea.setStyle("-fx-font-family: monospace; -fx-font-size: 12px;");

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("System Report");
        dialog.setHeaderText("Complete System Report");
        dialog.getDialogPane().setContent(reportArea);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Vehicle Identification System");
        alert.setHeaderText("Vehicle Identification System v1.0");
        alert.setContentText("""
            A comprehensive vehicle management system with granular access control.
            
            Features:
            • Granular Module-Based Access Control
            • Vehicle Registration Management
            • Customer Information System
            • Workshop Service History
            • Customer Query Management
            • Insurance Policy Tracking
            • Police Records Management
            • Dynamic Reports Dashboard
            • Admin Panel for Fine-Grained Access Control
            
            Admin can grant/deny access to EACH module individually.
            """);
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}