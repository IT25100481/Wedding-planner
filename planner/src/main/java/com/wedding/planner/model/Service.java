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
    private String status; // PENDING, APPROVED, REJECTED

    public Service() {}

    public Service(String id, String businessName, String category, String tradition,
                   String description, String contact, String price) {
        this.id = id;
        this.businessName = businessName;
        this.category = category;
        this.tradition = tradition;
        this.description = description;
        this.contact = contact;
        this.price = price;
        this.status = "PENDING";
    }

    // ── Getters & Setters ──
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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // ── Convert to file line ──
    public String toFileLine() {
        return String.join("|",
                id,
                businessName,
                category,
                tradition,
                description != null ? description.replace("|", ";") : "",
                contact,
                price,
                imagePath != null ? imagePath : "",
                status != null ? status : "PENDING");
    }

    // ── Create from file line ──
    public static Service fromFileLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 7) return null;
        Service service = new Service(
                parts[0], parts[1], parts[2], parts[3],
                parts[4], parts[5], parts[6]);
        if (parts.length > 7) service.setImagePath(parts[7]);
        if (parts.length > 8) service.setStatus(parts[8]);
        return service;
    }
}