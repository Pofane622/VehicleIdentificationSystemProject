package com.example.vehicleidentificationsystem.dao;

import com.example.vehicleidentificationsystem.utils.DatabaseConnection;
import com.example.vehicleidentificationsystem.models.InsurancePolicy;
import com.example.vehicleidentificationsystem.models.Claim;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InsuranceDAO {

    // Get all insurance policies
    public List<InsurancePolicy> getAllPolicies() {
        List<InsurancePolicy> policies = new ArrayList<>();
        String sql = "SELECT ip.*, v.make || ' ' || v.model as vehicle_info FROM InsurancePolicy ip " +
                "JOIN Vehicle v ON ip.vehicle_id = v.vehicle_id ORDER BY ip.policy_id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                InsurancePolicy policy = new InsurancePolicy();
                policy.setPolicyId(rs.getInt("policy_id"));
                policy.setVehicleId(rs.getInt("vehicle_id"));
                policy.setInsuranceCompany(rs.getString("insurance_company"));
                policy.setPolicyNumber(rs.getString("policy_number"));
                policy.setStartDate(rs.getDate("start_date").toLocalDate());
                policy.setEndDate(rs.getDate("end_date").toLocalDate());
                policy.setCoverageDetails(rs.getString("coverage_details"));
                policy.setVehicleInfo(rs.getString("vehicle_info"));
                policies.add(policy);
            }
        } catch (SQLException e) {
            System.err.println("Error getting policies: " + e.getMessage());
        }
        return policies;
    }

    // Add new insurance policy
    public boolean addPolicy(InsurancePolicy policy) {
        String sql = "INSERT INTO InsurancePolicy (vehicle_id, insurance_company, policy_number, start_date, end_date, coverage_details) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, policy.getVehicleId());
            pstmt.setString(2, policy.getInsuranceCompany());
            pstmt.setString(3, policy.getPolicyNumber());
            pstmt.setDate(4, Date.valueOf(policy.getStartDate()));
            pstmt.setDate(5, Date.valueOf(policy.getEndDate()));
            pstmt.setString(6, policy.getCoverageDetails());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding policy: " + e.getMessage());
            return false;
        }
    }

    // Delete insurance policy
    public boolean deletePolicy(int policyId) {
        String sql = "DELETE FROM InsurancePolicy WHERE policy_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, policyId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting policy: " + e.getMessage());
            return false;
        }
    }

    // Add a claim
    public boolean addClaim(int policyId, double claimAmount, String status) {
        String sql = "INSERT INTO Claim (policy_id, claim_amount, status) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, policyId);
            pstmt.setDouble(2, claimAmount);
            pstmt.setString(3, status);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding claim: " + e.getMessage());
            return false;
        }
    }

    // Get all claims
    public List<Claim> getAllClaims() {
        List<Claim> claims = new ArrayList<>();
        String sql = "SELECT * FROM Claim ORDER BY claim_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Claim claim = new Claim();
                claim.setClaimId(rs.getInt("claim_id"));
                claim.setPolicyId(rs.getInt("policy_id"));
                claim.setClaimDate(rs.getDate("claim_date").toLocalDate());
                claim.setClaimAmount(rs.getDouble("claim_amount"));
                claim.setStatus(rs.getString("status"));
                claims.add(claim);
            }
        } catch (SQLException e) {
            System.err.println("Error getting claims: " + e.getMessage());
        }
        return claims;
    }

    // Get claims by policy ID
    public List<Claim> getClaimsByPolicy(int policyId) {
        List<Claim> claims = new ArrayList<>();
        String sql = "SELECT * FROM Claim WHERE policy_id = ? ORDER BY claim_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, policyId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Claim claim = new Claim();
                claim.setClaimId(rs.getInt("claim_id"));
                claim.setPolicyId(rs.getInt("policy_id"));
                claim.setClaimDate(rs.getDate("claim_date").toLocalDate());
                claim.setClaimAmount(rs.getDouble("claim_amount"));
                claim.setStatus(rs.getString("status"));
                claims.add(claim);
            }
        } catch (SQLException e) {
            System.err.println("Error getting claims by policy: " + e.getMessage());
        }
        return claims;
    }

    // Update claim status
    public boolean updateClaimStatus(int claimId, String status) {
        String sql = "UPDATE Claim SET status = ? WHERE claim_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, claimId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating claim: " + e.getMessage());
            return false;
        }
    }

    // Delete claim
    public boolean deleteClaim(int claimId) {
        String sql = "DELETE FROM Claim WHERE claim_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, claimId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting claim: " + e.getMessage());
            return false;
        }
    }
}