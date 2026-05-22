package com.wedding.planner.model;

public class Inquiry {
    private String customerEmail;
    private String customerName;
    private String contactNo;
    private String message;
    private String vendorName = "General Inquiry";
    private String userRole; // Dropdown eken select karana 'Couple' hari 'Vendor' hari methanata enne

    public Inquiry() {}

    // Getters and Setters
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getContactNo() { return contactNo; }
    public void setContactNo(String contactNo) { this.contactNo = contactNo; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }
}