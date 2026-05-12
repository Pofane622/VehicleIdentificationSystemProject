package com.example.vehicleidentificationsystem.utils;

import java.sql.*;

public class DBDiagnostic {
    public static void main(String[] args) {
        System.out.println("=== PostgreSQL Connection Diagnostic ===\n");

        // Test different connection configurations
        String[] urls = {
                "jdbc:postgresql://localhost:5432/vehicle_identification_system",
                "jdbc:postgresql://localhost:5432/postgres",
                "jdbc:postgresql://127.0.0.1:5432/postgres",
                "jdbc:postgresql://localhost:5433/postgres"
        };

        String[] users = {"postgres", "postgresql", "postgresadmin"};
        String[] passwords = {"postgre123", "postgres", "password", "admin", ""};

        boolean connected = false;

        for (String url : urls) {
            for (String user : users) {
                for (String password : passwords) {
                    try {
                        System.out.println("Trying: " + url);
                        System.out.println("User: " + user + ", Password: " + (password.isEmpty() ? "(empty)" : "***"));

                        Connection conn = DriverManager.getConnection(url, user, password);
                        if (conn != null) {
                            System.out.println("✅ SUCCESS! Connected with:");
                            System.out.println("   URL: " + url);
                            System.out.println("   User: " + user);
                            System.out.println("   Password: " + (password.isEmpty() ? "(empty)" : password));
                            System.out.println("   Database: " + conn.getMetaData().getDatabaseProductName());
                            conn.close();
                            connected = true;
                            break;
                        }
                    } catch (SQLException e) {
                        System.out.println("   ❌ Failed: " + e.getMessage());
                    }
                    System.out.println();
                }
                if (connected) break;
            }
            if (connected) break;
        }

        if (!connected) {
            System.out.println("\n❌ Could not connect with any configuration!");
            System.out.println("\nPlease check:");
            System.out.println("1. Open pgAdmin or psql and verify your credentials");
            System.out.println("2. Run in psql: \\l to list databases");
            System.out.println("3. Run in psql: SELECT current_user; to see your username");
        }
    }
}