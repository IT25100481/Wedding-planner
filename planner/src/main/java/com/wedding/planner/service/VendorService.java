package com.wedding.planner.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service   //marks class as spring service component
public class VendorService {

    private static final String FILE_PATH = "planner/services.txt";

    // reads all vendors from file
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
    // ── GET BY ID (AdminController)──
    public com.wedding.planner.model.Service getVendorById(String id) {
        return getAllVendors().stream()
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // ── UPDATE (AdminController)──
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

    // ── DELETE (AdminController)──
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

    // ── GET TOTAL COUNT ──
    public int getTotalCount() {  //returns total number of vendors
        return getAllVendors().size();
    }

    // ── GET PENDING COUNT ──
    public int getPendingCount() {   //counts pending vendors
        return (int) getAllVendors().stream()
                .filter(v -> "PENDING".equals(v.getStatus()))
                .count();
    }

    // updates vendor approval status
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

    private void saveVendor(com.wedding.planner.model.Service vendor) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(vendor.toFileLine());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addVendor(com.wedding.planner.model.Service vendor) {
        saveVendor(vendor);
    }

    public void addVendor(com.wedding.planner.model.Service vendor, String status) {
        vendor.setStatus(status);
        saveVendor(vendor);
    }

    public void addVendor(com.wedding.planner.model.Service vendor, String status, String businessName) {
        vendor.setStatus(status);
        vendor.setBusinessName(businessName);
        saveVendor(vendor);
    }


}