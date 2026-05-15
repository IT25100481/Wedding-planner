package com.wedding.planner.model;

public class Payment {

    private String id;
    private String userId;        // links payment to logged-in client
    private String bookingId;
    private String vendorName;
    private double totalAmount;
    private double amountPaid;
    private String paymentMethod; // CASH, CARD, BANK_TRANSFER, PAYHERE
    private String paymentStatus; // PENDING, PARTIAL_PAID, FULLY_PAID
    private String transactionId;
    private String notes;
    private String createdAt;

    // ── Constructors ──
    public Payment() {}

    public Payment(String id, String userId, String bookingId, String vendorName,
                   double totalAmount, double amountPaid,
                   String paymentMethod, String paymentStatus,
                   String transactionId, String notes, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.bookingId = bookingId;
        this.vendorName = vendorName;
        this.totalAmount = totalAmount;
        this.amountPaid = amountPaid;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.transactionId = transactionId;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    // ── Getters & Setters ──
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    // ── Convert to file line ──
    public String toFileLine() {
        return String.join("|",
                id,
                userId != null ? userId : "",
                bookingId,
                vendorName,
                String.valueOf(totalAmount),
                String.valueOf(amountPaid),
                paymentMethod, paymentStatus,
                transactionId != null ? transactionId : "",
                notes != null ? notes.replace("|", ";") : "",
                createdAt);
    }

    // ── Create from file line ──
    // supports both old format (10 fields) and new format (11 fields with userId)
    public static Payment fromFileLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length == 10) {
            // old format — no userId field
            return new Payment(
                    parts[0], "",        // id, userId empty
                    parts[1], parts[2],  // bookingId, vendorName
                    Double.parseDouble(parts[3]),
                    Double.parseDouble(parts[4]),
                    parts[5], parts[6], parts[7], parts[8], parts[9]
            );
        } else if (parts.length >= 11) {
            // new format — has userId
            return new Payment(
                    parts[0], parts[1],  // id, userId
                    parts[2], parts[3],  // bookingId, vendorName
                    Double.parseDouble(parts[4]),
                    Double.parseDouble(parts[5]),
                    parts[6], parts[7], parts[8], parts[9], parts[10]
            );
        }
        return null;
    }
}