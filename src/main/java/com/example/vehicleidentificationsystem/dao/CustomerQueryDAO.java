package com.example.vehicleidentificationsystem.dao;

import com.example.vehicleidentificationsystem.utils.DatabaseConnection;
import com.example.vehicleidentificationsystem.models.CustomerQuery;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerQueryDAO {

    public List<CustomerQuery> getAllQueries() {
        List<CustomerQuery> queries = new ArrayList<>();
        String sql = "SELECT cq.*, cu.name as customer_name, v.make || ' ' || v.model as vehicle_info " +
                "FROM CustomerQuery cq " +
                "JOIN Customer cu ON cq.customer_id = cu.customer_id " +
                "JOIN Vehicle v ON cq.vehicle_id = v.vehicle_id " +
                "ORDER BY cq.query_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                CustomerQuery query = new CustomerQuery();
                query.setQueryId(rs.getInt("query_id"));
                query.setCustomerId(rs.getInt("customer_id"));
                query.setVehicleId(rs.getInt("vehicle_id"));
                query.setQueryDate(rs.getDate("query_date").toLocalDate());
                query.setQueryText(rs.getString("query_text"));
                query.setResponseText(rs.getString("response_text"));
                query.setStatus(rs.getString("status"));
                query.setCustomerName(rs.getString("customer_name"));
                query.setVehicleInfo(rs.getString("vehicle_info"));
                queries.add(query);
            }
        } catch (SQLException e) {
            System.err.println("Error getting queries: " + e.getMessage());
        }
        return queries;
    }

    public boolean addQuery(CustomerQuery query) {
        String sql = "INSERT INTO CustomerQuery (customer_id, vehicle_id, query_text, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, query.getCustomerId());
            pstmt.setInt(2, query.getVehicleId());
            pstmt.setString(3, query.getQueryText());
            pstmt.setString(4, query.getStatus());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding query: " + e.getMessage());
            return false;
        }
    }

    public boolean updateResponse(int queryId, String response, String status) {
        String sql = "UPDATE CustomerQuery SET response_text = ?, status = ? WHERE query_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, response);
            pstmt.setString(2, status);
            pstmt.setInt(3, queryId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating response: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteQuery(int queryId) {
        String sql = "DELETE FROM CustomerQuery WHERE query_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, queryId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting query: " + e.getMessage());
            return false;
        }
    }
}