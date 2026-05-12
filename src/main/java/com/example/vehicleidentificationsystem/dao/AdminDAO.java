package com.example.vehicleidentificationsystem.dao;

import com.example.vehicleidentificationsystem.utils.DatabaseConnection;
import com.example.vehicleidentificationsystem.models.AdminUser;
import java.sql.*;
import java.util.*;

public class AdminDAO {

    // Authenticate user and load their permissions
    public AdminUser authenticate(String username, String password) {
        String sql = "SELECT * FROM AdminUser WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                AdminUser user = new AdminUser();
                user.setId(rs.getInt("admin_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));

                // Load permissions for this user
                loadPermissions(user);

                System.out.println("✅ User authenticated: " + username + " (Role: " + user.getRole() + ")");
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
        }
        return null;
    }

    // Load permissions for a user
    private void loadPermissions(AdminUser user) {
        String sql = "SELECT module_name, has_access FROM UserPermissions WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, user.getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String module = rs.getString("module_name");
                boolean hasAccess = rs.getBoolean("has_access");
                user.addPermission(module, hasAccess);
            }
        } catch (SQLException e) {
            System.err.println("Error loading permissions: " + e.getMessage());
        }
    }

    // Get all users with their permissions
    public List<AdminUser> getAllUsers() {
        List<AdminUser> users = new ArrayList<>();
        String sql = "SELECT * FROM AdminUser ORDER BY admin_id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                AdminUser user = new AdminUser();
                user.setId(rs.getInt("admin_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                loadPermissions(user);
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error getting users: " + e.getMessage());
        }
        return users;
    }

    // Add new user with permissions
    public boolean addUser(AdminUser user, Map<String, Boolean> permissions) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Insert user with role
            String userSql = "INSERT INTO AdminUser (username, password, role, name, email, phone) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getName());
            pstmt.setString(5, user.getEmail());
            pstmt.setString(6, user.getPhone());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            int userId = -1;
            if (rs.next()) {
                userId = rs.getInt(1);
            }

            // Insert permissions
            String permSql = "INSERT INTO UserPermissions (user_id, module_name, has_access) VALUES (?, ?, ?)";
            for (Map.Entry<String, Boolean> entry : permissions.entrySet()) {
                PreparedStatement permStmt = conn.prepareStatement(permSql);
                permStmt.setInt(1, userId);
                permStmt.setString(2, entry.getKey());
                permStmt.setBoolean(3, entry.getValue());
                permStmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error adding user: " + e.getMessage());
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Update user permissions
    public boolean updatePermissions(int userId, String module, boolean hasAccess) {
        String sql = "INSERT INTO UserPermissions (user_id, module_name, has_access) VALUES (?, ?, ?) " +
                "ON CONFLICT (user_id, module_name) DO UPDATE SET has_access = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, module);
            pstmt.setBoolean(3, hasAccess);
            pstmt.setBoolean(4, hasAccess);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating permissions: " + e.getMessage());
            return false;
        }
    }

    // Delete user (deny all access)
    public boolean deleteUser(int userId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Delete permissions first
            String permSql = "DELETE FROM UserPermissions WHERE user_id = ?";
            PreparedStatement permStmt = conn.prepareStatement(permSql);
            permStmt.setInt(1, userId);
            permStmt.executeUpdate();

            // Delete user
            String userSql = "DELETE FROM AdminUser WHERE admin_id = ?";
            PreparedStatement userStmt = conn.prepareStatement(userSql);
            userStmt.setInt(1, userId);
            userStmt.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Get user permissions
    public Map<String, Boolean> getUserPermissions(int userId) {
        Map<String, Boolean> permissions = new HashMap<>();
        String sql = "SELECT module_name, has_access FROM UserPermissions WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                permissions.put(rs.getString("module_name"), rs.getBoolean("has_access"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting permissions: " + e.getMessage());
        }
        return permissions;
    }
}