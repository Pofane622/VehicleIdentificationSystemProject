package com.example.vehicleidentificationsystem.models;

public class Customer extends Person {
    private String address;

    public Customer() {
        super();
    }

    public Customer(String name, String address, String phone, String email) {
        super(name, phone, email);
        this.address = address;
    }

    @Override
    public String getRole() {
        return "Customer";
    }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String getContactInfo() {
        return super.getContactInfo() + ", Address: " + address;
    }
}