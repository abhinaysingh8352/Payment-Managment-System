package org.zetaproject.model.entites;

import org.zetaproject.model.enums.UserRole;

public class User {
    private Long id;
    private String name;
    private String email;
    private String password; // Storing the password directly, but in a real app, it should be a hash
    private UserRole role;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
}