package com.wedding.planner.service;

import com.wedding.planner.model.Payment;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private static final String FILE_PATH = "payments.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ── CREATE ──
    public void addPayment(Payment payment) {
        payment.setId(UUID.randomUUID().toString().substring(0, 8));
        payment.setCreatedAt(LocalDateTime.now().format(FORMATTER));

        // Auto set payment status
        if (payment.getAmountPaid() >= payment.getTotalAmount()) {
            payment.setPaymentStatus("FULLY_PAID");
        } else if (payment.getAmountPaid() > 0) {
            payment.setPaymentStatus("PARTIAL_PAID");
        } else {
            payment.setPaymentStatus("PENDING");
        }

        // Auto generate transaction ID if empty
        if (payment.getTransactionId() == null || payment.getTransactionId().isEmpty()) {
            payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(payment.toFileLine());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── READ ALL ──
    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return payments;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Payment payment = Payment.fromFileLine(line);
                    if (payment != null) payments.add(payment);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return payments;
    }

    // ── READ BY ID ──
    public Payment getPaymentById(String id) {
        return getAllPayments().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // ── READ BY BOOKING ──
    public List<Payment> getPaymentsByBooking(String bookingId) {
        List<Payment> result = new ArrayList<>();
        for (Payment p : getAllPayments()) {
            if (p.getBookingId().equals(bookingId)) {
                result.add(p);
            }
        }
        return result;
    }

    // ── TOTAL EARNINGS ──
    public double getTotalEarnings() {
        return getAllPayments().stream()
                .mapToDouble(Payment::getAmountPaid)
                .sum();
    }

    // ── UPDATE ──
    public void updatePayment(Payment updated) {
        List<Payment> payments = getAllPayments();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Payment payment : payments) {
                if (payment.getId().equals(updated.getId())) {
                    writer.write(updated.toFileLine());
                } else {
                    writer.write(payment.toFileLine());
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── DELETE ──
    public void deletePayment(String id) {
        List<Payment> payments = getAllPayments();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Payment payment : payments) {
                if (!payment.getId().equals(id)) {
                    writer.write(payment.toFileLine());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}