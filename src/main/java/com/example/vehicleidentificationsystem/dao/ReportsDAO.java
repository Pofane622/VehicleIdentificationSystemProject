package com.example.vehicleidentificationsystem.dao;

import com.example.vehicleidentificationsystem.utils.DatabaseConnection;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ReportsDAO {

    public Map<String, Integer> getAllStats() {
        Map<String, Integer> stats = new HashMap<>();

        String[] queries = {
                "SELECT COUNT(*) FROM Vehicle",
                "SELECT COUNT(*) FROM Customer",
                "SELECT COUNT(*) FROM ServiceRecord",
                "SELECT COUNT(*) FROM CustomerQuery",
                "SELECT COUNT(*) FROM InsurancePolicy",
                "SELECT COUNT(*) FROM PoliceReport",
                "SELECT COUNT(*) FROM Violation",
                "SELECT COUNT(*) FROM Claim WHERE status = 'Pending'",
                "SELECT COUNT(*) FROM InsurancePolicy WHERE end_date >= CURRENT_DATE",
                "SELECT COUNT(*) FROM Violation WHERE status = 'Unpaid'"
        };

        String[] statNames = {
                "totalVehicles", "totalCustomers", "totalServices", "totalQueries",
                "totalPolicies", "totalPoliceReports", "totalViolations", "pendingClaims",
                "activePolicies", "unpaidViolations"
        };

        try (Connection conn = DatabaseConnection.getConnection()) {
            for (int i = 0; i < queries.length; i++) {
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(queries[i])) {
                    if (rs.next()) {
                        stats.put(statNames[i], rs.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting stats: " + e.getMessage());
            // Set default values if error
            for (String name : statNames) {
                stats.put(name, 0);
            }
        }

        return stats;
    }

    public Map<String, Double> getFinancialStats() {
        Map<String, Double> stats = new HashMap<>();

        String[] queries = {
                "SELECT COALESCE(SUM(claim_amount), 0) FROM Claim WHERE status = 'Approved'",
                "SELECT COALESCE(SUM(fine_amount), 0) FROM Violation WHERE status = 'Unpaid'",
                "SELECT COALESCE(SUM(cost), 0) FROM ServiceRecord"
        };

        String[] statNames = {"totalClaims", "totalUnpaidFines", "totalServiceRevenue"};

        try (Connection conn = DatabaseConnection.getConnection()) {
            for (int i = 0; i < queries.length; i++) {
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(queries[i])) {
                    if (rs.next()) {
                        stats.put(statNames[i], rs.getDouble(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting financial stats: " + e.getMessage());
            for (String name : statNames) {
                stats.put(name, 0.0);
            }
        }

        return stats;
    }
}