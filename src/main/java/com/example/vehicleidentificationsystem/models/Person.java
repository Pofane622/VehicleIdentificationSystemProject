package com.example.vehicleidentificationsystem.models;

public abstract class Person {
    protected int id;
    protected String name;
    protected String phone;
    protected String email;

    public Person() {}

    public Person(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public abstract String getRole();

    public String getContactInfo() {
        return "Name: " + name + ", Phone: " + phone + ", Email: " + email;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}