package com.example.vehicleidentificationsystem.models;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class AdminUser extends Person {
    private String username;
    private String password;
    private String role;  // Added role field
    private LocalDateTime createdAt;
    private Map<String, Boolean> permissions;

    public AdminUser() {
        super();
        this.permissions = new HashMap<>();
        this.role = "user";
    }

    public AdminUser(String username, String password, String name, String email, String phone) {
        super(name, phone, email);
        this.username = username;
        this.password = password;
        this.role = "user";
        this.createdAt = LocalDateTime.now();
        this.permissions = new HashMap<>();
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Map<String, Boolean> getPermissions() { return permissions; }
    public void setPermissions(Map<String, Boolean> permissions) { this.permissions = permissions; }

    public void addPermission(String module, boolean hasAccess) {
        this.permissions.put(module, hasAccess);
    }

    public boolean hasPermission(String module) {
        return permissions.getOrDefault(module, false);
    }
}