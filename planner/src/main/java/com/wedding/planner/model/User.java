package com.wedding.planner.model;

public class User {
    protected String userName;
    protected String email;
    protected String password;
    protected String role;   // Vendor or Couple
    protected String style;  // Hindu, Christian, Sinhalese, Muslim, or Other


    //Default constructor
    public User() {

    }

    //Overloaded constructor
    public User(String userName, String email, String password, String role, String style){
        this.userName=userName;
        this.email=email;
        this.password=password;
        this.role=role;
        this.style=style;
    }

    // Standard Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getStyle() { return style; }
    public void setStyle(String style) { this.style = style; }
}