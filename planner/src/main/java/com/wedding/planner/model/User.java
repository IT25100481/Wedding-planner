package com.wedding.planner.model;

public class User {
    private String email;
    private String password;
    private String role;   // Vendor or Couple
    private String style;  // Hindu, Christian, Sinhalese, Muslim, or Other

    // Standard Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getStyle() { return style; }
    public void setStyle(String style) { this.style = style; }
}