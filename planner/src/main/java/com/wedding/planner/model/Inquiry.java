package com.wedding.planner.model;

public class Inquiry {
    private String customerName;
    private String contactNo;
    private String weddingDate;
    private String message;

    // Default Constructor
    public Inquiry() {}

    // Getters and Setters
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getContactNo() { return contactNo; }
    public void setContactNo(String contactNo) { this.contactNo = contactNo; }

    public String getWeddingDate() { return weddingDate; }
    public void setWeddingDate(String weddingDate) { this.weddingDate = weddingDate; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}