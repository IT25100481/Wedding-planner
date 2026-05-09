package com.wedding.planner.service;

import com.wedding.planner.model.AdminUser;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AdminService {

    private static final String FILE_PATH = "admins.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ── CREATE ──
    public void createAdmin(AdminUser admin) {
        admin.setId(UUID.randomUUID().toString().substring(0, 8));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(admin.toFileLine());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── READ ALL ──
    public List<AdminUser> getAllAdmins() {
        List<AdminUser> admins = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return admins;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    AdminUser admin = AdminUser.fromFileLine(line);
                    if (admin != null) admins.add(admin);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return admins;
    }

    // ── GET BY USERNAME ──
    public AdminUser getAdminByUsername(String username) {
        return getAllAdmins().stream()
                .filter(a -> a.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    // ── AUTHENTICATE ──
    public boolean authenticate(String username, String password) {
        AdminUser admin = getAdminByUsername(username);
        if (admin == null) return false;
        return admin.getPassword().equals(password);
    }

    // ── GET RECENT LOGINS ──
    public List<String> getRecentLogins() {
        List<String> logins = new ArrayList<>();
        for (AdminUser admin : getAllAdmins()) {
            if (admin.getLastLogin() != null && !admin.getLastLogin().isEmpty()) {
                logins.add(admin.getUsername() + " - " + admin.getLastLogin());
            }
        }
        return logins;
    }

    // ── UPDATE ──
    public void updateAdmin(AdminUser updated) {
        List<AdminUser> admins = getAllAdmins();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (AdminUser admin : admins) {
                if (admin.getId().equals(updated.getId())) {
                    writer.write(updated.toFileLine());
                } else {
                    writer.write(admin.toFileLine());
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── DELETE ──
    public void deleteAdmin(String id) {
        List<AdminUser> admins = getAllAdmins();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (AdminUser admin : admins) {
                if (!admin.getId().equals(id)) {
                    writer.write(admin.toFileLine());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}