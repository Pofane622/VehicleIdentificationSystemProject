package com.example.vehicleidentificationsystem.dao;

import com.example.vehicleidentificationsystem.utils.DatabaseConnection;
import com.example.vehicleidentificationsystem.models.Vehicle;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {

    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT v.*, c.name as owner_name FROM Vehicle v " +
                "LEFT JOIN Customer c ON v.owner_id = c.customer_id " +
                "ORDER BY v.vehicle_id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Vehicle vehicle = new Vehicle();
                vehicle.setVehicleId(rs.getInt("vehicle_id"));
                vehicle.setRegistrationNumber(rs.getString("registration_number"));
                vehicle.setMake(rs.getString("make"));
                vehicle.setModel(rs.getString("model"));
                vehicle.setYear(rs.getInt("year"));
                vehicle.setOwnerId(rs.getInt("owner_id"));
                vehicle.setOwnerName(rs.getString("owner_name"));
                vehicles.add(vehicle);
            }
        } catch (SQLException e) {
            System.err.println("Error getting vehicles: " + e.getMessage());
        }
        return vehicles;
    }

    public boolean addVehicle(Vehicle vehicle) {
        String sql = "INSERT INTO Vehicle (registration_number, make, model, year, owner_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vehicle.getRegistrationNumber());
            pstmt.setString(2, vehicle.getMake());
            pstmt.setString(3, vehicle.getModel());
            pstmt.setInt(4, vehicle.getYear());
            pstmt.setInt(5, vehicle.getOwnerId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding vehicle: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteVehicle(int id) {
        String sql = "DELETE FROM Vehicle WHERE vehicle_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting vehicle: " + e.getMessage());
            return false;
        }
    }

    public boolean updateVehicle(Vehicle vehicle) {
        String sql = "UPDATE Vehicle SET registration_number=?, make=?, model=?, year=?, owner_id=? WHERE vehicle_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vehicle.getRegistrationNumber());
            pstmt.setString(2, vehicle.getMake());
            pstmt.setString(3, vehicle.getModel());
            pstmt.setInt(4, vehicle.getYear());
            pstmt.setInt(5, vehicle.getOwnerId());
            pstmt.setInt(6, vehicle.getVehicleId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating vehicle: " + e.getMessage());
            return false;
        }
    }
}