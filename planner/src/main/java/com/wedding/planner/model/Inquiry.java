package com.wedding.planner.model;

public class Inquiry {
    private String customerEmail;
    private String customerName;
    private String contactNo;
    private String weddingDate;
    private String message;
    private String vendorName; // 👈 වෙන්ඩර් නම සේව් කරන්න මේක අනිවාර්යයි!

    // Default Constructor
    public Inquiry() {}

    // Getters and Setters
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getContactNo() { return contactNo; }
    public void setContactNo(String contactNo) { this.contactNo = contactNo; }

    public String getWeddingDate() { return weddingDate; }
    public void setWeddingDate(String weddingDate) { this.weddingDate = weddingDate; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }
}