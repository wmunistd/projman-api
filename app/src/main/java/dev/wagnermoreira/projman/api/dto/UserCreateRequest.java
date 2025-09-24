package dev.wagnermoreira.projman.api.dto;

public class UserCreateRequest {
    public String fullName;
    public String cpf;
    public String email;
    public String jobTitle;
    public String username;
    public String password;
    public String role; // ADMIN | MANAGER | EMPLOYEE
}


