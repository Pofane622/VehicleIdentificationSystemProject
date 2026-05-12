package com.example.vehicleidentificationsystem.models;

import java.time.LocalDate;

public class CustomerQuery {
    private int queryId;
    private int customerId;
    private int vehicleId;
    private LocalDate queryDate;
    private String queryText;
    private String responseText;
    private String status;
    private String customerName;
    private String vehicleInfo;

    public CustomerQuery() {}

    public int getQueryId() { return queryId; }
    public void setQueryId(int queryId) { this.queryId = queryId; }
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }
    public LocalDate getQueryDate() { return queryDate; }
    public void setQueryDate(LocalDate queryDate) { this.queryDate = queryDate; }
    public String getQueryText() { return queryText; }
    public void setQueryText(String queryText) { this.queryText = queryText; }
    public String getResponseText() { return responseText; }
    public void setResponseText(String responseText) { this.responseText = responseText; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getVehicleInfo() { return vehicleInfo; }
    public void setVehicleInfo(String vehicleInfo) { this.vehicleInfo = vehicleInfo; }
}