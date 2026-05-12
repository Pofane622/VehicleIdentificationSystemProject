package com.example.vehicleidentificationsystem.models;

import java.time.LocalDate;

public class Violation {
    private int violationId;
    private int vehicleId;
    private LocalDate violationDate;
    private String violationType;
    private double fineAmount;
    private String status;
    private String vehicleInfo;

    public Violation() {}

    public Violation(int vehicleId, String violationType, double fineAmount, String status) {
        this.vehicleId = vehicleId;
        this.violationType = violationType;
        this.fineAmount = fineAmount;
        this.status = status;
        this.violationDate = LocalDate.now();
    }

    public int getViolationId() { return violationId; }
    public void setViolationId(int violationId) { this.violationId = violationId; }
    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }
    public LocalDate getViolationDate() { return violationDate; }
    public void setViolationDate(LocalDate violationDate) { this.violationDate = violationDate; }
    public String getViolationType() { return violationType; }
    public void setViolationType(String violationType) { this.violationType = violationType; }
    public double getFineAmount() { return fineAmount; }
    public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getVehicleInfo() { return vehicleInfo; }
    public void setVehicleInfo(String vehicleInfo) { this.vehicleInfo = vehicleInfo; }
}