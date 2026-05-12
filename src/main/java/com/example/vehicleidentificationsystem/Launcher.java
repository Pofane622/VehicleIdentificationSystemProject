package com.example.vehicleidentificationsystem;

import com.example.vehicleidentificationsystem.controllers.AdminController;
import javafx.application.Application;
import javafx.stage.Stage;

public class Launcher extends Application {

    @Override
    public void start(Stage primaryStage) {
        AdminController.showLoginScreen(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}