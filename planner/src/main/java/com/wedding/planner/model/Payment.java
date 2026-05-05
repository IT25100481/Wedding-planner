package com.wedding.planner.model;

public class Payment {

    private String id;
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

    public Payment(String id, String bookingId, String vendorName,
                   double totalAmount, double amountPaid,
                   String paymentMethod, String paymentStatus,
                   String transactionId, String notes, String createdAt) {
        this.id = id;
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
                id, bookingId, vendorName,
                String.valueOf(totalAmount),
                String.valueOf(amountPaid),
                paymentMethod, paymentStatus,
                transactionId,
                notes.replace("|", ";"),
                createdAt);
    }

    // ── Create from file line ──
    public static Payment fromFileLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 10) return null;
        return new Payment(
                parts[0], parts[1], parts[2],
                Double.parseDouble(parts[3]),
                Double.parseDouble(parts[4]),
                parts[5], parts[6], parts[7], parts[8], parts[9]
        );
    }
}