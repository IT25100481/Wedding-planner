package com.wedding.planner.service;

import com.wedding.planner.model.ServicePackage;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ServicePackageService {

    private static final String FILE_PATH = "packages.txt";

    // ── CREATE ──
    public void addPackage(ServicePackage pkg) {
        pkg.setId(UUID.randomUUID().toString().substring(0, 8));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(pkg.toFileLine());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── READ ALL ──
    public List<ServicePackage> getAllPackages() {
        List<ServicePackage> packages = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return packages;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    ServicePackage pkg = ServicePackage.fromFileLine(line);
                    if (pkg != null) packages.add(pkg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return packages;
    }

    // ── READ BY ID ──
    public ServicePackage getPackageById(String id) {
        return getAllPackages().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // ── READ BY TYPE ──
    public List<ServicePackage> getPackagesByType(String type) {
        List<ServicePackage> all = getAllPackages();
        if (type == null || type.equals("ALL")) return all;
        List<ServicePackage> filtered = new ArrayList<>();
        for (ServicePackage p : all) {
            if (p.getPackageType().equalsIgnoreCase(type)) {
                filtered.add(p);
            }
        }
        return filtered;
    }

    // ── UPDATE ──
    public void updatePackage(ServicePackage updated) {
        List<ServicePackage> packages = getAllPackages();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (ServicePackage pkg : packages) {
                if (pkg.getId().equals(updated.getId())) {
                    writer.write(updated.toFileLine());
                } else {
                    writer.write(pkg.toFileLine());
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── DELETE ──
    public void deletePackage(String id) {
        List<ServicePackage> packages = getAllPackages();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (ServicePackage pkg : packages) {
                if (!pkg.getId().equals(id)) {
                    writer.write(pkg.toFileLine());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}