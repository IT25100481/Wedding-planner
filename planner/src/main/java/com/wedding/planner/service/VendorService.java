package com.wedding.planner.service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
public class VendorService {

    private static final String FILE_PATH = "services.txt";

    // ── READ ALL ──
    public List<com.wedding.planner.model.Service> getAllVendors() {
        List<com.wedding.planner.model.Service> vendors = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return vendors;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    com.wedding.planner.model.Service vendor = com.wedding.planner.model.Service.fromFileLine(line);
                    if (vendor != null) vendors.add(vendor);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vendors;
    }

    // ── GET BY ID ──
    public com.wedding.planner.model.Service getVendorById(String id) {
        return getAllVendors().stream()
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // ── ADD ──
    public void addVendor(com.wedding.planner.model.Service vendor) {
        vendor.setId(UUID.randomUUID().toString().substring(0, 8));
        if (vendor.getStatus() == null || vendor.getStatus().isBlank()) {
            vendor.setStatus("PENDING");
        }
        if (vendor.getImagePath() == null || vendor.getImagePath().isBlank()) {
            vendor.setImagePath("no-image.jpg");
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(vendor.toFileLine());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── UPDATE ──
    public void updateVendor(com.wedding.planner.model.Service updated) {
        List<com.wedding.planner.model.Service> vendors = getAllVendors();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (com.wedding.planner.model.Service vendor : vendors) {
                if (vendor.getId().equals(updated.getId())) {
                    updated.setImagePath(vendor.getImagePath());
                    writer.write(updated.toFileLine());
                } else {
                    writer.write(vendor.toFileLine());
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── DELETE ──
    public void deleteVendor(String id) {
        List<com.wedding.planner.model.Service> vendors = getAllVendors();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (com.wedding.planner.model.Service vendor : vendors) {
                if (!vendor.getId().equals(id)) {
                    writer.write(vendor.toFileLine());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── UPDATE STATUS (original) ──
    public void updateStatus(String id, String status) {
        List<com.wedding.planner.model.Service> vendors = getAllVendors();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (com.wedding.planner.model.Service vendor : vendors) {
                if (vendor.getId().equals(id)) {
                    vendor.setStatus(status);
                }
                writer.write(vendor.toFileLine());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── GET TOTAL COUNT (original) ──
    public int getTotalCount() {
        return getAllVendors().size();
    }

    // ── GET PENDING COUNT (original) ──
    public int getPendingCount() {
        return (int) getAllVendors().stream()
                .filter(v -> "PENDING".equals(v.getStatus()))
                .count();
    }
}