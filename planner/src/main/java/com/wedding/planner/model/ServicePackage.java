package com.wedding.planner.model;

public class ServicePackage {

    private String id;
    private String packageName;
    private String packageType; // BASIC, PREMIUM, CUSTOM
    private String description;
    private double price;
    private String inclusions;
    private String exclusions;
    private boolean isActive;

    // ── Constructors ──
    public ServicePackage() {}

    public ServicePackage(String id, String packageName, String packageType,
                          String description, double price,
                          String inclusions, String exclusions, boolean isActive) {
        this.id = id;
        this.packageName = packageName;
        this.packageType = packageType;
        this.description = description;
        this.price = price;
        this.inclusions = inclusions;
        this.exclusions = exclusions;
        this.isActive = isActive;
    }

    // ── Getters & Setters ──
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public String getPackageType() { return packageType; }
    public void setPackageType(String packageType) { this.packageType = packageType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getInclusions() { return inclusions; }
    public void setInclusions(String inclusions) { this.inclusions = inclusions; }

    public String getExclusions() { return exclusions; }
    public void setExclusions(String exclusions) { this.exclusions = exclusions; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    // ── Convert to file line ──
    public String toFileLine() {
        return String.join("|",
                id, packageName, packageType,
                description.replace("|", ";"),
                String.valueOf(price),
                inclusions.replace("|", ";"),
                exclusions.replace("|", ";"),
                String.valueOf(isActive));
    }

    // ── Create from file line ──
    public static ServicePackage fromFileLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 8) return null;
        return new ServicePackage(
                parts[0], parts[1], parts[2], parts[3],
                Double.parseDouble(parts[4]),
                parts[5], parts[6],
                Boolean.parseBoolean(parts[7])
        );
    }
}