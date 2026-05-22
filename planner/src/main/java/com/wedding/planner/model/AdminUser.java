package com.wedding.planner.model;

public class AdminUser{

    //Attributes
    private String id;
    private String username;
    private String password;
    private String email;
    private String lastLogin;

    // ── Polymorphism --> Method Overloading  ──
    //Default constructor
    public AdminUser() {}

    //Parameterized constructor
    public AdminUser(String id, String username, String password, String email, String lastLogin) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.lastLogin = lastLogin;
    }

    // ── Getters & Setters ──
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getLastLogin() { return lastLogin; }
    public void setLastLogin(String lastLogin) { this.lastLogin = lastLogin; }


    // ── File storage Methods (Abstraction layer)──
    public String toFileLine() {
        return String.join("|", id, username, password, email, lastLogin != null ? lastLogin : "");
    }

    public static AdminUser fromFileLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 4) return null;
        return new AdminUser(parts[0], parts[1], parts[2], parts[3],
                parts.length > 4 ? parts[4] : "");
    }
}