package com.wedding.planner.model;

public class Service {
    private String id;
    private String businessName;
    private String category;
    private String tradition;
    private String description;
    private String contact;
    private String price;
    private String imagePath;

    public Service() {}

    public Service(String id, String businessName, String category, String tradition, String description, String contact, String price) {
        this.id = id;
        this.businessName = businessName;
        this.category = category;
        this.tradition = tradition;
        this.description = description;
        this.contact = contact;
        this.price = price;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getTradition() { return tradition; }
    public void setTradition(String tradition) { this.tradition = tradition; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}