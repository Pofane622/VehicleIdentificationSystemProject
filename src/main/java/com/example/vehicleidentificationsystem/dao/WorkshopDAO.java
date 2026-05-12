package com.example.vehicleidentificationsystem.dao;

import com.example.vehicleidentificationsystem.utils.DatabaseConnection;
import com.example.vehicleidentificationsystem.models.ServiceRecord;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkshopDAO {

    public List<ServiceRecord> getAllServiceRecords() {
        List<ServiceRecord> records = new ArrayList<>();
        String sql = "SELECT sr.*, v.make || ' ' || v.model as vehicle_info FROM ServiceRecord sr " +
                "JOIN Vehicle v ON sr.vehicle_id = v.vehicle_id ORDER BY sr.service_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ServiceRecord record = new ServiceRecord();
                record.setServiceId(rs.getInt("service_id"));
                record.setVehicleId(rs.getInt("vehicle_id"));
                record.setServiceDate(rs.getDate("service_date").toLocalDate());
                record.setServiceType(rs.getString("service_type"));
                record.setDescription(rs.getString("description"));
                record.setCost(rs.getDouble("cost"));
                record.setMechanicName(rs.getString("mechanic_name"));
                record.setVehicleInfo(rs.getString("vehicle_info"));
                records.add(record);
            }
        } catch (SQLException e) {
            System.err.println("Error getting service records: " + e.getMessage());
        }
        return records;
    }

    public boolean addServiceRecord(ServiceRecord record) {
        String sql = "INSERT INTO ServiceRecord (vehicle_id, service_date, service_type, description, cost, mechanic_name) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, record.getVehicleId());
            pstmt.setDate(2, Date.valueOf(record.getServiceDate()));
            pstmt.setString(3, record.getServiceType());
            pstmt.setString(4, record.getDescription());
            pstmt.setDouble(5, record.getCost());
            pstmt.setString(6, record.getMechanicName());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding service record: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteServiceRecord(int serviceId) {
        String sql = "DELETE FROM ServiceRecord WHERE service_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, serviceId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting service record: " + e.getMessage());
            return false;
        }
    }
}