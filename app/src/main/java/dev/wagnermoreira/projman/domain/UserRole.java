package dev.wagnermoreira.projman.domain;

public enum UserRole {
    ADMIN("Admin"),
    MANAGER("Manager"),
    EMPLOYEE("Employee");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}


