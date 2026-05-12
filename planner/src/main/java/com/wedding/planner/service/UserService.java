package com.wedding.planner.service;

import com.wedding.planner.model.User;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.*;
import java.util.*;

@Service
public class UserService {
    private final String FILE_PATH = "src/main/resources/data/users.txt";

    // ── UNIQUE CHECK ──
    public boolean isUsernameTaken(String username) {
        try {
            Path path = Paths.get(FILE_PATH);
            if (!Files.exists(path)) return false;
            return Files.lines(path)
                    .map(line -> line.split(","))
                    // Index 1 is the Username column
                    .anyMatch(parts -> parts.length >= 5 && parts[1].equalsIgnoreCase(username));
        } catch (IOException e) { return false; }
    }

    // ── SAVE USER (5 Columns) ──
    public void saveUser(User user) {
        try {
            Files.createDirectories(Paths.get("src/main/resources/data"));
            String data = String.format("%s,%s,%s,%s,%s%n",
                    user.getFullName(), user.getUsername(), user.getEmail(), user.getPassword(), user.getRole());
            Files.write(Paths.get(FILE_PATH), data.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) { e.printStackTrace(); }
    }

    // ── FINDING LOGIC ──
    public User findByUsername(String username) {
        return findByField(1, username);
    }

    public User findByEmail(String email) {
        return findByField(2, email);
    }

    private User findByField(int index, String value) {
        try {
            Path path = Paths.get(FILE_PATH);
            if (!Files.exists(path)) return null;
            return Files.lines(path)
                    .map(line -> line.split(","))
                    .filter(parts -> parts.length >= 5 && parts[index].equalsIgnoreCase(value))
                    .map(parts -> new User(parts[0], parts[1], parts[2], parts[3], parts[4]))
                    .findFirst().orElse(null);
        } catch (IOException e) { return null; }
    }

    // ── UPDATE PASSWORD (Matches 5-Column Format) ──
    public void updatePassword(String email, String newPassword) {
        try {
            Path path = Paths.get(FILE_PATH);
            List<String> lines = Files.readAllLines(path);
            List<String> updatedLines = new ArrayList<>();

            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[2].equalsIgnoreCase(email)) {
                    // Update index 3 (Password) while keeping other 4 columns same
                    updatedLines.add(String.format("%s,%s,%s,%s,%s",
                            parts[0], parts[1], parts[2], newPassword, parts[4]));
                } else {
                    updatedLines.add(line);
                }
            }
            Files.write(path, updatedLines);
        } catch (IOException e) { e.printStackTrace(); }
    }
}