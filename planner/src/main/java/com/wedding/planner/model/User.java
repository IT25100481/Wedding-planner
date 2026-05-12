package com.wedding.planner.model;

public class User {
    private String fullName;
    private String username; // NEW FIELD
    private String email;
    private String password;
    private String role;

    public User() {}

    // Updated Constructor to include username
    public User(String fullName, String username, String email, String password, String role) {
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getUsername() { return username; } // NEW
    public void setUsername(String username) { this.username = username; } // NEW

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        // Updated to include username in the CSV format
        return fullName + "," + username + "," + email + "," + password + "," + role;
    }
}