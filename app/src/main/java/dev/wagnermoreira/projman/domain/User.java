package dev.wagnermoreira.projman.domain;

import java.util.Objects;

public class User {
    private Long id;
    private String fullName;
    private String cpf;
    private String email;
    private String jobTitle;
    private String username;
    private String passwordHash;
    private UserRole role;

    public User(
            Long id,
            String fullName,
            String cpf,
            String email,
            String jobTitle,
            String username,
            String passwordHash,
            UserRole role
    ) {
        this.id = id;
        this.fullName = fullName;
        this.cpf = cpf;
        this.email = email;
        this.jobTitle = jobTitle;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public static User ofNew(
            String fullName,
            String cpf,
            String email,
            String jobTitle,
            String username,
            String passwordHash,
            UserRole role
    ) {
        return new User(null, fullName, cpf, email, jobTitle, username, passwordHash, role);
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getCpf() { return cpf; }
    public String getEmail() { return email; }
    public String getJobTitle() { return jobTitle; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public UserRole getRole() { return role; }

    public void setId(Long id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public void setEmail(String email) { this.email = email; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public void setUsername(String username) { this.username = username; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setRole(UserRole role) { this.role = role; }

    public static boolean isValidEmail(String email) {
        return email != null && email.contains("@");
    }

    public static boolean isValidCpf(String cpf) {
        return cpf != null && cpf.replaceAll("\\D", "").length() == 11;
    }

    public static boolean isValidUsername(String username) {
        return username != null && username.length() >= 3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}


