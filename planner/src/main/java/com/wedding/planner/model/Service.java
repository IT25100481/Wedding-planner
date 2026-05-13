package com.wedding.planner.model;

import java.util.Scanner;

public class Service {
    private String id;
    private String businessName;
    private String category;
    private String tradition;
    private String description;
    private String contact;
    private String price;
    private String imagePath;
    private String status; // Ensure this field exists

    public Service() {}

    public static Service fromFileLine(String line) {
        if (line == null || line.isEmpty()) return null;
        try (Scanner sc = new Scanner(line).useDelimiter("\\|")) {
            Service s = new Service();
            if (sc.hasNext()) s.setId(sc.next());
            if (sc.hasNext()) s.setBusinessName(sc.next());
            if (sc.hasNext()) s.setCategory(sc.next());
            if (sc.hasNext()) s.setTradition(sc.next());
            if (sc.hasNext()) s.setDescription(sc.next());
            if (sc.hasNext()) s.setContact(sc.next());
            if (sc.hasNext()) s.setPrice(sc.next());
            if (sc.hasNext()) s.setImagePath(sc.next());
            if (sc.hasNext()) s.setStatus(sc.next());
            return s;
        } catch (Exception e) {
            return null;
        }
    }

    // ─── ADD THIS METHOD FOR SAVING ───
    // This fixes the "cannot find symbol method toFileLine()" error
    public String toFileLine() {
        return id + "|" + businessName + "|" + category + "|" + tradition + "|" +
                description + "|" + contact + "|" + price + "|" + imagePath + "|" + status;
    }

    // ─── ADD GETTER AND SETTER FOR STATUS ───
    // This fixes the "cannot find symbol method getStatus()/setStatus()" errors
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // ─── ENSURE ALL OTHER GETTERS/SETTERS ARE PRESENT ───
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