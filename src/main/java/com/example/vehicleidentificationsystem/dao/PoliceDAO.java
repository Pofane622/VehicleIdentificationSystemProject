package com.example.vehicleidentificationsystem.dao;

import com.example.vehicleidentificationsystem.utils.DatabaseConnection;
import com.example.vehicleidentificationsystem.models.PoliceReport;
import com.example.vehicleidentificationsystem.models.Violation;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PoliceDAO {

    // Get all police reports
    public List<PoliceReport> getAllPoliceReports() {
        List<PoliceReport> reports = new ArrayList<>();
        String sql = "SELECT pr.*, v.make || ' ' || v.model as vehicle_info FROM PoliceReport pr " +
                "JOIN Vehicle v ON pr.vehicle_id = v.vehicle_id ORDER BY pr.report_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                PoliceReport report = new PoliceReport();
                report.setReportId(rs.getInt("report_id"));
                report.setVehicleId(rs.getInt("vehicle_id"));
                report.setReportDate(rs.getDate("report_date").toLocalDate());
                report.setReportType(rs.getString("report_type"));
                report.setDescription(rs.getString("description"));
                report.setOfficerName(rs.getString("officer_name"));
                report.setVehicleInfo(rs.getString("vehicle_info"));
                reports.add(report);
            }
        } catch (SQLException e) {
            System.err.println("Error getting police reports: " + e.getMessage());
        }
        return reports;
    }

    // Add police report
    public boolean addPoliceReport(PoliceReport report) {
        String sql = "INSERT INTO PoliceReport (vehicle_id, report_date, report_type, description, officer_name) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, report.getVehicleId());
            pstmt.setDate(2, Date.valueOf(report.getReportDate()));
            pstmt.setString(3, report.getReportType());
            pstmt.setString(4, report.getDescription());
            pstmt.setString(5, report.getOfficerName());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding police report: " + e.getMessage());
            return false;
        }
    }

    // Delete police report
    public boolean deletePoliceReport(int reportId) {
        String sql = "DELETE FROM PoliceReport WHERE report_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reportId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting police report: " + e.getMessage());
            return false;
        }
    }

    // Get all violations
    public List<Violation> getAllViolations() {
        List<Violation> violations = new ArrayList<>();
        String sql = "SELECT v.*, ve.make || ' ' || ve.model as vehicle_info FROM Violation v " +
                "JOIN Vehicle ve ON v.vehicle_id = ve.vehicle_id ORDER BY v.violation_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Violation violation = new Violation();
                violation.setViolationId(rs.getInt("violation_id"));
                violation.setVehicleId(rs.getInt("vehicle_id"));
                violation.setViolationDate(rs.getDate("violation_date").toLocalDate());
                violation.setViolationType(rs.getString("violation_type"));
                violation.setFineAmount(rs.getDouble("fine_amount"));
                violation.setStatus(rs.getString("status"));
                violation.setVehicleInfo(rs.getString("vehicle_info"));
                violations.add(violation);
            }
        } catch (SQLException e) {
            System.err.println("Error getting violations: " + e.getMessage());
        }
        return violations;
    }

    // Add violation
    public boolean addViolation(Violation violation) {
        String sql = "INSERT INTO Violation (vehicle_id, violation_date, violation_type, fine_amount, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, violation.getVehicleId());
            pstmt.setDate(2, Date.valueOf(violation.getViolationDate()));
            pstmt.setString(3, violation.getViolationType());
            pstmt.setDouble(4, violation.getFineAmount());
            pstmt.setString(5, violation.getStatus());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding violation: " + e.getMessage());
            return false;
        }
    }

    // Delete violation
    public boolean deleteViolation(int violationId) {
        String sql = "DELETE FROM Violation WHERE violation_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, violationId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting violation: " + e.getMessage());
            return false;
        }
    }

    // Update violation status
    public boolean updateViolationStatus(int violationId, String status) {
        String sql = "UPDATE Violation SET status = ? WHERE violation_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, violationId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating violation: " + e.getMessage());
            return false;
        }
    }
}