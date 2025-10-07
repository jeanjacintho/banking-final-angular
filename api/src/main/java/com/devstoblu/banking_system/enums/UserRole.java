package com.devstoblu.banking_system.enums;

public enum UserRole {
    CLIENTE("user"),
    ADMIN("admin");

    private String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return this.role;
    }
}
