package com.example.vehicleidentificationsystem.models;

import java.time.LocalDate;

public class Claim {
    private int claimId;
    private int policyId;
    private LocalDate claimDate;
    private double claimAmount;
    private String status;

    public Claim() {}

    public Claim(int policyId, double claimAmount, String status) {
        this.policyId = policyId;
        this.claimAmount = claimAmount;
        this.status = status;
        this.claimDate = LocalDate.now();
    }

    // Getters
    public int getClaimId() { return claimId; }
    public int getPolicyId() { return policyId; }
    public LocalDate getClaimDate() { return claimDate; }
    public double getClaimAmount() { return claimAmount; }
    public String getStatus() { return status; }

    // Setters
    public void setClaimId(int claimId) { this.claimId = claimId; }
    public void setPolicyId(int policyId) { this.policyId = policyId; }
    public void setClaimDate(LocalDate claimDate) { this.claimDate = claimDate; }
    public void setClaimAmount(double claimAmount) { this.claimAmount = claimAmount; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Claim #" + claimId + " - $" + claimAmount + " (" + status + ")";
    }
}