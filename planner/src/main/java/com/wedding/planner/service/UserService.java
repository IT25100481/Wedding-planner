package com.wedding.planner.service;

import com.wedding.planner.model.User;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final String FILE_PATH = "src/main/resources/data/users.txt";

    // ── PRIVATE SECURITY HELPER: HASH PASSWORDS (SHA-256) ──
    private String hashPassword(String password) {
        if (password == null || password.trim().isEmpty()) return "";
        // If it's already an active hex hash, don't re-hash it again
        if (password.length() == 64 && password.matches("^[a-fA-F0-9]+$")) {
            return password;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error executing SHA-256 fallback encryption", e);
        }
    }

    // ── NEW: PUBLIC MATCHING METHOD FOR CONTROLLER LOG-IN VALIDATION ──
    public boolean checkPassword(String plainPassword, String hashedPassword) {
        return hashPassword(plainPassword).equals(hashedPassword);
    }

    // ── GET ALL USERS (Helper for Updates) ──
    private List<User> getAllUsers() {
        try {
            Path path = Paths.get(FILE_PATH);
            if (!Files.exists(path)) return new ArrayList<>();
            return Files.lines(path)
                    .map(line -> line.split(","))
                    .filter(parts -> parts.length >= 5)
                    .map(parts -> new User(parts[0], parts[1], parts[2], parts[3], parts[4]))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    // ── SAVE ALL USERS (Helper for Overwriting) ──
    private void saveAllUsers(List<User> users) {
        try {
            List<String> lines = users.stream()
                    .map(u -> String.format("%s,%s,%s,%s,%s",
                            u.getFullName(), u.getUsername(), u.getEmail(), hashPassword(u.getPassword()), u.getRole()))
                    .collect(Collectors.toList());
            Files.write(Paths.get(FILE_PATH), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── GET ALL USERS PUBLIC — used by AdminController ──
    public List<User> getAllUsersPublic() {
        return getAllUsers();
    }

    // ── DELETE BY USERNAME — used by AdminController ──
    public void deleteByUsername(String username) {
        List<User> users = getAllUsers();
        users.removeIf(u -> u.getUsername().equalsIgnoreCase(username));
        saveAllUsers(users);
    }

    // ── UPDATE FULL NAME ──
    public void updateFullName(String username, String newName) {
        List<User> users = getAllUsers();
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                u.setFullName(newName);
                break;
            }
        }
        saveAllUsers(users);
    }

    // ── UPDATE PASSWORD BY USERNAME (From Profile Page) ──
    public void updatePasswordByUsername(String username, String newPassword) {
        List<User> users = getAllUsers();
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                u.setPassword(hashPassword(newPassword)); // Hashed safely here
                break;
            }
        }
        saveAllUsers(users);
    }

    // ── UNIQUE CHECK ──
    public boolean isUsernameTaken(String username) {
        try {
            Path path = Paths.get(FILE_PATH);
            if (!Files.exists(path)) return false;
            return Files.lines(path)
                    .map(line -> line.split(","))
                    .anyMatch(parts -> parts.length >= 5 && parts[1].equalsIgnoreCase(username));
        } catch (IOException e) { return false; }
    }

    // ── SAVE USER (Single Append on Registration) ──
    public void saveUser(User user) {
        try {
            Files.createDirectories(Paths.get("src/main/resources/data"));
            String data = String.format("%s,%s,%s,%s,%s%n",
                    user.getFullName(), user.getUsername(), user.getEmail(), hashPassword(user.getPassword()), user.getRole());
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

    // ── UPDATE PASSWORD (Forgot Password Flow) ──
    public void updatePassword(String email, String newPassword) {
        List<User> users = getAllUsers();
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                u.setPassword(hashPassword(newPassword)); // Hashed safely here
                break;
            }
        }
        saveAllUsers(users);
    }
}