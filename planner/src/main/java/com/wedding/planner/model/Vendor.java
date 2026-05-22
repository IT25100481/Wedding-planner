package com.wedding.planner.model;

public class Vendor extends BaseUser {

    private String name;

    public Vendor(String name) {
        this.name = name;
    }

    @Override
    public String getRole() {
        return "VENDOR";
    }

    public String getName() {
        return name;
    }
}
