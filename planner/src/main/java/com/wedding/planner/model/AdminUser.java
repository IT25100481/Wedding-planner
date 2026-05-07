package com.wedding.planner.model;

// Inheritance: AdminUser extends User
public class AdminUser extends User{

    //Attributes
    private String userID;
    private String lastLogin;

    //Default Constructor
    public AdminUser() {
    }

    //Overloaded Constructor (Parameterized)
    public AdminUser(String userName, String email,String password,String role,String style,String userID,String lastLogin) {
        super(userName,email,password,role,style); // call parent constructor
        this.userName = userName;
        this.userID = userID;
        this.lastLogin = lastLogin;
    }

    //Getters and Setters
    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }
    public String getLastLogin() {
        return lastLogin;
    }
    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }
}
