package com.example.vehicleidentificationsystem.models;

import java.time.LocalDate;

public class PoliceReport {
    private int reportId;
    private int vehicleId;
    private LocalDate reportDate;
    private String reportType;
    private String description;
    private String officerName;
    private String vehicleInfo;

    public PoliceReport() {}

    public PoliceReport(int vehicleId, String reportType, String description, String officerName) {
        this.vehicleId = vehicleId;
        this.reportType = reportType;
        this.description = description;
        this.officerName = officerName;
        this.reportDate = LocalDate.now();
    }

    public int getReportId() { return reportId; }
    public void setReportId(int reportId) { this.reportId = reportId; }
    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }
    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getOfficerName() { return officerName; }
    public void setOfficerName(String officerName) { this.officerName = officerName; }
    public String getVehicleInfo() { return vehicleInfo; }
    public void setVehicleInfo(String vehicleInfo) { this.vehicleInfo = vehicleInfo; }
}