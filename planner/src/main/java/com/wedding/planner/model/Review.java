package com.wedding.planner.model;

public class Review {

    private String id;
    private String userId;       // username of the reviewer
    private String fullName;     // display name
    private String serviceType;  // e.g. Photography, Catering, Decor
    private String vendorName;
    private int rating;          // 1–5
    private String comment;
    private String createdAt;
    private String status;       // PENDING, APPROVED, REJECTED

    // ── Constructors ──
    public Review() {}

    public Review(String id, String userId, String fullName, String serviceType,
                  String vendorName, int rating, String comment,
                  String createdAt, String status) {
        this.id = id;
        this.userId = userId;
        this.fullName = fullName;
        this.serviceType = serviceType;
        this.vendorName = vendorName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
        this.status = status;
    }

    // ── Getters & Setters ──
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // ── Serialize to one file line (pipe-delimited) ──
    public String toFileLine() {
        return String.join("|",
                id,
                userId != null ? userId : "",
                fullName != null ? fullName.replace("|", ";") : "",
                serviceType != null ? serviceType : "",
                vendorName != null ? vendorName.replace("|", ";") : "",
                String.valueOf(rating),
                comment != null ? comment.replace("|", ";").replace("\n", " ") : "",
                createdAt != null ? createdAt : "",
                status != null ? status : "PENDING"
        );
    }

    // ── Deserialize from a file line ──
    public static Review fromFileLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 9) return null;
        try {
            return new Review(
                    parts[0], parts[1], parts[2], parts[3],
                    parts[4], Integer.parseInt(parts[5]),
                    parts[6], parts[7], parts[8]
            );
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
